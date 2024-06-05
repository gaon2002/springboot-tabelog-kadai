package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.form.HouseRegisterForm;
import com.example.nagoyameshi.repository.HouseRepository;
import com.example.nagoyameshi.service.HouseService;


@Controller
//クラスに@RequestMappingアノテーションをつける：ルートパスの基準値を設定できる
//	例：@RequestMapping("/admin/houses")であれば、そのコントローラ内の各メソッドが担当するURLは「https://ドメイン名/admin/houses/○○○」となる
@RequestMapping("/admin/houses")
public class AdminHouseController {
//	オブジェクトを外部から提供（注入）することを、依存性の注入（Dependency Injection、DI）
//		※今回は、AdminHouseRepositoryを作成せず、HouseRepositoryからオブジェクトを引用している
//	依存先のオブジェクトをfinalで宣言(コンストラクタインジェクション)
	private final HouseRepository houseRepository;
	private final HouseService houseService;
	
//	本来はコンストラクタに「@Autowired」を付けるが、コンストラクタがひとつの場合は省略可能
	public AdminHouseController(HouseRepository houseRepository, HouseService houseService) {
		 this.houseRepository = houseRepository;
		 this.houseService = houseService;
	}
	
	@GetMapping
	public String index(Model model,
						@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
//						@RequestParamアノテーションつけ、フォームから送信されたパラメータ（リクエストパラメータ）をその引数にバインドする（割り当てる）
//							・name属性：取得するリクエストパラメータ名　（%を使うことで、部分一致検索ができる）
//							・required属性：そのリクエストパラメータが必須かどうか（デフォルトはtrue）
//							・defaultValue属性：リクエストパラメータの値が指定されていない、または空の場合のデフォルト値
						@RequestParam(name = "keyword", required = false) String keyword) {
		
		Page<House> housePage;
		
		 if (keyword != null && !keyword.isEmpty()) {
             housePage = houseRepository.findByNameLike("%" + keyword + "%", pageable);                
         } else {
             housePage = houseRepository.findAll(pageable);
         }  
				
//		第1引数("houses"):ビュー側から参照するデータ
//		第2引数(houses):ビュー側に渡すデータ
		model.addAttribute("housePage", housePage);
		model.addAttribute("keyword", keyword);
		
		return "admin/houses/index";
	}
	
	 @GetMapping("/{id}")
     public String show(@PathVariable(name = "id") Integer id, Model model) {
         House house = houseRepository.getReferenceById(id);
         
         model.addAttribute("house", house);
         
         return "admin/houses/show";
	 }
	 
//	 まず、ビューにフォームクラスのインスタンスを渡す
	 @GetMapping("/register")
     public String register(Model model) {
         model.addAttribute("houseRegisterForm", new HouseRegisterForm());
         return "admin/houses/register";
     }   
	 
	 @PostMapping("/create")
//	 メソッドの引数に@ModelAttributeアノテーションをつけ、フォームから送信されたデータ（フォームクラスのインスタンス）をその引数にバインド（割り当て）できる
     public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm,
    		 			  BindingResult bindingResult,
    		 			  RedirectAttributes redirectAttributes) {        
         if (bindingResult.hasErrors()) {
             return "admin/houses/register";
         }
         
         houseService.create(houseRegisterForm);
         
         redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");    
         
//       店舗一覧にリダイレクトする
         return "redirect:/admin/houses";
	 }

}
