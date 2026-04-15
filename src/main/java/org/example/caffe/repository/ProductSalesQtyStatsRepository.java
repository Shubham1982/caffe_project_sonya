package org.example.caffe.repository;

import org.example.caffe.domain.ProductSalesStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductSalesQtyStatsRepository extends JpaRepository<ProductSalesStats, Long> {
    Optional<ProductSalesStats> findByProductId(Long productId);
}
