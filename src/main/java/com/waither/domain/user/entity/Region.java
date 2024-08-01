package com.waither.domain.user.entity;

import com.waither.userservice.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "region")
@Entity
public class Region extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 지역명 (String으로)
    @Column(name = "region_name")
    private String regionName;

    // 경도
    @Column(name = "longitude")
    private double longitude;

    // 위도
    @Column(name = "latitude")
    private double latitude;

    public void update(String newRegionName, double newLongitude, double newLatitude) {
        regionName = newRegionName;
        longitude = newLongitude;
        latitude = newLatitude;
    }

}
