package com.waither.domain.weather.openapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor // 역직렬화를 위한 기본 생성자
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class AccuweatherLocationApiResponse {

	@JsonProperty("Version")
	private int version;
	@JsonProperty("Key")
	private String key;
	@JsonProperty("Type")
	private String type;
	@JsonProperty("Rank")
	private int rank;
	@JsonProperty("LocalizedName")
	private String localizedName;
	@JsonProperty("EnglishName")
	private String englishName;
	@JsonProperty("PrimaryPostalCode")
	private String primaryPostalCode;
	@JsonProperty("Region")
	private Region region;
	@JsonProperty("Country")
	private Country country;
	@JsonProperty("AdministrativeArea")
	private AdministrativeArea administrativeArea;
	@JsonProperty("TimeZone")
	private TimeZone timeZone;
	@JsonProperty("GeoPosition")
	private GeoPosition geoPosition;
	@JsonProperty("IsAlias")
	private boolean isAlias;
	@JsonProperty("ParentCity")
	private ParentCity parentCity;
	@JsonProperty("SupplementalAdminAreas")
	private List<SupplementalAdminAreas> supplementalAdminAreas;
	@JsonProperty("DataSets")
	private List<String> dataSets;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Region {
		@JsonProperty("ID")
		private String id;
		@JsonProperty("LocalizedName")
		private String localizedName;
		@JsonProperty("EnglishName")
		private String englishName;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Country {
		@JsonProperty("ID")
		private String id;
		@JsonProperty("LocalizedName")
		private String localizedName;
		@JsonProperty("EnglishName")
		private String englishName;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AdministrativeArea {
		@JsonProperty("ID")
		private String id;
		@JsonProperty("LocalizedName")
		private String localizedName;
		@JsonProperty("EnglishName")
		private String englishName;
		@JsonProperty("Level")
		private int level;
		@JsonProperty("LocalizedType")
		private String localizedType;
		@JsonProperty("EnglishType")
		private String englishType;
		@JsonProperty("CountryID")
		private String countryID;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TimeZone {
		@JsonProperty("Code")
		private String code;
		@JsonProperty("Name")
		private String name;
		@JsonProperty("GmtOffset")
		private int gmtOffset;
		@JsonProperty("IsDaylightSaving")
		private boolean isDaylightSaving;
		@JsonProperty("NextOffsetChange")
		private String nextOffsetChange;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class GeoPosition {
		@JsonProperty("Latitude")
		private double latitude;
		@JsonProperty("Longitude")
		private double longitude;
		@JsonProperty("Elevation")
		private Elevation elevation;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Elevation {
		@JsonProperty("Metric")
		private Metric metric;
		@JsonProperty("Imperial")
		private Imperial imperial;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Metric {
		@JsonProperty("Value")
		private int value;
		@JsonProperty("Unit")
		private String unit;
		@JsonProperty("UnitType")
		private int unitType;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Imperial {
		@JsonProperty("Value")
		private int value;
		@JsonProperty("Unit")
		private String unit;
		@JsonProperty("UnitType")
		private int unitType;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ParentCity {
		@JsonProperty("Key")
		private String key;
		@JsonProperty("LocalizedName")
		private String localizedName;
		@JsonProperty("EnglishName")
		private String englishName;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SupplementalAdminAreas {
		@JsonProperty("Level")
		private int level;
		@JsonProperty("LocalizedName")
		private String localizedName;
		@JsonProperty("EnglishName")
		private String englishName;
	}
}
