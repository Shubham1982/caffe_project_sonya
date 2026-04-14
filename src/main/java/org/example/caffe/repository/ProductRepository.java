package org.example.caffe.repository;

import org.example.caffe.domain.Product;
import org.example.caffe.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@EnableJpaRepositories
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

     Optional<Product> findByIdAndIsActiveIsTrue(Long id);

     @Query(value = "SELECT p.* FROM products p " +
             "LEFT JOIN (SELECT product_name, SUM(quantity) as total_qty FROM order_items WHERE status != 'CANCELLED' GROUP BY product_name) oi " +
             "ON p.product_name = oi.product_name " +
             "WHERE p.is_active IS TRUE " +
             "ORDER BY COALESCE(oi.total_qty, 0) DESC, p.product_name ASC", nativeQuery = true)
     List<Product> findAllProductsAndIsActiveIsTrue();
 
     @Query(value = "SELECT p.* FROM products p " +
             "LEFT JOIN (SELECT product_name, SUM(quantity) as total_qty FROM order_items WHERE status != 'CANCELLED' GROUP BY product_name) oi " +
             "ON p.product_name = oi.product_name " +
             "WHERE p.is_active IS TRUE " +
             "ORDER BY COALESCE(oi.total_qty, 0) DESC, p.product_name ASC",
             countQuery = "SELECT count(*) FROM products WHERE is_active IS TRUE",
             nativeQuery = true)
     Page<Product> findAllProductsAndIsActiveIsTrue(Pageable pageable);

     Optional<Product> findByIdAndIsActiveTrue(Long productId);
}