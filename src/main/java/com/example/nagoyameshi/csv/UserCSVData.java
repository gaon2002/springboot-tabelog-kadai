package com.example.nagoyameshi.csv;

import com.orangesignal.csv.annotation.CsvColumn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@com.orangesignal.csv.annotation.CsvEntity
/**
 * CSVデータ用 ADD:2024-07-13
 */
public class UserCSVData {
	@CsvColumn(name = "ID")
    private Integer id;
    
	@CsvColumn(name = "名前")
    private String name;
        
	@CsvColumn(name = "フリガナ")
    private String furigana;    
        
	@CsvColumn(name = "郵便番号")
    private String postalCode;
        
	@CsvColumn(name = "住所")
    private String address;
        
	@CsvColumn(name = "電話番号")
    private String phoneNumber;
    
	@CsvColumn(name = "E-mailアドレス")
    private String email;
}

