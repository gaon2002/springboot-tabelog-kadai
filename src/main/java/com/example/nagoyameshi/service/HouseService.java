package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.dto.HouseScoreAvg;
import com.example.nagoyameshi.dto.HouseScoreDto;
import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.form.HouseEditForm;
import com.example.nagoyameshi.form.HouseRegisterForm;
import com.example.nagoyameshi.repository.HouseRepository;
import com.example.nagoyameshi.repository.ReviewRepository;



@Service
public class HouseService {
    private final HouseRepository houseRepository;
    private final ReviewRepository reviewRepository;  

    
    public HouseService(HouseRepository houseRepository, ReviewRepository reviewRepository) {
        this.houseRepository = houseRepository;    
        this.reviewRepository = reviewRepository;
    }    
    
    @Transactional
//  @Transactionalアノテーション： データベース操作が完全に成功するか、すべて失敗するかのどちらかに白黒はっきりさせられるため、データの整合性を保つことができる
//   ＜店舗情報登録＞
    public void create(HouseRegisterForm houseRegisterForm) {
//    	エンティティ（Houseクラス）をインスタンス化
        House house = new House();
        
        MultipartFile imageFile = houseRegisterForm.getImageFile();
        
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
            house.setImageName(hashedImageName);
        }
        
//      ViewからControllerを通してhouseRegisterFormに受け取った引数をインスタンス化したエンティティーの各フィールドにセットする
        house.setName(houseRegisterForm.getName());                
        house.setDescription(houseRegisterForm.getDescription());
        house.setPriceMax(houseRegisterForm.getPriceMax());
        house.setPriceMin(houseRegisterForm.getPriceMin());
        house.setCapacity(houseRegisterForm.getCapacity());
        house.setPostalCode(houseRegisterForm.getPostalCode());
        house.setAddress(houseRegisterForm.getAddress());
        house.setPhoneNumber(houseRegisterForm.getPhoneNumber());
                    
        houseRepository.save(house);
    }  
    
    @Transactional
//  ＜店舗情報編集＞
    public void update(HouseEditForm houseEditForm) {
        House house = houseRepository.getReferenceById(houseEditForm.getId());
        MultipartFile imageFile = houseEditForm.getImageFile();
        
        if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename(); 
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            house.setImageName(hashedImageName);
        }
        
        house.setName(houseEditForm.getName());                
        house.setDescription(houseEditForm.getDescription());
        house.setPriceMax(houseEditForm.getPriceMax());
        house.setPriceMin(houseEditForm.getPriceMin());
        house.setCapacity(houseEditForm.getCapacity());
        house.setPostalCode(houseEditForm.getPostalCode());
        house.setAddress(houseEditForm.getAddress());
        house.setPhoneNumber(houseEditForm.getPhoneNumber());
                    
        houseRepository.save(house);
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
    
//  houseRepositoryとreviewRepositoryを使用してデータを取得（TOPページに表示）
    public List<HouseScoreDto> getHousesOrderedByAverageScore() {
//    	レビューリポジトリからスコアTop5の店舗を取得
        List<HouseScoreAvg> houseScores = reviewRepository.findHouseAverageScoresOrderedByScore();

        List<HouseScoreDto> houseDtos = new ArrayList<>();
        
        for (HouseScoreAvg houseScore : houseScores) {
            House house = houseRepository.findById(houseScore.getHouseId()).orElse(null);
            if (house != null) {
                HouseScoreDto dto = new HouseScoreDto();
                dto.setHouseId(house.getId());
                dto.setName(house.getName());
                dto.setImageName(house.getImageName());
                dto.setDescription(house.getDescription());
                dto.setPriceMax(house.getPriceMax());
                dto.setPriceMin(house.getPriceMin());
                dto.setCapacity(house.getCapacity());
                dto.setPostalCode(house.getPostalCode());
                dto.setAddress(house.getAddress());
                dto.setPhoneNumber(house.getPhoneNumber());
                dto.setAverageScore(houseScore.getAverageScore());

                houseDtos.add(dto);
            }
        }

        return houseDtos;
    }
   

//  houseIdごとにscoreの平均を計算し、HouseScoreAvgに格納
    public Double getHouseAverageScore(Integer houseId) {
        return reviewRepository.findAverageScoreByHouseId(houseId);
    }

//  Reviewリポジトリの実装（house.idでグループ化し、scoreの平均を算出して、高い順に並べ替え）
    public List<HouseScoreAvg> getHousesOrderedByAverageScoreSorted() {
        return reviewRepository.findHouseAverageScoresSortedOrderedByScore();
    }

