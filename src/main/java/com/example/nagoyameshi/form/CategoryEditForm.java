package com.example.nagoyameshi.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//全フィールドに値をセットするための引数つきコンストラクタを自動生成することができます（Lombokの機能）
//	コントローラからビューにインスタンスを渡すときに、編集する民宿データの値をセットするため
@AllArgsConstructor
public class CategoryEditForm {
	
	 @NotNull
//	 データを更新する際は「どのデータを更新するか」という情報が必要です。そのため、フォームクラスにはid用のフィールドも定義
     private Integer id;    
     
     @NotBlank(message = "カテゴリー名を入力してください。")
     private String category;
         
     private MultipartFile imageFile;
     

}
