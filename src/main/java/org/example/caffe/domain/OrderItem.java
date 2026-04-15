package org.example.caffe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.caffe.config.AbstractAuditingEntity;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_name_status", columnList = "productName, status")
})
public class OrderItem extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private String productName;
    private Double productPrice;
    private Double productActualMadePrice;
    private Long quantity;
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;
}
