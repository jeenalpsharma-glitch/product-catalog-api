package com.example.product_catalog_api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.product_catalog_api.dto.CreateProductRequest;
import com.example.product_catalog_api.dto.PatchProductRequest;
import com.example.product_catalog_api.dto.ProductResponse;
import com.example.product_catalog_api.entity.Product;
import com.example.product_catalog_api.service.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
    }

    @Test
    void getProductByIdShouldReturnServiceResponse() {
        ProductResponse response = response("LAPTOP-001", "MacBook");
        when(productService.getProductById(productId)).thenReturn(response);

        ProductResponse result = productController.getProductById(productId);

        assertSame(response, result);
        verify(productService).getProductById(productId);
    }

    @Test
    void getAllProductsShouldReturnServiceResponses() {
        ProductResponse first = response("SKU-001", "First");
        ProductResponse second = response("SKU-002", "Second");
        when(productService.getAllProducts()).thenReturn(List.of(first, second));

        List<ProductResponse> result = productController.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("SKU-001", result.get(0).getSku());
        assertEquals("SKU-002", result.get(1).getSku());
        verify(productService).getAllProducts();
    }

    @Test
    void updateProductShouldDelegateToService() {
        CreateProductRequest request = createRequest("LAPTOP-001", "MacBook");
        ProductResponse response = response("LAPTOP-001", "MacBook");
        when(productService.updateProduct(productId, request)).thenReturn(response);

        ProductResponse result = productController.updateProduct(productId, request);

        assertSame(response, result);
        verify(productService).updateProduct(productId, request);
    }

    @Test
    void deleteProductShouldDelegateToService() {
        productController.deleteProduct(productId);

        verify(productService).deleteProduct(productId);
    }

    @Test
    void patchProductShouldDelegateToService() {
        PatchProductRequest request = new PatchProductRequest();
        request.setName("MacBook Pro");
        ProductResponse response = response("LAPTOP-001", "MacBook Pro");
        when(productService.patchProduct(productId, request)).thenReturn(response);

        ProductResponse result = productController.patchProduct(productId, request);

        assertSame(response, result);
        verify(productService).patchProduct(productId, request);
    }

    @Test
    void createProductShouldReturnCreatedStatusAndLocation() {
        CreateProductRequest request = createRequest("LAPTOP-001", "MacBook");
        ProductResponse response = response("LAPTOP-001", "MacBook");
        when(productService.createProduct(request)).thenReturn(response);

        var result = productController.createProduct(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("/api/v1/products/" + response.getId(), result.getHeaders().getLocation().toString());
        assertSame(response, result.getBody());
        verify(productService).createProduct(request);
    }

    private CreateProductRequest createRequest(String sku, String name) {
        CreateProductRequest request = new CreateProductRequest();
        request.setSku(sku);
        request.setName(name);
        request.setPrice(new BigDecimal("999.99"));
        request.setQuantity(5);
        return request;
    }

    private ProductResponse response(String sku, String name) {
        Product product = new Product(UUID.randomUUID());
        product.setSku(sku);
        product.setName(name);
        product.setPrice(new BigDecimal("999.99"));
        product.setQuantity(5);
        return new ProductResponse(product);
    }
}
