package org.example.caffe.service.factory;

import org.example.caffe.domain.DailyExpense;
import org.example.caffe.dto.ProfitChartDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class YearlyExpenseChartGenerator implements ExpenseChartGenerator {

    @Override
    public String getType() {
        return "YEARLY";
    }

    @Override
    public ProfitChartDto generateChart(List<DailyExpense> expenses, int targetYear) {
        // Show last 5 years up to targetYear
        int startYear = targetYear - 4;
        Map<Integer, Double> totalByYear = new HashMap<>();
        for (int y = startYear; y <= targetYear; y++) totalByYear.put(y, 0.0);

        for (DailyExpense e : expenses) {
            int y = e.getExpenseDate().getYear();
            if (totalByYear.containsKey(y)) {
                totalByYear.put(y, totalByYear.get(y) + (e.getTotalAmount() != null ? e.getTotalAmount() : 0.0));
            }
        }

        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        for (int y = startYear; y <= targetYear; y++) {
            labels.add(String.valueOf(y));
            data.add(totalByYear.get(y));
        }

        double max = data.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double min = data.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);

        return ProfitChartDto.builder().labels(labels).data(data).maxProfit(max).minProfit(min).build();
    }
}
