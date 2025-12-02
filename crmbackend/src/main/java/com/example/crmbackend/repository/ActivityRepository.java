package com.example.crmbackend.repository;

import com.example.crmbackend.entity.Activity;
import com.example.crmbackend.entity.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Repository for Activity entity
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Page<Activity> findByLeadId(Long leadId, Pageable pageable);
    
    @Query("SELECT a FROM Activity a WHERE a.nextFollowUpAt IS NOT NULL AND a.nextFollowUpAt <= :dateTime")
    List<Activity> findUpcomingFollowUps(@Param("dateTime") LocalDateTime dateTime);

    @Query("""
        SELECT new map(l.customer.id as customerId,
                       SUM(
                         CASE a.type
                           WHEN 'CALL' THEN 5
                           WHEN 'EMAIL' THEN 3
                           WHEN 'MEETING' THEN 10
                           WHEN 'NOTE' THEN 1
                           ELSE 0
                         END
                       ) as score)
        FROM Activity a
        JOIN a.lead l
        WHERE l.customer.id IN :customerIds
        GROUP BY l.customer.id
    """)
    List<Map<String, Object>> findActivityScoresByCustomerIds(@Param("customerIds") List<Long> customerIds);

    @Query("""
        SELECT c.email
        FROM Activity a
        JOIN a.lead l
        JOIN l.customer c
        WHERE a.id = :activityId
    """)
    String findActivityEmailById(@Param("activityId") Long activityId);
}