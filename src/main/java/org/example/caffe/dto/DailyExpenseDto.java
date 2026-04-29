package org.example.caffe.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyExpenseDto {
    private Long inventoryId;
    private Double quantity;
    private String notes;
    private LocalDate expenseDate; // optional; defaults to today if null
}
