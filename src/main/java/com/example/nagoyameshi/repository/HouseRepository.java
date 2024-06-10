package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.House;

//	リポジトリはデータベースにアクセスし、CRUD処理を行うインターフェース
//		インターフェース＝メソッドの名前、引数の型、戻り値の型のみを定義したもの（メソッドの具体的な処理内容は記述しない）。
//		複数のクラスに共通の機能を持たせたいときに使う
//	<House, Integer>：JpaRepository<エンティティのクラス型, 主キーのデータ型>
//	JpaRepositoryインターフェースを継承するだけで、基本的なCRUD操作を行うためのメソッドが利用可能になる。
public interface HouseRepository extends JpaRepository<House, Integer>  {
	
//	findBy○○○Like()メソッドを定義するとSQLのLIKE句と同様のクエリを実行できるようになる（「○○○」の部分には検索対象のカラム名が入る）
//	キーワード検索、並び替え(新着順・価格の安い順)あり
	
	public Page<House> findByNameLike(String nameKeyword, Pageable pageable);   
	public Page<House> findAll(Pageable pageable);  
	
    public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);  
    public Page<House> findByNameLikeOrAddressLikeOrderByPriceMinAsc(String nameKeyword, String addressKeyword, Pageable pageable);  

//  エリア検索、並び替え(新着順・価格の安い順)あり
    public Page<House> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
    public Page<House> findByAddressLikeOrderByPriceMinAsc(String area, Pageable pageable);
    
    
//  全件表示、並び替え(新着順・価格の安い順)あり
    public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);
    public Page<House> findAllByOrderByPriceMinAsc(Pageable pageable);  
    
    
//  利用金額
    public Page<House> findByPriceMinGreaterThanEqualAndPriceMaxLessThanEqualOrderByCreatedAtDesc(Integer priceMax, Integer priceMin, Pageable pageable);
    public Page<House> findByPriceMinGreaterThanEqualAndPriceMaxLessThanEqualOrderByPriceMinAsc(Integer priceMax, Integer priceMin, Pageable pageable);
//  最小利用金額指定
    public Page<House> findByPriceMinGreaterThanEqualOrderByCreatedAtDesc(Integer priceMin, Pageable pageable);
    public Page<House> findByPriceMinGreaterThanEqualOrderByPriceMinAsc(Integer priceMin, Pageable pageable);
//  最大利用金額指定
    public Page<House> findByPriceMaxLessThanEqualOrderByCreatedAtDesc(Integer priceMax, Pageable pageable);
    public Page<House> findByPriceMaxLessThanEqualOrderByPriceMinAsc(Integer priceMax, Pageable pageable);
	
//  新着5件表示
    public List<House> findTop5ByOrderByCreatedAtDesc();
}
