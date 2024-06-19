package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer>{
	
	public List<Review> findTop6ByHouseAndDisplayOrderByCreatedAtDesc(House house, int display);

	public Page<Review> findAllByHouse(House house, Pageable pageable);

	public long countByHouse(House house);

	public Review findByHouseAndUser(House house, User user);

}

