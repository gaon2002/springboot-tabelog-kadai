package com.example.nagoyameshi.entity;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity

//クラスに@Tableアノテーションをつけることで、そのエンティティにマッピング（対応づけ）されるテーブル名を指定できる。
@Table(name = "category")
//クラスに@Dataアノテーションをつけ、ゲッターやセッターなどを自動生成する
@Data

public class Category {
//	主キーには@Idおよび@GeneratedValueアノテーションをつける
// 	エンティティのフィールドに@Idアノテーションをつけることで、そのフィールドを主キーに指定できる。
	 @Id
//	@GeneratedValueアノテーションをつけ『strategy = GenerationType.IDENTITY』を指定することで、テーブル内のAUTO_INCREMENTを指定したカラム（idカラム）を利用して値を生成するようになる。
     @GeneratedValue(strategy = GenerationType.IDENTITY)
//	 各フィールドに@Columnアノテーションをつけ、対応づけるカラム名を指定
     @Column(name = "id")
     private Integer id;
 
     @Column(name = "category")
     private String category;
	
     @Column(name = "image_name")
     private String imageName;
 
 //  insertable属性とupdatable属性をfalseにすることで、データベース側(schema.sql)に設定したデフォルト値（CURRENT_TIMESTAMP）が自動的に挿入されるようになる。
     @Column(name = "created_at", insertable = false, updatable = false)
     private Timestamp createdAt;
 
     @Column(name = "updated_at", insertable = false, updatable = false)
     private Timestamp updatedAt;
     
     @ManyToMany(mappedBy = "categories")
     private Set<House> houses;
     // getters and setters
     
     
//   hashCodeおよびequalsメソッドを手動でオーバーライドすることで、エンティティ間の無限ループを防ぎ
     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         Category category = (Category) o;
         return id.equals(category.id);
     }

     @Override
     public int hashCode() {
         return Objects.hash(id);
     }
     
}

