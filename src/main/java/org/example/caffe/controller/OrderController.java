package org.example.caffe.controller;

import org.example.caffe.domain.Order;
import org.example.caffe.domain.OrderItem;
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

    @PutMapping("/items/{id}/quantity")
    public OrderItem updateQuantity(
            @PathVariable("id") Long id,
            @RequestParam("quantity") Long quantity
    ) {
        return orderService.updateQuantity(id, quantity);
    }

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable("id") Long id) {
        return orderService.getOrderById(id);
    }
    @GetMapping("/getAllOrders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable("id") Long id) {
        return orderService.softDeleteOrder(id);
    }
}
