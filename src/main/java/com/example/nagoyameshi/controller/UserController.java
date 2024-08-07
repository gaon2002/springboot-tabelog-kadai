package com.example.nagoyameshi.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.form.UserPasswordChangeForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.security.UserDetailsServiceImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.model.checkout.Session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;    
    private final RoleRepository roleRepository;
    private final UserService userService; 
    private final StripeService stripeService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    
    public UserController(UserRepository userRepository, UserService userService, StripeService stripeService, RoleRepository roleRepository, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userRepository = userRepository; 
        this.userService = userService; 
        this.stripeService = stripeService;
        this.roleRepository = roleRepository;
        this.userDetailsServiceImpl = userDetailsServiceImpl;

    }
         
//  ユーザー情報の確認
    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {         
//    	userDetailsImpl.getUser().getId()で現在ログインしているユーザー（getUser()）のid（getId()）を取得
//    	userRepositoryからgetReferenceById()を使って最新情報を取得する
    	User user = userRepository.findById(userDetailsImpl.getUser().getId()).orElse(null);
        
        model.addAttribute("user", user);
        
        return "user/index";
    }
    
//  マイページ表示
    @GetMapping("/my_page")
    public String mypage(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {         
//    	userDetailsImpl.getUser().getId()で現在ログインしているユーザー（getUser()）のid（getId()）を取得
//    	userRepositoryからgetReferenceById()を使って最新情報を取得する
    	User user = userRepository.findById(userDetailsImpl.getUser().getId()).orElse(null);
        
        model.addAttribute("user", user);
        
        return "user/my_page";
    }
    
//  ユーザー情報の編集➀：フォームへ元情報をセットする
    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {        
    	User user = userRepository.findById(userDetailsImpl.getUser().getId()).orElse(null);
        
//      保存されているユーザー情報を編集フォームに入れ込むため、各フィールドの項目をインスタンス化
        UserEditForm userEditForm = new UserEditForm(user.getId(), 
        											 user.getRole(),
        											 user.getName(),
        											 user.getFurigana(),
        											 user.getAge(),
        											 user.getBirthday(), 
        											 user.getPostalCode(),
        											 user.getAddress(),
        											 user.getPhoneNumber(),
        											 user.getEmail());
        
        model.addAttribute("userEditForm", userEditForm);
        
        return "user/edit";
    }    
    
//  ユーザー情報の編集➁：編集されたユーザー情報を更新する
    @PostMapping("/update")
    public String update(@ModelAttribute @Validated UserEditForm userEditForm, 
    					 BindingResult bindingResult,
    					 RedirectAttributes redirectAttributes) 
    {

    	if (bindingResult.hasErrors()) {
    		
            return "user/edit";
        }

        try {
            userService.update(userEditForm);
            
        } catch (IllegalArgumentException e) {
            bindingResult.reject("errorMessage", e.getMessage());
            return "user/edit";
            
        } catch (Exception e) {
            bindingResult.reject("errorMessage", "情報の更新に失敗しました。");
            return "user/edit";
        }

        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        return "redirect:/user";
    }
    	
    
//  サブスク料金の支払いのための情報を作り、ビューに渡してStripeに情報を渡しつつ、Stripeを起動
    @PostMapping("/create-checkout-session")
    public String createCheckoutSession(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
    									 RedirectAttributes redirectAttributes,
    									 HttpServletRequest httpServletRequest,
    									 HttpServletResponse response,
    									 Model model) {
    	
    	User user = userDetailsImpl.getUser();
    	
        try {
//        	stripeServiceの決済セッション(createCheckoutSession())を実行のためのuser情報(付随データ)を渡す。
            String redirectUrl = stripeService.createCheckoutSession(user, httpServletRequest, response);
            
         // HttpServletResponseを使用してリダイレクトを実行
            response.sendRedirect(redirectUrl);
            
            return null;

            
        } catch (StripeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/verify";
            
        } catch (IOException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/verify";
        }
    }
    
//  サブスク料金支払処理が成功した後の処理
    @GetMapping("/success")
//  セッションIDから関連するサブスクリプション情報を取得
    @Transactional
    public String success(@RequestParam("session_id") String sessionId, 
    					  RedirectAttributes redirectAttributes, 
    					  Model model)
    {
    	try {
    		Role role = roleRepository.findByName("ROLE_PAID");
    		
//	    	StripeのセッションIDを使って特定のセッションを取得します。
	    	Session session = Session.retrieve(sessionId);
	    	
//  	  	取得したセッションから顧客IDを取得。
	        String customerId = session.getCustomer();
	        
	        User user = userRepository.findByRememberToken(customerId);
	        
	        if (user == null) {
	            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが見つかりませんでした。");
	            return "redirect:/error";
	        }
	        
	        user.setSubscriptionStartDate(LocalDate.now());
//	        user.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
	        user.setSubscribe(2);
	        user.setRole(role);
	        
	        userRepository.save(user);
	        
	        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getEmail());
	        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	        SecurityContextHolder.getContext().setAuthentication(auth);
	        
	        redirectAttributes.addFlashAttribute("successMessage", "支払処理が成功しました。");
	        return "redirect:/houses";
        
        
    	}catch (StripeException e) {
    		model.addAttribute("error", e.getMessage());
            return "error";
    	}
    }
    
