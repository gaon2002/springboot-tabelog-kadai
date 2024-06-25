package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
//  お気に入り一覧に表示するコマンド
    public Page<House> findByIdIn(List<Integer> houseList, Pageable pageable);
    
//	レビュー一覧の店舗検索に使用（ReviewService）
    public List<House> findByNameContaining(String name);
    
//  人気の高い順に並べ替えする際のキーワード検索
//    HouseRepositoryでスコアの並べ替えを考える必要はない。ReviewRepositoryから取得した結果を使うだけ。
//    	・SELECT h FROM House：Houseエンティティからhというエイリアス（別名）を使ってデータを選択。（= Houseテーブルからデータを取得する）
//    	・h WHERE：条件文　（以下の時にマッチする）
//    		-h.name LIKE :name：h.name（名前）が指定されたnameパラメータに一致するか
//    		-OR：もしくは
//    		-h.address LIKE :h.address（住所）が指定されたaddressパラメータに一致する場合
//    	・AND：さらに条件追加
//    		-h.id：h.idが指定されたidsパラメータに含まれる場合にマッチ
//    		-IN :ids：複数のIDを含むコレクション（例えばリストやセット）の中からマッチするh.idを取得
    @Query("SELECT h FROM House h WHERE (h.name LIKE :name OR h.address LIKE :address) AND h.id IN :ids")
    public Page<House> findByNameLikeOrAddressLikeAndIdIn(
        @Param("name") String name, 
        @Param("address") String address, 
        @Param("ids") List<Integer> ids,
        Pageable pageable);
    
//  リストを文字列化して渡すことでクエリに埋め込む。
//	・SELECT h FROM House：Houseエンティティからhというエイリアス（別名）を使ってデータを選択。（= Houseテーブルからデータを取得する）
//	・h WHERE：条件文　（以下の時にマッチする）
//		-h.id：h.idが指定されたidsパラメータに含まれる場合にマッチ
//		-IN :ids：複数のIDを含むコレクション（例えばリストやセット）の中からマッチするh.idを取得
    @Query("SELECT h FROM House h WHERE h.id IN :ids")
    public Page<House> findAllByIdsIn(
        @Param("ids") List<Integer> ids,
        Pageable pageable);
    
    
    @Query("SELECT h FROM House h WHERE (h.priceMin >= :priceMin AND h.priceMax <= :priceMax) AND h.id IN :ids")
    public Page<House> findByPriceMinGreaterThanEqualAndPriceMaxLessThanEqual(
        @Param("priceMax") Integer priceMax, 
        @Param("priceMin") Integer priceMin, 
        @Param("ids") List<Integer> ids,
        Pageable pageable);
    
    @Query("SELECT h FROM House h WHERE (h.priceMin >= :priceMin) AND h.id IN :ids")
    public Page<House> findByPriceMinGreaterThanEqual(
        @Param("priceMin") Integer priceMin, 
        @Param("ids") List<Integer> ids,
        Pageable pageable);
    
    @Query("SELECT h FROM House h WHERE (h.priceMax <= :priceMax) AND h.id IN :ids")
    public Page<House> findByPriceMaxLessThanEqual(
        @Param("priceMax") Integer priceMax, 
        @Param("ids") List<Integer> ids,
        Pageable pageable);

}
