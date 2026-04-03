package org.example.caffe.service;

import org.apache.http.protocol.HTTP;
import org.example.caffe.domain.Order;
import org.example.caffe.domain.OrderItem;
import org.example.caffe.domain.Product;
import org.example.caffe.dto.CreateOrderRequest;
import org.example.caffe.dto.OrderDto;
import org.example.caffe.error.ResourceNotFoundException;
import org.example.caffe.repository.OrderItemRepository;
import org.example.caffe.repository.OrderRepository;
import org.example.caffe.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(List<CreateOrderRequest> request) {

        Order order = new Order();
        List<OrderItem> listOrderItem = new ArrayList<>();
        Double totalPrice = 0.0;

        for (CreateOrderRequest req : request) {

            Product product = productRepository
                    .findByIdAndIsActiveTrue(req.getProductId()).get();
            if(Objects.isNull(product))
                throw new ResourceNotFoundException("Product not found");

            OrderItem item = new OrderItem();
            item.setProductName(product.getProductName());
            item.setProductPrice(product.getProductPrice());
            item.setProductActualMadePrice(product.getProductActualMadePrice());
            item.setQuantity(req.getQty().longValue());
            item.setIsActive(true);
            item.setOrder(order);
            item.setTotalPrice(req.getQty()* product.getProductPrice());
            listOrderItem.add(item);
            totalPrice = totalPrice + req.getQty()* product.getProductPrice();
        }
        order.setOrderItems(listOrderItem);

        return orderRepository.save(order);
    }
    @Transactional
    public OrderItem updateQuantity(Long orderItemId, Long quantity) {

        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order item not found"));

        // update item quantity
        item.setQuantity(quantity);
        item.setTotalPrice(item.getProductPrice() * quantity);
        return orderItemRepository.save(item);
    }

    public OrderDto getOrderById(Long id) {
        OrderDto orderDto = new OrderDto();
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + id));

        List<OrderItem> activeItems = order.getOrderItems()
                .stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsActive()))
                .toList();

        orderDto.setId(order.getId());
        orderDto.setOrderItems(activeItems);
        Double totalAmount = activeItems.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
        orderDto.setTotalAmount(totalAmount);

        return orderDto;
    }

    @Transactional
    public String softDeleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.getOrderItems()
                .forEach(item -> item.setIsActive(false));

        orderRepository.save(order);

        return "Order deleted successfully";
    }
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .peek(order -> order.setOrderItems(
                        order.getOrderItems()
                                .stream()
                                .filter(item -> Boolean.TRUE.equals(item.getIsActive()))
                                .toList()
                ))
                .toList();
    }
}