package org.example.caffe.controller;

import org.example.caffe.dto.DashboardDto;
import org.example.caffe.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final OrderService orderService;

    public DashboardController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/getOrderData")
    public DashboardDto getDashboardData(
            @RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {
        return orderService.getDashboardData(startDate, endDate);
    }
}
