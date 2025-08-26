package com.project.controller;

import com.project.dto.ApiResponse;
import com.project.dto.ProductDto;
import com.project.dto.ProductRequest;
import com.project.entity.Product;
import com.project.mapper.ProductMapper;
import com.project.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productService.createProduct(product);
        ProductDto productDto = productMapper.toDto(savedProduct);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProduct.getId())
                .toUri();
                
        return ResponseEntity.created(location)
                .body(ApiResponse.success("Product created successfully", productDto));
    }

    @GetMapping
    @Operation(summary = "Get all active products", description = "Retrieve a paginated list of all active products")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products = productService.getActiveProducts();
        
        // Manual pagination since we're not using Spring Data JPA's pagination here
        int start = (int) pageable.getOffset();
        
        List<ProductDto> productDtos = products.stream()
                .skip(start)
                .limit(pageable.getPageSize())
                .map(productMapper::toDto)
                .collect(Collectors.toList());
                
        Page<ProductDto> pageResult = new PageImpl<>(
            productDtos, 
            pageable, 
            products.size()
        );
        
        return ResponseEntity.ok(ApiResponse.success(pageResult));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a product by its ID")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        var product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(productMapper.toDto(product)));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU", description = "Retrieve a product by its SKU")
    public ResponseEntity<ApiResponse<ProductDto>> getProductBySku(@PathVariable String sku) {
        var product = productService.getProductBySku(sku);
        return ResponseEntity.ok(ApiResponse.success(productMapper.toDto(product)));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieve products by category ID")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
                
        Pageable pageable = PageRequest.of(page, size);
        List<Product> products = productService.getProductsByCategory(categoryId);
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        
        List<ProductDto> productDtos = products.stream()
                .skip(start)
                .limit(pageable.getPageSize())
                .map(productMapper::toDto)
                .collect(Collectors.toList());
                
        Page<ProductDto> pageResult = new PageImpl<>(
            productDtos, 
            pageable, 
            products.size()
        );
        
        return ResponseEntity.ok(ApiResponse.success(pageResult));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products", description = "Retrieve products with low stock")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
                
        List<Product> products = productService.getLowStockProducts(threshold);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(ApiResponse.success(productDtos));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products with filters")
    public ResponseEntity<ApiResponse<List<ProductDto>>> searchProducts(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
                
        List<Product> products = productService.searchProducts(searchTerm, categoryId, minPrice, maxPrice);
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(ApiResponse.success(productDtos));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
                
        Product product = productService.getProductById(id);
        productMapper.updateEntityFromRequest(request, product);
        Product updatedProduct = productService.updateProduct(id, product);
        ProductDto productDto = productMapper.toDto(updatedProduct);
        
        return ResponseEntity.ok(
            ApiResponse.success("Product updated successfully", productDto)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product stock", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ProductDto>> updateStock(
            @PathVariable Long id,
            @RequestParam Integer newStock,
            @RequestParam String reason) {
                
        Product updatedProduct = productService.updateProductStock(id, newStock, reason);
        ProductDto productDto = productMapper.toDto(updatedProduct);
        
        return ResponseEntity.ok(
            ApiResponse.success("Stock updated successfully", productDto)
        );
    }
}
