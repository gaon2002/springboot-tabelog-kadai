//Eventクラスの役割はListenerクラスにイベントが発生したことを知らせる、またイベントに関する情報も保持できる。

package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEvent;

import com.example.nagoyameshi.entity.User;

import lombok.Getter;

//Eventクラスはイベントに関する情報を保持するので、外部（具体的にはListenerクラス）からそれらの情報を取得できるようにゲッターを定義する。
@Getter

//ApplicationEventクラスを継承：イベントを作成するための基本的なクラスで、イベントのソース（発生源）などを保持
public class SignupEvent extends ApplicationEvent {
	
//	このイベントは会員登録したユーザーの情報（Userオブジェクト）とリクエストを受けたURL（https://ドメイン名/signup）を保持する。
    private User user;
    private String requestUrl;        

    public SignupEvent(Object source, User user, String requestUrl) {
//    	superでスーパークラス（親クラス）のコンストラクタを呼び出し、イベントのソース（source：発生源）を渡す。
//    	イベントのソースとは、具体的にはPublisherクラスのインスタンスのこと。
        super(source);
        
        this.user = user;        
        this.requestUrl = requestUrl;
    }
}