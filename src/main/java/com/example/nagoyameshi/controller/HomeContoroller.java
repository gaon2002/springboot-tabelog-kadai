package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.repository.HouseRepository;

@Controller
public class HomeContoroller {
	
	 private final HouseRepository houseRepository;        
	     
	 public HomeContoroller(HouseRepository houseRepository) {
	     this.houseRepository = houseRepository;            
	 }    
	
	
//	@GetMapping("/")アノテーションをつけた場合、アプリのトップページに（GETメソッドで）アクセスされたときにそのメソッドが実行されるようになる。
	@GetMapping("/")
	public String index(Model model) {
		
        List<House> newHouses = houseRepository.findTop5ByOrderByCreatedAtDesc();
        model.addAttribute("newHouses", newHouses); 
		
//		return "index";と記述すると、src/main/resources/templatesフォルダ内にあるindex.htmlが呼び出される。
		return "index";
	}

}
