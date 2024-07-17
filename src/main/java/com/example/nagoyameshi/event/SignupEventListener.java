//このListenerクラスの役割は、SignupEventクラスからの通知を受け、メール認証用のメールを送信すること。

package com.example.nagoyameshi.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.VerificationTokenService;

//ListenerクラスのインスタンスがDIコンテナに登録されるアノテーション
//	→@EventListenerアノテーションがついたメソッドをSpring Boot側が自動的に検出し、イベント発生時に実行する。
@Component
public class SignupEventListener {
     private final VerificationTokenService verificationTokenService;    
     private final JavaMailSender javaMailSender;
     
     public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
         this.verificationTokenService = verificationTokenService;        
         this.javaMailSender = mailSender;
     }
 
//   @EventListenerアノテーションをつけたメソッドはイベント発生時に実行される。
//     →「どのイベントの発生時か」を指定するため、通知を受け付けるEventクラスをメソッドの引数に設定。
     @EventListener
     
//   SignupEventから発生の通知を受けた際に以下のメソッドが実行される。
     private void onSignupEvent(SignupEvent signupEvent) {
    	 
//    	 各ユーザーのtokenを作成し、verification_tokensデータに保管
         User user = signupEvent.getUser();
//       tokenをUUIDで生成
         String token = UUID.randomUUID().toString();
         
//       user_idとtokenを保管
         verificationTokenService.create(user, token);
         
//       メール送信の際の件名と本文を作成
         String recipientAddress = user.getEmail();
         String subject = "メール認証";
//       サインアップリクエストが行われたエンドポイントの URL を取得。これは通常、サーバー側でリクエストを処理した際の URL になる。
         String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;
         String message = "以下のリンクをクリックして会員登録を完了してください。";
         
         
         SimpleMailMessage mailMessage = new SimpleMailMessage(); 
//       メールアドレスのセット
         mailMessage.setTo(recipientAddress);
//       メールの件名をセット
         mailMessage.setSubject(subject);
//       メール本文（メッセージとtokenを含むURL）をセット
         mailMessage.setText(message + "\n" + confirmationUrl);
//       メールを送信
         try {
             javaMailSender.send(mailMessage);
         } catch (MailException ex) {
             // エラーログを出力
             System.err.println("メール送信に失敗しました: " + ex.getMessage());
         }
     }
}
