package com.example.nagoyameshi.confing;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * EntityデータをCSVデータへ変換するための初期処理
 * ADD:2024-07-13
 */
@Configuration
public class JavaConfig {
	
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
