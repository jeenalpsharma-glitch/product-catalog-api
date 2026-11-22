package com.example.product_catalog_api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private HttpServletRequest request;

    @Test
    void handleNotFoundShouldReturn404ApiError() {
        when(request.getRequestURI()).thenReturn("/api/v1/products/123");

        ResponseEntity<ApiError> response = handler.handleNotFound(
                new ProductNotFoundException("Product doesn't exist"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Product doesn't exist", response.getBody().getMessage());
        assertEquals("/api/v1/products/123", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleDuplicateShouldReturn409ApiError() {
        when(request.getRequestURI()).thenReturn("/api/v1/products");

        ResponseEntity<ApiError> response = handler.handleDuplicate(
                new DuplicateSkuException("SKU already exists"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("SKU already exists", response.getBody().getMessage());
        assertEquals("/api/v1/products", response.getBody().getPath());
    }

    @Test
    void handleBadRequestShouldReturnFirstFieldValidationError() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/products");

        MethodArgumentNotValidException exception = org.mockito.Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError(
                "createProductRequest", "name", "must not be blank");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiError> response = handler.handleBadRequest(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("name must not be blank", response.getBody().getMessage());
        assertEquals("/api/v1/products", response.getBody().getPath());
    }

    @Test
    void exceptionsShouldPreserveTheirMessages() {
        ProductNotFoundException notFound = new ProductNotFoundException("missing");
        DuplicateSkuException duplicate = new DuplicateSkuException("duplicate");

        assertEquals("missing", notFound.getMessage());
        assertEquals("duplicate", duplicate.getMessage());
    }

    @Test
    void apiErrorShouldInitializeAllFields() {
        ApiError error = new ApiError(
                422, "Unprocessable Entity", "invalid product", "/api/v1/products");

        assertEquals(422, error.getStatus());
        assertEquals("Unprocessable Entity", error.getError());
        assertEquals("invalid product", error.getMessage());
        assertEquals("/api/v1/products", error.getPath());
        assertNotNull(error.getTimestamp());
    }
}
