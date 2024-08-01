package com.waither.domain.weather.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waither.weatherservice.exception.WeatherExceptionHandler;
import com.waither.weatherservice.response.WeatherErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenApiUtil {

	@Value("${openapi.forecast.key}")
	private String forecastKey;

	@Value("${openapi.accuweather.key}")
	private String accuweatherKey;

	// 기상청 Api (초단기, 단기)
	public List<ForeCastOpenApiResponse.Item> callForeCastApi(
		int nx,
		int ny,
		String baseDate,
		String baseTime,
		int numOfRows,
		String apiUrl
	) throws URISyntaxException {

		int pageNo = 1;
		String dataType = "JSON";

		WebClient webClient = WebClient.create();
		String uriString = apiUrl +
			"?serviceKey=" + forecastKey +
			"&numOfRows=" + numOfRows +
			"&pageNo=" + pageNo +
			"&dataType=" + dataType +
			"&base_date=" + baseDate +
			"&base_time=" + baseTime +
			"&nx=" + nx +
			"&ny=" + ny;

		URI uri = new URI(uriString);

		log.info("[*] 기상청 Api : {}", uri);

		ForeCastOpenApiResponse.Response response = webClient.get()
			.uri(uri)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(ForeCastOpenApiResponse.class)
			.onErrorResume(throwable -> {
				throw new WeatherExceptionHandler(WeatherErrorCode.WEATHER_OPENAPI_ERROR);
			})
			.block().getResponse();

		if (response.getHeader().getResultCode().equals("00")) {
			return response.getBody().getItems().getItem();
		} else {
			throw new WeatherExceptionHandler(WeatherErrorCode.WEATHER_OPENAPI_ERROR);
		}
	}

	// 기상청 OpenApi 반환값 팔터링 작업 (리스트, 반환받은 날짜 + 시간 기준으로 오름차순)
	public List<String> apiResponseListFilter(List<ForeCastOpenApiResponse.Item> items, String category) {
		return items.stream()
			.filter(item -> item.getCategory().equals(category))
			.sorted(Comparator.comparing(item -> item.getFcstDate() + item.getFcstTime()))
			.map(ForeCastOpenApiResponse.Item::getFcstValue)
			.toList();
	}

	// 기상청 OpenApi 반환값 추출 작업 (가장 가까운 시간대의 값 추출)
	public String apiResponseStringFilter(List<ForeCastOpenApiResponse.Item> items, String category) {
		return items.stream()
			.sorted(Comparator.comparing(item -> item.getFcstDate() + item.getFcstTime()))
			.filter(item -> item.getCategory().equals(category))
			.map(ForeCastOpenApiResponse.Item::getFcstValue)
			.findFirst().orElse(null);
	}

	// 재난 문자 Api
	public List<MsgOpenApiResponse.Item> callAdvisoryApi(String location, String today) throws
		URISyntaxException,
		IOException {

		WebClient webClient = WebClient.create();

		String uriString = "http://apis.data.go.kr/1360000/WthrWrnInfoService/getWthrWrnList" +
			"?serviceKey=" + forecastKey +
			"&numOfRows=" + "10" +
			"&pageNo=" + "1" +
			"&dataType=" + "JSON" +
			"&stnId=" + location +
			"&fromTmFc=" + today +
			"&toTmFc=" + today;

		URI uri = new URI(uriString);

		log.info("[*] 기상 특보 Api : {}", uri);

		String responseString = webClient.get()
			.uri(uri)
			.headers(httpHeaders -> {
				httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				httpHeaders.set("Accept", "*/*;q=0.9");
			})
			.retrieve().bodyToMono(String.class)
			.onErrorResume(throwable -> {
				throw new WeatherExceptionHandler(WeatherErrorCode.WEATHER_OPENAPI_ERROR);
			})
			.block();

		ObjectMapper objectMapper = new ObjectMapper();
		MsgOpenApiResponse.Response response = objectMapper.readValue(responseString, MsgOpenApiResponse.class)
			.getResponse();

		if (response.getHeader().getResultCode().equals("00")) {
			return response.getBody().getItems().getItem();
		} else if (response.getHeader().getResultCode().equals("03")) {
			log.info("특보 내용 없음");
			throw new WeatherExceptionHandler(WeatherErrorCode.WEATHER_OPENAPI_ERROR);
		} else {
			log.info("특보 오류");
			throw new WeatherExceptionHandler(WeatherErrorCode.WEATHER_OPENAPI_ERROR);
		}
	}

	public String convertLocalDateToString(LocalDate localDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return localDate.format(formatter);
	}

	public List<AirKoreaOpenApiResponse.Items> callAirKorea(String searchDate) throws URISyntaxException {
		int pageNo = 1;
		int numOfRows = 10;
		String dataType = "JSON";

		WebClient webClient = WebClient.create();
		String uriString = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMinuDustFrcstDspth" +
			"?serviceKey=" + forecastKey +
			"&numOfRows=" + numOfRows +
			"&pageNo=" + pageNo +
			"&returnType=" + dataType +
			"&searchDate=" + searchDate;

		URI uri = new URI(uriString);

		log.info("[*] 에어코리아 Api : {}", uri);

		AirKoreaOpenApiResponse.Response response = webClient.get()
			.uri(uri)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(AirKoreaOpenApiResponse.class)
			.blockOptional()
			.orElseThrow(() -> new WeatherExceptionHandler(WeatherErrorCode.WEATHER_OPENAPI_ERROR))
			.getResponse();

		if (response.getHeader().getResultCode().equals("00")) {

			List<AirKoreaOpenApiResponse.Items> items = response.getBody()
				.getItems()
				.stream()
				.sorted(Comparator.comparing(AirKoreaOpenApiResponse.Items::getInformData).reversed())
				.toList();

			String data = items.get(0).getInformGrade();

			parseAirKoreaResponseToMap(data);

			return items;
		} else {
			log.info("[*] OpenApi Error : {}", response.getHeader().getResultMsg());
			throw new WeatherExceptionHandler(WeatherErrorCode.WEATHER_OPENAPI_ERROR);
		}
	}

	public Map<String, String> parseAirKoreaResponseToMap(String data) {
		Map<String, String> map = Arrays.stream(data.split(","))
			.map(s -> s.split(" : "))
			.collect(HashMap::new, (m, arr) -> m.put(arr[0], arr[1]), HashMap::putAll);

		map.forEach((key, value) -> log.info(key + " -> " + value));

		return map;
	}

	// Accuweather Api 호출
	public String callAccuweatherLocationApi(double latitude, double longitude) throws
		URISyntaxException,
		JsonProcessingException {

		WebClient webClient = WebClient.create();
		String uriString = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search" +
			"?apikey=" + accuweatherKey +
			"&q=" + latitude + "," + longitude +
			"&language=" + "ko-kr" +
			"&details=" + "false" +
			"&toplevel=" + "false";

		URI uri = new URI(uriString);

		log.info("[*] Accuweather Location Api : {}", uri);

		String jsonString = webClient.get()
			.uri(uri)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve().bodyToMono(String.class)
			.onErrorResume(throwable -> {
				throw new WeatherExceptionHandler(WeatherErrorCode.WEATHER_OPENAPI_ERROR);
			})
			.block();

		ObjectMapper objectMapper = new ObjectMapper();
		AccuweatherLocationApiResponse response = objectMapper.readValue(jsonString,
			AccuweatherLocationApiResponse.class);

		log.info("[*] 위도: " + latitude + " 경도: " + longitude + " -> 지역명 : {}",
			response.getAdministrativeArea().getLocalizedName());
		return response.getAdministrativeArea().getLocalizedName();
	}
}
