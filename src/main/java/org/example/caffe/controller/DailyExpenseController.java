package org.example.caffe.controller;

import org.example.caffe.domain.DailyExpense;
import org.example.caffe.dto.DailyExpenseDto;
import org.example.caffe.dto.ExpenseDashboardDto;
import org.example.caffe.dto.ProfitChartDto;
import org.example.caffe.service.DailyExpenseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.example.caffe.dto.PaginatedGroupedExpenseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/expense")
public class DailyExpenseController {

    private final DailyExpenseService dailyExpenseService;

    public DailyExpenseController(DailyExpenseService dailyExpenseService) {
        this.dailyExpenseService = dailyExpenseService;
    }

    // -------------------------------------------------------------------------
    // PLACE EXPENSE – equivalent to /api/order/place
    // -------------------------------------------------------------------------
    @PostMapping("/place")
    public String placeExpense(@RequestBody List<DailyExpenseDto> dtos) {
        dailyExpenseService.placeExpense(dtos);
        return "Expense added";
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------
    @PutMapping("/update/{id}")
    public DailyExpense updateExpense(@PathVariable Long id, @RequestBody DailyExpenseDto dto) {
        return dailyExpenseService.updateExpense(id, dto);
    }

    // -------------------------------------------------------------------------
    // GET by ID
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    public DailyExpense getExpenseById(@PathVariable Long id) {
        return dailyExpenseService.getExpenseById(id);
    }

    // -------------------------------------------------------------------------
    // GET ALL (optional filters: date range, inventoryId) with Pagination
    // -------------------------------------------------------------------------
    @GetMapping("/getall")
    public PaginatedGroupedExpenseDto getAllExpenses(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
            @RequestParam(required = false) Long inventoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expenseDate,desc") String[] sort) {

        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

        return dailyExpenseService.getAllExpenses(startDate, endDate, inventoryId, pageable);
    }

    // -------------------------------------------------------------------------
    // DELETE (soft)
    // -------------------------------------------------------------------------
    @DeleteMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        return dailyExpenseService.deleteExpense(id);
    }

    // -------------------------------------------------------------------------
    // DASHBOARD – GET /api/expense/dashboard/getExpenseData?startDate=&endDate=
    // -------------------------------------------------------------------------
    @GetMapping("/dashboard/getExpenseData")
    public ExpenseDashboardDto getExpenseDashboard(
            @RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {
        return dailyExpenseService.getExpenseDashboard(startDate, endDate);
    }

    // -------------------------------------------------------------------------
    // EXPENSE CHART – GET
    // /api/expense/dashboard/expense-chart?reportType=MONTHLY&year=2024
    // -------------------------------------------------------------------------
    @GetMapping("/dashboard/expense-chart")
    public ProfitChartDto getExpenseChart(
            @RequestParam(required = false) Long inventoryId,
            @RequestParam String reportType,
            @RequestParam(required = false) Integer year) {
        return dailyExpenseService.getExpenseChart(inventoryId, reportType, year);
    }
}
