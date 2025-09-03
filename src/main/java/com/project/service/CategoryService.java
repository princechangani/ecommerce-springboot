package com.project.service;

import com.project.dto.CreateCategoryRequest;
import com.project.entity.Category;
import com.project.entity.Product;
import com.project.exception.ResourceAlreadyExists;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.CategoryRepository;
import com.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "name", name));
    }

    public Category createCategory(CreateCategoryRequest request) {
        // Check if category with same name already exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExists("Category with name '" + request.getName() + "' already exists");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(true);

        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CreateCategoryRequest request) {
        Category category = getCategoryById(id);

        // Check if new name conflicts with existing category (excluding current one)
        if (!category.getName().equals(request.getName()) && 
            categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExists("Category with name '" + request.getName() + "' already exists");
        }

        category.setName(request.getName());
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        
        // Check if category has products
        List<Product> productsInCategory = productRepository.findByCategoryId(id);
        if (!productsInCategory.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing products. Move or delete products first.");
        }

        categoryRepository.delete(category);
    }

    public void deactivateCategory(Long id) {
        Category category = getCategoryById(id);
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    public void activateCategory(Long id) {
        Category category = getCategoryById(id);
        category.setIsActive(true);
        categoryRepository.save(category);
    }

    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        return productRepository.findByCategoryId(categoryId);
    }

    public long getCategoryProductCount(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.size();
    }

    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}
