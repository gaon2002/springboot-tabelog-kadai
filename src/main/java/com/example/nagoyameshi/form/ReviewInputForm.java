package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// レビュー新規入力用フォーム

@Data

public class ReviewInputForm {
	
	// 登録フィールド設定
	@NotNull(message = "idを入力してください")
	private Integer id;

	@NotNull(message = "評価の星の数を選択してください")
	private Integer score;
	
	@NotBlank(message = "民宿の評価コメントを入力してください。")
	private String comment;
	
	private Integer display;

}
