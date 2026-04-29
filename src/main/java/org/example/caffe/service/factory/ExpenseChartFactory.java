package org.example.caffe.service.factory;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExpenseChartFactory {
    private final Map<String, ExpenseChartGenerator> generatorMap;

    public ExpenseChartFactory(List<ExpenseChartGenerator> generators) {
        this.generatorMap = generators.stream()
                .collect(Collectors.toMap(g -> g.getType().toUpperCase(), g -> g));
    }

    public ExpenseChartGenerator getGenerator(String reportType) {
        if (reportType == null) {
            return null;
        }
        return generatorMap.get(reportType.toUpperCase());
    }
}
