package com.example.crmbackend.repository;

import com.example.crmbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Tag entity
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {}