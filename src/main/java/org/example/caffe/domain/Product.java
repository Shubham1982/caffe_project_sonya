package org.example.caffe.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.caffe.config.AbstractAuditingEntity;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "products")
public class Product extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private Double productPrice;
    private Double productActualMadePrice;
    private String productImage;
    private Boolean isActive;
}
