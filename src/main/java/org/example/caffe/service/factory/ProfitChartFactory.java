package org.example.caffe.service.factory;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProfitChartFactory {
    private final Map<String, ProfitChartGenerator> generatorMap;

    public ProfitChartFactory(List<ProfitChartGenerator> generators) {
        this.generatorMap = generators.stream()
                .collect(Collectors.toMap(g -> g.getType().toUpperCase(), g -> g));
    }

    public ProfitChartGenerator getGenerator(String reportType) {
        if (reportType == null) {
            return null;
        }
        return generatorMap.get(reportType.toUpperCase());
    }
}
