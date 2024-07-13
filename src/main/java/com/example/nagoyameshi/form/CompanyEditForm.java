package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//全フィールドに値をセットするための引数つきコンストラクタを自動生成することができます（Lombokの機能）
//	コントローラからビューにインスタンスを渡すときに、編集する民宿データの値をセットするため
@AllArgsConstructor
public class CompanyEditForm {
	
	 @NotNull
//	 データを更新する際は「どのデータを更新するか」という情報が必要です。そのため、フォームクラスにはid用のフィールドも定義
	 private Integer id;    
     
     
     @NotBlank(message = "会社名を入力してください。")
     private String companyName;
 	
     @NotBlank(message = "代表者氏名を入力してください。")
     private String principle;
 	
     @NotBlank(message = "郵便番号を入力してください。")
     private String postalCode;
 	
     @NotBlank(message = "住所を入力してください。")
     private String address;
 	
     @NotBlank(message = "設立年月日を入力してください。")
     private String establishment;
 	
     @NotBlank(message = "資本金を入力してください。")
     private String capitalStock;
 	
     @NotBlank(message = "事業内容説明を入力してください。")
     private String business;
 	
     @NotBlank(message = "従業員数を入力してください。")
     private String numberOfMember;
 	
     @NotBlank(message = "電話番号を入力してください。")
     private String phoneNumber;


}
