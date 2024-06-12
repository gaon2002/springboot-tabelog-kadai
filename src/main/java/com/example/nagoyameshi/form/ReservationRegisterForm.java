package com.example.nagoyameshi.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationRegisterForm {    
    private Integer houseId;
        
    private Integer userId;    
        
    private String reservedDate;    
        
    private String reservedTime;    
    
    private Integer numberOfPeople;
    
}
