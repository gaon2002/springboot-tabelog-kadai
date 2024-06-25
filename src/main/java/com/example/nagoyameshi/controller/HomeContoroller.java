package com.example.nagoyameshi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.dto.HouseScoreDto;
import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.repository.HouseRepository;
import com.example.nagoyameshi.service.HouseService;
import com.example.nagoyameshi.service.ReviewService;

@Controller
public class HomeContoroller {
	
	 private final HouseRepository houseRepository;    
	 private final ReviewService reviewService;
	 private final HouseService houseService;
	 
	     
	 public HomeContoroller(HouseRepository houseRepository, ReviewService reviewService, HouseService houseService) {
	     this.houseRepository = houseRepository;      
	     this.reviewService = reviewService;
	     this.houseService = houseService;
	 }    
	
	
//	@GetMapping("/")アノテーションをつけた場合、アプリのトップページに（GETメソッドで）アクセスされたときにそのメソッドが実行されるようになる。
	@GetMapping("/")
	public String index(Model model) {
		
        List<House> newHouses = houseRepository.findTop5ByOrderByCreatedAtDesc();
        
//    	店舗のスコア平均を計算して出力する
        Map<Integer, Double> houseAverageScore = new HashMap<>();
        
        for (House house : newHouses) {
            Double averageScore = houseService.getHouseAverageScore(house.getId());
            houseAverageScore.put(house.getId(), averageScore);
        }
        
//    人気順5件表示（HouseServiceで）
        List<HouseScoreDto> houseScore = houseService.getHousesOrderedByAverageScore();

      
        model.addAttribute("houseScore", houseScore);
        model.addAttribute("newHouses", newHouses); 
        model.addAttribute("houseAverageScore", houseAverageScore);
		
//		return "index";と記述すると、src/main/resources/templatesフォルダ内にあるindex.htmlが呼び出される。
		return "index";
	}

	

	
}
