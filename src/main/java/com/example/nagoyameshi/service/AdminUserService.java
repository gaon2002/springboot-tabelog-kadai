package com.example.nagoyameshi.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.AdminUserEditForm;
import com.example.nagoyameshi.form.AdminUserRegisterForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class AdminUserService {
	
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public AdminUserService(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

//  管理者新規登録
  @Transactional
  public User create(AdminUserRegisterForm adminUserRegisterForm) {
      User user = new User();
           
      Role role = roleRepository.findByName("ROLE_ADMIN");
      
      user.setName(adminUserRegisterForm.getName());
      user.setFurigana(adminUserRegisterForm.getFurigana());
      user.setBirthday(adminUserRegisterForm.getBirthday());
      user.setAge(adminUserRegisterForm.getAge());
      user.setPostalCode(adminUserRegisterForm.getPostalCode());
      user.setAddress(adminUserRegisterForm.getAddress());
      user.setPhoneNumber(adminUserRegisterForm.getPhoneNumber());
      user.setEmail(adminUserRegisterForm.getEmail());
//    パスワードはハッシュ化：SpringSecurityが提供するPasswordEncoderインターフェースのencode()メソッドを利用。
      user.setPassword(passwordEncoder.encode(adminUserRegisterForm.getPassword()));
      user.setRole(role);
      user.setSubscribe(adminUserRegisterForm.getSubscribe());
//    メール認証が完了するまで、falseとする
      user.setEnabled(true);        
      
      return userRepository.save(user);
  }
  
  
//管理者情報更新
  @Transactional
  public void update(AdminUserEditForm adminUserEditForm) {
      Optional<User> optionalUser = userRepository.findById(adminUserEditForm.getId());
      
      if (optionalUser.isPresent()) {
          User user = optionalUser.get();

      user.setName(adminUserEditForm.getName());
      user.setFurigana(adminUserEditForm.getFurigana());
      user.setBirthday(adminUserEditForm.getBirthday());
      user.setAge(adminUserEditForm.getAge());
      user.setPostalCode(adminUserEditForm.getPostalCode());
      user.setAddress(adminUserEditForm.getAddress());
      user.setPhoneNumber(adminUserEditForm.getPhoneNumber());
      user.setEmail(adminUserEditForm.getEmail());
      user.setRole(adminUserEditForm.getRole());
      user.setSubscribe(adminUserEditForm.getSubscribe());
      
      userRepository.save(user);
      
      } else {
          // ユーザーが存在しない場合の処理
          // 例：例外を投げる、エラーメッセージをログに出力するなど
          throw new RuntimeException("User not found with id: " + adminUserEditForm.getId());
      }
  }    
  
  
// メールアドレスが登録済みかどうかをチェックする
  public boolean isEmailRegistered(String email) {
//  	メールアドレスでユーザーを検索
      User user = userRepository.findByEmail(email);  
//    メールアドレス登録済みであればtrueを返す。また、メールアドレスが登録済み（変数userがnull）でなければfalseを返す。
      return user != null;
  }    
  
// パスワードとパスワード（確認用）の入力値が一致するかどうかをチェック
  public boolean isSamePassword(String password, String passwordConfirmation) {
      return password.equals(passwordConfirmation);
  }     
  
//	メールアドレスが変更されたかどうかをチェックする
  public boolean isEmailChanged(AdminUserEditForm adminUserEditForm) {
	  Optional<User> optionalUser = userRepository.findById(adminUserEditForm.getId());

      if (optionalUser.isPresent()) {
          User currentUser = optionalUser.get();
          return !adminUserEditForm.getEmail().equals(currentUser.getEmail());
      } else {
          // ユーザーが存在しない場合の処理
          throw new RuntimeException("User not found with id: " + adminUserEditForm.getId());
      }    
  }  
	
}
