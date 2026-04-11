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

     @Query(value = "select * from products where is_active is true", nativeQuery = true)
     List<Product> findAllProductsAndIsActiveIsTrue();

     @Query(value = "select * from products where is_active is true", countQuery = "select count(*) from products where is_active is true", nativeQuery = true)
     Page<Product> findAllProductsAndIsActiveIsTrue(Pageable pageable);

     List<Product> findByIdInAndIsActiveIsTrue(Set<Long> productId);

     Optional<Product> findByIdAndIsActiveTrue(Long productId);
}