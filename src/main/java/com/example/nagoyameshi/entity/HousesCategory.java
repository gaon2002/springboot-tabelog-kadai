package com.example.nagoyameshi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity

//クラスに@Tableアノテーションをつけることで、そのエンティティにマッピング（対応づけ）されるテーブル名を指定できる。
@Table(name = "houses_category")
//クラスに@Dataアノテーションをつけ、ゲッターやセッターなどを自動生成する
@Data
public class HousesCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "house_id")
	private House house;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	
    // コンストラクタ
    public HousesCategory() {
    }

    public HousesCategory(Integer id, House house, Category category) {
        this.id = id;
        this.house = house;
        this.category = category;
    }
}
