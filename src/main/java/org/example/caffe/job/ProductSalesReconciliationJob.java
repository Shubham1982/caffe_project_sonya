package org.example.caffe.job;

import lombok.extern.slf4j.Slf4j;
import org.example.caffe.domain.ProductSalesStats;
import org.example.caffe.repository.OrderItemRepository;
import org.example.caffe.repository.ProductOrderQuantity;
import org.example.caffe.repository.ProductSalesQtyStatsRepository;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@DisallowConcurrentExecution
public class ProductSalesReconciliationJob extends QuartzJobBean {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductSalesQtyStatsRepository statsRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting Product Sales Reconciliation Job...");

        //get all ordered item qty product vise
        List<ProductOrderQuantity> productQuantities = orderItemRepository.findProductOrderQuantities();

        if (productQuantities.isEmpty()){
            throw new JobExecutionException("order Item not found hence skipping this job");
        }

        for (ProductOrderQuantity result : productQuantities) {

            Long productId = result.getProductId();
            Long calculatedTotal = result.getTotalQuantity();

            ProductSalesStats stats = statsRepository.findByProductId(productId)
                    .orElseGet(() -> ProductSalesStats.builder().productId(productId).totalQuantitySold(0L).build());

            if (!stats.getTotalQuantitySold().equals(calculatedTotal)) {
                log.warn("Discrepancy found for productId {}: Stats={}, Calculated={}. Correcting.",
                        productId, stats.getTotalQuantitySold(), calculatedTotal);
                stats.setTotalQuantitySold(calculatedTotal);
                stats.setCreatedBy("ProductSalesReconciliationJob");
                stats.setLastModifiedBy("ProductSalesReconciliationJob");
                statsRepository.save(stats);
            }
        }
        log.info("Product Sales Reconciliation Job completed");
    }
}
