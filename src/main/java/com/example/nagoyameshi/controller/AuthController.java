package com.example.nagoyameshi.controller;
//認証機能（ログイン、会員登録）用のコントローラ


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.event.SignupEventPublisher;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.service.UserService;
import com.example.nagoyameshi.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
	
    private final UserService userService;    
//  イベント発生させた際に渡すクラス
    private final SignupEventPublisher signupEventPublisher;
    
    private final VerificationTokenService verificationTokenService;
    
    public AuthController(UserService userService, SignupEventPublisher signupEventPublisher, VerificationTokenService verificationTokenService) {      
        this.userService = userService;        
        this.signupEventPublisher = signupEventPublisher;
        this.verificationTokenService = verificationTokenService;
    }  
	
	
//	"http://yourdomain.com/login"にアクセスすると、このメソッドが実行される。
    @GetMapping("/login")
    public String login() {        
//    	login()メソッドが実行されたときに、auth/loginという名前のビュー（HTMLページ）を返す。
        return "auth/login";
    }
    
    @GetMapping("/signup")
    public String signup(Model model) {        
        model.addAttribute("signupForm", new SignupForm());
        return "auth/signup";
    }    
    
    @PostMapping("/signup")
//    引数でHttpServletRequestオブジェクトを受け取る
    public String signup(@ModelAttribute @Validated SignupForm signupForm,
    					 BindingResult bindingResult,
    					 Model model,
    					 RedirectAttributes redirectAttributes,
    					 HttpServletRequest httpServletRequest) {     
    	
        // メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (userService.isEmailRegistered(signupForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);                       
        }    
        
        // パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
        if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }        
        
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }
        
//        userService.create(signupForm);
//        redirectAttributes.addFlashAttribute("successMessage", "会員登録が完了しました。");
        
        User createdUser = userService.create(signupForm);
//      EventPublisherに渡すURLを取得：イベントを発生させるビューページのURLを取得
        String requestUrl = new String(httpServletRequest.getRequestURL());
//      メール認証イベントを発行する：SignupEventPublisherクラスのpublishSignupEvent()を実行
        signupEventPublisher.publishSignupEvent(createdUser, requestUrl);
        
        model.addAttribute ("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");  

        return "auth/verify"; 
    }    
    
//  メール認証用URL（/signup/verify）を受け取りUrl内のtokenと、verificationToken内のtokenが一致するかを確認し、ユーザーを有効化する
    @GetMapping("/signup/verify")
    
    public String verify(@RequestParam(name = "token") String token, Model model) {
    	
//    	URLに記載しているtokenをもとに、verificationToken内のtoken文字列を取得
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        
//      取得出来ていたら、verificationTokenはnullにならない（tokenが存在する）
        if (verificationToken != null) {
//        	tokenからuser_idを取得
            User user = verificationToken.getUser();  
//          対象のユーザーを有効にするメソッドを実行
            userService.enableUser(user);
            String successMessage = "会員登録が完了しました。";
            model.addAttribute("successMessage", successMessage);            
        } else {
            String errorMessage = "トークンが無効です。";
            model.addAttribute("errorMessage", errorMessage);
        }
        
//      会員用の店舗一覧に戻りたい
        return "auth/verify";         
    }    
}
