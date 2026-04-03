package org.example.caffe.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.caffe.config.AbstractAuditingEntity;
import org.example.caffe.domain.OrderItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDto extends AbstractAuditingEntity implements Serializable {

    private Long id;

    private Double totalAmount;
    private List<OrderItem> orderItems = new ArrayList<>();

}
