package org.example.caffe.repository;

import org.example.caffe.domain.OrderItem;
import org.example.caffe.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByStatusAndCreatedDateBetween(OrderStatus status, Instant start, Instant end);
}

