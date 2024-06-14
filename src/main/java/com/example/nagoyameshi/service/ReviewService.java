package com.example.nagoyameshi.service;

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
				
		review.setHouse(house);
		review.setUser(user);
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
	

	public boolean hasUserAlreadyReviewed(House house, User user) {
		// レビュー情報を取得し、特定の住宅とユーザーに関連するレビューが存在するかどうかを確認するロジックを実装
        Review reviewHouseAndUser = reviewRepository.findByHouseAndUser(house, user);

        return reviewHouseAndUser != null; // レビューが存在する場合はtrueを返す
        
	}
	
	
}