//  サブスク支払いをキャンセルした場合、ユーザ画面に戻る
    @GetMapping("/cancel")
    public String cancelPayment(RedirectAttributes redirectAttributes) {
    	
    	redirectAttributes.addFlashAttribute("successMessage", "支払処理をキャンセルしました。");
        // キャンセル時に表示するビューを返す（例：cancel.html）
        return "redirect:/user";
    }
    
    
//	カード情報更新➀：このメソッドはユーザーが新しいカード情報を入力するフォームを表示
    @GetMapping("/UserUpdatePayment")

    public String updatePaymentMethodForm(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    									  Model model) {
 
    	String customerId = userDetailsImpl.getUser().getRememberToken(); // Stripeの顧客ID
        System.out.println("Customer ID: " + customerId);  //OK
        
        PaymentMethod paymentMethod = stripeService.getDefaultPaymentMethod(customerId);
        
        if (paymentMethod != null && paymentMethod.getCard() != null) {
            model.addAttribute("cardBrand", paymentMethod.getCard().getBrand());
            model.addAttribute("last4", paymentMethod.getCard().getLast4());
            model.addAttribute("expiryMonth", paymentMethod.getCard().getExpMonth());
            model.addAttribute("expiryYear", paymentMethod.getCard().getExpYear());
        } else {
            System.out.println("Payment method or card details are null.");
        }

        return "user/UserUpdatePayment";
    }

//	カード情報更新➁：新しいカード情報を受け取り、Stripeに更新リクエストを送る
    @PostMapping("/update-payment-method")
    public String updatePaymentMethod(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            						  @RequestParam String paymentMethodId,
            						  RedirectAttributes redirectAttributes,
            						  Model model) 
    {
    	
    	String customerId = userDetailsImpl.getUser().getRememberToken();
    	
        try {
            stripeService.updatePaymentMethod(customerId, paymentMethodId);
            redirectAttributes.addFlashAttribute("successMessage", "支払情報が更新されました。");
            return "redirect:/houses";
            
        } catch (StripeException e) {
        	
         	if (e.getStripeError() != null) {
                System.out.println("Stripe Error: " + e.getStripeError().getMessage());
                System.out.println("Stripe Error Code: " + e.getStripeError().getCode());
                System.out.println("Stripe Error Type: " + e.getStripeError().getType());
            }
        	model.addAttribute("errorMessage", "支払情報の更新に失敗しました。再試行してください。");
            return "user/UserUpdatePayment";
        }
    }
    
    
//  サブスク解除
    @GetMapping("/cancel-subscription")
    public String cancelSubscription(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    								 RedirectAttributes redirectAttributes,
    								 Model model)
    {
    	
    	User user = userDetailsImpl.getUser();
        String customerId = user.getRememberToken();
        Role role = roleRepository.findByName("ROLE_FREE");
        
        try {
//        	Stripeの解除メソッド呼び出し
            stripeService.cancelSubscription(customerId);
            stripeService.deletePaymentMethods(customerId);
            
//          サブスク開始年月日をnullにする
            user.setRole(role);
            user.setSubscribe(3);
            user.setRememberToken(null);
            user.setSubscriptionStartDate(null);
            user.setSubscriptionEndDate(LocalDate.now());
            
            userRepository.save(user);
            
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getEmail());
	        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	        SecurityContextHolder.getContext().setAuthentication(auth);
            
            redirectAttributes.addFlashAttribute("successMessage", "有料会員が解除されました。");
            return "redirect:/user";
            
        } catch (StripeException e) {
        	redirectAttributes.addFlashAttribute("errorMessage", "エラーが発生しました。");
            return "redirect:/user";
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
      
     
//    退会確認画面表示
      @GetMapping("/confirm_withdraw")
      public String confirmWithdraw(Model model) {
          // 必要に応じてモデルにデータを追加
          return "user/confirm_withdraw"; // Thymeleafテンプレートの名前
      }
      
//	  有料会員ユーザー情報のの削除（DBからも削除する）
      @PostMapping("/PAIDdelete")
      public String paid_delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    		  			   HttpServletRequest request, HttpServletResponse response,
    		  			   RedirectAttributes redirectAttributes)
      {
    	User user = userDetailsImpl.getUser();
    	String customerId = user.getRememberToken();
        
        try {
//        	Stripeの解除メソッド呼び出し
            stripeService.cancelSubscription(customerId);
            stripeService.deletePaymentMethods(customerId);
  		
            userRepository.deleteById(user.getId());
  		
//		ログアウト処理
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
  		
  		redirectAttributes.addFlashAttribute("successMessage", "退会しました。");
  		
  		return "redirect:/?loggedOut";
  		
        } catch (StripeException e) {
        	redirectAttributes.addFlashAttribute("errorMessage", "エラーが発生しました。");
            return "redirect:/user";
        }
  	}
      
	  @PostMapping("/FREEdelete")
      public String free_delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
              HttpServletRequest request, HttpServletResponse response,
              RedirectAttributes redirectAttributes) 
      {
		try {
			// reviewRepositoryを使ってデータのCRUD処理を行う、deleteById(受け取った引数)メソッドで削除
			Integer user = userDetailsImpl.getUser().getId();
			userRepository.deleteById(user);
			
			// ログアウト処理
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				new SecurityContextLogoutHandler().logout(request, response, auth);
			}
				redirectAttributes.addFlashAttribute("successMessage", "退会しました。");
				return "redirect:/?loggedOut";
				
			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("errorMessage", "エラーが発生しました。");
				return "redirect:/user";
			}
      }

}
