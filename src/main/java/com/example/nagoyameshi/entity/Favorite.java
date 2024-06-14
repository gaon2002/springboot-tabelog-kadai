package com.example.nagoyameshi.entity;

import java.sql.Timestamp;

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
@Table(name = "favorites")
@Data

public class Favorite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "house_id")
	private House house;
	
	// @ManyToOne：1つのエンティティ（テーブル）が他のエンティティと多対1の関係を持つことを示す
	// 「予約」を基準に考える。予約は1人に対して多数存在し得るため「多」、予約が持てるユーザーは1人だけのため「一」、よって「多対一」の関係
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
	
}
