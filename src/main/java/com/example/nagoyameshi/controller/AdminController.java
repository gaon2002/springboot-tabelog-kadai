package com.example.nagoyameshi.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.csv.UserCSVData;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;
import com.orangesignal.csv.Csv;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.handlers.CsvEntityListHandler;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/users")
public class AdminController {
    private final UserRepository userRepository;   
    
 // クラスの自動インスタンス化 ADD:2024-07-13	
    @Autowired	
    private ModelMapper modelMapper; // EntityをCSV出力用のクラスにコピーするための仕掛け ADD:2024-07-13
    
    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;                
    }    
    
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
    					@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
    					Model model) {
    	
        Page<User> userPage;
        
//      キーワード検索ボックスに何か記載されていたら
        if (keyword != null && !keyword.isEmpty()) {
            userPage = userRepository.findByNameLikeOrFuriganaOrEmailLike("%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%", pageable);   
//      検索ワードがなければ、全表示
        } else {
            userPage = userRepository.findAll(pageable);
        }        
        
        model.addAttribute("userPage", userPage);        
        model.addAttribute("keyword", keyword);                
        
        return "admin/users/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model) {
        
        Optional<User> optionalUser = userRepository.findById(id);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("user", user);
        } else {
            // Handle the case where the user is not found
            model.addAttribute("errorMessage", "ユーザーが見つかりません");
            return "admin/users/error"; // Assuming you have an error page
        }
        
        
        return "admin/users/show";
    }
    
    /* CSV出力用に追加 ADD:2024-07-13 */
	@GetMapping("/csv")
	public void csvOutput(@RequestParam(name = "keyword2", required = false) String keyword,
			HttpServletResponse response) throws IOException
	{
	
		System.out.println("csv出力開始");	//【ＯＫ】
		System.out.println("keyword：" + keyword);	//【ＯＫ】
		
		// CSV出力データリスト
		List<User> usersList = null;

		/* キーワード検索ボックスの入力有無確認 */
		// 入力あり
		if (keyword != null && !keyword.isEmpty()) {
			// キーワードに部分一致するデータ取得
			usersList = userRepository.findByNameLikeOrFuriganaOrEmailLike("%" + keyword + "%", "%" + keyword + "%", "%" + keyword + "%");
			System.out.println("usersList：" + usersList);	//【ＯＫ】
		}
		// 入力なし 
		else {
			// 全件取得
			usersList = userRepository.findAll();
		}
		/* CSV出力データ有無のチェック */
		// 出力データあり
		if (usersList != null && usersList.size() != 0) {
			System.out.println("フォルダへ出力開始");	//【ＯＫ】
			try {
				/* EntityリストをCSV用データリストへ変換 */
				// CSVリスト
				List<UserCSVData> csvDataList = new ArrayList<>();
				
//				usersListの各Userオブジェクトについて、繰り返し処理を実行
				for(User user : usersList) {
//					modelMapperを使用し、userオブジェクトをUserCSVDataクラスのインスタンスにマッピングする。
//					userのプロパティがUserCSVDataの対応するプロパティにコピーされる。
					UserCSVData userCSVData = modelMapper.map(user, UserCSVData.class);
					
//					マッピングされたUserCSVDataオブジェクトをcsvDataListに追加
					csvDataList.add(userCSVData);
				}
				System.out.println("csvDataList：" + csvDataList);	//【ＯＫ】
				
				/** ファイル名取得 */
//				ResourceBundle：アプリケーションが異なるロケール（言語や地域）に対応するために、メッセージや設定値を外部ファイルに分離して管理するためのクラス
				ResourceBundle rb = ResourceBundle.getBundle("nagoyameshi");
				// CSV保存用パス
				String csvpath = rb.getString("csv.dirpath");

				// CSVファイル名取得
				String filename = rb.getString("csv.filename.001");
				
				System.out.println("rb：" + rb);
				System.out.println("csvpath：" + csvpath);
				System.out.println("filename：" + filename);
				

				// CSV出力用フォルダの作成（mkdir：make directory）
//				　・new File(csvpath)は、このパスに対応するFileオブジェクトを作成します。このオブジェクトは、実際にはファイルやディレクトリを指し示す抽象的な表現です。
				File dir = new File(csvpath);
				
				if (dir.exists()) {
				    System.out.println("ディレクトリはすでに存在します。");
				} else {
//					・mkdir()メソッド：このFileオブジェクトが指すディレクトリを実際に作成。
				    if (dir.mkdir()) {
				        System.out.println("ディレクトリの作成に成功しました。");
				    } else {
				        System.out.println("ディレクトリの作成に失敗しました。");
				    }
				}
				
				// CSV出力情報作成
				csvpath = csvpath + filename;

				// 区切り文字と囲み文字、エスケープ文字を指定して CSV 形式設定情報を構築
				CsvConfig cfg = new CsvConfig(',', '"', '"');

				File fileout = new File(csvpath);
				String encode = "MS932";
				Csv.save(
						csvDataList,
						fileout,
						encode,
						cfg,
						new CsvEntityListHandler<UserCSVData>(UserCSVData.class));
			} catch (Exception e) {

			}
		}

		/* 呼び出し元の画面の状態を維持するための処理 */
		// キーワードをURLエンコードする(URLエンコードしないと全角文字はエラーとなる)
		String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());
		String url = "/admin/users?keyword=" + encodedKeyword;
		/* 
		 * 呼び出し元の画面にリダイレクト
		 * 今回は手っ取り早く会員一覧画面のキーワード検索ボックスの値を
		 * URLパラメータに設定し、会員一覧画面より検索処理を実施したように
		 * 処理することにより、CSVボタン押下時の状態を作り出す方法を選択
		 * 
		 */
		response.sendRedirect(url);
	}

}