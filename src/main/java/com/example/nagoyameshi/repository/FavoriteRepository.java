package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.entity.User;


@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
	
	public Page<Favorite> findAllByUser(User user, Pageable pageable);
	
//	house_id と user_idが存在するもの
	public Favorite findByHouseAndUser(House house, User user);

	public List<Favorite> findByUserId(Integer userId);
	

}

