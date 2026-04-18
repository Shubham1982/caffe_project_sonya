package org.example.caffe.repository;

import org.example.caffe.domain.Product;
import org.example.caffe.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.example.caffe.dto.ProductSummaryDto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@EnableJpaRepositories
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

     @Query("SELECT p.id AS id, p.productName AS productName FROM Product p WHERE p.isActive = true")
     List<ProductSummaryDto> findAllActiveProductSummaries();

     Optional<Product> findByIdAndIsActiveIsTrue(Long id);

     @Query(value = "SELECT p.* FROM products p " +
             "LEFT JOIN product_sales_qty_stats stats ON p.id = stats.product_id " +
             "WHERE p.is_active IS TRUE " +
             "ORDER BY " +
             "CASE WHEN stats.last_modified_date >= :fromDate " +
             "THEN COALESCE(stats.total_quantity_sold, 0) ELSE 0 END DESC, " +
             "p.created_date DESC, " +
             "p.product_name ASC",
             countQuery = "SELECT count(*) FROM products WHERE is_active IS TRUE",
             nativeQuery = true)
     Page<Product> findAllProductsAndIsActiveIsTrue(
             @Param("fromDate") Timestamp fromDate,
             Pageable pageable
     );

     @Query(value = "SELECT p.* FROM products p " +
             "LEFT JOIN product_sales_qty_stats stats ON p.id = stats.product_id " +
             "WHERE p.is_active IS TRUE " +
             "ORDER BY " +
             "CASE WHEN stats.last_modified_date >= :fromDate " +
             "THEN COALESCE(stats.total_quantity_sold, 0) ELSE 0 END DESC, " +
             "p.created_date DESC, " +
             "p.product_name ASC",
             nativeQuery = true)
     List<Product> findAllProductsAndIsActiveIsTrue(
             @Param("fromDate") Timestamp fromDate
     );

     Optional<Product> findByIdAndIsActiveTrue(Long productId);

     Optional<Product> findByProductName(String productName);

     List<Product> findByProductNameContainingIgnoreCaseAndIsActiveTrue(String productName);
}