package com.example.crmbackend.repository;

import com.example.crmbackend.entity.CustomerTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for CustomerTag entity
 */
@Repository
public interface CustomerTagRepository extends JpaRepository<CustomerTag, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM CustomerTag ct WHERE ct.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") Long customerId);
}