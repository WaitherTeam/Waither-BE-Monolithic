package com.waither.domain.weather.openapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MsgOpenApiResponse {

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
		private String stnId;
		private String title;
		private String tmFc;
		private String tmSeq;

		@Override
		public String toString() {
			return "Item{" +
				"stnId='" + stnId + '\'' +
				", title='" + title + '\'' +
				", tmFc='" + tmFc + '\'' +
				", tmSeq='" + tmSeq + '\'' +
				'}';
		}
	}
}
