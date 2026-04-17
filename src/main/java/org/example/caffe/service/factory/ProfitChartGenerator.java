package org.example.caffe.service.factory;

import org.example.caffe.dto.ProfitChartDto;

import java.util.List;

public interface ProfitChartGenerator {
    ProfitChartDto generateProfitChart(Long productId, Integer year);

    String getType();

    default Double getMaxProfit(List<Double> data) {
        return data.stream().max(Double::compareTo).orElse(0.0);
    }

    default Double getMinProfit(List<Double> data) {
        return data.stream().min(Double::compareTo).orElse(0.0);
    }
}
