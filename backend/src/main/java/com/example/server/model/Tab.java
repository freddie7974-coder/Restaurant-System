package com.example.server.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents an open tab for a table.
 * A tab groups all orders placed at a table during one sitting.
 * When the customer is done, they close the tab and pay.
 */
@Entity
@Table(name = "tabs")
public class Tab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tableNumber;

    @Enumerated(EnumType.STRING)
    private TabStatus status = TabStatus.OPEN;

    private double totalAmount = 0.0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    // Payment details
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    // For M-Pesa simulation
    private String mpesaPhone;
    private String mpesaTransactionId;

    public enum TabStatus {
        OPEN,       // customers are still ordering
        CLOSED,     // customers requested bill
        PAID        // payment confirmed
    }

    public enum PaymentMethod {
        MPESA,
        CARD,
        CASH
    }

    public enum PaymentStatus {
        UNPAID,
        PENDING,    // payment initiated, waiting confirmation
        PAID,
        FAILED
    }

    public Tab() {}

    public Tab(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    // Getters and Setters
    public Long getId()                                  { return id; }
    public int getTableNumber()                          { return tableNumber; }
    public void setTableNumber(int t)                    { this.tableNumber = t; }
    public TabStatus getStatus()                         { return status; }
    public void setStatus(TabStatus s)                   { this.status = s; }
    public double getTotalAmount()                       { return totalAmount; }
    public void setTotalAmount(double a)                 { this.totalAmount = a; }
    public LocalDateTime getOpenedAt()                   { return openedAt; }
    public LocalDateTime getClosedAt()                   { return closedAt; }
    public void setClosedAt(LocalDateTime t)             { this.closedAt = t; }
    public PaymentMethod getPaymentMethod()              { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod m)        { this.paymentMethod = m; }
    public PaymentStatus getPaymentStatus()              { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus s)        { this.paymentStatus = s; }
    public String getMpesaPhone()                        { return mpesaPhone; }
    public void setMpesaPhone(String p)                  { this.mpesaPhone = p; }
    public String getMpesaTransactionId()                { return mpesaTransactionId; }
    public void setMpesaTransactionId(String t)          { this.mpesaTransactionId = t; }
}
