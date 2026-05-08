package org.example.caffe.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseDetailDto {
    private Double price;
    private Double quantity;
    private LocalDate expenseDate;
    private Double totalAmount;
    private String notes;
}
