package com.example.crmbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Activity entity representing follow-up logs and interactions
 */
@Entity
@Table(name = "activities", indexes = {
    @Index(name = "idx_activity_lead", columnList = "lead_id"),
    @Index(name = "idx_activity_next_followup", columnList = "next_followup_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lead lead; // Nullable if associated with customer directly


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String outcome;

    @Column(name = "next_followup_at")
    private LocalDateTime nextFollowUpAt; // For reminder scheduling

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private boolean sentReminder;
    private boolean subscribedReminder;

    public enum Type {
        CALL, EMAIL, MEETING, NOTE
    }
}

