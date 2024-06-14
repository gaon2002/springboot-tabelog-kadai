package com.example.nagoyameshi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.HouseRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class FavoriteService {
	
    @Autowired
    private FavoriteRepository favoriteRepository; // お気に入り情報をデータベースに保存するためのリポジトリ
    
    public FavoriteService(HouseRepository houseRepository, FavoriteRepository favoriteRepository, UserRepository userRepository) {
    	this.favoriteRepository = favoriteRepository;
    }

//  ■お気に入り登録
    @Transactional
    public void subscribe(House house, User user) {
        // ここでお気に入り情報をデータベースに保存する処理を実装します
        // 例えば、favoriteRepositoryを使用してデータベースに新しいお気に入り情報を登録するなど
    	Favorite favorite = new Favorite();
    	
    		favorite.setHouse(house);
    		favorite.setUser(user);
    		favoriteRepository.save(favorite);
    }

    
//  ■お気に入り登録されているかどうかを判断
	public boolean hasUserAlreadyFavorited(House house, User user) {
		// レビュー情報を取得し、特定の住宅とユーザーに関連するレビューが存在するかどうかを確認するロジックを実装
		
        Favorite favoriteHouseAndUser = favoriteRepository.findByHouseAndUser(house, user);

        return favoriteHouseAndUser != null; // レビューが存在する場合はtrueを返す
        
	}
	
//	■お気に入り解除
	@Transactional
	public void unsubscribe(House house, User user) {
	    Favorite favorite = favoriteRepository.findByHouseAndUser(house, user);
	    if (favorite != null) {
	        favoriteRepository.delete(favorite);
	    }
	}

}
