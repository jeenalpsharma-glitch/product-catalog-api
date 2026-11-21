package com.example.product_catalog_api.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.product_catalog_api.service.ProductService;

import jakarta.validation.Valid;

import com.example.product_catalog_api.dto.CreateProductRequest;
import com.example.product_catalog_api.dto.PatchProductRequest;
import com.example.product_catalog_api.dto.ProductResponse;

import java.util.UUID;
// import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/api/v1/products/{id}")
    public ProductResponse getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @GetMapping("/api/v1/products")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @PutMapping("/api/v1/products/{id}")
    public ProductResponse updateProduct(@PathVariable UUID id,
            @RequestBody @Valid CreateProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/api/v1/products/{id}")
    public void deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }

    @PatchMapping("/api/v1/products/{id}")
    public ProductResponse patchProduct(@PathVariable UUID id,
            @RequestBody @Valid PatchProductRequest request) {
        return productService.patchProduct(id, request);
    }

    @PostMapping("/api/v1/products")
    public ProductResponse createProduct(@RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

}
