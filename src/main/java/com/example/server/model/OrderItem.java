package com.example.server.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class OrderItem {

    @NotBlank(message = "Item name cannot be blank")
    private String name;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private String customization;

    // Price snapshot at time of ordering
    private double price;

    public OrderItem() {}

    public OrderItem(String name, int quantity, String customization, double price) {
        this.name          = name;
        this.quantity      = quantity;
        this.customization = customization;
        this.price         = price;
    }

    public String getName()                      { return name; }
    public void setName(String n)                { this.name = n; }
    public int getQuantity()                     { return quantity; }
    public void setQuantity(int q)               { this.quantity = q; }
    public String getCustomization()             { return customization; }
    public void setCustomization(String c)       { this.customization = c; }
    public double getPrice()                     { return price; }
    public void setPrice(double p)               { this.price = p; }
}
