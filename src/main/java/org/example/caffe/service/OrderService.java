package org.example.caffe.service;

import org.example.caffe.domain.Order;
import org.example.caffe.domain.OrderItem;
import org.example.caffe.domain.OrderStatus;
import org.example.caffe.domain.Product;
import org.example.caffe.dto.CreateOrderRequest;
import org.example.caffe.dto.OrderDto;
import org.example.caffe.error.ResourceNotFoundException;
import org.example.caffe.repository.OrderItemRepository;
import org.example.caffe.repository.OrderRepository;
import org.example.caffe.repository.ProductRepository;
import org.example.caffe.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository, ProductRepository productRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public Order createOrder(List<CreateOrderRequest> request) {

        Order order = new Order();
        order.setIsActive(true);
        List<OrderItem> listOrderItem = new ArrayList<>();
        Double totalPrice = 0.0;

        for (CreateOrderRequest req : request) {

            Product product = productRepository
                    .findByIdAndIsActiveTrue(req.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + req.getProductId()));

            OrderItem item = new OrderItem();
            item.setProductName(product.getProductName());
            item.setProductPrice(product.getProductPrice());
            item.setProductActualMadePrice(product.getProductActualMadePrice());
            item.setQuantity(req.getQty().longValue());
            item.setOrder(order);
            item.setTotalPrice(req.getQty() * product.getProductPrice());
            item.setStatus(OrderStatus.PLACED);   // ← industrial default status
            listOrderItem.add(item);
            totalPrice = totalPrice + req.getQty() * product.getProductPrice();
        }
        order.setOrderItems(listOrderItem);

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateItemStatus(Long itemId, OrderStatus newStatus) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + itemId));

        // Prevent re-activating a cancelled item
        if (item.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a cancelled order item.");
        }

        item.setStatus(newStatus);
        orderItemRepository.save(item);
        return item.getOrder();
    }

    @Transactional
    public Order updateOrder(OrderDto orderDto) {
        Order order = orderRepository.findByIdAndIsActiveTrue(orderDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderDto.getId()));

        for (OrderItem dtoItem : orderDto.getOrderItems()) {
            if (dtoItem.getId() != null) {
                order.getOrderItems().stream()
                        .filter(item -> item.getId().equals(dtoItem.getId()))
                        .findFirst()
                        .ifPresent(existingItem -> {
                            existingItem.setQuantity(dtoItem.getQuantity());
                            existingItem.setTotalPrice(dtoItem.getQuantity() * existingItem.getProductPrice());
                        });
            }
        }
        return orderRepository.save(order);
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        OrderDto dto = toOrderDtoWithActiveItems(order);
        if (dto.getOrderItems().isEmpty()) {
            throw new ResourceNotFoundException("Order not found or has no active items");
        }
        return dto;
    }

    @Transactional
    public String softDeleteOrder(Long orderId) {
        Order order = orderRepository.findByIdAndIsActiveTrue(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Cancel all line items when order is soft-deleted
        order.getOrderItems()
                .forEach(item -> item.setStatus(OrderStatus.CANCELLED));
        order.setIsActive(false);

        orderRepository.save(order);

        return "Order cancelled successfully";
    }

    public List<OrderDto> getAllOrders(Integer page, Integer size) {
        List<Order> orders;
        if (page != null || size != null) {
            int currentPage = (page != null) ? page : 0;
            int pageSize = (size != null) ? size : 10;
            Pageable pageable = PageRequest.of(currentPage, pageSize);
            orders = orderRepository.findAllByIsActiveTrue(pageable).getContent();
        } else {
            orders = orderRepository.findAllByIsActiveTrue();
        }

        List<OrderDto> dtos = new ArrayList<>();
        for (Order order : orders) {
            OrderDto dto = toOrderDtoWithActiveItems(order);
            if (!dto.getOrderItems().isEmpty()) {
                dtos.add(dto);
            }
        }
        return dtos;
    }

    /**
     * Builds an OrderDto excluding any CANCELLED order items.
     * totalAmount is recalculated from non-cancelled items only.
     */
    private OrderDto toOrderDtoWithActiveItems(Order order) {
        List<OrderItem> activeItems = order.getOrderItems() == null
                ? new ArrayList<>()
                : order.getOrderItems().stream()
                        .filter(item -> item.getStatus() != OrderStatus.CANCELLED)
                        .toList();

        OrderDto dto = orderMapper.toDto(order);
        dto.setOrderItems(activeItems);
        dto.setTotalAmount(calculateTotalAmount(activeItems));
        return dto;
    }

    private Double calculateTotalAmount(List<OrderItem> activeItems) {
        return activeItems.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }

    public String updateItemQty(Long itemId, Long qty) {
        OrderItem orderItem = orderItemRepository.findById(itemId).orElseThrow(
                () -> new ResourceNotFoundException("Item not found with id: " + itemId));
        if(qty>0){
            orderItem.setQuantity(qty);
            orderItem.setTotalPrice(orderItem.getProductPrice() * qty);
            orderItemRepository.save(orderItem);
            return "Quatity updated";
        }
        return "Quatity should be greater than Zero updated";
    }
}