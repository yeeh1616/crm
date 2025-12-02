package com.example.crmbackend.repository;

import com.example.crmbackend.entity.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Lead entity
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    @Query("""
        SELECT l 
        FROM Lead l 
        JOIN FETCH l.customer 
        JOIN FETCH l.owner 
    """)
    Page<Lead> findAll(Pageable pageable);

    @Query("""
        SELECT l 
        FROM Lead l 
        JOIN FETCH l.owner 
        JOIN FETCH l.customer 
        WHERE l.customer.id = :customerId
    """)
    Page<Lead> findByCustomerId(Long customerId, Pageable pageable);

    @Query("""
        SELECT l 
        FROM Lead l 
        JOIN FETCH l.customer 
        JOIN FETCH l.owner 
        WHERE l.id = :leadId
    """)
    Optional<Lead> findById(@Param("leadId") Long leadId);

    @Query("SELECT l FROM Lead l JOIN FETCH l.owner u")
    Page<Lead> findLeadUserAll(Pageable pageable);
    
    Page<Lead> findByStatus(Lead.Status status, Pageable pageable);
    
    Page<Lead> findByStage(Lead.Stage stage, Pageable pageable);
    
    @Query("SELECT l FROM Lead l WHERE l.owner.id = :ownerId AND l.status = :status")
    Page<Lead> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") Lead.Status status, Pageable pageable);
    
    @Query("SELECT l FROM Lead l WHERE l.source = :source")
    Page<Lead> findBySource(@Param("source") String source, Pageable pageable);
    
    @Query("SELECT l FROM Lead l WHERE l.convertedAt IS NOT NULL")
    List<Lead> findConvertedLeads();
    
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.source = :source")
    long countBySource(@Param("source") String source);
    
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.source = :source AND l.convertedAt IS NOT NULL")
    long countConvertedBySource(@Param("source") String source);
}

