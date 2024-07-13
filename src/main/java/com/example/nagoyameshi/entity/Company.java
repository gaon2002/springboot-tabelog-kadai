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

//クラスに@Tableアノテーションをつけることで、そのエンティティにマッピング（対応づけ）されるテーブル名を指定できる。
@Table(name = "company")
//クラスに@Dataアノテーションをつけ、ゲッターやセッターなどを自動生成する
@Data

public class Company {
//	主キーには@Idおよび@GeneratedValueアノテーションをつける
//	エンティティのフィールドに@Idアノテーションをつけることで、そのフィールドを主キーに指定できる。
	@Id
//	@GeneratedValueアノテーションをつけ『strategy = GenerationType.IDENTITY』を指定することで、テーブル内のAUTO_INCREMENTを指定したカラム（idカラム）を利用して値を生成するようになる。
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	 各フィールドに@Columnアノテーションをつけ、対応づけるカラム名を指定
	@Column(name = "id")
	private Integer id;

	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "principle")
	private String principle;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "establishment")
	private String establishment;
	
	@Column(name = "capital_stock")
	private String capitalStock;
	
	@Column(name = "business")
	private String business;
	
	@Column(name = "number_of_member")
	private String numberOfMember;
	
	@Column(name = "phone_number")
	private String phoneNumber;

//  insertable属性とupdatable属性をfalseにすることで、データベース側(schema.sql)に設定したデフォルト値（CURRENT_TIMESTAMP）が自動的に挿入されるようになる。
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
   
   
}
