package org.example.caffe.repository;

import org.example.caffe.domain.DailyExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyExpenseRepository extends JpaRepository<DailyExpense, Long> {

    Optional<DailyExpense> findByIdAndIsActiveTrue(Long id);

    List<DailyExpense> findAllByIsActiveTrueOrderByExpenseDateDesc();

    List<DailyExpense> findAllByExpenseDateAndIsActiveTrue(LocalDate date);

    List<DailyExpense> findAllByInventoryIdAndIsActiveTrue(Long inventoryId);

    List<DailyExpense> findAllByInventoryIdAndExpenseDateAndIsActiveTrue(Long inventoryId, LocalDate date);

    // For dashboard: sum totalAmount and count within a date range (using
    // createdDate from audit)
    @Query("SELECT e FROM DailyExpense e WHERE e.isActive = true AND e.expenseDate BETWEEN :startDate AND :endDate")
    List<DailyExpense> findAllByExpenseDateBetweenAndIsActiveTrue(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // For expense chart: sum by month
    @Query("SELECT e FROM DailyExpense e WHERE e.isActive = true AND FUNCTION('YEAR', e.expenseDate) = :year")
    List<DailyExpense> findAllByYearAndIsActiveTrue(@Param("year") int year);

    // For expense chart: sum by month filtered by inventoryId
    @Query("SELECT e FROM DailyExpense e WHERE e.isActive = true AND e.inventoryId = :inventoryId AND FUNCTION('YEAR', e.expenseDate) = :year")
    List<DailyExpense> findAllByInventoryIdAndYearAndIsActiveTrue(@Param("inventoryId") Long inventoryId,
            @Param("year") int year);
}
