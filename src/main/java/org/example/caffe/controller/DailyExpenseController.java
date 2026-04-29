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
    public DailyExpense placeExpense(@RequestBody DailyExpenseDto dto) {
        return dailyExpenseService.placeExpense(dto);
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
    // GET ALL (optional filters: date, inventoryId)
    // -------------------------------------------------------------------------
    @GetMapping("/getall")
    public List<DailyExpense> getAllExpenses(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
            @RequestParam(required = false) Long inventoryId) {
        return dailyExpenseService.getAllExpenses(date, inventoryId);
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
