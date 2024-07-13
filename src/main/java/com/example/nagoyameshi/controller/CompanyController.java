package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Company;
import com.example.nagoyameshi.form.CompanyEditForm;
import com.example.nagoyameshi.repository.CompanyRepository;
import com.example.nagoyameshi.service.CompanyService;

import jakarta.transaction.Transactional;

@RequestMapping("/company")
@Controller
public class CompanyController {

	private final CompanyRepository companyRepository;
	private final CompanyService companyService;

    public CompanyController(CompanyRepository companyRepository, CompanyService companyService) {
        this.companyRepository = companyRepository;
        this.companyService = companyService;
    }

//  会社情報の確認
    @GetMapping("/index")
    @Transactional
    public String index(Model model) {
        Company company = null;
        try {
            company = companyRepository.findById(1).orElse(null);
        } catch (Exception e) {
            System.out.println("Error fetching company with ID 1: " + e.getMessage());
            e.printStackTrace(); // スタックトレースを表示
            model.addAttribute("errorMessage", "会社情報の取得に失敗しました。");
            return "error"; // エラーページにリダイレクト
        }

        model.addAttribute("company", company);
        return "company/index";
    }
    
    
//  会社情報の編集➀：フォームへ元情報をセットする
    @GetMapping("/edit")
    public String edit(Model model) {        
    	
    	Company company = companyRepository.findById(1).orElse(null);
    	
//      保存されている会社情報を編集フォームに入れ込むため、各フィールドの項目をインスタンス化
        CompanyEditForm companyEditForm = new CompanyEditForm(company.getId(),
        													  company.getCompanyName(),
        													  company.getPrinciple(),
        													  company.getPostalCode(),
        													  company.getAddress(),
        													  company.getEstablishment(),
        													  company.getCapitalStock(), 
        													  company.getBusiness(),
        													  company.getNumberOfMember(), 
        													  company.getPhoneNumber());
        
        model.addAttribute("companyEditForm", companyEditForm);
        
        return "company/edit";
    }    
    
//  会社情報の編集➁：編集されたユーザー情報を更新する
    @PostMapping("/update")
    public String update(@ModelAttribute @Validated CompanyEditForm companyEditForm, 
    					 BindingResult bindingResult, 
    					 RedirectAttributes redirectAttributes) 
    {
        
        if (bindingResult.hasErrors()) {
            return "company/edit";
        }
        
//      companyServiceのupdate()メソッドを実行
        companyService.update(companyEditForm);
        
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        
//      user/index.htmlに戻って表示
        return "redirect:/company/index";
    }    
	
}
