package com.example.nagoyameshi.form;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

// レビュー更新用データ(フォームクラス)

@Data
@AllArgsConstructor		// 全フィールドに値をセットするための引数付きコンストラクタを自動生成できる
//	コンストラクタが必要な理由：
//		新規にデータを登録するフォームクラスはコントローラーに空のインスタンスを生成し、渡すだけでよい
//		更新の場合はすでに存在するデータを更新するので、どのデータを更新するかという情報が必要
//		UI向上のため、現行データを表示したいので、各フィールドに更新前の値がセットされたインスタンスを生成しビューに渡すため
//		そのため、全フィールドに値をセットするための引数付きコンストラクタが必要となっている

public class ReviewEditForm {
	
	// 編集用のフォームクラスではどのデータを更新するか明確にするため、id用のフィールド設定も必要
	@NotNull
	private Integer id;
	
	@NotNull(message = "評価の星の数を選択してください")
	private Integer score;
	
	@NotBlank(message = "店舗の評価コメントを入力してください。")
	private String comment;

	public Integer display;


}
