package com.example.product_catalog_api.service;

import org.springframework.stereotype.Service;
import com.example.product_catalog_api.repository.ProductRepository;
import com.example.product_catalog_api.dto.CreateProductRequest;
import com.example.product_catalog_api.dto.ProductResponse;
import com.example.product_catalog_api.dto.PatchProductRequest;
import java.util.UUID;
import com.example.product_catalog_api.entity.Product;
import com.example.product_catalog_api.exception.DuplicateSkuException;
import com.example.product_catalog_api.exception.ProductNotFoundException;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException("SKU already exists");
        }
        UUID newId = UUID.randomUUID();
        Product product = new Product(newId);
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        Product savedProduct = productRepository.save(product);
        return new ProductResponse(savedProduct);
    }

    public ProductResponse getProductById(UUID id) {
        Optional<Product> result = productRepository.findById(id);
        if (result.isEmpty()) {
            throw new ProductNotFoundException("Product doesn't exist");
        }
        Product product = result.get();
        return new ProductResponse(product);

    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> responses = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            responses.add(new ProductResponse(product));
        }
        return responses;
    }

    public ProductResponse updateProduct(UUID id, CreateProductRequest request) {
        Optional<Product> result = productRepository.findById(id);
        if (result.isEmpty()) {
            throw new ProductNotFoundException("Product doesn't exist");
        }
        Product product = result.get();
        if (!product.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException("SKU already exists");
        }
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        Product savedProduct = productRepository.save(product);
        return new ProductResponse(savedProduct);

    }

    public void deleteProduct(UUID id) {
        Optional<Product> result = productRepository.findById(id);
        if (result.isEmpty()) {
            throw new ProductNotFoundException("Product doesn't exist");
        }
        productRepository.deleteById(id);
    }

    public ProductResponse patchProduct(UUID id, PatchProductRequest request) {
        Optional<Product> result = productRepository.findById(id);
        if (result.isEmpty()) {
            throw new ProductNotFoundException("Product doesn't exist");
        }
        Product product = result.get();
        if (request.getSku() != null && !product.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException("SKU already exists");
        }
        if (request.getSku() != null) {
            product.setSku(request.getSku());
        }
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        Product savedProduct = productRepository.save(product);
        return new ProductResponse(savedProduct);
    }
}
