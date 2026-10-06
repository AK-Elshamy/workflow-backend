package com.elshamy.workflow.repository;

import com.elshamy.workflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}