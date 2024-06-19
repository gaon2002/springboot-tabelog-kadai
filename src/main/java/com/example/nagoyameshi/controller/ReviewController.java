package com.example.nagoyameshi.controller;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewInputForm;
import com.example.nagoyameshi.repository.HouseRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReviewService;



@Controller

public class ReviewController {
	
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	private final HouseRepository houseRepository;
	
	public ReviewController(UserRepository userRepository, ReviewRepository reviewRepository, ReviewService reviewService, HouseRepository houseRepository) {
		this.reviewRepository = reviewRepository;
		this.reviewService = reviewService;
		this.houseRepository = houseRepository;
	}
	
	// 店舗レビュー一覧の表示（10件ずつ表示）
	// reviews.html
	@GetMapping("houses/{id}/reviews")
	// @AuthenticationPrincipalアノテーションを使用し、認証されたユーザーの情報をUserDetailsオブジェクトとして受け取る。
	public String review(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, @PathVariable(name = "id") Integer id, Model model) {

//    	Optional型を用いてエンティティの存在チェックを行う
        Optional<House> houseOpt = houseRepository.findById(id);
        if (!houseOpt.isPresent()) {
            // ハウスが見つからない場合のエラーハンドリングを追加する
            return "error/404"; // 例: 404ページにリダイレクト
        }
        
        House house = houseOpt.get();
		
		Page<Review> houseReviews = reviewRepository.findAllByHouse(house, pageable);
		
		model.addAttribute("house", house);
		model.addAttribute("houseReview", houseReviews);
		
		return "reviews/index";
		
	}
	
// 管理者用店舗レビュー一覧の表示（10件ずつ表示）
// admin/reviews.html
	@GetMapping("admin/reviews")
	// @AuthenticationPrincipalアノテーションを使用し、認証されたユーザーの情報をUserDetailsオブジェクトとして受け取る。
	public String review(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {

		Page<Review> houseReviews = reviewRepository.findAll(pageable);
		
		model.addAttribute("houseReviews", houseReviews);
		
		return "admin/reviews/index";
		
	}
	
// 管理者用店舗レビュー一覧でレビューを非表示にする
	
	// admin/reviews/index.html
	// 非表示(display=1)にしたレビューを更新するメソッド：更新情報をreviewService(Repositoryを使ってデータ更新)に送信、エラーがあればビューに表示する
	@PostMapping("/admin/reviews/{reviewId}/update")
	public String undisplay(@PathVariable("id") Integer id,
							@ModelAttribute @Validated ReviewEditForm reviewEditForm, 
							BindingResult bindingResult, 
							RedirectAttributes redirectAttributes) {
		
		
		if (bindingResult.hasErrors()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "入力にエラーがあります。");
	        return "redirect:/admin/reviews";
	    }
		
		
		reviewService.undisplay(reviewEditForm);
		
		redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
		
		return "redirect:/admin/reviews";
	}
	
	
//	reviewInput.html
//	レビューの新規投稿➀：ビューにReviewInputFormのインスタンスを渡す
	@GetMapping("houses/{id}/reviews/reviewInput")
    public String register(@PathVariable(name = "id") Integer id, Model model) {
		
//    	Optional型を用いてエンティティの存在チェックを行う
        Optional<House> houseOpt = houseRepository.findById(id);
        if (!houseOpt.isPresent()) {
            // ハウスが見つからない場合のエラーハンドリングを追加する
            return "error/404"; // 例: 404ページにリダイレクト
        }
        
        House house = houseOpt.get();
		
		
		
		model.addAttribute("house", house);
		model.addAttribute("reviewInputForm", new ReviewInputForm());
        
       
        
        return "reviews/reviewInput";
    }    

	
//	reviewInput.html
//	レビューの新規投稿➁：インプット画面から情報を入手し、DBを更新する(ReviewService)
//	メソッド：ビューで入力されたデータをReviewInputFormで受け取る
	@PostMapping("houses/{id}/reviews/input")	//@GetMappingはメソッドとGETの処理を行うURLを紐づける役割。
	public String create(@PathVariable(name = "id") Integer id, 
						 @ModelAttribute @Validated ReviewInputForm reviewInputForm,
						 BindingResult bindingResult,
						 RedirectAttributes redirectAttributes,
						 @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						 Model model) {
				
		
		 reviewService.create(id, reviewInputForm, userDetailsImpl);
		
		 Optional<House> houseOpt = houseRepository.findById(id);
	        if (!houseOpt.isPresent()) {
	            // ハウスが見つからない場合のエラーハンドリングを追加する
	            return "error/404"; // 例: 404ページにリダイレクト
	        }
	        
	        House house = houseOpt.get();
		 
		 model.addAttribute("house", house);
		 
		 redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");  
		 
		 return "redirect:/houses/{id}";
		 
	}
	
	
	
	// reviewEdit.html
	// レビュー更新の元情報を表示する 
	@GetMapping("/houses/{id}/reviews/{reviewId}/reviewEdit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,@PathVariable(name = "id") Integer id,@PathVariable(name = "reviewId") Integer reviewId, Model model) {
		// ReviewgetReferenceById()で情報を最新にする。
		
		Optional<House> houseOpt = houseRepository.findById(id);
        if (!houseOpt.isPresent()) {
            // ハウスが見つからない場合のエラーハンドリングを追加する
            return "error/404"; // 例: 404ページにリダイレクト
        }
        
        House house = houseOpt.get();

        
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (!reviewOpt.isPresent()) {
            // ハウスが見つからない場合のエラーハンドリングを追加する
            return "error/404"; // 例: 404ページにリダイレクト
        }
        
        Review review = reviewOpt.get();
        
		
		// すでに情報が登録されているため、UserEditFormをインスタンス化し、どこに何の情報を入れるかをクリアにする
		// どこに入れるかはedit.htmlの*{}で指定(表示も入力もできるthymeleaf要素)
		ReviewEditForm reviewEdit = new ReviewEditForm(review.getId(), review.getScore(), review.getComment(), review.getDisplay());
		
		model.addAttribute("house", house);
		model.addAttribute("review", review);
		model.addAttribute("reviewEdit", reviewEdit);
		
		
//		■テンプレートへのパス指定で動的な部分をそのまま使用するのではなく、正しいテンプレートファイルの場所を静的な文字列で指定する必要があります。
//		　例）テンプレートファイルがsrc/main/resources/templates/reviews/reviewEdit.htmlにある場合は、下記のように書く必要があります
		return "reviews/reviewEdit";
	}
	
	
	// reviewEdit.html
	// 更新したレビューを更新するメソッド：エラーがなければ更新情報をuserService(Repositoryを使ってデータ更新)に送信、エラーがあればビューに表示する
	@PostMapping("/houses/{id}/reviews/{reviewId}/update")
	public String update(@ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		// エラーがあればインプットフォームにエラーを表示する
		
		if(bindingResult.hasErrors()) {
			return "reviews/reviewEdit";
		}
		
		reviewService.update(reviewEditForm);

		
		redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
		
		return "redirect:/houses/{id}";
	}
	
	
	
	// レビューの削除（DBからも削除する）
	@PostMapping("/houses/{id}/reviews/{reviewId}/delete")
	// show.htmlで削除が選択されたデータのidを引数で受け取る
	public String delete(@PathVariable(name = "reviewId") Integer reviewId, RedirectAttributes redirectAttributes) {
//		reviewRepositoryを使ってデータのCRUD処理を行う、deleteById(受け取った引数)メソッドで削除
		
		reviewRepository.deleteById(reviewId);
		
		redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
		
		return "redirect:/houses/{id}";
	}
	
	
}