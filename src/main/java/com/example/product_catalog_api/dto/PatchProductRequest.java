package com.example.product_catalog_api.dto;

import java.math.BigDecimal;

import com.example.product_catalog_api.entity.Product.ProductStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class PatchProductRequest {
    @Size(min = 3, max = 32)
    @Pattern(regexp = "[a-zA-Z0-9-]+")
    private String sku;
    @Size(min = 1, max = 120)
    private String name;
    @Size(max = 1000)
    private String description;
    @DecimalMin(value = "0.0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;
    @Min(0)
    private Integer quantity;
    private ProductStatus status;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

}
