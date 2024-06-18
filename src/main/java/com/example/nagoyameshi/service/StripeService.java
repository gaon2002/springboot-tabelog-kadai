//Stripe関連の支払処理をまとめるページ

package com.example.nagoyameshi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {
	
	    private final UserRepository userRepository;    

	    public StripeService(UserRepository userRepository) {
	        this.userRepository = userRepository; 
	    }

	    // Spring FrameworkやSpring BootなどのJavaベースのフレームワークで使用されるアノテーション
		// ・Springの依存性注入機能を使用して、外部の構成ファイルや環境変数から値を注入するために使用される
		// ・"${stripe.api-key}"：外部の構成ファイルや環境変数から取得したい値を指定するプレースホルダー
		// ・stripe.api-key：プロパティキーで、外部の構成ファイル（例えば、application.propertiesやapplication.yml）や環境変数にこのキーに対応する値が設定されていることを期待している
		// 		Springは自動的にstripe.api-keyに対応する値を取得し、@Valueアノテーションが付けられたフィールドやメソッドの引数に注入する
		@Value("${stripe.api-key}")
		// stripeApiKeyフィールドに外部の構成から取得されたAPIキーを注入する
		private String stripeApiKey;
		
     // セッションを作成し、Stripeに必要な情報を返す
     public String createCheckoutSession(User user) throws StripeException{
//    	 APIのシークレットキーを記述
    	 Stripe.apiKey = stripeApiKey;
//       リクエストのURLを取得
//         String requestUrl = new String(httpServletRequest.getRequestURL());
    	 
    	 // 顧客作成のパラメーターを構築
    	 CustomerCreateParams customerParams = 
    	     CustomerCreateParams.builder()
	            .setName(user.getName())
	            .setEmail(user.getEmail())
	            .build();
    	            
	    // Stripe上に顧客を作成
	    Customer customer = Customer.create(customerParams);

	    // 顧客IDをユーザーテーブルにセット＆保存 (事前に顧客IDのフィールドがあることを前提)
	    	user.setRememberToken(customer.getId());
			userRepository.save(user);
    	 
         
//       セッション作成のためのパラメーターを構築
         SessionCreateParams params =
             SessionCreateParams.builder()
//             	  支払方法を設定(CARD)
                 .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
//                支払いアイテムを追加：（数量と価格IDを設定）
                 .addLineItem(
                		 SessionCreateParams.LineItem.builder()
                		 .setQuantity(1L)
                         .setPrice("price_1PR7FQRuhzAsDRtmL2QSoXi7")
                         .build())
                 
//               セッションモードを購読（Subscription）に設定
                 .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                 
//               支払成功時のリダイレクトURL
                 .setSuccessUrl("http://localhost:8080/success?session_id={CHECKOUT_SESSION_ID}")
//               支払いキャンセル時のリダイレクトURL
                 .setCancelUrl("http://localhost:8080/cancel")
                 
                 .build();
         try {
//        	 Stripe APIを呼び出してセッションを作成
             Session session = Session.create(params);
//           成功時には作成されたセッションのIDを返す
             
             System.out.println("Stripe Session ID: " + session.getId()); // デバッグ用ログ
             
             return session.getId();
             
         } catch (StripeException e) {
//        	 StripeExceptionが発生した場合、スタックトレースを出力し、空文字列を返す
             e.printStackTrace();
             return "";
         }
     } 
     
//   解約時のメソッド
     public void cancelSubscription(String subscriptionId) throws StripeException {
    	 Subscription subscription = Subscription.retrieve(subscriptionId);
    	 	subscription.cancel();
     }
     
//   カード情報更新メソッド
     public void updatePaymentMethod(String customerId, String paymentMethodId) throws StripeException {
         CustomerUpdateParams params = CustomerUpdateParams.builder()
                 .setInvoiceSettings(CustomerUpdateParams.InvoiceSettings.builder()
                         .setDefaultPaymentMethod(paymentMethodId).build())
                 .build();
         Customer.retrieve(customerId).update(params);
     }
}
