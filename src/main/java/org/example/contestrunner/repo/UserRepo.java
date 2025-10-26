package org.example.contestrunner.repo;

import org.example.contestrunner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByHandle(String handle);
}