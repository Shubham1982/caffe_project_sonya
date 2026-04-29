package org.example.caffe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseDashboardDto {
    private Double totalExpenseAmount;
    private Long expenseCount;
    private Double averageExpensePerDay;
}
