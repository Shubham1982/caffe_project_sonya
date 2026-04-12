package org.example.caffe.repository;

import org.example.caffe.domain.Order;
import org.springframework.data.domain.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndIsActiveTrue(Long id);
    List<Order> findAllByIsActiveTrueOrderByIdDesc();
    Page<Order> findAllByIsActiveTrueOrderByIdDesc(Pageable pageable);
}
