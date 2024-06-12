package com.example.nagoyameshi.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.House;
import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.HouseRepository;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReservationService {
	
	private final ReservationRepository reservationRepository;  
    private final HouseRepository houseRepository;  
    private final UserRepository userRepository;  
    
    public ReservationService(ReservationRepository reservationRepository, HouseRepository houseRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;  
        this.houseRepository = houseRepository;  
        this.userRepository = userRepository;  
    }    
    
    
//   予約情報の登録
    
    @Transactional
    public void create(ReservationRegisterForm reservationRegisterForm) { 
        Reservation reservation = new Reservation();
        House house = houseRepository.getReferenceById(reservationRegisterForm.getHouseId());
        User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
        LocalDate reservedDate = LocalDate.parse(reservationRegisterForm.getReservedDate());
        LocalTime reservedTime = LocalTime.parse(reservationRegisterForm.getReservedTime());         
                
        reservation.setHouse(house);
        reservation.setUser(user);
        reservation.setReservedDate(reservedDate);
        reservation.setReservedTime(reservedTime);
        reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());
        
        reservationRepository.save(reservation);
    }    
	
	
	
     // 来店人数が定員以下かどうかをチェックする
     public boolean isWithinCapacity(Integer numberOfPeople, Integer capacity) {
         return numberOfPeople <= capacity;
     }
     
}
