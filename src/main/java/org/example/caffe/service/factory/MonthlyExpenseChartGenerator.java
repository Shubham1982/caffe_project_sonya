package org.example.caffe.service.factory;

import org.example.caffe.domain.DailyExpense;
import org.example.caffe.dto.ProfitChartDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MonthlyExpenseChartGenerator implements ExpenseChartGenerator {

    @Override
    public String getType() {
        return "MONTHLY";
    }

    @Override
    public ProfitChartDto generateChart(List<DailyExpense> expenses, int targetYear) {
        Map<Integer, Double> totalByMonth = new HashMap<>();
        for (int i = 1; i <= 12; i++) totalByMonth.put(i, 0.0);

        for (DailyExpense e : expenses) {
            int month = e.getExpenseDate().getMonthValue();
            totalByMonth.put(month, totalByMonth.get(month) + (e.getTotalAmount() != null ? e.getTotalAmount() : 0.0));
        }

        // Always start from January; end at the last month that actually has data.
        int maxMonth = expenses.isEmpty() ? LocalDate.now().getMonthValue()
                : expenses.stream().mapToInt(e -> e.getExpenseDate().getMonthValue()).max().orElse(LocalDate.now().getMonthValue());

        String[] monthNames = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        for (int i = 1; i <= maxMonth; i++) {
            labels.add(monthNames[i]);
            data.add(totalByMonth.get(i));
        }

        double max = data.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double min = data.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);

        return ProfitChartDto.builder().labels(labels).data(data).maxProfit(max).minProfit(min).build();
    }
}
