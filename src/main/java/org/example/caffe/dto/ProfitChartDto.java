package org.example.caffe.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProfitChartDto {
    private List<String> labels;
    private List<Double> data;
    private Double maxProfit;
    private Double minProfit;
}
