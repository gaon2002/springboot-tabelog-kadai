package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;

@Controller
public class ReservationController {
    private final ReservationRepository reservationRepository;      
    
    public ReservationController(ReservationRepository reservationRepository) {        
        this.reservationRepository = reservationRepository;              
    }    

    @GetMapping("/reservations")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    					@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
    					Model model) {
    	
//    	ログイン時に保持されているデータからユーザー情報を取得し、user変数へ保管
        User user = userDetailsImpl.getUser();
//      対象のユーザー情報にひも付く予約情報を取得し、reservationPage変数へ保管
        Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        model.addAttribute("reservationPage", reservationPage);         
        
        return "reservations/index";
    }
}