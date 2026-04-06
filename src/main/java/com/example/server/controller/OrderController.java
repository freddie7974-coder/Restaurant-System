package com.example.server.controller;

import com.example.server.model.Order;
import com.example.server.model.OrderStatus;
import com.example.server.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Tablet places a new order */
    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody Order newOrder) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(newOrder));
    }

    /** Full history */
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /** Active orders — kitchen queue on startup */
    @GetMapping("/active")
    public List<Order> getActiveOrders() {
        return orderService.getActiveOrders();
    }

    /** All orders for a specific table */
    @GetMapping("/table/{tableNumber}")
    public List<Order> getOrdersByTable(@PathVariable int tableNumber) {
        return orderService.getOrdersByTable(tableNumber);
    }

    /** Active orders for a specific waiter section */
    @GetMapping("/section/{section}")
    public List<Order> getOrdersBySection(@PathVariable String section) {
        return orderService.getActiveOrdersBySection(section);
    }

    /** Single order by id */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Kitchen updates order status */
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {

        Order updated = orderService.updateOrderStatus(id, request.getStatus());
        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    public static class StatusUpdateRequest {
        private OrderStatus status;
        public OrderStatus getStatus()       { return status; }
        public void setStatus(OrderStatus s) { this.status = s; }
    }
}
