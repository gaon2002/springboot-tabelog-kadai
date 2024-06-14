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
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpServletRequest;

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
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {         
//    	userDetailsImpl.getUser().getId()で現在ログインしているユーザー（getUser()）のid（getId()）を取得
//    	userRepositoryからgetReferenceById()を使って最新情報を取得する
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
        
        model.addAttribute("user", user);
        
        return "user/index";
    }
    
//  ユーザー情報の編集：フォームへ元情報をセットする
    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {        
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
        
//      保存されているユーザー情報を編集フォームに入れ込むため、各フィールドの項目をインスタンス化
        UserEditForm userEditForm = new UserEditForm(user.getId(), user.getRole(), user.getName(), user.getFurigana(), user.getAge(), user.getBirthday(), user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail(),user.getSubscribe());
        
        model.addAttribute("userEditForm", userEditForm);
        
        return "user/edit";
    }    
    
//    編集されたユーザー情報を更新する
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
    
//  サブスク料金の支払い
    @PostMapping("/create-checkout-session")
    public String createCheckoutSession(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, HttpServletRequest httpServletRequest, Model model) {
    	User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
    	
        try {
//        	stripeServiceの決済セッション(createCheckoutSession())を実行
            String sessionId = stripeService.createCheckoutSession(user, httpServletRequest);
            // フロントエンドにセッションIDを渡す
            model.addAttribute("sessionId", sessionId);
            
            return "redirect:/houses/index"; 
            
        } catch (StripeException e) {
            // エラーハンドリング
            model.addAttribute("error", e.getMessage());
            return "error";
        }
        
    }
    
//  サブスク料金支払処理が成功した後の処理
    @GetMapping("/success")
    public String success(@RequestParam("sessionId") String sessionId, Model model) {
        // セッションIDからユーザーを特定し、サブスクリプション情報を更新
        // 省略
        
        Intgeger userId = ...; // セッションIDからユーザーIDを取得
        User user = userRepository.findById(userId).orElseThrow();
        user.setSubscriptionStartDate(LocalDate.now());
        user.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
        
        userRepository.save(user);

        return "success";
    }

}
