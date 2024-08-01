package com.waither.domain.weather.repository;

import com.waither.weatherservice.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

	@Query("SELECT r FROM Region r WHERE r.startLat <= :lat AND :lat <= r.endLat AND r.startLon <= :lon AND :lon <= r.endLon")
	List<Region> findRegionByLatAndLong(double lat, double lon);

	@Query("SELECT r FROM Region r WHERE r.startX <= :x AND :x <= r.endX AND r.startY <= :y AND :y <= r.endY")
	List<Region> findRegionByXAndY(int x, int y);
}
