package com.waither.domain.weather.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "region")
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String regionName;
	private double startLat;
	private double endLat;
	private double startLon;
	private double endLon;
	private int startX;
	private int endX;
	private int startY;
	private int endY;
	private int regionCode;

	public String toString() {
		return "Region{" +
			"id=" + id +
			", regionName='" + regionName + '\'' +
			", startLat=" + startLat +
			", endLat=" + endLat +
			", startLon=" + startLon +
			", endLon=" + endLon +
			", startX=" + startX +
			", endX=" + endX +
			", startY=" + startY +
			", endY=" + endY +
			", regionCode=" + regionCode +
			'}';
	}
}
