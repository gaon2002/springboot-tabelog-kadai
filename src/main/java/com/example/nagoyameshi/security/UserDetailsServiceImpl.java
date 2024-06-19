//ユーザー情報の取得、UserDetailsImplクラスのインスタンスを生成など、ビジネスロジックを担当

package com.example.nagoyameshi.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;


@Service
//	UserDetailsServiceインターフェースを実装
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;    
    
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;        
    }
    
    @Cacheable("users")
    @Override
//  UserDetailsServiceインターフェースで定義されている抽象メソッドはloadUserByUsername()のみ
//    ※UserDetailsServiceImplクラスの役割は、UserDetailsImplクラスのインスタンスを生成すること
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {  
        try {
//        	ユーザーID(email)で対象データを取得：ログイン時のアドレス入力フォームから取得
            User user = userRepository.findByEmail(email);
            
//          対象ユーザーのroleを取得
            String userRoleName = user.getRole().getName();
            
//          空の権限リストを作成
//            Collection<GrantedAuthority>：
//            ・Spring Securityで認可を管理するために使用されるGrantedAuthorityインターフェースを実装するオブジェクトのコレクションを定義。
//            ・GrantedAuthorityは、ユーザーの権限（ロールや権限）を表す。
//            new ArrayList<>()：
//            ・ユーザーに関連する権限（ロール）を保持するための空のリストを作成
//            ・ArrayListという具体的な実装クラスのインスタンスを作成
            Collection<GrantedAuthority> authorities = new ArrayList<>();     
            
//          ユーザーのロールをGrantedAuthorityオブジェクトとしてリストに追加します。
//            ・authorities：上で作成したGrantedAuthorityオブジェクトのコレクション
//            ・authorities.add()でこのコレクションに新しい要素を追加
            authorities.add(new SimpleGrantedAuthority(userRoleName));
            

            
//          ユーザー情報と権限リストをUserDetailsImplオブジェクトとして返す
            return new UserDetailsImpl(user, authorities);
            
        } catch (Exception e) {
            throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
        }
    }   
}