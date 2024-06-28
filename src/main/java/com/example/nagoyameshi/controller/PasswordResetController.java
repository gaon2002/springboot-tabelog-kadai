package com.example.nagoyameshi.controller;

import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.form.UserPasswordResetForm;
import com.example.nagoyameshi.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;

@Controller
public class PasswordResetController {
	
	private UserService userService;
	private JavaMailSender mailSender;
	
	public PasswordResetController (UserService userService, JavaMailSender mailSender) {
		this.userService = userService;
		this.mailSender = mailSender;
	}

//  パスワードのリセット➀：フォーム送信
    @GetMapping("/auth/UserNewPasswordIssue")
    public String showForgotPasswordForm() {
    	 System.out.println("パスワードリセット画面の表示を試みます。"); // デバッグメッセージ
        return "auth/UserNewPasswordIssue";
    }

//  パスワードリセット➁：パスワードを再発行し、メールを送信
    @PostMapping("/auth/UserNewPasswordIssue")
    public String processForgotPassword(String email, RedirectAttributes redirectAttributes) {
//    	token作成
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(email, token);

//      リセットのためのtokenのついたURLを作成
        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        try {
//        	メール送信：メール内容は、下のsendEmail()で実行
            sendEmail(email, resetUrl);
            redirectAttributes.addFlashAttribute("successMessage", "パスワード再発行リンクをメールで送信しました。");
        } catch (MessagingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "メール送信に失敗しました。");
        }

        return "redirect:/auth/UserNewPasswordIssue";
    }

//  パスワードリセット③：パスワードを再発行のためのメール発行・送信
    private void sendEmail(String email, String resetUrl) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(email);
        helper.setSubject("NAGOYAMESHI：パスワード再発行リンク");
        helper.setText("以下のリンクをクリックしてパスワードを再発行してください: " + resetUrl);

        mailSender.send(message);
    }
    
    
//  パスワード再発行➀：ユーザーがパスワード再発行リンクをクリックすると、新しいパスワードを設定するためのフォームを表示
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
    	
    	System.out.println("パスワード再発行画面の表示を試みます。"); // デバッグメッセージ
    	
        PasswordResetToken resetToken = userService.getPasswordResetToken(token);
        if (resetToken == null) {
            model.addAttribute("message", "無効なトークンです。");
            return "redirect:/login";
        }
        model.addAttribute("token", token);
        model.addAttribute("userPasswordResetForm", new UserPasswordResetForm());
        return "auth/UserResetPassword";
    }

//  パスワード再発行➁：ユーザーがパスワード再発行リンクをクリックすると、新しいパスワードを設定するためのフォームを表示
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @Valid UserPasswordResetForm userPasswordResetForm,
                                       BindingResult result,
                                       RedirectAttributes redirectAttributes) {
    	
    	System.out.println("パスワード再発行を試みます。"); // デバッグメッセージ
    	
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "フォームにエラーがあります。");
            return "redirect:/reset-password?token=" + token;
        }
        
        boolean success = userService.updatePassword(token, userPasswordResetForm.getNewPassword());
        
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "パスワードを更新しました。");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "パスワードの更新に失敗しました。");
            return "redirect:/reset-password?token=" + token;
        }
    }

}
