package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Category;

public interface CategoryReository extends JpaRepository<Category, Integer> {

	Page<Category> findByCategoryLike(String categoryKeyword, Pageable pageable);

}
