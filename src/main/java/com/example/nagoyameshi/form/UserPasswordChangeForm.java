package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPasswordChangeForm {


    @NotBlank(message = "現在のパスワードを入力してください。")
    private String currentPassword;

    @NotBlank(message = "新しいパスワードを入力してください。")
    private String newPassword;

    @NotBlank(message = "確認用パスワードを入力してください。")
    private String confirmNewPassword;

    // Getters and setters
	
}
