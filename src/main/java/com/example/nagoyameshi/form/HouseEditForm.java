package com.example.nagoyameshi.form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.custom.NotEmptyList;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//	全フィールドに値をセットするための引数つきコンストラクタを自動生成することができます（Lombokの機能）
//		コントローラからビューにインスタンスを渡すときに、編集する民宿データの値をセットするため
@AllArgsConstructor
public class HouseEditForm {

	 @NotNull
//	 データを更新する際は「どのデータを更新するか」という情報が必要です。そのため、フォームクラスにはid用のフィールドも定義
     private Integer id;    
     
     @NotBlank(message = "店舗名を入力してください。")
     private String name;
         
     private MultipartFile imageFile;
     
     @NotBlank(message = "説明を入力してください。")
     private String description;   
     
     @NotNull(message = "最大利用料金を入力してください。")
     @Min(value = 1, message = "利用料金は1円以上に設定してください。")
     private Integer priceMax; 
     
     @NotNull(message = "最小利用料金を入力してください。")
     @Min(value = 1, message = "利用料金は1円以上に設定してください。")
     private Integer priceMin; 
     
     @NotNull(message = "定員を入力してください。")
     @Min(value = 1, message = "定員は1人以上に設定してください。")
     private Integer capacity;       
     
     @NotBlank(message = "郵便番号を入力してください。")
     private String postalCode;
     
     @NotBlank(message = "住所を入力してください。")
     private String address;
     
     @NotBlank(message = "電話番号を入力してください。")
     private String phoneNumber;
     
 	@NotEmptyList(message = "カテゴリを選択してください。")
     private List<Integer> categoryIds;
 	
}
