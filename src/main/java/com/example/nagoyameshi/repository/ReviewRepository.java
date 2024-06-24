package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.dto.HouseScoreAvg;
import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer>{
	
	public List<Review> findTop6ByHouseAndDisplayOrderByCreatedAtDesc(House house, int display);

	public Page<Review> findAllByHouse(House house, Pageable pageable);
	
//  レビュー一覧：複数のHouse IDに対応するレビューを取得
    public Page<Review> findByHouseIdIn(List<Integer> houseIds, Pageable pageable);
//	レビュー一覧：複数のUser IDに対応するレビューを取得
//  ・findByは検索を意味する
//  ・UserIdInは特定のフィールド（この場合はuserId）が指定されたリストのいずれかに含まれることを意味する。
//  ・したがって、このメソッドは、userIdフィールドが指定されたIDリストに含まれるReviewエンティティを検索する。
    public Page<Review> findByUserIdIn(List<Integer> userIds, Pageable pageable);

	public long countByHouse(House house);

	public Review findByHouseAndUser(House house, User user);
	
//	店舗のスコア平均を計算する
//	 @Queryアノテーションが必要な理由：Spring Data JPAが自動的にクエリメソッドを生成する際、
//	 クエリの結果の型とメソッドの戻り値の型が一致しない場合や、複雑なクエリが必要な場合に明示的にJPQLクエリを指定するためです。
	@Query("SELECT AVG(r.score) FROM Review r WHERE r.house.id = :houseId")
	public Double findAverageScoreByHouseId(@Param("houseId") Integer houseId);
	
	
//	「人気の高い順」に並べ替えるためのカスタムクエリの追加
	@Query("SELECT new com.example.nagoyameshi.dto.HouseScoreAvg(r.house.id, AVG(r.score)) " +
	           "FROM Review r GROUP BY r.house.id ORDER BY AVG(r.score) DESC")
//	HouseScoreAvgDTOというDTOクラスを定義して、結果を保持
	public List<HouseScoreAvg> findTop5HouseAverageScoresOrderedByScore();


}

