package com.example.nagoyameshi.controller;

import java.util.Optional;

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

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.form.CategoryEditForm;
import com.example.nagoyameshi.form.CategoryRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
	private final CategoryRepository categoryRepository;
	private final CategoryService categoryService;
  
    
	public CategoryController (CategoryRepository categoryRepository, CategoryService categoryService) {
		this.categoryRepository = categoryRepository;
		this.categoryService = categoryService;
	}


	@GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {
	
	Page<Category> categoryPage;
	
    if (keyword != null && !keyword.isEmpty()) {
    	
        categoryPage = categoryRepository.findByCategoryLike("%" + keyword + "%", pageable);
    
        }else {
        	categoryPage = categoryRepository.findAll(pageable);
        }
    
    model.addAttribute("categoryPage", categoryPage);
    model.addAttribute("keyword", keyword);
    
    return "admin/categories/index";

    }
	
//	 ＜カテゴリー情報登録➀＞	
//	 まず、ビューにフォームクラスのインスタンスを渡す
	 @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("categoryRegisterForm", new CategoryRegisterForm());
        return "admin/categories/register";
    }   
	 
//	 ＜カテゴリー情報登録➁＞
	 @PostMapping("/create")
//	 メソッドの引数に@ModelAttributeアノテーションをつけ、フォームから送信されたデータ（フォームクラスのインスタンス）をその引数にバインド（割り当て）できる
    public String create(@ModelAttribute @Validated CategoryRegisterForm categoryRegisterForm,
   		 			  BindingResult bindingResult,
   		 			  RedirectAttributes redirectAttributes) {        
        if (bindingResult.hasErrors()) {
            return "admin/categories/register";
        }
        
        categoryService.create(categoryRegisterForm);
        
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリーを登録しました。");    
        
//      カテゴリー一覧にリダイレクトする
        return "redirect:/admin/categories";
	 }
	 

//	 ＜カテゴリー情報編集➀：元情報をビューに渡すSTEP＞
	 @GetMapping("/{id}/edit")
     public String edit(@PathVariable(name = "id") Integer id, Model model) {
//		 該当するidの情報をcategoryテーブルから取得する
//	    	Optional型を用いてエンティティの存在チェックを行う
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (!categoryOpt.isPresent()) {
            // ハウスが見つからない場合のエラーハンドリングを追加する
            return "error/404"; // 例: 404ページにリダイレクト
        }
        
        Category category = categoryOpt.get();
        
        String imageName = category.getImageName();
        
//      フォームクラスをインスタンス化
        CategoryEditForm categoryEditForm = new CategoryEditForm(category.getId(), category.getCategory(), null);
        
        model.addAttribute("imageName", imageName);
//      インスタンス化したフォームクラスの値をビューに返す（コンストラクタは"houseEditForm"で作成済み）
        model.addAttribute("categoryEditForm", categoryEditForm);
        
        return "admin/categories/edit";
     }    
	 
//	 ＜カテゴリー情報編集➁：元情報をビューに渡すSTEP＞
	 @PostMapping("/{id}/update")
     public String update(@ModelAttribute @Validated CategoryEditForm categoryEditForm,
    		 			  BindingResult bindingResult,
    		 			  RedirectAttributes redirectAttributes) { 
//		 エラーがあれば、editビューに表示
		 if (bindingResult.hasErrors()) {
             return "admin/categories/edit";
         }
         
//		 categoryServiceのupdate()を実行
         categoryService.update(categoryEditForm);
         
         redirectAttributes.addFlashAttribute("successMessage", "カテゴリー情報を編集しました。");
         
//       編集が成功したらカテゴリー一覧に成功メッセージを表示
         return "redirect:/admin/categories";
     }    
	 
	
//	＜カテゴリー削除＞
	@PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
//		 対象idの情報を削除
        categoryRepository.deleteById(id);
                
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリー情報を削除しました。");
        
//      削除が成功したら店舗一覧に成功メッセージを表示
        return "redirect:/admin/categories";
    }    

}
