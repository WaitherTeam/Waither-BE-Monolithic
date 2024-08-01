package com.waither.domain.weather.openapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirKoreaOpenApiResponse {

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
		private int numOfRows;
		private int pageNo;
		private List<Items> items;
		private int totalCount;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Items {
		private String imageUrl1;
		private String imageUrl2;
		private String imageUrl3;
		private String dataTime;
		private String informGrade;
		private String informData;
		private String informOverall;
		private String informCause;
		private String imageUrl6;
		private String imageUrl5;
		private String informCode;
		private String imageUrl4;

		public String toString() {
			return "Items{" +
				", imageUrl1='" + imageUrl1 + '\'' +
				", imageUrl2='" + imageUrl2 + '\'' +
				", imageUrl3='" + imageUrl3 + '\'' +
				", imageUrl4='" + imageUrl4 + '\'' +
				", imageUrl5='" + imageUrl5 + '\'' +
				", imageUrl6='" + imageUrl6 + '\'' +
				", dataTime='" + dataTime + '\'' +
				", informGrade='" + informGrade + '\'' +
				", informData='" + informData + '\'' +
				", informOverall='" + informOverall + '\'' +
				", informCause='" + informCause + '\'' +
				", informCode='" + informCode + '\'' +
				'}';
		}
	}
}
