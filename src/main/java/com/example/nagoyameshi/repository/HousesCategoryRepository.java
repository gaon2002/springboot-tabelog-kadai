package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.example.nagoyameshi.entity.HousesCategory;

import jakarta.transaction.Transactional;

@Repository
public interface HousesCategoryRepository extends JpaRepository<HousesCategory, Integer> {
    List<HousesCategory> findByHouseId(Integer houseId);

    @Modifying
    @Transactional
    void deleteByHouseId(Integer houseId);
}
