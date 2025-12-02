package com.example.crmbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lead entity representing sales leads
 */
@Entity
@Table(name = "leads", indexes = {
    @Index(name = "idx_lead_customer", columnList = "customer_id"),
    @Index(name = "idx_lead_owner", columnList = "owner_id"),
    @Index(name = "idx_lead_stage", columnList = "stage"),
    @Index(name = "idx_lead_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer; // Nullable until converted

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage stage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(precision = 19, scale = 2)
    private BigDecimal value;

    @Column(name = "expected_close_date")
    private LocalDate expectedCloseDate;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt; // Timestamp when converted to customer

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Stage {
        NEW, CONTACTED, QUALIFIED, PROPOSAL, WON, LOST
    }

    public enum Status {
        ACTIVE, LOST, ARCHIVED
    }

    public boolean isConverted() {
        return convertedAt != null;
    }
}

