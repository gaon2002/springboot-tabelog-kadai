package com.example.nagoyameshi.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRegisterForm {
	
	// 登録フィールド設定
	
	@NotBlank(message = "カテゴリー名を入力してください。")
	private String category;
	
	// ビューでアップロードされたファイルを受け取るフィールドをMultipartFile型で設定
	private MultipartFile imageFile;

}
