package com.example.nagoyameshi.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
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
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
//      パスワードはハッシュ化：SpringSecurityが提供するPasswordEncoderインターフェースのencode()メソッドを利用。
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setSubscribe(signupForm.getSubscribe());
        user.setEnabled(true);        
        
        return userRepository.save(user);
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
}
