package com.example.nagoyameshi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewInputForm;
import com.example.nagoyameshi.repository.HouseRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;

@Service
public class ReviewService {
	
	private final ReviewRepository reviewRepository;
	private final HouseRepository houseRepository;
	private final UserRepository userRepository;
	
	public ReviewService(ReviewRepository reviewRepository,HouseRepository houseRepository, UserRepository userRepository){
		this.reviewRepository = reviewRepository;
		this.houseRepository = houseRepository;
		this.userRepository =  userRepository;
	}
	
//	店舗のレビュー一覧表示はReviewControllerだけで処理

	
	
//	店舗の評価を新規投稿
	@Transactional
	public void create(@PathVariable(name = "id") Integer id, ReviewInputForm reviewInputForm, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Review review = new Review();
		
		Integer userId = Integer.valueOf(userDetailsImpl.getUser().getId());
				
		House house = houseRepository.getReferenceById(id);
		User user = userRepository.getReferenceById(userId);
		Integer display = 0;
				
		review.setHouse(house);
		review.setUser(user);
		review.setDisplay(display);
		review.setScore(reviewInputForm.getScore());
		review.setComment(reviewInputForm.getComment());
        
        // reviewテーブルの保存
		reviewRepository.save(review);
	}
	
	
//	店舗の評価を編集
	@Transactional
	public void update(ReviewEditForm reviewEditForm) {
		Review review = reviewRepository.getReferenceById(reviewEditForm.getId());
		
		review.setScore(reviewEditForm.getScore());
		review.setComment(reviewEditForm.getComment());
        
        // reviewテーブルの保存
		reviewRepository.save(review);
	}

	// 店舗のキーワード検索を実現する
	public Page<Review> findReviewsByHouseName(String name, Pageable pageable) {
		
	    List<House> houses = houseRepository.findByNameContaining(name);
	    
	    if (houses.isEmpty()) {
	        return Page.empty();
	    }
	    
	    List<Integer> houseIds = houses.stream().map(House::getId).collect(Collectors.toList());
	    
	    Page<Review> page = reviewRepository.findByHouseIdIn(houseIds, pageable);
	    
	    return page;
	    		
//	    		reviewRepository.findByHouseIdIn(houseIds, pageable);
	}

	
	// ユーザーのキーワード検索を実現する
	public Page<Review> findReviewsByUserEmail(String email, Pageable pageable) {
	    List<User> users = userRepository.findByEmailContaining(email);
	    
	    if (users.isEmpty()) {
	        return Page.empty();
	    }
	    
//	    Service層では複数のHouseやUserに対応するレビューを取得できるようになり、
//	    Controllerにて適切に判断されてビューに渡されるようになる。
	    
//	    ・users.stream(): usersリストをストリームに変換します。
//	    ・map(User::getId): 各UserオブジェクトからそのIDを抽出します。User::getIdはUserクラスのgetIdメソッド参照です。
//	    ・collect(Collectors.toList()): 抽出されたIDをリストに収集。
	    
	    List<Integer> userIds = users.stream().map(User::getId).collect(Collectors.toList());
	    return reviewRepository.findByUserIdIn(userIds, pageable);
	}

//	管理者用：店舗のレビューを非表示にする
	@Transactional
	public void undisplay(ReviewInputForm reviewInputForm) {
		
		// Debugging line
		
		Review review = reviewRepository.getReferenceById(reviewInputForm.getId());
		
		review.setDisplay(reviewInputForm.getDisplay());       
        // reviewテーブルの保存
		reviewRepository.save(review);
	}

	
//	ユーザーがすでにレビュー済みであればを判断するメソッド
	public boolean hasUserAlreadyReviewed(House house, User user) {
		// レビュー情報を取得し、特定の住宅とユーザーに関連するレビューが存在するかどうかを確認するロジックを実装
        Review reviewHouseAndUser = reviewRepository.findByHouseAndUser(house, user);

        return reviewHouseAndUser != null; // レビューが存在する場合はtrueを返す
        
	}


}