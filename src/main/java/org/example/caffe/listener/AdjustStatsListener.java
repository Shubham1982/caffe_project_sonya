package org.example.caffe.listener;

import org.example.caffe.domain.ProductSalesStats;
import org.example.caffe.event.AdjustStatsEvent;
import org.example.caffe.repository.ProductSalesQtyStatsRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AdjustStatsListener {

    private final ProductSalesQtyStatsRepository statsRepository;

    public AdjustStatsListener(ProductSalesQtyStatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Async
    @EventListener
    public void handleAdjustStatsEvent(AdjustStatsEvent event) {
        ProductSalesStats stats = statsRepository.findByProductId(event.getProductId())
                .orElseGet(() -> ProductSalesStats.builder().productId(event.getProductId()).totalQuantitySold(0L).build());
        stats.setTotalQuantitySold(stats.getTotalQuantitySold() + event.getQuantityDelta());
        statsRepository.save(stats);
    }
}
