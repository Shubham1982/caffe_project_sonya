package org.example.caffe.service.factory;

import org.example.caffe.domain.DailyExpense;
import org.example.caffe.dto.ProfitChartDto;

import java.util.List;

public interface ExpenseChartGenerator {
    String getType();
    ProfitChartDto generateChart(List<DailyExpense> expenses, int targetYear);
}
