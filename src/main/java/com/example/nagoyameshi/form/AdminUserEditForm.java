package com.example.nagoyameshi.form;

import java.sql.Date;

import com.example.nagoyameshi.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminUserEditForm {
	@NotNull
    private Integer id;
	
	@NotNull
    private Role role;  

	@NotBlank(message = "氏名を入力してください。")
    private String name;
    
    @NotBlank(message = "フリガナを入力してください。")
    private String furigana;
    
    @NotNull(message = "年齢を入力してください。")
    private Integer age;
	    
    @NotNull(message = "誕生日を入力してください。")
    private Date birthday;
    
    @NotBlank(message = "郵便番号を入力してください。")
    private String postalCode;
    
    @NotBlank(message = "住所を入力してください。")
    private String address;
    
    @NotBlank(message = "電話番号を入力してください。")
    private String phoneNumber;
    
    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "メールアドレスは正しい形式で入力してください。")
    private String email;
    
    @NotNull(message = "選択してください")
    private Integer subscribe;   
}
