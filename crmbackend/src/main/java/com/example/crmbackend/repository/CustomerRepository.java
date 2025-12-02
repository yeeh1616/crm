package com.example.crmbackend.repository;

import com.example.crmbackend.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository for Customer entity
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("""
       SELECT DISTINCT c
       FROM Customer c
       JOIN FETCH c.owner o
       JOIN FETCH c.segment s
       LEFT JOIN FETCH c.tags t
       WHERE c.deletedAt IS NULL
        AND c.id = :customerId
       ORDER BY c.id DESC
       """)
    Optional<Customer> findById(@Param("customerId") Long customerId);

    // Find non-deleted customers
    @Query("SELECT c FROM Customer c LEFT JOIN c.segment s WHERE c.deletedAt IS NULL")
    Page<Customer> findAllActive(Pageable pageable);
    
    // Find by owner (non-deleted)
    @Query("SELECT c FROM Customer c WHERE c.owner.id = :ownerId AND c.deletedAt IS NULL")
    Page<Customer> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);
    
    // Find by email (non-deleted)
    @Query("SELECT c FROM Customer c WHERE c.email = :email AND c.deletedAt IS NULL")
    Optional<Customer> findByEmail(@Param("email") String email);
    
    // Find by source (non-deleted)
    @Query("SELECT c FROM Customer c WHERE c.source = :source AND c.deletedAt IS NULL")
    Page<Customer> findBySource(@Param("source") String source, Pageable pageable);
    
    // Find by tags (non-deleted)
    @Query("SELECT DISTINCT c FROM Customer c JOIN c.tags t WHERE t.id IN :tagIds AND c.deletedAt IS NULL")
    Page<Customer> findByTagIds(@Param("tagIds") List<Long> tagIds, Pageable pageable);
    
    // Find by segment (non-deleted)
    @Query("SELECT DISTINCT c FROM Customer c JOIN c.segment s WHERE s.id = :segmentId AND c.deletedAt IS NULL")
    Page<Customer> findBySegmentId(@Param("segmentId") Long segmentId, Pageable pageable);
    
    // Find by segment and tags (non-deleted)
    @Query("""
       SELECT DISTINCT c
       FROM Customer c
       JOIN c.segment s
       JOIN c.tags t
       WHERE c.deletedAt IS NULL
        AND (:segmentId IS NULL OR s.id = :segmentId)
        AND (:tagIds IS NULL OR t.id IN :tagIds)
       ORDER BY c.id DESC
       """)
    Page<Customer> findBySegmentIdAndTagIds(@Param("segmentId") Long segmentId, @Param("tagIds") List<Long> tagIds, Pageable pageable);
    
    // Count active customers by owner
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.owner.id = :ownerId AND c.deletedAt IS NULL")
    long countByOwnerId(@Param("ownerId") Long ownerId);
    
    // Find all active customers (for admin)
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL")
    List<Customer> findAllActive();

    @Query("""
        SELECT COUNT(c)
        FROM Customer c
        LEFT JOIN c.owner o
        WHERE c.deletedAt IS NULL
          AND (:ownerId IS NULL OR c.owner.id = :ownerId)
          AND (:segmentId IS NULL OR c.segment.id = :segmentId)
          AND (
            :tagIds IS NULL OR EXISTS (
                SELECT 1 FROM c.tags t WHERE t.id IN :tagIds
            )
          )
    """)
    Long countCustomers(Long ownerId, Long segmentId, List<Long> tagIds);
}
