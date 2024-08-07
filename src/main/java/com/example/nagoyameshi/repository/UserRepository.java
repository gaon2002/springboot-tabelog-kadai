package com.example.nagoyameshi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
//	ユーザーID = emailで検索
	public User findByEmail(String email);

//	ユーザー検索とページネーション
	public Page<User> findByNameLikeOrFuriganaOrEmailLike(String nameKeyword, String furiganaKeyword,String emailKeyword,Pageable pageable);
	
//	CSV化のためのユーザー検索
	public List<User> findByNameLikeOrFuriganaOrEmailLike(String nameKeyword, String furiganaKeyword,String emailKeyword);
	
//	Userテーブルに保管したStripeのCustomer IDを取得 (UserController：success()メソッドで使用
	public User findByRememberToken(String CustomerId);

	public User getReferenceByName(String name);

//	レビュー一覧の店舗検索に使用（ReviewService）
    public List<User> findByEmailContaining(String email);
    
    
//	ROLEがADMINのデータを抜き出す
	public List<User> findByRoleId(int i);
	
	
	@EntityGraph(attributePaths = {"role"})
    Optional<User> findWithRoleById(Integer id);


}
