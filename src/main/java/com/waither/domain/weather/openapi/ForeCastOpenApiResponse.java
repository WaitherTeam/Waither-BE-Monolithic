package com.waither.domain.weather.openapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForeCastOpenApiResponse {
	private Response response;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Response {
		private Header header;
		private Body body;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Body {
		private String dataType;
		private Items items;
		private int pageNo;
		private int numOfRows;
		private int totalCount;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Items {
		private List<Item> item;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Item {
		private String baseDate;
		private String baseTime;
		private String category;
		private String fcstDate;
		private String fcstTime;
		private String fcstValue;
		private int nx;
		private int ny;

		public String toString() {
			return "Item{" +
				"baseDate='" + baseDate + '\'' +
				", baseTime='" + baseTime + '\'' +
				", category='" + category + '\'' +
				", fcstDate='" + fcstDate + '\'' +
				", fcstTime='" + fcstTime + '\'' +
				", fcstValue='" + fcstValue + '\'' +
				", nx=" + nx +
				", ny=" + ny +
				'}';
		}
	}
}
