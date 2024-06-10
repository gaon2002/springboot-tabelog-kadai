package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
//	ユーザーID = emailで検索
	public User findByEmail(String email);

//	ユーザー検索とページネーション
	public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);

}
