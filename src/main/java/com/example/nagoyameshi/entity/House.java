package com.example.nagoyameshi.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity

//	クラスに@Tableアノテーションをつけることで、そのエンティティにマッピング（対応づけ）されるテーブル名を指定できる。
@Table(name = "houses")
//	クラスに@Dataアノテーションをつけ、ゲッターやセッターなどを自動生成する
@Data
public class House {
//	主キーには@Idおよび@GeneratedValueアノテーションをつける
// 	エンティティのフィールドに@Idアノテーションをつけることで、そのフィールドを主キーに指定できる。
	 @Id
//	@GeneratedValueアノテーションをつけ『strategy = GenerationType.IDENTITY』を指定することで、テーブル内のAUTO_INCREMENTを指定したカラム（idカラム）を利用して値を生成するようになる。
     @GeneratedValue(strategy = GenerationType.IDENTITY)
//	 各フィールドに@Columnアノテーションをつけ、対応づけるカラム名を指定
     @Column(name = "id")
     private Integer id;
 
     @Column(name = "name")
     private String name;
 
     @Column(name = "image_name")
     private String imageName;
 
     @Column(name = "description")
     private String description;
 
     @Column(name = "price_max")
     private Integer priceMax;
     
     @Column(name = "price_min")
     private Integer priceMin;
 
     @Column(name = "capacity")
     private Integer capacity;
 
     @Column(name = "postal_code")
     private String postalCode;
 
     @Column(name = "address")
     private String address;
 
     @Column(name = "phone_number")
     private String phoneNumber;
 
//  insertable属性とupdatable属性をfalseにすることで、データベース側(schema.sql)に設定したデフォルト値（CURRENT_TIMESTAMP）が自動的に挿入されるようになる。
     @Column(name = "created_at", insertable = false, updatable = false)
     private Timestamp createdAt;
 
     @Column(name = "updated_at", insertable = false, updatable = false)
     private Timestamp updatedAt;
     
     
//     Houseエンティティで@OneToMany関係を定義し、そのフェッチタイプをLAZYにしている場合、セッションが閉じられた後にそのプロパティにアクセスしようとすると、LazyInitializationExceptionが発生します。この問題を解決するために、以下の方法を検討できます。
     
//     @OneToMany(mappedBy = "house")
//     private Set<HousesCategory> houseCategories = new HashSet<>();

 

}
