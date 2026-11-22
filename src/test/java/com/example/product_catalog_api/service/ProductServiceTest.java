package com.example.product_catalog_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.product_catalog_api.dto.CreateProductRequest;
import com.example.product_catalog_api.dto.PatchProductRequest;
import com.example.product_catalog_api.dto.ProductResponse;
import com.example.product_catalog_api.entity.Product;
import com.example.product_catalog_api.entity.Product.ProductStatus;
import com.example.product_catalog_api.exception.DuplicateSkuException;
import com.example.product_catalog_api.exception.ProductNotFoundException;
import com.example.product_catalog_api.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
    }

    @Test
    void createProductShouldSaveAndReturnProduct() {
        CreateProductRequest request = createRequest("LAPTOP-001", "MacBook");
        request.setDescription("Laptop");
        request.setStatus(ProductStatus.DISCONTINUED);

        when(productRepository.save(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response.getId());
        assertEquals("LAPTOP-001", response.getSku());
        assertEquals("MacBook", response.getName());
        assertEquals("Laptop", response.getDescription());
        assertEquals(new BigDecimal("999.99"), response.getPrice());
        assertEquals(5, response.getQuantity());
        assertEquals(ProductStatus.DISCONTINUED, response.getStatus());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals("LAPTOP-001", captor.getValue().getSku());
    }

    @Test
    void createProductShouldThrowWhenSkuAlreadyExists() {
        CreateProductRequest request = createRequest("LAPTOP-001", "MacBook");
        when(productRepository.existsBySku("LAPTOP-001")).thenReturn(true);

        assertThrows(DuplicateSkuException.class,
                () -> productService.createProduct(request));
        verify(productRepository, never()).save(org.mockito.ArgumentMatchers.any(Product.class));
    }

    @Test
    void getProductByIdShouldReturnProduct() {
        Product product = product("LAPTOP-001", "MacBook");
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(productId);

        assertEquals(productId, response.getId());
        assertEquals("LAPTOP-001", response.getSku());
        assertEquals("MacBook", response.getName());
    }

    @Test
    void getProductByIdShouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductById(productId));
    }

    @Test
    void getAllProductsShouldReturnMappedResponses() {
        Product first = product("SKU-001", "First");
        Product second = product("SKU-002", "Second");
        when(productRepository.findAll()).thenReturn(List.of(first, second));

        List<ProductResponse> responses = productService.getAllProducts();

        assertEquals(2, responses.size());
        assertEquals("SKU-001", responses.get(0).getSku());
        assertEquals("SKU-002", responses.get(1).getSku());
    }

    @Test
    void getAllProductsShouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), productService.getAllProducts());
    }

    @Test
    void updateProductShouldUpdateAndSaveProduct() {
        Product existing = product("LAPTOP-001", "Old Name");
        CreateProductRequest request = createRequest("LAPTOP-002", "New Name");
        request.setDescription("Updated description");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySku("LAPTOP-002")).thenReturn(false);
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse response = productService.updateProduct(productId, request);

        assertEquals("LAPTOP-002", response.getSku());
        assertEquals("New Name", response.getName());
        assertEquals("Updated description", response.getDescription());
        verify(productRepository).save(existing);
    }

    @Test
    void updateProductShouldAllowUnchangedSku() {
        Product existing = product("LAPTOP-001", "MacBook");
        CreateProductRequest request = createRequest("LAPTOP-001", "Updated MacBook");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse response = productService.updateProduct(productId, request);

        assertEquals("Updated MacBook", response.getName());
        verify(productRepository, never()).existsBySku("LAPTOP-001");
    }

    @Test
    void updateProductShouldThrowWhenProductDoesNotExist() {
        CreateProductRequest request = createRequest("LAPTOP-001", "MacBook");
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct(productId, request));
        verify(productRepository, never()).save(org.mockito.ArgumentMatchers.any(Product.class));
    }

    @Test
    void updateProductShouldThrowWhenNewSkuAlreadyExists() {
        Product existing = product("LAPTOP-001", "MacBook");
        CreateProductRequest request = createRequest("LAPTOP-002", "MacBook Pro");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySku("LAPTOP-002")).thenReturn(true);

        assertThrows(DuplicateSkuException.class,
                () -> productService.updateProduct(productId, request));
        verify(productRepository, never()).save(org.mockito.ArgumentMatchers.any(Product.class));
    }

    @Test
    void patchProductShouldUpdateOnlyProvidedFields() {
        Product existing = product("LAPTOP-001", "MacBook");
        existing.setDescription("Old description");
        existing.setQuantity(5);

        PatchProductRequest request = new PatchProductRequest();
        request.setName("MacBook Pro");
        request.setPrice(new BigDecimal("1200.00"));

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse response = productService.patchProduct(productId, request);

        assertEquals("MacBook Pro", response.getName());
        assertEquals(new BigDecimal("1200.00"), response.getPrice());
        assertEquals("Old description", response.getDescription());
        assertEquals(5, response.getQuantity());
        verify(productRepository).save(existing);
    }

    @Test
    void patchProductShouldUpdateSkuWhenAvailable() {
        Product existing = product("LAPTOP-001", "MacBook");
        PatchProductRequest request = new PatchProductRequest();
        request.setSku("LAPTOP-002");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySku("LAPTOP-002")).thenReturn(false);
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse response = productService.patchProduct(productId, request);

        assertEquals("LAPTOP-002", response.getSku());
    }

    @Test
    void patchProductShouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.patchProduct(productId, new PatchProductRequest()));
    }

    @Test
    void patchProductShouldThrowWhenNewSkuAlreadyExists() {
        Product existing = product("LAPTOP-001", "MacBook");
        PatchProductRequest request = new PatchProductRequest();
        request.setSku("LAPTOP-002");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySku("LAPTOP-002")).thenReturn(true);

        assertThrows(DuplicateSkuException.class,
                () -> productService.patchProduct(productId, request));
        verify(productRepository, never()).save(org.mockito.ArgumentMatchers.any(Product.class));
    }

    @Test
    void deleteProductShouldDeleteExistingProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product("SKU-001", "First")));

        productService.deleteProduct(productId);

        verify(productRepository).deleteById(productId);
    }

    @Test
    void deleteProductShouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProduct(productId));
        verify(productRepository, never()).deleteById(productId);
    }

    private CreateProductRequest createRequest(String sku, String name) {
        CreateProductRequest request = new CreateProductRequest();
        request.setSku(sku);
        request.setName(name);
        request.setPrice(new BigDecimal("999.99"));
        request.setQuantity(5);
        return request;
    }

    private Product product(String sku, String name) {
        Product product = new Product(productId);
        product.setSku(sku);
        product.setName(name);
        product.setPrice(new BigDecimal("999.99"));
        product.setQuantity(5);
        return product;
    }
}
