package com.example.nagoyameshi.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Company;
import com.example.nagoyameshi.form.CompanyEditForm;
import com.example.nagoyameshi.repository.CompanyRepository;

@Service
public class CompanyService {

	    private final CompanyRepository companyRepository;
	    
	    public CompanyService(CompanyRepository companyRepository) {
	        this.companyRepository = companyRepository;

	    }    
	

//  ユーザー情報更新
    @Transactional
    public void update(CompanyEditForm companyEditForm) {
        Optional<Company> optionalCompany = companyRepository.findById(companyEditForm.getId());
        
        if (optionalCompany.isPresent()) 
        {
            Company company = optionalCompany.get();

	        company.setCompanyName(companyEditForm.getCompanyName());
	        company.setPrinciple(companyEditForm.getPrinciple());
	        company.setPostalCode(companyEditForm.getPostalCode());
	        company.setAddress(companyEditForm.getAddress());
	        company.setEstablishment(companyEditForm.getEstablishment());
	        company.setCapitalStock(companyEditForm.getCapitalStock());
	        company.setBusiness(companyEditForm.getBusiness());
	        company.setNumberOfMember(companyEditForm.getNumberOfMember());
	        company.setPhoneNumber(companyEditForm.getPhoneNumber());
	        
	        companyRepository.save(company);
        
        } else {
            // ユーザーが存在しない場合の処理
            // 例：例外を投げる、エラーメッセージをログに出力するなど
            throw new RuntimeException("company not found with id: " + companyEditForm.getId());
        }
    }    

}
