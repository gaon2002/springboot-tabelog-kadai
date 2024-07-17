package com.example.nagoyameshi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.HouseRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.HouseService;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/houses")
public class HouseController {
	private final HouseRepository houseRepository;
	private final CategoryRepository categoryRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	private final FavoriteService favoriteService;     
	private final HouseService houseService;
    
	public HouseController(HouseRepository houseRepository, ReviewRepository reviewRepository, ReviewService reviewService, FavoriteService favoriteService, HouseService houseService, CategoryRepository categoryRepository) {
		this.houseRepository = houseRepository;
		this.reviewRepository = reviewRepository;
		this.categoryRepository = categoryRepository;
		this.reviewService = reviewService;
		this.favoriteService = favoriteService;      
		this.houseService = houseService;
    }     
  
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "priceMin", required = false) Integer priceMin,
                        @RequestParam(name = "priceMax", required = false) Integer priceMax,
                        @RequestParam(name = "order", required = false) String order,
                        @RequestParam(name = "categoryIds", required = false) List<Integer> category,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {
		List<Category> categories = categoryRepository.findAll();	    
    	
        Page<House> housePage;
        
//      キーワード検索
        if (keyword != null && !keyword.isEmpty()) {
        	if (order != null && order.equals("priceMinAsc")) {
//        		並べ替え：最小利用金額の小さい順
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceMinAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
        	} else if ("scoreDesc".equals(order)) {
//              スコアの高い順に並べ替え
        		housePage = houseService.getHouseAverageScoreSorted(keyword, keyword, pageable);
            } else {
//        		並べ替え：新着順
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }   
//      エリア検索
        } else if (area != null && !area.isEmpty()) {
        	if (order != null && order.equals("priceMinAsc")) {
                housePage = houseRepository.findByAddressLikeOrderByPriceMinAsc("%" + area + "%", pageable);
        	} else if ("scoreDesc".equals(order)) {
//              スコアの高い順に並べ替え
        		housePage = houseService.getHouseAverageScoreSorted(area, area, pageable);
            } else {
                housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }     
//      ※利用金額の下限と上限を設定して検索
        } else if (priceMin != null && priceMax != null) {
        	if (order != null && order.equals("priceMinAsc")) {
                housePage = houseRepository.findByPriceMinGreaterThanEqualAndPriceMaxLessThanEqualOrderByPriceMinAsc(priceMin, priceMax, pageable);
        	} else if ("scoreDesc".equals(order)) {
//              スコアの高い順に並べ替え
        		housePage = houseService.getHouseAverageScoreSortedGreaterAndLess(priceMin, priceMax, pageable);
            } else {
                housePage = houseRepository.findByPriceMinGreaterThanEqualAndPriceMaxLessThanEqualOrderByCreatedAtDesc(priceMin, priceMax, pageable);
            }     
        	
//      ※利用金額の下限を設定して検索
        } else if (priceMin != null && priceMax == null) {
        	if (order != null && order.equals("priceMinAsc")) {
                housePage = houseRepository.findByPriceMinGreaterThanEqualOrderByPriceMinAsc(priceMin, pageable);
        	} else if ("scoreDesc".equals(order)) {
//              スコアの高い順に並べ替え
        		housePage = houseService.getHouseAverageScoreSortedGreater(priceMin, pageable);
            } else {
                housePage = houseRepository.findByPriceMinGreaterThanEqualOrderByCreatedAtDesc(priceMin, pageable);
            }   

//      ※利用金額の上限を設定して検索
        } else if (priceMin == null && priceMax != null) {
        	if (order != null && order.equals("priceMinAsc")) {
                housePage = houseRepository.findByPriceMaxLessThanEqualOrderByPriceMinAsc(priceMax, pageable);
        	} else if ("scoreDesc".equals(order)) {
//              スコアの高い順に並べ替え
        		housePage = houseService.getHouseAverageScoreSortedLess(priceMax, pageable);
            } else {
                housePage = houseRepository.findByPriceMaxLessThanEqualOrderByCreatedAtDesc(priceMax, pageable);
            }  
        
//       ※カテゴリー検索
         } else if (category != null && category != null) {
         	if (order != null && order.equals("priceMinAsc")) {
	         	housePage = houseRepository.findByCategoryIdsOrderByPriceMinAsc(category, pageable);
         	} else if ("scoreDesc".equals(order)) {
//              スコアの高い順に並べ替え
        		housePage = houseService.getHouseAverageScoreSortedCategory(category, pageable);
	        } else {
	           	housePage = houseRepository.findByCategoryIdsOrderByCreatedAtDesc(category, pageable);
	        }  
        	
//        	※検索内容がなかったら全表示
        } else {
        	if (order != null && order.equals("priceMinAsc")) {
        		housePage = houseRepository.findAllByOrderByPriceMinAsc(pageable);
			} else if (order != null && "scoreDesc".equals(order)) {
			    // スコアの高い順に並べ替え
				housePage = houseService.getHouseAverageScoreSortedAll(pageable);
			} else {
			    housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);   
			}
        }
        	
        
//    	店舗のスコア平均を計算して出力する
	        Map<Integer, Double> houseAverageScore = new HashMap<>();
	        
	        for (House house : housePage) {
	        	
	            Double averageScore = houseService.getHouseAverageScore(house.getId());
	            
	            houseAverageScore.put(house.getId(), averageScore);
	        }
	        
       
        
        model.addAttribute("categories", categories);
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("priceMax", priceMax);         
        model.addAttribute("priceMin", priceMin); 
        model.addAttribute("categoryIds", category); 
        model.addAttribute("order", order);
        model.addAttribute("houseAverageScore", houseAverageScore);

        return "houses/index";
    }

       
//  店舗詳細表示のためのデータ
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id,
                       Model model,
                       @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
    	
//    	Optional型を用いてエンティティの存在チェックを行う
        Optional<House> houseOpt = houseRepository.findById(id);
        if (!houseOpt.isPresent()) {
            // ハウスが見つからない場合のエラーハンドリングを追加する
            return "error/404"; // 例: 404ページにリダイレクト
        }
        
        House house = houseOpt.get();
        
        boolean hasUserAlreadyReviewed = false;
        boolean hasUserAlreadyFavorited = false;
        
        if (userDetailsImpl != null) {
            User user = userDetailsImpl.getUser();
            hasUserAlreadyReviewed = reviewService.hasUserAlreadyReviewed(house, user);
            hasUserAlreadyFavorited = favoriteService.hasUserAlreadyFavorited(house, user);
        }

        // 対象の民宿のコメントを6件表示する(引数のhouseはReviewEntityで設定したhouse)
        List<Review> newReviews = reviewRepository.findTop6ByHouseAndDisplayOrderByCreatedAtDesc(house, 0);
        
        long totalReviewCount = reviewRepository.countByHouse(house);
        

//    	店舗のスコア平均を計算して出力する
    	Double houseAverageScore = houseService.getHouseAverageScore(id);
    	
//    	カテゴリ表示
    	List<Category> categories = categoryRepository.findAll();	  

        // 『店舗詳細ビューに渡すデータ』
    	model.addAttribute("categories", categories);
        model.addAttribute("house", house);
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        model.addAttribute("reviews", newReviews);
        model.addAttribute("hasUserAlreadyFavorited", hasUserAlreadyFavorited);
        model.addAttribute("hasUserAlreadyReviewed", hasUserAlreadyReviewed);
        model.addAttribute("totalReviewCount", totalReviewCount);
        model.addAttribute("houseAverageScore", houseAverageScore);

        return "houses/show";
    }

    
}