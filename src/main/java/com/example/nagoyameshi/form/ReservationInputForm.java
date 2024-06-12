package com.example.nagoyameshi.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
     @NotBlank(message = "予約日を選択してください。")
     private String reservedDate;    
     
     @NotBlank(message = "予約時間を選択してください。")
     private String reservedTime;    
     
     
     @NotNull(message = "予約人数を入力してください。")
     @Min(value = 1, message = "予約人数は1人以上に設定してください。")
     private Integer numberOfPeople; 
 
     
}
