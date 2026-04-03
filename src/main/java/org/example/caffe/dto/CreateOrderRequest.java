package org.example.caffe.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CreateOrderRequest {
    private Long productId;
    private Double qty;
}
