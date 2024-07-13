//Stripe関連の支払処理をまとめるページ

package com.example.nagoyameshi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class StripeService {
	
	    private final UserRepository userRepository;    

	    public StripeService(UserRepository userRepository){
	        this.userRepository = userRepository; 
	        
	    }
	    
//	    APIキーをStripe.apiKeyに設定する処理を、メソッド内に入れるのではなく、サービスクラスの初期化時に行うのが適切
	    @PostConstruct
	    public void init() {
	        // StripeのAPIキーを設定
	        Stripe.apiKey = stripeApiKey;
	    }

	    // Spring FrameworkやSpring BootなどのJavaベースのフレームワークで使用されるアノテーション
		// ・Springの依存性注入機能を使用して、外部の構成ファイルや環境変数から値を注入するために使用される
		// ・"${stripe.api-key}"：外部の構成ファイルや環境変数から取得したい値を指定するプレースホルダー
		// ・stripe.api-key：プロパティキーで、外部の構成ファイル（例えば、application.propertiesやapplication.yml）や環境変数にこのキーに対応する値が設定されていることを期待している
		// 		Springは自動的にstripe.api-keyに対応する値を取得し、@Valueアノテーションが付けられたフィールドやメソッドの引数に注入する
		@Value("${stripe.api-key}")
		// stripeApiKeyフィールドに外部の構成から取得されたAPIキーを注入する
		private String stripeApiKey;

		// ユーティリティメソッドを追加
		private boolean hasText(String str) {
		    return str != null && !str.trim().isEmpty();
		}
		
     // セッションを作成し、Stripeに必要な情報を返す
     public String createCheckoutSession(User user, HttpServletRequest httpServletRequest, HttpServletResponse response) throws StripeException{
    	
//      リクエストのURLを取得(ビューのエンドポイントが表示される：Request URL: http://localhost:8080/user/create-checkout-session)
 		String requestUrl = new String(httpServletRequest.getRequestURL());
    	 
 		Customer customer = null;
 		
 		// 顧客IDの検証
 	    String rememberToken = user.getRememberToken();
 		
 		if (hasText(rememberToken)){
 			// 既存の顧客IDを使用
 	        try {
 	            customer = Customer.retrieve(rememberToken);
 	        } catch (Exception e) {

 	        }
 	        
 	    } 
 		
 		if (customer == null) {
 	        // 新しい顧客を作成
 	        CustomerCreateParams customerParams = 
 	            CustomerCreateParams.builder()
 	                .setName(user.getName())
 	                .setEmail(user.getEmail())
 	                .build();
 	        
 	        try {
 	            customer = Customer.create(customerParams);
 	            user.setRememberToken(customer.getId());
 	            userRepository.save(user);
 	        } catch (Exception e) {
 	            throw e;
 	        }
 	    }
		
//      Checkout セッションを作成する
        SessionCreateParams params =
            SessionCreateParams.builder()
//				支払方法を設定(CARD)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
//              支払いアイテムを追加：（数量と価格IDを設定）
                .addLineItem(
                		 SessionCreateParams.LineItem.builder()
                		 .setQuantity(1L)
                         .setPrice("price_1PYKCpRuhzAsDRtmGB6tlo5b")
                         .build())
                 
//               支払モードを購読（Subscription）に設定
                 .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                 
                 .setCustomer(customer.getId())
                 .setClientReferenceId(user.getId().toString())
                 
//               支払成功時のリダイレクトURL
                 .setSuccessUrl(requestUrl.replace("/create-checkout-session", "") + "/success?session_id={CHECKOUT_SESSION_ID}")
//               支払いキャンセル時のリダイレクトURL
                 .setCancelUrl(requestUrl.replace("/create-checkout-session", "") + "/error") // 支払情報の再登録できるようにボタン設定
                 
                 .build();
         
//       Stripe APIを呼び出してセッションを作成
//        ・上記で作成したparameterをsessionに格納する、及び他の必要な情報が格納される
//        ・ここで支払い画面を表示するURLも作成(getUrl()で取得できる)
         Session session;
         try {
             session = Session.create(params);
         } catch (StripeException e) {
             throw e;
         }
         
         return session.getUrl(); // リダイレクトURLを返す
     } 
     
//	解約時のメソッド
     public void cancelSubscription(String customerId) throws StripeException {
         try {
             // 顧客IDからサブスクリプションを取得
             System.out.println("cancelSubscription()メソッドスタート");

             SubscriptionCollection subscriptions = Subscription.list(
                 SubscriptionListParams.builder()
                     .setCustomer(customerId)
                     .setStatus(SubscriptionListParams.Status.ACTIVE)
                     .build()
             );

             if (subscriptions == null) {
                 System.out.println("subscriptionsはnullです");
                 throw new IllegalStateException("サブスクリプションが見つかりませんでした。");
             }

             if (subscriptions.getData().isEmpty()) {
                 System.out.println("サブスクリプションが空です");
                 throw new IllegalStateException("サブスクリプションが見つかりませんでした。");
             }

             // サブスクリプションの情報を出力
             System.out.println("subscriptions：" + subscriptions);

             // 1つの顧客に複数のサブスクリプションがある場合、適切なサブスクリプションを選択するロジックを追加
             Subscription subscription = subscriptions.getData().get(0);

             subscription.cancel();

         } catch (StripeException e) {
             System.out.println("StripeExceptionが発生しました：" + e.getMessage());
             throw e;
         } catch (Exception e) {
             System.out.println("その他の例外が発生しました：" + e.getMessage());
             throw new RuntimeException("サブスクリプションのキャンセル中にエラーが発生しました。", e);
         }
     }
     
     
//	支払い方法の削除メソッド
     public void deletePaymentMethods(String customerId) throws StripeException {
         // 顧客に関連するすべての支払い方法を取得
         PaymentMethodListParams listParams = PaymentMethodListParams.builder()
             .setCustomer(customerId)
             .setType(PaymentMethodListParams.Type.CARD)
             .build();

         PaymentMethodCollection paymentMethods = PaymentMethod.list(listParams);

         for (PaymentMethod paymentMethod : paymentMethods.getData()) {
             // 各支払い方法を削除
             paymentMethod.detach();
         }
     }
     
     
//   カード情報更新メソッド
     
     public void updatePaymentMethod(String customerId, String paymentMethodId) throws StripeException {
    	    // PaymentMethodをカスタマーにアタッチ
    	    PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
    	    PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
    	        .setCustomer(customerId)
    	        .build();
    	    paymentMethod.attach(attachParams);

    	    // カスタマーのデフォルト支払い方法を更新
    	    Customer customer = Customer.retrieve(customerId);
    	    CustomerUpdateParams params = CustomerUpdateParams.builder()
    	        .setInvoiceSettings(
    	            CustomerUpdateParams.InvoiceSettings.builder()
    	                .setDefaultPaymentMethod(paymentMethodId)
    	                .build()
    	        )
    	        .build();
    	    customer.update(params);
    	}
     
//   カード情報表示
     public PaymentMethod getDefaultPaymentMethod(String customerId) {
         try {
        	 
             Customer customer = Customer.retrieve(customerId);
             String defaultPaymentMethodId = customer.getInvoiceSettings().getDefaultPaymentMethod();
             
             if (defaultPaymentMethodId != null) {
                 return PaymentMethod.retrieve(defaultPaymentMethodId);
             }
         } catch (StripeException e) {
             // エラーログ記録やエラーハンドリング
         }
         return null;
     }
     
}
