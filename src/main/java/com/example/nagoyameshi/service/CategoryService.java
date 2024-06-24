package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.repository.CategoryReository;

@Service
public class CategoryService {
	
    private final CategoryReository categoryRepository;    
    
    public CategoryService(CategoryReository categoryRepository) {
        this.categoryRepository = categoryRepository;        
    }    

    @Transactional
//  @Transactionalアノテーション： データベース操作が完全に成功するか、すべて失敗するかのどちらかに白黒はっきりさせられるため、データの整合性を保つことができる
//   ＜店舗情報登録＞
    public void create(CategoryRegisterForm CategoryRegisterForm) {
//    	エンティティ（Houseクラス）をインスタンス化
        Category category = new Category();
        
        MultipartFile imageFile = CategoryRegisterForm.getImageFile();
        
//      ファイル名の重複を防ぐためファイル名をUUIDで別名に変更する
        if (!imageFile.isEmpty()) {
//        	元のファイル名を取得
            String imageName = imageFile.getOriginalFilename(); 
//          オリジナルの画像名から、下に記述しているgenerateNewFileName()でファイル名を変更し、hashedImageNameにセット
            String hashedImageName = generateNewFileName(imageName);
//          指定したパスに画像ファイルを保管
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
//          下に記述しているcopyImageFile()メソッドでファイル名を変更しファイルをコピー処理する
            copyImageFile(imageFile, filePath);
//          houseエンティティーに処理した情報をセットする
            category.setImageName(hashedImageName);
        }
        
//      ViewからControllerを通してhouseRegisterFormに受け取った引数をインスタンス化したエンティティーの各フィールドにセットする
        category.setCategory(CategoryRegisterForm.getCategory());                

                    
        categoryRepository.save(category);
    }  
    
    
    @Transactional
//  ＜カテゴリー情報編集＞
    public void update(CategoryEditForm categoryEditForm) {
        Category category = categoryRepository.getReferenceById(categoryEditForm.getId());
        MultipartFile imageFile = categoryEditForm.getImageFile();
        
        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename(); 
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            category.setImageName(hashedImageName);
        }
        
        category.setCategory(categoryEditForm.getCategory());                

                    
        categoryRepository.save(category);
    }    
    
    
    // UUIDを使って生成したファイル名を変更し、create()メソッドに渡す
    public String generateNewFileName(String fileName) {
        String[] fileNames = fileName.split("\\.");                
        for (int i = 0; i < fileNames.length - 1; i++) {
            fileNames[i] = UUID.randomUUID().toString();            
        }
        String hashedFileName = String.join(".", fileNames);
        return hashedFileName;
    }     
    
    // 画像ファイルを指定したファイルにコピーする
    public void copyImageFile(MultipartFile imageFile, Path filePath) {           
        try {
//        	InputStreamクラス：バイト（byte）の入力ストリームを表現するための抽象クラス（入力ストリーム＝ファイルなどからデータを読み取る際のデータの流れ）。
//        	MultipartFileインターフェースが提供するメソッド。ファイルの内容を読み取るためのInputStreamオブジェクトを取得する。
            Files.copy(imageFile.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }          
    } 
	
}
