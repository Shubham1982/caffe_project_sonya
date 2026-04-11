package org.example.caffe.controller;

import org.example.caffe.domain.Order;
import org.example.caffe.domain.OrderStatus;
import org.example.caffe.dto.CreateOrderRequest;
import org.example.caffe.dto.OrderDto;
import org.example.caffe.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/createOrder")
    public Order createOrder(@RequestBody List<CreateOrderRequest> request) {
        return orderService.createOrder(request);
    }

    @PutMapping("/update")
    public Order updateOrder(@RequestBody OrderDto orderDto) {
        return orderService.updateOrder(orderDto);
    }

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable("id") Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/getAllOrders")
    public List<OrderDto> getAllOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return orderService.getAllOrders(page, size);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable("id") Long id) {
        return orderService.softDeleteOrder(id);
    }

    /**
     * Update the status of a specific order item.
     * Example: PUT /api/orders/items/3/status?status=CONFIRMED
     * Valid values: PLACED, CONFIRMED, PREPARING, READY, DELIVERED, CANCELLED
     */
    @PutMapping("/items/{itemId}/status")
    public Order updateItemStatus(
            @PathVariable Long itemId,
            @RequestParam OrderStatus status) {
        return orderService.updateItemStatus(itemId, status);
    }

    @PutMapping("/items/{itemId}")
    public String updateItemQty(
            @PathVariable Long itemId,
            @RequestParam Long qty) {
        return orderService.updateItemQty(itemId, qty);
    }
}
