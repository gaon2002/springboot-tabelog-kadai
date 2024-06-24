package com.example.nagoyameshi.dto;


public class HouseScoreDto {
    private Integer houseId;
    private String name;
    private String imageName;
    private String description;
    private Integer priceMax;
    private Integer priceMin;
    private Integer capacity;
    private String postalCode;
    private String address;
    private String phoneNumber;
    private Double averageScore;

    public HouseScoreDto(Integer houseId,String name, String imageName, String description, Integer priceMax, Integer priceMin, Integer capacity, String postalCode, String address, String phoneNumber, Double averageScore) {
        this.houseId = houseId;
        this.name = name;
        this.imageName = imageName;
        this.description = description;
        this.priceMax = priceMax;
        this.priceMin = priceMin;
        this.capacity = capacity;
        this.postalCode = postalCode;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.averageScore = averageScore;
    }
    
//  デフォルトコンストラクタ
    public HouseScoreDto() {
    	
    }

//  ゲッター
    public Integer getHouseId() {
        return houseId;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getImageName() {
    	return  imageName;
    }
    	
    public String getDescription() {
    	return  description;
    }
    	
    public Integer getPriceMax() {
    	return  priceMax;
    }
    	
    public Integer getPriceMin() {
    	return  priceMin;
    }
    	
    public Integer getCapacity() {
    	return  capacity;
    }
    	
    public String getPostalCode() {
    	return  postalCode;
    }
    	
    public String getAddress() {
    	return  address;
    }
    	
    public String getPhoneNumber() {
    	return  phoneNumber;
    }
    
    public Double getAverageScore() {
        return averageScore;
    }
    
//  セッター
    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriceMax(Integer priceMax) {
        this.priceMax = priceMax;
    }

    public void setPriceMin(Integer priceMin) {
        this.priceMin = priceMin;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
}
