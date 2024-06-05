package com.example.nagoyameshi.repository;

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
	public Page<House> findByNameLike(String keyword, Pageable pageable);

}
