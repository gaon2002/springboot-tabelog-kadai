package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeContoroller {
//	@GetMapping("/")アノテーションをつけた場合、アプリのトップページに（GETメソッドで）アクセスされたときにそのメソッドが実行されるようになる。
	
	@GetMapping("/")
	public String index() {
		
//		return "index";と記述すると、src/main/resources/templatesフォルダ内にあるindex.htmlが呼び出される。
		return "index";
	}

}
