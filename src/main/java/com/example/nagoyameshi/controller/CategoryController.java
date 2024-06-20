package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.repository.CategoryReository;

@Controller
@RequestMapping("/categories")
public class CategoryController {
	private final CategoryReository categoryRepository;
  
    
	public CategoryController (CategoryReository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}


	@GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {
	
	Page<Category> categoryPage;
	
    if (keyword != null && !keyword.isEmpty()) {
    	
        categoryPage = categoryRepository.findByCategoryLike("%" + keyword + "%", pageable);
    
        }else {
        	categoryPage = categoryRepository.findAll(pageable);
        }
    
    model.addAttribute("categoryPage", categoryPage);
    model.addAttribute("keyword", keyword);
    
    return "categories/index";

    }
}
