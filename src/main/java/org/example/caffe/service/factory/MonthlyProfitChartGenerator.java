package org.example.caffe.service.factory;

import org.example.caffe.domain.OrderItem;
import org.example.caffe.domain.OrderStatus;
import org.example.caffe.dto.ProfitChartDto;
import org.example.caffe.repository.OrderItemRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MonthlyProfitChartGenerator implements ProfitChartGenerator {

    private final OrderItemRepository orderItemRepository;

    public MonthlyProfitChartGenerator(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public String getType() {
        return "MONTHLY";
    }

    @Override
    public ProfitChartDto generateProfitChart(Long productId, Integer year) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        Instant start = Year.of(targetYear).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = Year.of(targetYear).atMonth(12).atEndOfMonth().atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
        
        List<OrderItem> items;
        if (productId != null) {
            items = orderItemRepository.findByStatusAndProductIdAndCreatedDateBetween(OrderStatus.PLACED, productId, start, end);
        } else {
            items = orderItemRepository.findByStatusAndCreatedDateBetween(OrderStatus.PLACED, start, end);
        }

        Map<Integer, Double> profitByMonth = new HashMap<>();
        for (int i = 1; i <= 12; i++) profitByMonth.put(i, 0.0);

        for (OrderItem item : items) {
            int month = item.getCreatedDate().atZone(ZoneOffset.UTC).getMonthValue();
            double profit = (item.getTotalPrice() != null ? item.getTotalPrice() : 0.0) 
                          - ((item.getProductActualMadePrice() != null ? item.getProductActualMadePrice() : 0.0) * item.getQuantity());
            profitByMonth.put(month, profitByMonth.get(month) + profit);
        }

        // Always start from January; end at the last month that actually has data.
        // Months before first data show 0; months after last data are excluded.
        int maxMonth = items.isEmpty() ? LocalDate.now().getMonthValue()
                : items.stream()
                        .mapToInt(item -> item.getCreatedDate().atZone(ZoneOffset.UTC).getMonthValue())
                        .max().orElse(LocalDate.now().getMonthValue());

        String[] monthNames = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        for (int i = 1; i <= maxMonth; i++) {   // Jan → maxMonth
            labels.add(monthNames[i]);
            data.add(profitByMonth.get(i));
        }
        
        Double maxProfit = getMaxProfit(data);
        Double minProfit = getMinProfit(data);
        
        return ProfitChartDto.builder().labels(labels).data(data).maxProfit(maxProfit).minProfit(minProfit).build();
    }
}
