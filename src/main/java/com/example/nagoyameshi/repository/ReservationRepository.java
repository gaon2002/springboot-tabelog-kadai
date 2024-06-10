package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	
//	reservationsテーブルから対象のuser_idの情報を取得する
	public Page<Reservation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

}



