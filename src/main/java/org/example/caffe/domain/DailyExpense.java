package org.example.caffe.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.caffe.config.AbstractAuditingEntity;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "daily_expenses", indexes = {
        @Index(name = "idx_expense_date", columnList = "expenseDate"),
        @Index(name = "idx_expense_inventory", columnList = "inventoryId"),
        @Index(name = "idx_expense_active", columnList = "isActive")
})
public class DailyExpense extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate expenseDate;

    private Long inventoryId;

    // Snapshot fields – captured at time of placing expense
    private String materialName;
    private Double price;

    private Double quantity;
    private Double totalAmount;

    private String notes;

    private Boolean isActive;
}
