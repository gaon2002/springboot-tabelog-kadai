//レビューのスコア平均値を格納するデータ転送オブジェクト（DTO）として使用

// Reviewエンティティで加工したデータを保持できるように、HouseScoreAvgオブジェクトを作成する。
// HouseScoreAvgクラスは、少なくともハウスIDとスコアの平均値をフィールドとして持っている必要がある。

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
