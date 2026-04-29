package org.example.caffe.service;

import org.example.caffe.domain.DailyExpense;
import org.example.caffe.domain.Inventory;
import org.example.caffe.dto.DailyExpenseDto;
import org.example.caffe.dto.ExpenseDashboardDto;
import org.example.caffe.dto.ProfitChartDto;
import org.example.caffe.error.ResourceNotFoundException;
import org.example.caffe.repository.DailyExpenseRepository;
import org.example.caffe.repository.InventoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.example.caffe.service.factory.ExpenseChartFactory;
import org.example.caffe.service.factory.ExpenseChartGenerator;

import java.util.List;

@Service
public class DailyExpenseService {

    private final DailyExpenseRepository dailyExpenseRepository;
    private final InventoryRepository inventoryRepository;
    private final ExpenseChartFactory expenseChartFactory;

    public DailyExpenseService(DailyExpenseRepository dailyExpenseRepository,
            InventoryRepository inventoryRepository,
            ExpenseChartFactory expenseChartFactory) {
        this.dailyExpenseRepository = dailyExpenseRepository;
        this.inventoryRepository = inventoryRepository;
        this.expenseChartFactory = expenseChartFactory;
    }

    // -------------------------------------------------------------------------
    // PLACE EXPENSE (mirrors createOrder)
    // -------------------------------------------------------------------------
    @Transactional
    @CacheEvict(value = { "expense", "expenseList", "expenseDashboard", "expenseCharts" }, allEntries = true)
    public DailyExpense placeExpense(DailyExpenseDto dto) {
        Inventory inventory = inventoryRepository.findByIdAndIsActiveTrue(dto.getInventoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory item not found with id: " + dto.getInventoryId()));

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        DailyExpense expense = new DailyExpense();
        expense.setInventoryId(inventory.getId());
        expense.setMaterialName(inventory.getMaterialName()); // snapshot
        expense.setPrice(inventory.getPrice()); // snapshot
        expense.setIsActive(true);

        if (dto.getExpenseDate() == null)
            dto.setExpenseDate(LocalDate.now());
        applyDtoFields(dto, expense);

        return dailyExpenseRepository.save(expense);
    }

    // -------------------------------------------------------------------------
    // READ – single
    // -------------------------------------------------------------------------
    @Cacheable(value = "expense", key = "#id")
    public DailyExpense getExpenseById(Long id) {
        return dailyExpenseRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
    }

    // -------------------------------------------------------------------------
    // READ – list with optional filters
    // -------------------------------------------------------------------------
    @Cacheable(value = "expenseList", key = "{#date, #inventoryId}")
    public List<DailyExpense> getAllExpenses(LocalDate date, Long inventoryId) {
        if (date != null && inventoryId != null) {
            return dailyExpenseRepository.findAllByInventoryIdAndExpenseDateAndIsActiveTrue(inventoryId, date);
        } else if (date != null) {
            return dailyExpenseRepository.findAllByExpenseDateAndIsActiveTrue(date);
        } else if (inventoryId != null) {
            return dailyExpenseRepository.findAllByInventoryIdAndIsActiveTrue(inventoryId);
        }
        return dailyExpenseRepository.findAllByIsActiveTrueOrderByExpenseDateDesc();
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------
    @Transactional
    @CacheEvict(value = { "expense", "expenseList", "expenseDashboard", "expenseCharts" }, allEntries = true)
    public DailyExpense updateExpense(Long id, DailyExpenseDto dto) {
        DailyExpense expense = dailyExpenseRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        applyDtoFields(dto, expense);

        return dailyExpenseRepository.save(expense);
    }

    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // PRIVATE HELPER – apply DTO fields onto an expense (used by place & update)
    // -------------------------------------------------------------------------
    private void applyDtoFields(DailyExpenseDto dto, DailyExpense expense) {
        if (dto.getQuantity() != null) {
            if (dto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
            expense.setQuantity(dto.getQuantity());
            expense.setTotalAmount(expense.getPrice() * dto.getQuantity());
        }
        if (dto.getNotes() != null) {
            expense.setNotes(dto.getNotes());
        }
        if (dto.getExpenseDate() != null) {
            expense.setExpenseDate(dto.getExpenseDate());
        }
    }

    // -------------------------------------------------------------------------
    // DELETE – soft delete
    // -------------------------------------------------------------------------
    @CacheEvict(value = { "expense", "expenseList", "expenseDashboard", "expenseCharts" }, allEntries = true)
    public String deleteExpense(Long id) {
        DailyExpense expense = dailyExpenseRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        expense.setIsActive(false);
        dailyExpenseRepository.save(expense);
        return "Expense deleted successfully";
    }

    // -------------------------------------------------------------------------
    // DASHBOARD (mirrors OrderService.getDashboardData)
    // -------------------------------------------------------------------------
    @Cacheable(value = "expenseDashboard", key = "{#startDate, #endDate}")
    public ExpenseDashboardDto getExpenseDashboard(LocalDate startDate, LocalDate endDate) {
        List<DailyExpense> expenses = dailyExpenseRepository.findAllByExpenseDateBetweenAndIsActiveTrue(startDate,
                endDate);

        double total = expenses.stream()
                .mapToDouble(e -> e.getTotalAmount() != null ? e.getTotalAmount() : 0.0)
                .sum();

        long count = expenses.size();

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double avgPerDay = days > 0 ? total / days : 0.0;

        return ExpenseDashboardDto.builder()
                .totalExpenseAmount(total)
                .expenseCount(count)
                .averageExpensePerDay(avgPerDay)
                .build();
    }

    // -------------------------------------------------------------------------
    // EXPENSE CHART (mirrors OrderService.getProfitChartData – MONTHLY + YEARLY)
    // -------------------------------------------------------------------------
    @Cacheable(value = "expenseCharts", key = "{#inventoryId, #reportType, #year}")
    public ProfitChartDto getExpenseChart(Long inventoryId, String reportType, Integer year) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        List<DailyExpense> expenses = (inventoryId != null)
                ? dailyExpenseRepository.findAllByInventoryIdAndYearAndIsActiveTrue(inventoryId, targetYear)
                : dailyExpenseRepository.findAllByYearAndIsActiveTrue(targetYear);

        ExpenseChartGenerator generator = expenseChartFactory.getGenerator(reportType);
        if (generator == null) {
            generator = expenseChartFactory.getGenerator("MONTHLY"); // Default
        }
        return generator.generateChart(expenses, targetYear);
    }
}
