//付帯させる機能
//誰に、どのページへのアクセスを許可するかを設定
//	ログインページのURL
//	ログインフォームの送信先URL
//	ログイン成功時または失敗時のリダイレクト先URL
//	ログアウト時のリダイレクト先URL
//PasswordEncoderインターフェースを利用したパスワードのハッシュアルゴリズム（ハッシュ化のルール）の設定

package com.example.nagoyameshi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration			// 設定用クラスであることをしている
@EnableWebSecurity		// SpringSecurityによるセキュリティ機能を有効にし、認証・認可のルール、ログイン・アウト処理などの各種設定を行えるようになる
@EnableMethodSecurity	// メソッドレベルでのセキュリティ機能を有効にする。
public class WebSecurityConfig {
	
	@Bean	// @Bean：そのメソッドの戻り値(インスタンス)がDIコンテナに登録される（情報が保持されるようになる）
			// 	DIコンテナに登録されたインスタンスをBeanと呼ぶ
	
	// メソッド：SecurityFilterChainクラスで誰に、どのページへのアクセスを許可するかを設定する
	// 		HttpSecurityクラスを使うと、HTTPリクエストのセキュリティに関する設定が行える
	
//	有料会員と無料会員のアクセス制限の付け方は？
	
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			.authorizeHttpRequests((requests) -> requests
					// すべてのユーザーにアクセスを許可するURL。 ()内にルートパスを記述する。
					.requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/" ,"/signup/**", "/houses", "/houses/{id}", "/stripe/webhook","/houses/{id}/reviews/","/houses/*/reviews/**","check-role").permitAll()
					// 管理者のみのアクセスを許可するURL
					.requestMatchers("/admin/**").hasRole("ADMIN")
					// 上記以外はログインが必要（ロールは問わない）
					.anyRequest().authenticated()
			)
			
			.formLogin((form) -> form	//ログイン時のURLを設定するメソッド
					// ログインページのURLを設定
					.loginPage("/login")
					// ログインフォームの送信先URLを設定
					.loginProcessingUrl("/login")
					// ログイン成功時のリダイレクト先URLを設定
					.defaultSuccessUrl("/?loggedIn")
					// ログイン失敗時のリダイレクト先URLを設定・・・https://ドメイン名/login?error
					// login.htmlの<div th:if="${param.error}" class="alert alert-danger">に表示
					.failureUrl("/login?error")
					.permitAll()
			)
			.logout((logout) -> logout	//ログアウト時のURLを設定するメソッド
					// ログアウト時のリダイレクト先URLを設定
					.logoutSuccessUrl("/?loggedOut")
					.permitAll()
			)
//			.sessionManagement() // セッション管理の設定
//            		.invalidSessionUrl("/session-timeout")
//            		.maximumSessions(1)
//            		.expiredUrl("/login?expired")
//            		.and()
//            		.sessionFixation().newSession()
			
			// ★CSRFトークンを返すパスはCSRF対策から除外
			// 　・CSRF（クロス・サイト・リクエスト・フォージェリ）:サイバー攻撃の一種。この攻撃を受けると「ログイン済みユーザーになりすましたアプリやサービスの利用」「アプリやサービスのデータ漏洩（ろうえい）」といった重大な問題が発生
			// 　・今回のように外部からPOST送信を受ける場合、そのままではCSRF対策のチェックによってアクセスが拒否されてしまうので、「/stripe/webhook」に対するPOST送信についてはCSRF対策のチェックを行わないように設定
			.csrf(csrf -> csrf.ignoringRequestMatchers("/stripe/webhook"));
			

		
		
		return http.build();
	}
	
	 protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	        auth.inMemoryAuthentication()
	                .withUser("user")
	                .password(passwordEncoder().encode("password"))
	                .roles("PAID");
	    }

	
	@Bean //@Beanアノテーションを付けることで、そのメソッドの戻り値をDIコンテナに格納できる
	// メソッド：パスワードのハッシュアルゴリズムを設定する
	public PasswordEncoder passwordEncoder() {
		// BCryptPasswordEncoder()クラスのインスタンスを返すことで、パスワードのハッシュアルゴリズムを「BCrypt」に設定
		return new BCryptPasswordEncoder();
	}

}
