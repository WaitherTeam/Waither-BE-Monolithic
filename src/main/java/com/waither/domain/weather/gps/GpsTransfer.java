package com.waither.domain.weather.gps;

import com.waither.global.exception.CustomException;
import com.waither.global.response.WeatherErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
// @Component
public class GpsTransfer {

	// 격자 간격 (단위: km)
	private static final double GRID_INTERVAL = 5.0;
	// 지구 반경 (단위: km), 격자 간격(GRID)을 이용하여 계산됨
	private static final double EARTH_RADIUS = 6371.00877 / GRID_INTERVAL;
	// 기준점 X좌표 (단위: GRID)
	private static final double REFERENCE_X_COORDINATE = 43;
	// 기준점 Y좌표 (단위: GRID)
	private static final double REFERENCE_Y_COORDINATE = 136;
	// degree 를 radian 으로 변환하는 상수
	private static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
	// 기준점 경도 (단위: degree)
	private static final double REFERENCE_LONGITUDE = 126.0 * DEGREES_TO_RADIANS;
	// 기준점 위도 (단위: degree)
	private static final double REFERENCE_LATITUDE = 38.0 * DEGREES_TO_RADIANS;
	// 투영 위도1 (단위: degree)
	private static final double PROJECTION_LATITUDE_1 = 30.0 * DEGREES_TO_RADIANS;
	// 투영 위도2 (단위: degree)
	private static final double PROJECTION_LATITUDE_2 = 60.0 * DEGREES_TO_RADIANS;
	// radian 을 degree 로 변환하는 상수
	private static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;

	public static LatXLngY convertGpsToGrid(double lat, double lng) {
		double sn = Math.tan(Math.PI * 0.25 + PROJECTION_LATITUDE_2 * 0.5) / Math.tan(
			Math.PI * 0.25 + PROJECTION_LATITUDE_1 * 0.5);
		sn = Math.log(Math.cos(PROJECTION_LATITUDE_1) / Math.cos(PROJECTION_LATITUDE_2)) / Math.log(sn);
		double sf = Math.tan(Math.PI * 0.25 + PROJECTION_LATITUDE_1 * 0.5);
		sf = Math.pow(sf, sn) * Math.cos(PROJECTION_LATITUDE_1) / sn;
		double ro = Math.tan(Math.PI * 0.25 + REFERENCE_LATITUDE * 0.5);
		ro = EARTH_RADIUS * sf / Math.pow(ro, sn);

		double ra = Math.tan(Math.PI * 0.25 + (lat) * DEGREES_TO_RADIANS * 0.5);
		ra = EARTH_RADIUS * sf / Math.pow(ra, sn);
		double theta = lng * DEGREES_TO_RADIANS - REFERENCE_LONGITUDE;
		if (theta > Math.PI)
			theta -= 2.0 * Math.PI;
		if (theta < -Math.PI)
			theta += 2.0 * Math.PI;
		theta *= sn;
		double x = Math.floor(ra * Math.sin(theta) + REFERENCE_X_COORDINATE + 0.5);
		double y = Math.floor(ro - ra * Math.cos(theta) + REFERENCE_Y_COORDINATE + 0.5);

		return LatXLngY.builder()
			.lat(lat)
			.lng(lng)
			.x(x)
			.y(y)
			.build();
	}

	public static LatXLngY convertGridToGps(double x, double y) {
		double sn = Math.tan(Math.PI * 0.25 + PROJECTION_LATITUDE_2 * 0.5) / Math.tan(
			Math.PI * 0.25 + PROJECTION_LATITUDE_1 * 0.5);
		sn = Math.log(Math.cos(PROJECTION_LATITUDE_1) / Math.cos(PROJECTION_LATITUDE_2)) / Math.log(sn);
		double sf = Math.tan(Math.PI * 0.25 + PROJECTION_LATITUDE_1 * 0.5);
		sf = Math.pow(sf, sn) * Math.cos(PROJECTION_LATITUDE_1) / sn;
		double ro = Math.tan(Math.PI * 0.25 + REFERENCE_LATITUDE * 0.5);
		ro = EARTH_RADIUS * sf / Math.pow(ro, sn);
		double xn = x - REFERENCE_X_COORDINATE;
		double yn = ro - y + REFERENCE_Y_COORDINATE;
		double ra = Math.sqrt(xn * xn + yn * yn);
		if (sn < 0.0) {
			ra = -ra;
		}
		double alat = Math.pow((EARTH_RADIUS * sf / ra), (1.0 / sn));
		alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;
		double theta;
		if (Math.abs(xn) <= 0.0) {
			theta = 0.0;
		} else {
			if (Math.abs(yn) <= 0.0) {
				theta = Math.PI * 0.5;
				if (xn < 0.0) {
					theta = -theta;
				}
			} else
				theta = Math.atan2(xn, yn);
		}
		double alon = theta / sn + REFERENCE_LONGITUDE;
		double lat = alat * RADIANS_TO_DEGREES;
		double lng = alon * RADIANS_TO_DEGREES;
		return LatXLngY.builder()
			.lat(lat)
			.lng(lng)
			.x(x)
			.y(y)
			.build();
	}

	// ex) lat = 37.57142000, lon = 126.96580000 -> 108
	public static String convertGpsToRegionCode(double lat, double lon) {
		String regionCode = null;
		try {
			InputStream inputStream = GpsTransfer.class.getResourceAsStream("/api/Region.xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0); // 시트 인덱스, 첫 번째 시트를 가져옴

			for (int i = 2; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell lonCell = row.getCell(3);
					Cell latCell = row.getCell(4);

					// 셀이 비어 있는지 확인
					if (lonCell != null && latCell != null) {
						String lonValue = lonCell.toString().replace(",", "");
						String latValue = latCell.toString().replace(",", "");

						double rowLon = Double.parseDouble(lonValue);
						double rowLat = Double.parseDouble(latValue);

						// 위도 경도에 일치하는 지역코드
						if (lon == rowLon && lat == rowLat) {
							String stnId = row.getCell(0).toString();
							regionCode = stnId;
							break; // 일치하는 stn_id를 찾으면 반복 종료
						}
					}
				}
			}
			workbook.close();
		} catch (IOException e) {
			throw new CustomException(WeatherErrorCode.WEATHER_MAIN_ERROR);
		}
		return regionCode;
	}
}
