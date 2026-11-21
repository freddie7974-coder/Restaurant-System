package com.example.server.repository;

import com.example.server.model.Order;
import com.example.server.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusIn(List<OrderStatus> statuses);
    List<Order> findByTableNumber(int tableNumber);
    List<Order> findByTabId(Long tabId);
    List<Order> findByWaiterSection(String section);
    List<Order> findByStatusInAndWaiterSection(List<OrderStatus> statuses, String section);
}
