package org.example.caffe.repository;

import org.example.caffe.domain.OrderItem;
import org.example.caffe.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByStatusAndCreatedDateBetween(OrderStatus status, Instant start, Instant end);

    @Query("SELECT oi.productId AS productId, SUM(oi.quantity) AS totalQuantity FROM OrderItem oi WHERE oi.status != 'CANCELLED' GROUP BY oi.productId")
    List<ProductOrderQuantity> findProductOrderQuantities();
}

