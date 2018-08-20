package com.arnoldgalovics.blog.jpalocking.repository;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.util.UUID;

@Entity
public class VersionedProduct {
    @Id
    private UUID id;

    private String name;

    private int stock;

    @Version
    private int version;

    protected VersionedProduct() {
        this.id = UUID.randomUUID();
    }

    public VersionedProduct(String name, int stock) {
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
