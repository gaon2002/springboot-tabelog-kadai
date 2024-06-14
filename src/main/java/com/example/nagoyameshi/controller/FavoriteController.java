package com.example.nagoyameshi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.HouseRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;

@Controller
public class FavoriteController {
	
    @Autowired
    private FavoriteService favoriteService;
    private FavoriteRepository favoriteRepository;
    private final HouseRepository houseRepository;
    
    public FavoriteController(HouseRepository houseRepository, FavoriteRepository favoriteRepository, FavoriteService favoriteService) {
    	this.favoriteService = favoriteService;
        this.favoriteRepository = favoriteRepository;
        this.houseRepository= houseRepository;
    }

//  ■お気に入り登録
    @PostMapping("/favorite/addFavorite")
    public String subscribe(@RequestParam("houseId") Integer houseId,
    						Model model,
    						@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
    						RedirectAttributes redirectAttributes) {
    	
        User user = userDetailsImpl.getUser();
        Optional<House> houseOptional = houseRepository.findById(houseId);
        
//      ■houseOptionalに値があれば
        if (houseOptional.isPresent()) {
//        	■houseOptionalから値を取得する
        	House house = houseOptional.get();
//        	■favoriteServiceのsubscribe()メソッドを実行する
        	favoriteService.subscribe(house, user);

         	redirectAttributes.addFlashAttribute("successMessage", "お気に入りに登録しました。");
        	
        }else {
        	redirectAttributes.addFlashAttribute("errorMessage", "指定された民宿が見つかりませんでした。");
        }
		 
//      ※return "redirect:/houses/{houseId}";の部分で {houseId}を正しく認識させる必要があり、リダイレクトURLの形式をPath Variableとして渡す値をRedirectAttributesで明示的に対応させる。
        return "redirect:/houses/" + houseId;
    }

//   ■お気に入り一覧表示（10件ずつ表示）
	// favorite/index.html
	@GetMapping("/favorite")
	public String favorite(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			  @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			  Model model) {

		Integer userId = Integer.valueOf(userDetailsImpl.getUser().getId());
		
		// 対象のユーザーに関連するFavoriteエンティティのリストを取得
		List<Favorite> favorites = favoriteRepository.findByUserId(userId);
		
		// Favoriteエンティティから関連するHouseエンティティを取得し、リストに追加
		List<Integer> houseList = new ArrayList<>();
		for (Favorite favorite : favorites) {
		Integer houseId = favorite.getHouse().getId();
		houseList.add(houseId);
		}

		// ➀で作成したリストからhouse_idをマッチングさせて、HouseRepositoryからHouseの情報を取得する
		// 実際にRepositoryのカスタムメソッドを使用して、必要なHouseデータを取得。
		Page<House> housePages = houseRepository.findByIdIn(houseList, pageable);
		
		model.addAttribute("housePages", housePages);
		
		return "favorite/index";
  		
	}
		
	
//		■お気に入り削除
		@PostMapping("/favorite/delete")
		public String removeFavorite(@RequestParam("houseId") Integer houseId,
									 Model model,
									 @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
									 RedirectAttributes redirectAttributes) {
			
		    User user = userDetailsImpl.getUser();
		    Optional<House> houseOptional = houseRepository.findById(houseId);
		    
//		      ■houseOptionalに値があれば
		        if (houseOptional.isPresent()) {
//		        	■houseOptionalから値を取得する
		        	House house = houseOptional.get();
//		        	■favoriteServiceのsubscribe()メソッドを実行する
		        	favoriteService.unsubscribe(house, user);

		        	redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");
		        	
		        }else {
		        	redirectAttributes.addFlashAttribute("errorMessage", "指定された店舗が見つかりませんでした。");
		        }
		    
		    
//	      	※return "redirect:/houses/{houseId}";の部分で {houseId}を正しく認識させる必要があり、リダイレクトURLの形式をPath Variableとして渡す値をRedirectAttributesで明示的に対応させる。
		    return "redirect:/houses/" + houseId;
		}
		
	        

}