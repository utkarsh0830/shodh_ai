package org.example.contestrunner.repo;

import org.example.contestrunner.model.Contest;
import org.example.contestrunner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ContestRepo extends JpaRepository<Contest, Long> {}