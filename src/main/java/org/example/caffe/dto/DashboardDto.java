package org.example.caffe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDto {
    private Double totalAmount;
    private Double actualTotalAmount;
    private Double profit;
    private Long orderItemCount;
}
