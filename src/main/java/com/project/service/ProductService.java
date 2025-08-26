package com.project.service;

import com.project.entity.Category;
import com.project.entity.InventoryTransaction;
import com.project.entity.Product;
import com.project.enums.InventoryTransactionType;
import com.project.exception.ResourceAlreadyExists;
import com.project.exception.ResourceNotFound;
import com.project.repository.CategoryRepository;
import com.project.repository.InventoryTransactionRepository;
import com.project.repository.ProductRepository;
import com.project.util.PaginatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    public Product createProduct(Product product) {
        if (productRepository.findBySku(product.getSku()).isPresent()) {
            throw new ResourceAlreadyExists("Product with SKU " + product.getSku() + " already exists");
        }
        
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFound("Category not found"));
        
        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFound("Product not found with SKU: " + sku));
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Product not found with ID: " + id));
    }

    public PaginatedResult<Product> getProductsPaginated(Long categoryId, int page, int size) {
        List<Product> allProducts = categoryId != null ? 
            productRepository.findByCategoryIdAndIsActiveTrue(categoryId) : 
            productRepository.findByIsActiveTrue();
            
        // Manual pagination
        int start = page * size;
        int end = Math.min((start + size), allProducts.size());
        
        List<Product> pageContent = allProducts.subList(start, end);
        
        return new PaginatedResult<>(
            pageContent,
            allProducts.size(),
            page,
            size
        );
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProducts(String searchTerm, Long categoryId, Double minPrice, Double maxPrice) {
        return productRepository.searchProducts(
                searchTerm,
                categoryId,
                minPrice != null ? java.math.BigDecimal.valueOf(minPrice) : null,
                maxPrice != null ? java.math.BigDecimal.valueOf(maxPrice) : null
        );
    }

    @Transactional
    public Product updateProductStock(Long productId, Integer newStock, String reason) {
        Product product = getProductById(productId);
        int quantityChange = newStock - product.getStockQuantity();
        
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(product);
        transaction.setQuantityChange(quantityChange);
        transaction.setType(InventoryTransactionType.ADJUSTMENT);
        transaction.setReferenceType("adjustment");
        transaction.setNotes(reason);
        
        product.setStockQuantity(newStock);
        Product updatedProduct = productRepository.save(product);
        
        inventoryTransactionRepository.save(transaction);
        
        return updatedProduct;
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        
        if (productDetails.getName() != null) {
            product.setName(productDetails.getName());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getPrice() != null) {
            product.setPrice(productDetails.getPrice());
        }
        if (productDetails.getCostPrice() != null) {
            product.setCostPrice(productDetails.getCostPrice());
        }
        if (productDetails.getStockQuantity() != null) {
            product.setStockQuantity(productDetails.getStockQuantity());
        }
        if (productDetails.getWeight() != null) {
            product.setWeight(productDetails.getWeight());
        }
        if (productDetails.getDimensions() != null) {
            product.setDimensions(productDetails.getDimensions());
        }
        
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setIsActive(false);
        productRepository.save(product);
    }
}