//  キーワード検索後に並べ替えするメソッド
    public Page<House> getHouseAverageScoreSorted(String keyword, String address, Pageable pageable) {
    	// 1. Houseエンティティのリストを平均スコアでソート(ReviewRepositoryのメソッド呼び出し)して取得
        List<HouseScoreAvg> houseScores = getHousesOrderedByAverageScoreSorted();
//      ・houseScoresリストからHouseのIDのみを抽出し、新しいリストidsに格納
//        -houseScores.stream()：houseScoresリストをストリームに変換。
//        	※ストリームは、データのシーケンス（順序のある一連の要素）を処理するための抽象化であり、様々な操作（フィルタリング、マッピング、ソートなど）をサポート。
        List<Integer> ids = houseScores.stream()
//        		・ストリームの各要素（HouseScoreAvgオブジェクト）に対してgetHouseIdメソッドを適用し、新しいストリームを生成。
//        		 -mapメソッド：元のストリームの各要素に対して関数を適用し、その結果を新しいストリームとして返す。
//        		  この新しいストリームは、元のHouseScoreAvgオブジェクトの代わりにhouseId（Integer型のID）を含む。
//        		  (HouseScoreAvg::getHouseId)：HouseScoreAvgクラスのインスタンスメソッドgetHouseIdを参照
                .map(HouseScoreAvg::getHouseId)
//              ・map()で作成された新しいストリームの要素をListに変換し、リストに収集する。
//                ※collectメソッド：ストリームの終端操作で、ストリームの要素を指定されたコレクタ（この場合はリスト）に収集される
                .collect(Collectors.toList());

        // 2. IDリストが空の場合、空のページを返す
        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

     // 3. キーワードと住所に基づいてフィルタリングし、ページングされた結果を取得
        Page<House> result = houseRepository.findByNameLikeOrAddressLikeAndIdIn(
                "%" + keyword + "%", "%" + keyword + "%", ids, pageable);

     // 4. 結果をJavaコードで並び替える
        List<House> sortedHouses = result.stream()
//        								・ストリームの要素（Houseオブジェクト）を、カスタムの比較ロジックに基づいて並び替え
//        								  -Comparator.comparingInt(h -> ids.indexOf(h.getId()))：各Houseオブジェクトのidが、idsリストのどの位置にあるかを基準に並び替えるためのコンパレータを生成
//        									※h：Houseオブジェクトを表す、h.getId()はそのHouseオブジェクトのIDを取得
//        									※ids.indexOf(h.getId())：そのIDがidsリストのどの位置にあるかを返す
                                         .sorted(Comparator.comparingInt(h -> ids.indexOf(h.getId())))
//                                       -並び替えたストリームの要素をリストに収集。
                                         .collect(Collectors.toList());

     // 5. 並び替えた結果を新しいPageImplオブジェクトとして返す
        return new PageImpl<>(sortedHouses, pageable, result.getTotalElements());
    }
    
//  全てを表示後に並べ替えするメソッド
    public Page<House> getHouseAverageScoreSortedAll(Pageable pageable) {
        List<HouseScoreAvg> houseScores = getHousesOrderedByAverageScoreSorted();
        List<Integer> ids = houseScores.stream()
                .map(HouseScoreAvg::getHouseId)
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<House> result = houseRepository.findAllByIdsIn(ids, pageable);

        // 並び替えをJava codeで行う
        List<House> sortedHouses = result.stream()
                                         .sorted(Comparator.comparingInt(h -> ids.indexOf(h.getId())))
                                         .collect(Collectors.toList());

        return new PageImpl<>(sortedHouses, pageable, result.getTotalElements());
    }
    
//  利用金額(上限下限両方入る場合)から検索表示後に並べ替えするメソッド
    public Page<House> getHouseAverageScoreSortedGreaterAndLess(Integer priceMax, Integer priceMin, Pageable pageable) {
        List<HouseScoreAvg> houseScores = getHousesOrderedByAverageScoreSorted();
        List<Integer> ids = houseScores.stream()
                .map(HouseScoreAvg::getHouseId)
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<House> result = houseRepository.findByPriceMinGreaterThanEqualAndPriceMaxLessThanEqual(priceMax, priceMin, ids, pageable);

        // 並び替えをJava codeで行う
        List<House> sortedHouses = result.stream()
                                         .sorted(Comparator.comparingInt(h -> ids.indexOf(h.getId())))
                                         .collect(Collectors.toList());

        return new PageImpl<>(sortedHouses, pageable, result.getTotalElements());
    }

//  利用金額(下限が入る場合)から検索表示後に並べ替えするメソッド
    public Page<House> getHouseAverageScoreSortedGreater(Integer priceMin, Pageable pageable) {
        List<HouseScoreAvg> houseScores = getHousesOrderedByAverageScoreSorted();
        List<Integer> ids = houseScores.stream()
                .map(HouseScoreAvg::getHouseId)
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<House> result = houseRepository.findByPriceMinGreaterThanEqual(priceMin, ids, pageable);

        // 並び替えをJava codeで行う
        List<House> sortedHouses = result.stream()
                                         .sorted(Comparator.comparingInt(h -> ids.indexOf(h.getId())))
                                         .collect(Collectors.toList());

        return new PageImpl<>(sortedHouses, pageable, result.getTotalElements());
    }
    
//  利用金額(上限両方入る場合)から検索表示後に並べ替えするメソッド
    public Page<House> getHouseAverageScoreSortedLess(Integer priceMax, Pageable pageable) {
        List<HouseScoreAvg> houseScores = getHousesOrderedByAverageScoreSorted();
        List<Integer> ids = houseScores.stream()
                .map(HouseScoreAvg::getHouseId)
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<House> result = houseRepository.findByPriceMaxLessThanEqual(priceMax, ids, pageable);

        // 並び替えをJava codeで行う
        List<House> sortedHouses = result.stream()
                                         .sorted(Comparator.comparingInt(h -> ids.indexOf(h.getId())))
                                         .collect(Collectors.toList());

        return new PageImpl<>(sortedHouses, pageable, result.getTotalElements());
    }


    
    
}