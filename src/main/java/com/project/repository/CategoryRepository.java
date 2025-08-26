package com.project.repository;

import com.project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
    
    List<Category> findByIsActiveTrue();
    
    List<Category> findByParentIsNullAndIsActiveTrue();
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.parent.id = :parentId")
    List<Category> findByParentIdAndIsActiveTrue(@Param("parentId") Long parentId);
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.parent IS NULL")
    List<Category> findRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.parent.id = :parentId")
    List<Category> findSubCategories(@Param("parentId") Long parentId);
    
    boolean existsByName(String name);
}
