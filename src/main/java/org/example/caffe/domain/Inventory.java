package org.example.caffe.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.caffe.config.AbstractAuditingEntity;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "inventory", indexes = {
        @Index(name = "idx_inventory_material_name", columnList = "materialName"),
        @Index(name = "idx_inventory_active", columnList = "isActive")
})
public class Inventory extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String materialName;

    private Double price;

    private Boolean isActive;
}
