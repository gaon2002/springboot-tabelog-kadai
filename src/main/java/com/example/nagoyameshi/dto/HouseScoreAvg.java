package com.example.nagoyameshi.dto;

public class HouseScoreAvg {
    private Integer houseId;
    private Double averageScore;

    public HouseScoreAvg(Integer houseId, Double averageScore) {
        this.houseId = houseId;
        this.averageScore = averageScore;
    }

    public Integer getHouseId() {
        return houseId;
    }

    public Double getAverageScore() {
        return averageScore;
    }
}
