package com.example.nagoyameshi.service;


import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.form.UserPasswordChangeForm;
import com.example.nagoyameshi.repository.PasswordResetTokenRepository;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;



@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;        
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
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
    
//  ユーザー情報更新
    @Transactional
    public void update(UserEditForm userEditForm) {
        Optional<User> optionalUser = userRepository.findById(userEditForm.getId());
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Integer subscribe = userEditForm.getSubscribe();
            Role role = null;

            if (subscribe != null) {
                if (subscribe.equals(2)) {
                    role = roleRepository.findByName("ROLE_PAID");
                } else if (subscribe.equals(3)) {
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
        
        userRepository.save(user);
        
        } else {
            // ユーザーが存在しない場合の処理
            // 例：例外を投げる、エラーメッセージをログに出力するなど
            throw new RuntimeException("User not found with id: " + userEditForm.getId());
        }
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
    
//	メールアドレスが変更されたかどうかをチェックする
    public boolean isEmailChanged(UserEditForm userEditForm) {
        User currentUser = userRepository.getReferenceById(userEditForm.getId());
        return !userEditForm.getEmail().equals(currentUser.getEmail());      
    }  
    
//  パスワード変更
    @Transactional
    public boolean changePassword(Integer userId, UserPasswordChangeForm userPasswordChangeForm) {
        User user = userRepository.getReferenceById(userId);
        
        if (user == null || !passwordEncoder.matches(userPasswordChangeForm.getCurrentPassword(), user.getPassword())) {
            return false;
        }

//      パスワードはハッシュ化：SpringSecurityが提供するPasswordEncoderインターフェースのencode()メソッドを利用。
        user.setPassword(passwordEncoder.encode(userPasswordChangeForm.getNewPassword()));
        
        userRepository.save(user);
        return true;
    }
    
    
//  ユーザーとパスワードリセットトークンの処理を行うサービス層を実装。
    public void createPasswordResetTokenForUser(String email, String token) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("ユーザーが見つかりません。");
        }
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(calculateExpiryDate(24 * 60));
        passwordResetTokenRepository.save(myToken);
    }

    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public boolean updatePassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        if ((resetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return false;
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
        return true;
    }

 // トークンの有効期限を計算するメソッド
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();	// 現在の日時を取得
        cal.setTime(new Date());	// 現在の日時をカレンダーに設定
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);	// 有効期限を分単位で追加
        return new Date(cal.getTime().getTime());	// 有効期限の日時を返す
    }
}


