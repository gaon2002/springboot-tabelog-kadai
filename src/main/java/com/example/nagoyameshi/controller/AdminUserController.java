package com.example.nagoyameshi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.AdminUserEditForm;
import com.example.nagoyameshi.form.AdminUserRegisterForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.AdminUserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/administrator")
public class AdminUserController {
    private final UserRepository userRepository;        
    private final AdminUserService adminUserService;
    
    public AdminUserController(UserRepository userRepository, AdminUserService adminUserService ) {
        this.userRepository = userRepository;       
        this.adminUserService = adminUserService;
    }    
    
//  管理者一覧表示
    @GetMapping("/list")
    public String index(Model model) {
    	
            List<User> adminUsers = userRepository.findByRoleId(1);
            
            model.addAttribute("adminUsers", adminUsers);
            
            return "admin/administrator/index";
        }
    
//  管理者マイページ表示
    @GetMapping("/my_page")
    public String mypage(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = null;
        try {
            user = userRepository.findById(userDetailsImpl.getUser().getId()).orElse(null);
            if (user == null) {
                model.addAttribute("errorMessage", "ユーザー情報が見つかりませんでした。");
                return "admin/administrator/error"; // エラーページのテンプレート名
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "ユーザー情報の取得に失敗しました。");
            return "admin/administrator/error"; // エラーページのテンプレート名
        }

        model.addAttribute("user", user);
        return "admin/administrator/my_page"; // 正しいテンプレートのパス
    }
        
//  管理者詳細表示
    @GetMapping("/detail/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model) {
        
        User user = userRepository.findById(id).orElse(null);
        
        model.addAttribute("user", user);
        
        return "admin/administrator/show";
    }    
    
    
//  管理者新規登録➀：ユーザー登録フォームをビューに送信
    @GetMapping("/register")
    public String signup(Model model) {        
        model.addAttribute("adminUserRegisterForm", new AdminUserRegisterForm());
        return "admin/administrator/register";
    }    
    
//  管理者新規登録➁：ユーザー登録フォームから情報を受け取りテーブルに保管
    @PostMapping("/register")
//    引数でHttpServletRequestオブジェクトを受け取る
    public String signup(@ModelAttribute @Validated AdminUserRegisterForm adminUserRegisterForm,
    					 BindingResult bindingResult,
    					 Model model,
    					 RedirectAttributes redirectAttributes,
    					 HttpServletRequest httpServletRequest) {     
    	
        // メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (adminUserService.isEmailRegistered(adminUserRegisterForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);                       
        }    
        
        // パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
        if (!adminUserService.isSamePassword(adminUserRegisterForm.getPassword(), adminUserRegisterForm.getPasswordConfirmation())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
            bindingResult.addError(fieldError);
        }        
        
        if (bindingResult.hasErrors()) {
        	
        	model.addAttribute("adminUserRegisterForm", adminUserRegisterForm);
            return "admin/administrator/register";
        }
        
        adminUserService.create(adminUserRegisterForm);

        redirectAttributes.addFlashAttribute("successMessage", "新規に管理者を登録しました。");	
        
        return "redirect:/admin/administrator/list";
    }    
    
//  管理者情報の編集➀：フォームへ元情報をセットする
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") Integer id, Model model) {    
    	
    	Optional<User> optionalUser = userRepository.findById(id);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // 保存されているユーザー情報を編集フォームに入れ込むため、各フィールドの項目をインスタンス化
            AdminUserEditForm adminUserEditForm = new AdminUserEditForm(
                user.getId(),
                user.getRole(),
                user.getName(),
                user.getFurigana(),
                user.getAge(),
                user.getBirthday(),
                user.getPostalCode(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getSubscribe()
            );

            model.addAttribute("adminUserEditForm", adminUserEditForm);
            
            return "admin/administrator/edit";
            
        } else {
            // ユーザーが存在しない場合のエラーハンドリング
            model.addAttribute("errorMessage", "User not found with id: " + id);
            return "error/404"; // 適切なエラーページにリダイレクト
        }
    }
    
//  管理者情報の編集➁：編集された管理者情報を受け取り更新する
    @PostMapping("/update")
    public String update(@ModelAttribute @Validated AdminUserEditForm adminUserEditForm,
    					 BindingResult bindingResult,
    					 RedirectAttributes redirectAttributes,
    					 Model model) {
    	
        // メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (adminUserService.isEmailChanged(adminUserEditForm) && adminUserService.isEmailRegistered(adminUserEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);                       
        }
        
        if (bindingResult.hasErrors()) {
//        	エラーメッセージを表示するためにモデルにエラーメッセージを追加
        	model.addAttribute("adminUserEditForm", adminUserEditForm);
        	
            return "admin/administrator/edit";
        }
        
//      userServiceのupdate()メソッドを実行
        adminUserService.update(adminUserEditForm);
        
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        
//      admin/administrator/list.htmlに戻って表示
        return "redirect:/admin/administrator/list";
    }    
    
//	 ＜管理者情報削除＞
	 @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
//		 対象idの情報を削除
        userRepository.deleteById(id);
                
        redirectAttributes.addFlashAttribute("successMessage", "店舗情報を削除しました。");
        
//      削除が成功したら店舗一覧に成功メッセージを表示
        return "redirect:/admin/administrator/list";
    }    
    
}