package com.example.nagoyameshi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.dto.HouseScoreDto;
import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.service.HouseService;

@Controller
public class HomeContoroller {
	
	 private final HouseService houseService;
	 private final CategoryRepository categoryRepository;  
	 
	     
	 public HomeContoroller(HouseService houseService, CategoryRepository categoryRepository) {
	     this.houseService = houseService;
	     this.categoryRepository = categoryRepository;
	 }    
	
	
//	@GetMapping("/")アノテーションをつけた場合、アプリのトップページに（GETメソッドで）アクセスされたときにそのメソッドが実行されるようになる。
	@GetMapping("/")
	public String index(Model model) {
		
		List<House> newHouses = houseService.getTop5Houses();
		
		List<Category> categories = categoryRepository.findAll();	    
	    
        
//    	店舗のスコア平均を計算して出力する
        Map<Integer, Double> houseAverageScore = new HashMap<>();
        
        for (House house : newHouses) {
            Double averageScore = houseService.getHouseAverageScore(house.getId());
            houseAverageScore.put(house.getId(), averageScore);
        }
        
//    人気順5件表示（HouseServiceで）
        List<HouseScoreDto> houseScore = houseService.getHousesOrderedByAverageScore();

        model.addAttribute("categories", categories);
        model.addAttribute("houseScore", houseScore);
        model.addAttribute("newHouses", newHouses); 
        model.addAttribute("houseAverageScore", houseAverageScore);
		
//		return "index";と記述すると、src/main/resources/templatesフォルダ内にあるindex.htmlが呼び出される。
		return "index";
	}

	

	
}
