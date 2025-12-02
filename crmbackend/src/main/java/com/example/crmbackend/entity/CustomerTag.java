package com.example.crmbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CustomerTag entity for many-to-many relationship between segments and customers
 */
@Entity
@Table(name = "customer_tag", indexes = {
    @Index(name = "idx_customer_tag_customer", columnList = "customer_id"),
    @Index(name = "idx_customer_tag_tag", columnList = "tag_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}