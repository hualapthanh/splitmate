package com.pm.expenseservice.repository;

import com.pm.expenseservice.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN e.payers p LEFT JOIN e.splits s " +
           "WHERE (e.userId = :userId OR p.userId = :userId OR s.userId = :userId) " +
           "AND e.status = 'CONFIRMED' ORDER BY e.date DESC, e.createdAt DESC")
    List<Expense> findAllAvailableForUser(@Param("userId") UUID userId);

    @Query("SELECT e FROM Expense e WHERE e.groupId = :groupId AND e.status = 'CONFIRMED' ORDER BY e.date DESC, e.createdAt DESC")
    List<Expense> findAllByGroupId(@Param("groupId") UUID groupId);
}
