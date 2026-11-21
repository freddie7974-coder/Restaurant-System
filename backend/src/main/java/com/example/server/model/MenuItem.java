package com.example.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name cannot be blank")
    private String name;

    private String description;

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    // Whether this item is currently available
    // Kitchen can toggle this if they run out of ingredients
    private boolean available = true;

    // Category e.g. "Burgers", "Drinks", "Desserts"
    private String category;

    // NEW: Field for photo links
    private String imageUrl;

    public MenuItem() {}

    public MenuItem(String name, String description, double price) {
        this.name        = name;
        this.description = description;
        this.price       = price;
    }

    public MenuItem(String name, String description, double price, String category) {
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.category    = category;
    }

    public MenuItem(String name, String description, double price, String category, String imageUrl) {
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.category    = category;
        this.imageUrl    = imageUrl;
    }

    public Long getId()                          { return id; }
    public String getName()                      { return name; }
    public void setName(String n)                { this.name = n; }
    public String getDescription()               { return description; }
    public void setDescription(String d)         { this.description = d; }
    public double getPrice()                     { return price; }
    public void setPrice(double p)               { this.price = p; }
    public boolean isAvailable()                 { return available; }
    public void setAvailable(boolean a)          { this.available = a; }
    public String getCategory()                  { return category; }
    public void setCategory(String c)            { this.category = c; }
    public String getImageUrl()                  { return imageUrl; }
    public void setImageUrl(String url)          { this.imageUrl = url; }
}
