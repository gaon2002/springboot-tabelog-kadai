package com.example.nagoyameshi.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;



@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;        
        this.passwordEncoder = passwordEncoder;
    }    
    
//    ユーザー新規登録
    @Transactional
    public User create(SignupForm signupForm) {
        User user = new User();
             
        Integer subscribe = signupForm.getSubscribe();
        Role role = null;
        
        if (subscribe != null) {
            if (subscribe.equals(2)) {
                role = roleRepository.findByName("ROLE_PAID");
                
            }if (subscribe.equals(3)) {
                role = roleRepository.findByName("ROLE_FREE");
            }
        }
        
        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setBirthday(signupForm.getBirthday());
        user.setAge(signupForm.getAge());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
//      パスワードはハッシュ化：SpringSecurityが提供するPasswordEncoderインターフェースのencode()メソッドを利用。
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setSubscribe(subscribe);
//      メール認証が完了するまで、falseとする
        user.setEnabled(false);        
        
        return userRepository.save(user);
    }
    
//    ユーザー情報更新
    @Transactional
    public void update(UserEditForm userEditForm) {
        User user = userRepository.getReferenceById(userEditForm.getId());
        
        Integer subscribe = userEditForm.getSubscribe();
        Role role = null;
        
        if (subscribe != null) {
            if (subscribe.equals(2)) {
                role = roleRepository.findByName("ROLE_PAID");
                
            }if (subscribe.equals(3)) {
                role = roleRepository.findByName("ROLE_FREE");
            }
        }
        
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setBirthday(userEditForm.getBirthday());
        user.setAge(userEditForm.getAge());
        user.setPostalCode(userEditForm.getPostalCode());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());
        user.setRole(role);
        user.setSubscribe(subscribe);
//      メール認証が完了するまで、falseとする
        
        userRepository.save(user);
    }    
    
    
    
    // メールアドレスが登録済みかどうかをチェックする
    public boolean isEmailRegistered(String email) {
//    	メールアドレスでユーザーを検索
        User user = userRepository.findByEmail(email);  
//      メールアドレス登録済みであればtrueを返す。また、メールアドレスが登録済み（変数userがnull）でなければfalseを返す。
        return user != null;
    }    
    
    // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェック
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }     
    
    // メール認証用のページ（https://ドメイン名/signup/verify?token=生成したトークン）において、認証に成功した際に実行するメソッド
    @Transactional
    public void enableUser(User user) {
//    	ユーザーを有効にする。
        user.setEnabled(true); 
        userRepository.save(user);
    }    
    
    // メールアドレスが変更されたかどうかをチェックする
    public boolean isEmailChanged(UserEditForm userEditForm) {
        User currentUser = userRepository.getReferenceById(userEditForm.getId());
        return !userEditForm.getEmail().equals(currentUser.getEmail());      
    }  
}
