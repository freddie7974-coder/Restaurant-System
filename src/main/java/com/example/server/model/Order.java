package com.example.server.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Table number must be at least 1")
    private int tableNumber;

    @ElementCollection
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime placedAt;

    // Links this order to an open tab
    // Null if no tab has been opened for this table yet
    private Long tabId;

    // Which waiter section this table belongs to
    private String waiterSection;

    public Order() {}

    public Order(int tableNumber, List<OrderItem> items) {
        this.tableNumber = tableNumber;
        this.items       = items;
        this.status      = OrderStatus.PENDING;
    }

    // Getters and Setters
    public Long getId()                          { return id; }
    public int getTableNumber()                  { return tableNumber; }
    public void setTableNumber(int n)            { this.tableNumber = n; }
    public List<OrderItem> getItems()            { return items; }
    public void setItems(List<OrderItem> i)      { this.items = i; }
    public OrderStatus getStatus()               { return status; }
    public void setStatus(OrderStatus s)         { this.status = s; }
    public LocalDateTime getPlacedAt()           { return placedAt; }
    public Long getTabId()                       { return tabId; }
    public void setTabId(Long t)                 { this.tabId = t; }
    public String getWaiterSection()             { return waiterSection; }
    public void setWaiterSection(String s)       { this.waiterSection = s; }
}
