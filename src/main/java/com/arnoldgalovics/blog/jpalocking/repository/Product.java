package com.arnoldgalovics.blog.jpalocking.repository;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Product {
    @Id
    private UUID id;

    private String name;

    private int stock;

    protected Product() {
        this.id = UUID.randomUUID();
    }

    public Product(String name, int stock) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.stock = stock;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
