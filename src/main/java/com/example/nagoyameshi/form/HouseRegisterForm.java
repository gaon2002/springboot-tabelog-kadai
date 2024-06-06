package com.example.nagoyameshi.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HouseRegisterForm {
	
	// 登録フィールド設定
	
	@NotBlank(message = "店舗名を入力してください。")
	private String name;
	
	// ビューでアップロードされたファイルを受け取るフィールドをMultipartFile型で設定
	private MultipartFile imageFile;
	
	@NotBlank(message = "説明を入力してください。")
	private String description;
	
	@NotNull(message = "最大利用料金を入力してください。")
	@Min(value = 1, message = "利用料金は1円以上に設定してください")
	private Integer priceMax;
	
	@NotNull(message = "最小利用料金を入力してください。")
	@Min(value = 1, message = "利用料金は1円以上に設定してください")
	private Integer priceMin;
	
	@NotNull(message = "定員を入力してください。")
	@Min(value = 1, message = "定員は1人以上に設定してください")
	private Integer capacity;
	
	@NotBlank(message = "郵便番号を入力してください。")
	private String postalCode;
	
	@NotBlank(message = "住所を入力してください。")
	private String address;
	
	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;
	

}
