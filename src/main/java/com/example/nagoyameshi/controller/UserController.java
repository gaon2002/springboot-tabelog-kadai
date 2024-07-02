package com.example.nagoyameshi.controller;

import java.time.LocalDate;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.form.UserPasswordChangeForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;    
    private final UserService userService; 
    private final StripeService stripeService;
    
    public UserController(UserRepository userRepository, UserService userService, StripeService stripeService) {
        this.userRepository = userRepository; 
        this.userService = userService; 
        this.stripeService = stripeService;
    }
         
//  ユーザー情報の確認
    @GetMapping
    @Transactional
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {         
//    	userDetailsImpl.getUser().getId()で現在ログインしているユーザー（getUser()）のid（getId()）を取得
//    	userRepositoryからgetReferenceById()を使って最新情報を取得する
    	User user = userRepository.findById(userDetailsImpl.getUser().getId()).orElse(null);
        
        model.addAttribute("user", user);
        
        return "user/index";
    }
    
//  ユーザー情報の編集➀：フォームへ元情報をセットする
    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {        
    	User user = userRepository.findById(userDetailsImpl.getUser().getId()).orElse(null);
        
//      保存されているユーザー情報を編集フォームに入れ込むため、各フィールドの項目をインスタンス化
        UserEditForm userEditForm = new UserEditForm(user.getId(), user.getRole(), user.getName(), user.getFurigana(), user.getAge(), user.getBirthday(), user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail(),user.getSubscribe());
        
        model.addAttribute("userEditForm", userEditForm);
        
        return "user/edit";
    }    
    
//  ユーザー情報の編集➁：編集されたユーザー情報を更新する
    @PostMapping("/update")
    public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);                       
        }
        
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }
        
//      userServiceのupdate()メソッドを実行
        userService.update(userEditForm);
        
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        
//      user/index.htmlに戻って表示
        return "redirect:/user";
    }    
    
//  サブスク料金の支払いのための情報を作り、ビューに渡してStripeに情報を渡しつつ、Stripeを起動
    @PostMapping("/create-checkout-session")
    public String createCheckoutSession(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
    									 RedirectAttributes redirectAttributes,
    									 HttpServletRequest httpServletRequest,
    									 Model model) {
    	
    	System.out.println("createCheckoutSession method called"); //　【後で消す】メソッドが呼び出されているか
    	
    	User user = userDetailsImpl.getUser();
    	
    	System.out.println(user);	//【後で消す】userの中身確認
    	
        try {
//        	stripeServiceの決済セッション(createCheckoutSession())を実行のためのuser情報(付随データ)を渡す。
            String sessionId = stripeService.createCheckoutSession(user, httpServletRequest);
            
            System.out.println("Generated Session ID: " + sessionId); // 【後で消す】デバッグ用ログ
            
            // フロントエンドにセッションIDを渡す
            model.addAttribute("sessionId", sessionId);
            
            return "auth/verify";

            
            
        } catch (StripeException e) {
            // エラーハンドリング
        	model.addAttribute("errorMessage", e.getMessage());
            return "auth/verify";
        }
    }
    
//  サブスク料金支払処理が成功した後の処理
    @GetMapping("/success")
//  セッションIDから関連するサブスクリプション情報を取得
    public String success(@RequestParam("session_id") String sessionId, Model model) {
    	try {
//	    	StripeのセッションIDを使って特定のセッションを取得します。
	    	Session session = Session.retrieve(sessionId);
//  	  	取得したセッションから顧客IDを取得。
	        String customerId = session.getCustomer();
	        
	        User user = userRepository.findByRememberToken(customerId);
	        
	        user.setSubscriptionStartDate(LocalDate.now());
	        user.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
	        
	        userRepository.save(user);
	        
	        return "success";
        
        
    	}catch (StripeException e) {
    		model.addAttribute("error", e.getMessage());
            return "error";
    	}
    }
    
//    パスワード変更➀：パスワード変更画面の表示
      @GetMapping("/UserPasswordChange")
      @Transactional
      public String showChangePasswordForm(Model model) {
    	  
          model.addAttribute("UserPasswordChangeForm", new UserPasswordChangeForm());
          return "user/UserPasswordChange";
      }

//    パスワード変更➁入力された現行パスワードと新パスワードを受け取り、処理
      @PostMapping("/change-password")
      @Transactional
      public String changePassword(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                                   @Valid UserPasswordChangeForm userPasswordChangeForm,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model)
      {
          if (bindingResult.hasErrors()) {
        	  model.addAttribute("UserPasswordChangeForm", userPasswordChangeForm); 
              return "user/UserPasswordChange";
          }

          boolean success = userService.changePassword(userDetailsImpl.getUser().getId(), userPasswordChangeForm);
          if (!success) {
              model.addAttribute("errorMessage", "現在のパスワードが正しくありません。");
              model.addAttribute("UserPasswordChangeForm", userPasswordChangeForm); 
              return "user/UserPasswordChange";
          }

          redirectAttributes.addFlashAttribute("successMessage", "パスワードが変更されました。");
//          model.addAttribute("UserPasswordChangeForm", new UserPasswordChangeForm()); // 成功時に新しいフォームオブジェクトを設定
          return "redirect:/houses";
      }

}
