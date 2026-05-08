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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface DailyExpenseRepository extends JpaRepository<DailyExpense, Long> {

    Optional<DailyExpense> findByIdAndIsActiveTrue(Long id);

    List<DailyExpense> findAllByIsActiveTrueOrderByExpenseDateDesc();

    List<DailyExpense> findAllByExpenseDateAndIsActiveTrue(LocalDate date);

    List<DailyExpense> findAllByInventoryIdAndIsActiveTrue(Long inventoryId);

    List<DailyExpense> findAllByInventoryIdAndExpenseDateAndIsActiveTrue(Long inventoryId, LocalDate date);

    // Paginated methods for getall API
    Page<DailyExpense> findAllByIsActiveTrueOrderByExpenseDateDesc(Pageable pageable);

    @Query("SELECT e FROM DailyExpense e WHERE e.isActive = true AND e.expenseDate >= :startDate AND e.expenseDate <= :endDate")
    Page<DailyExpense> findAllByExpenseDateBetweenAndIsActiveTruePageable(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    Page<DailyExpense> findAllByInventoryIdAndIsActiveTrue(Long inventoryId, Pageable pageable);

    @Query("SELECT e FROM DailyExpense e WHERE e.isActive = true AND e.inventoryId = :inventoryId AND e.expenseDate >= :startDate AND e.expenseDate <= :endDate")
    Page<DailyExpense> findAllByInventoryIdAndExpenseDateBetweenAndIsActiveTruePageable(@Param("inventoryId") Long inventoryId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);


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
