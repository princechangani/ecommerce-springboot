package com.project.controller;

import com.project.dto.CreateCategoryRequest;
import com.project.entity.Category;
import com.project.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category Management", description = "APIs for managing product categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all active categories", description = "Retrieve a list of all active categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve a category by its ID")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/root")
    @Operation(summary = "Get root categories", description = "Retrieve all root categories (no parent)")
    public ResponseEntity<List<Category>> getRootCategories() {
        // This functionality needs to be added to CategoryService
        // For now, we'll return all categories and filter on the frontend
        List<Category> allCategories = categoryService.getAllCategories();
        List<Category> rootCategories = allCategories.stream()
                .filter(category -> category.getParent() == null)
                .toList();
        return ResponseEntity.ok(rootCategories);
    }

    @GetMapping("/{parentId}/subcategories")
    @Operation(summary = "Get subcategories", description = "Retrieve subcategories of a parent category")
    public ResponseEntity<List<Category>> getSubCategories(@PathVariable Long parentId) {
        // This functionality needs to be added to CategoryService
        // For now, we'll return all categories and filter on the frontend
        List<Category> allCategories = categoryService.getAllCategories();
        List<Category> subCategories = allCategories.stream()
                .filter(category -> category.getParent() != null && 
                        category.getParent().getId().equals(parentId))
                .toList();
        return ResponseEntity.ok(subCategories);
    }

    @PostMapping
    @Operation(summary = "Create new category", description = "Create a new product category")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        if (request.getParentId() != null) {
            Category parent = categoryService.getCategoryById(request.getParentId());
            category.setParent(parent);
        }
        
        category.setIsActive(true);
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing category")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Soft delete a category")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deactivateCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate category", description = "Activate a deactivated category")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> activateCategory(@PathVariable Long id) {
        categoryService.activateCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/products")
    @Operation(summary = "Get products by category", description = "Retrieve all products in a specific category")
    public ResponseEntity<List<com.project.entity.Product>> getProductsByCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getProductsByCategory(id));
    }

    @GetMapping("/{id}/product-count")
    @Operation(summary = "Get category product count", description = "Get the number of products in a category")
    public ResponseEntity<Long> getCategoryProductCount(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryProductCount(id));
    }
}
