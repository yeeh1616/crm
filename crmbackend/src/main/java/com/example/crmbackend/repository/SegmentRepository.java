package com.example.crmbackend.repository;

import com.example.crmbackend.entity.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Segment entity
 */
@Repository
public interface SegmentRepository extends JpaRepository<Segment, Long> {}

