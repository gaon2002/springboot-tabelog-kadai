package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.HouseRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/houses")
public class HouseController {
	private final HouseRepository houseRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	private final FavoriteService favoriteService;     
    
	public HouseController(HouseRepository houseRepository, ReviewRepository reviewRepository, ReviewService reviewService, FavoriteService favoriteService) {
		this.houseRepository = houseRepository;
		this.reviewRepository = reviewRepository;
		this.reviewService = reviewService;
		this.favoriteService = favoriteService;           
    }     
  
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "priceMin", required = false) Integer priceMin,
                        @RequestParam(name = "priceMax", required = false) Integer priceMax,
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {
    	
        Page<House> housePage;
        
        System.out.println("priceMin: " + priceMin);
        System.out.println("priceMax: " + priceMax);
        System.out.println("pageable: " + pageable);
                
        if (keyword != null && !keyword.isEmpty()) {
        	if (order != null && order.equals("priceMinAsc")) {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceMinAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }   
        } else if (area != null && !area.isEmpty()) {
        	if (order != null && order.equals("priceMinAsc")) {
                housePage = houseRepository.findByAddressLikeOrderByPriceMinAsc("%" + area + "%", pageable);
            } else {
                housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }     
//        ※利用金額の下限と上限を設定して検索
        } else if (priceMin != null && priceMax != null) {
        	if (order != null && order.equals("priceMinAsc")) {
                housePage = houseRepository.findByPriceMinGreaterThanEqualAndPriceMaxLessThanEqualOrderByPriceMinAsc(priceMax, priceMin, pageable);
            } else {
                housePage = houseRepository.findByPriceMinGreaterThanEqualAndPriceMaxLessThanEqualOrderByCreatedAtDesc(priceMax, priceMin, pageable);
            }     
        	
//        ※利用金額の下限を設定して検索
        } else if (priceMin != null && priceMax == null) {
        	if (order != null && order.equals("priceMinAsc")) {
                housePage = houseRepository.findByPriceMinGreaterThanEqualOrderByPriceMinAsc(priceMin, pageable);
            } else {
                housePage = houseRepository.findByPriceMinGreaterThanEqualOrderByCreatedAtDesc(priceMin, pageable);
            }   

//        ※利用金額の上限を設定して検索
        } else if (priceMin == null && priceMax != null) {
        	if (order != null && order.equals("priceMinAsc")) {
                housePage = houseRepository.findByPriceMaxLessThanEqualOrderByPriceMinAsc(priceMax, pageable);
            } else {
                housePage = houseRepository.findByPriceMaxLessThanEqualOrderByCreatedAtDesc(priceMax, pageable);
            }   
        	
        } else {
        	if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findAllByOrderByPriceMinAsc(pageable);
            } else {
                housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);   
            }        
        }                
        
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("priceMax", priceMax);         
        model.addAttribute("priceMin", priceMin); 
        model.addAttribute("order", order);
        
        return "houses/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id,
    				   Model model,
    				   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
    	
        House house = houseRepository.getReferenceById(id);
        
        boolean hasUserAlreadyReviewed = false;
		boolean hasUserAlreadyFavorited = false;
		
		if(userDetailsImpl != null) {
			User user = userDetailsImpl.getUser();
			hasUserAlreadyReviewed = reviewService.hasUserAlreadyReviewed(house, user);
			hasUserAlreadyFavorited = favoriteService.hasUserAlreadyFavorited(house, user);
		}
        
// 対象の民宿のコメントを6件表示する(引数のhouseはReviewEntityで設定したhouse)
		List<Review> newReviews = reviewRepository.findTop6ByHouseOrderByCreatedAtDesc(house);
		long totalReviewCount = reviewRepository.countByHouse(house);
        
//      『店舗詳細ビューに渡すデータ』
        model.addAttribute("house", house);
        model.addAttribute("reservationInputForm", new ReservationInputForm());
		model.addAttribute("reviews", newReviews);
		model.addAttribute("hasUserAlreadyFavorited", hasUserAlreadyFavorited);
		model.addAttribute("hasUserAlreadyReviewed", hasUserAlreadyReviewed);
		model.addAttribute("totalReviewCount", totalReviewCount);
        
        return "houses/show";
    }    
    
}