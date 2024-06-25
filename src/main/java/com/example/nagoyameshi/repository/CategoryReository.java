package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Category;

public interface CategoryReository extends JpaRepository<Category, Integer> {


	public Page<Category> findByCategoryLike(String categoryKeyword, Pageable pageable);   
	public Page<Category> findAll(Pageable pageable);  
	
//    public Page<Category> findByCategoryLikeOrderByCreatedAtDesc(String categoryKeyword, Pageable pageable);  
//    public Page<Category> findByCategoryLikeOrderByPriceMinAsc(String categoryKeyword, Pageable pageable);  

}
