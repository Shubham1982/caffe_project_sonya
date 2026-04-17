package org.example.caffe.service.factory;

import org.example.caffe.domain.OrderItem;
import org.example.caffe.domain.OrderStatus;
import org.example.caffe.dto.ProfitChartDto;
import org.example.caffe.repository.OrderItemRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class YearlyProfitChartGenerator implements ProfitChartGenerator {

    private final OrderItemRepository orderItemRepository;

    public YearlyProfitChartGenerator(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public String getType() {
        return "YEARLY";
    }

    @Override
    public ProfitChartDto generateProfitChart(Long productId, Integer year) {
        List<OrderItem> items;
        if (productId != null) {
            items = orderItemRepository.findByStatusAndProductId(OrderStatus.PLACED, productId);
        } else {
            items = orderItemRepository.findByStatus(OrderStatus.PLACED);
        }

        Map<Integer, Double> profitByYear = new TreeMap<>();
        for (OrderItem item : items) {
            int itemYear = item.getCreatedDate().atZone(ZoneOffset.UTC).getYear();
            double profit = (item.getTotalPrice() != null ? item.getTotalPrice() : 0.0)
                          - ((item.getProductActualMadePrice() != null ? item.getProductActualMadePrice() : 0.0) * item.getQuantity());
            profitByYear.put(itemYear, profitByYear.getOrDefault(itemYear, 0.0) + profit);
        }

        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        
        if (!profitByYear.isEmpty()) {
            int minYear = profitByYear.keySet().stream().min(Integer::compareTo).orElse(LocalDate.now().getYear());
            int maxYear = LocalDate.now().getYear();
            for (int y = minYear; y <= maxYear; y++) {
                labels.add(String.valueOf(y));
                data.add(profitByYear.getOrDefault(y, 0.0));
            }
        }

        Double maxProfit = getMaxProfit(data);
        Double minProfit = getMinProfit(data);

        return ProfitChartDto.builder().labels(labels).data(data).maxProfit(maxProfit).minProfit(minProfit).build();
    }
}
