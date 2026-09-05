package com.pm.budgetservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "group_id")
    private UUID groupId;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false)
    private BudgetScope scope;

    @Column(name = "category", nullable = false)
    @Builder.Default
    private String category = "ALL";

    @Column(name = "amount_limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountLimit;

    @Column(name = "current_spent", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal currentSpent = BigDecimal.ZERO;

    @Column(name = "period_month", nullable = false, length = 7)
    private String periodMonth;

    @Column(name = "alert_80_sent", nullable = false)
    @Builder.Default
    private boolean alert80Sent = false;

    @Column(name = "alert_90_sent", nullable = false)
    @Builder.Default
    private boolean alert90Sent = false;

    @Column(name = "alert_100_sent", nullable = false)
    @Builder.Default
    private boolean alert100Sent = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
