//Publisherクラス：コントローラなど、イベントを発生させたい処理（このアプリではAuthControllerのsignup()メソッド）の中で呼び出して使う。

package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;


//@ComponentアノテーションをつけてDIコンテナに登録し、呼び出すクラス（今回はコントローラ）に対して依存性の注入（DI）を行えるようにする。
@Component
public class SignupEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;                
    }
    

    public void publishSignupEvent(User user, String requestUrl) {
    	
//      イベントを発行するには、ApplicationEventPublisherインターフェースが提供するpublishEvent()メソッドを使う   
//    		・SignupEvent()イベントの第1引数には、自分自身（this）のインスタンスを渡している：このインスタンスがイベントのソース（発生源）として渡される
//    	AuthControllerクラスのsignup()メソッド（POST）内でこのメソッドを呼び出し、ユーザーの会員登録が完了したタイミングでイベントを発行する
        applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
    }
}