package com.elshamy.workflow.repository;


import com.elshamy.workflow.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
    SELECT DISTINCT p
    FROM Project p
    LEFT JOIN p.members m
    WHERE p.owner.id = :userId
       OR m.id = :userId
    """)
    List<Project> findVisibleToUser(@Param("userId") Long userId);

}