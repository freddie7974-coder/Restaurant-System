package com.example.server.repository;

import com.example.server.model.Tab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TabRepository extends JpaRepository<Tab, Long> {
    Optional<Tab> findByTableNumberAndStatus(int tableNumber, Tab.TabStatus status);
    List<Tab> findByStatus(Tab.TabStatus status);
}
