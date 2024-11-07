package com.waither.domain.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waither.domain.user.entity.Setting;
import com.waither.domain.user.entity.Survey;
import com.waither.domain.user.entity.User;
import com.waither.domain.user.entity.UserMedian;
import com.waither.domain.user.entity.enums.Season;
import com.waither.domain.user.exception.UserErrorCode;
import com.waither.domain.user.repository.SettingRepository;
import com.waither.domain.user.repository.SurveyRepository;
import com.waither.domain.user.repository.UserRepository;
import com.waither.domain.weather.dto.response.MainWeatherResponse;
import com.waither.domain.weather.dto.response.ReportResponse;
import com.waither.domain.weather.entity.DailyWeather;
import com.waither.domain.weather.entity.ExpectedWeather;
import com.waither.domain.weather.entity.Region;
import com.waither.domain.weather.entity.WeatherAdvisory;
import com.waither.domain.weather.gps.GpsTransfer;
import com.waither.domain.weather.openapi.ForeCastOpenApiResponse;
import com.waither.domain.weather.openapi.MsgOpenApiResponse;
import com.waither.domain.weather.openapi.OpenApiUtil;
import com.waither.domain.weather.repository.DailyWeatherRepository;
import com.waither.domain.weather.repository.ExpectedWeatherRepository;
import com.waither.domain.weather.repository.RegionRepository;
import com.waither.domain.weather.repository.WeatherAdvisoryRepository;
import com.waither.global.event.WeatherEvent;
import com.waither.global.exception.CustomException;
import com.waither.global.response.WeatherErrorCode;
import com.waither.global.utils.CalculateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.waither.global.utils.WeatherMessageUtil.getCurrentSeason;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WeatherService {

	private final OpenApiUtil openApiUtil;
	private final DailyWeatherRepository dailyWeatherRepository;
	private final ExpectedWeatherRepository expectedWeatherRepository;
	private final WeatherAdvisoryRepository weatherAdvisoryRepository;
	private final RegionRepository regionRepository;
	private final SurveyRepository surveyRepository;
	private final UserRepository userRepository;
	private final ApplicationEventPublisher applicationEventPublisher;


	public void createExpectedWeather(
		int nx,
		int ny,
		String baseDate,
		String baseTime
	) throws URISyntaxException {

		// 1시간마다 업데이트 (1일 24회)
		List<ForeCastOpenApiResponse.Item> items = openApiUtil.callForeCastApi(nx, ny, baseDate, baseTime, 60,
			"http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst");

		List<String> expectedTempList = openApiUtil.apiResponseListFilter(items, "T1H");
		List<String> expectedRainList = openApiUtil.apiResponseListFilter(items, "RN1");
		List<String> expectedPtyList = openApiUtil.apiResponseListFilter(items, "PTY");
		List<String> expectedSkyList = openApiUtil.apiResponseListFilter(items, "SKY");

		ForeCastOpenApiResponse.Item item = items.get(0);

		List<Region> region = regionRepository.findRegionByXAndY(item.getNx(), item.getNy());
		String regionName = region.get(0).getRegionName();
		String key = regionName + "_" + item.getFcstDate() + "_" + item.getFcstTime();

		ExpectedWeather expectedWeather = ExpectedWeather.builder()
			.id(key)
			.expectedTemp(expectedTempList)
			.expectedRain(expectedRainList)
			.expectedPty(expectedPtyList)
			.expectedSky(expectedSkyList)
			.build();

		//이벤트 전송
		applicationEventPublisher.publishEvent(new WeatherEvent.ExpectRain(regionName, expectedRainList));

		ExpectedWeather save = expectedWeatherRepository.save(expectedWeather);
		log.info("[*] 예상 기후 : {}", save);
	}

	public void createDailyWeather(
		int nx,
		int ny,
		String baseDate,
		String baseTime
	) throws URISyntaxException {

		// Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 업데이트 (1일 8회)
		List<ForeCastOpenApiResponse.Item> items = openApiUtil.callForeCastApi(nx, ny, baseDate, baseTime, 350,
			"http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst");

		String pop = openApiUtil.apiResponseStringFilter(items, "POP");
		String tmp = openApiUtil.apiResponseStringFilter(items, "TMP");
		String tmn = openApiUtil.apiResponseStringFilter(items, "TMN");
		String tmx = openApiUtil.apiResponseStringFilter(items, "TMX");
		String reh = openApiUtil.apiResponseStringFilter(items, "REH");
		String vec = openApiUtil.apiResponseStringFilter(items, "VEC");
		String wsd = openApiUtil.apiResponseStringFilter(items, "WSD");

		ForeCastOpenApiResponse.Item item = items.get(0);

		List<Region> region = regionRepository.findRegionByXAndY(item.getNx(), item.getNy());
		String regionName = region.get(0).getRegionName();
		String key = regionName + "_" + item.getFcstDate() + "_" + item.getFcstTime();

		DailyWeather dailyWeather = DailyWeather.builder()
			.id(key)
			.pop(pop)
			.tmp(tmp)
			.tempMin(tmn)
			.tempMax(tmx)
			.humidity(reh)
			.windVector(vec)
			.windDegree(wsd)
			.build();

		//이벤트 전송
		applicationEventPublisher.publishEvent(new WeatherEvent.WindStrength(regionName, Double.valueOf(wsd)));

		DailyWeather save = dailyWeatherRepository.save(dailyWeather);
		log.info("[*] 하루 온도 : {}", save);

	}

	public void createWeatherAdvisory(double latitude, double longitude) throws URISyntaxException, IOException {
		LocalDate now = LocalDate.now();
		String today = openApiUtil.convertLocalDateToString(now);

		String location = GpsTransfer.convertGpsToRegionCode(latitude, longitude);
		List<MsgOpenApiResponse.Item> items = openApiUtil.callAdvisoryApi(location, today);

		String msg = items.get(0).getTitle();

		String key = location + "_" + today;
		WeatherAdvisory weatherAdvisory = WeatherAdvisory.builder()
			.id(key)
			.message(msg)
			.build();

		//이벤트 전송
		applicationEventPublisher.publishEvent(new WeatherEvent.WeatherWarning(location, msg));

		WeatherAdvisory save = weatherAdvisoryRepository.save(weatherAdvisory);
		log.info("[*] 기상 특보 : {}", save);
	}

	public void createAirKorea(String searchTime) throws URISyntaxException {
		openApiUtil.callAirKorea(searchTime);
	}

	public void convertLocation(double latitude, double longitude) throws URISyntaxException, JsonProcessingException {
		openApiUtil.callAccuweatherLocationApi(latitude, longitude);
	}

	public MainWeatherResponse getMainWeather(double latitude, double longitude) {
		LocalDateTime now = LocalDateTime.now();

		List<Region> regionList = regionRepository.findRegionByLatAndLong(latitude, longitude);
		if (regionList.isEmpty())
			throw new CustomException(WeatherErrorCode.REGION_NOT_FOUND);

		Region region = regionList.get(0);
		String regionName = region.getRegionName();

		log.info("[Main - api] region : {}", regionName);

		String expectedWeatherKey = regionName + "_" + convertLocalDateTimeToString(now);

		LocalDateTime dailyWeatherBaseTime = convertLocalDateTimeToDailyWeatherTime(now);
		String dailyWeatherKey = regionName + "_" + convertLocalDateTimeToString(dailyWeatherBaseTime.plusHours(1));

		log.info("[Main - api] dailyWeatherKey : {}", dailyWeatherKey);
		log.info("[Main - api] expectedWeatherKey : {}", expectedWeatherKey);

		DailyWeather dailyWeather = getDailyWeather(region, dailyWeatherKey, now);

		log.info(regionName + "[Main - api] DailyWeather : {}", dailyWeather);

		ExpectedWeather expectedWeather = getExpectedWeather(region, now);

		log.info(regionName + "[Main - api] ExpectedWeather : {}", expectedWeather);

		MainWeatherResponse weatherMainResponse = MainWeatherResponse.from(
			dailyWeather.getPop(), dailyWeather.getTmp(), dailyWeather.getTempMin(),
			dailyWeather.getTempMax(), dailyWeather.getHumidity(),
			dailyWeather.getWindVector(), dailyWeather.getWindDegree(),
			expectedWeather.getExpectedTemp(),
			expectedWeather.getExpectedRain(), expectedWeather.getExpectedPty(),
			expectedWeather.getExpectedSky()
		);
		log.info(regionName + "[Main - api] MainWeatherResponse : {}", weatherMainResponse);

		return weatherMainResponse;
	}

	/**
	 * 일일 날씨 정보를 조회합니다. 정보가 없을 경우 새로 생성합니다.
	 *
	 * @param region 지역 정보
	 * @param dailyWeatherKey 일일 날씨 정보 조회 키
	 * @param now 현재 시간
	 * @return DailyWeather 일일 날씨 정보 객체
	 * @throws CustomException 날씨 정보 생성 실패 시
	 */
	private DailyWeather getDailyWeather(Region region, String dailyWeatherKey, LocalDateTime now) {
		return dailyWeatherRepository.findById(dailyWeatherKey)
				.orElseGet(() -> {
					log.info("[Main / Report - api] Daily Weather Error");
					try {
						LocalDateTime dailyTime = convertLocalDateTimeToDailyWeatherTime(now);
						String[] baseTime = convertLocalDateTimeToString(dailyTime).split("_");
						createDailyWeather(region.getStartX(), region.getStartY(), baseTime[0], baseTime[1]);
					} catch (URISyntaxException e) {
						throw new CustomException(WeatherErrorCode.WEATHER_URI_ERROR);
					}
					return dailyWeatherRepository.findById(dailyWeatherKey)
							.orElseThrow(() -> new CustomException(WeatherErrorCode.DAILY_NOT_FOUND));
				});
	}

	/**
	 * 어제의 날씨 정보를 조회합니다. 정보가 없을 경우 새로 생성합니다.
	 *
	 * @param region 지역 정보
	 * @param yesterday 어제 날짜
	 * @return DailyWeather 어제의 일일 날씨 정보 객체
	 * @throws CustomException 날씨 정보 생성 실패 시 (WEATHER_URI_ERROR 또는 DAILY_NOT_FOUND)
	 */
	private DailyWeather getYesterdayWeather(Region region, LocalDateTime yesterday) {
		LocalDateTime yesterdayWeatherBaseTime = convertLocalDateTimeToDailyWeatherTime(yesterday);
		String yesterdayWeatherKey = region.getRegionName() + "_" + convertLocalDateTimeToString(yesterdayWeatherBaseTime.plusHours(1));

		return dailyWeatherRepository.findById(yesterdayWeatherKey)
				.orElseGet(() -> {
					log.info("[Report - api] Yesterday's Daily Weather Not Found");
					try {
						String[] baseTime = convertLocalDateTimeToString(yesterdayWeatherBaseTime).split("_");
						createDailyWeather(region.getStartX(), region.getStartY(), baseTime[0], baseTime[1]);
					} catch (URISyntaxException e) {
						throw new CustomException(WeatherErrorCode.WEATHER_URI_ERROR);
					}
					return dailyWeatherRepository.findById(yesterdayWeatherKey)
							.orElseThrow(() -> new CustomException(WeatherErrorCode.DAILY_NOT_FOUND));
				});
	}

	/**
	 * 예상 날씨 정보를 조회합니다. 정보가 없을 경우 새로 생성합니다.
	 *
	 * @param region 지역 정보
	 * @param now 현재 시간
	 * @return ExpectedWeather 예상 날씨 정보 객체
	 * @throws CustomException 날씨 정보 생성 실패 시
	 */
	private ExpectedWeather getExpectedWeather(Region region, LocalDateTime now) {
		String expectedWeatherKey = region.getRegionName() + "_" + convertLocalDateTimeToString(now);
		return expectedWeatherRepository.findById(expectedWeatherKey)
				.orElseGet(() -> {
					log.info("[Main - api] Expected Weather Error");
					try {
						String[] baseTime = convertLocalDateTimeToString(now.minusHours(1)).split("_");
						createExpectedWeather(region.getStartX(), region.getStartY(), baseTime[0], baseTime[1]);
					} catch (URISyntaxException e) {
						throw new CustomException(WeatherErrorCode.WEATHER_URI_ERROR);
					}
					return expectedWeatherRepository.findById(expectedWeatherKey)
							.orElseThrow(() -> new CustomException(WeatherErrorCode.EXPECTED_NOT_FOUND));
				});
	}

	public String convertLocalDateTimeToString(LocalDateTime time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = time.format(formatter);

		String[] lst = formattedDateTime.split(" ");
		String baseDate = lst[0].replace("-", "");

		String[] temp = lst[1].split(":");
		String baseTime = temp[0] + "00";

		return baseDate + "_" + baseTime;
	}

	public LocalDateTime convertLocalDateTimeToDailyWeatherTime(LocalDateTime time) {

		// DailyWeather 정보는 3시간마다
		List<Integer> scheduledHours = Arrays.asList(2, 5, 8, 11, 14, 17, 20, 23);

		int currentHour = time.getHour();
		int adjustedHour = scheduledHours.stream()
			.filter(hour -> hour <= currentHour)
			.reduce((first, second) -> second)
			.orElse(scheduledHours.get(scheduledHours.size() - 1));

		// 현재 시간이 첫 스케줄 시간(0시)보다 작을 경우, 전날의 마지막 스케줄 시간으로 설정
		if (currentHour < scheduledHours.get(0)) {
			time = time.minusDays(1);
		}

		return time.withHour(adjustedHour).withMinute(0).withSecond(0).withNano(0);
	}

	public List<Region> getRegionList() {
		return regionRepository.findAll();
	}

	public String convertGpsToRegionName(double latitude, double longitude) {
		return regionRepository.findRegionByLatAndLong(latitude, longitude).get(0).getRegionName();
	}

	public double getWindChill(double latitude, double longitude, LocalDateTime baseTime) {
		String regionName = convertGpsToRegionName(latitude, longitude);

		LocalDateTime dailyWeatherBaseTime = convertLocalDateTimeToDailyWeatherTime(baseTime.minusHours(1));
		String dailyWeatherKey = regionName + "_" + convertLocalDateTimeToString(dailyWeatherBaseTime);

		DailyWeather dailyWeather = dailyWeatherRepository.findById(dailyWeatherKey)
				.orElseThrow(() -> new CustomException(WeatherErrorCode.DAILY_NOT_FOUND));

		return CalculateUtil.calculateWindChill(Double.valueOf(dailyWeather.getTmp()), Double.valueOf(dailyWeather.getWindDegree()));
	}

	/**
	 * 특정 위치의 날씨 레포트 정보를 가져옵니다.
	 *
	 * @param latitude 위도
	 * @param longitude 경도
	 * @return ReportResponse 날씨 레포트 응답 객체
	 * @throws CustomException 지역을 찾을 수 없거나 날씨 정보 조회 실패 시
	 */
	public ReportResponse getReport(User currentUser, double latitude, double longitude) {
		LocalDateTime now = LocalDateTime.now();
		Season season = getCurrentSeason();

		User user = userRepository.findById(currentUser.getId())
				.orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

		UserMedian currentSeasonMedian = user.getUserMedian().stream()
				.filter(median -> median.getSeason() == season)
				.findFirst()
				.orElseThrow(() -> new CustomException(UserErrorCode.DATA_NOT_FOUND));

		// 입력받은 위도와 경도로 해당 지역 조회
		List<Region> regionList = regionRepository.findRegionByLatAndLong(latitude, longitude);
		if (regionList.isEmpty())
			throw new CustomException(WeatherErrorCode.REGION_NOT_FOUND);

		Region region = regionList.get(0);
		String regionName = region.getRegionName();

		log.info("[Report - api] region : {}", regionName);

		// 일일 날씨 정보 조회를 위한 키 생성
		LocalDateTime dailyWeatherBaseTime = convertLocalDateTimeToDailyWeatherTime(now);
		String dailyWeatherKey = regionName + "_" + convertLocalDateTimeToString(dailyWeatherBaseTime.plusHours(1));

		log.info("[Report - api] dailyWeatherKey : {}", dailyWeatherKey);

		// 금일, 어제 날씨 정보 조회
		DailyWeather dailyWeather = getDailyWeather(region, dailyWeatherKey, now);
		DailyWeather yesterdayWeather = getYesterdayWeather(region, now.minusDays(1));

		// 날씨 변화 계산
		ReportResponse.WeatherChange weatherChange = calculateWeatherChange(dailyWeather, yesterdayWeather);

		// 온도 인식 분석 수행
		ReportResponse.TemperaturePerception userPerception =
				analyzeTemperaturePerception(season, Double.parseDouble(dailyWeather.getTmp()));

		// 날씨 조언 생성
		List<String> advices = generateWeatherAdvices(currentSeasonMedian, season, dailyWeather, yesterdayWeather);

		return ReportResponse.builder()
				.date(now.toLocalDate())
				.advices(advices)
				.weatherChange(weatherChange)
				.userPerception(userPerception)
				.pop(dailyWeather.getPop())
				.temp(dailyWeather.getTmp())
				.tempMin(dailyWeather.getTempMin())
				.tempMax(dailyWeather.getTempMax())
				.humidity(dailyWeather.getHumidity())
				.windVector(dailyWeather.getWindVector())
				.windDegree(dailyWeather.getWindDegree())
				.userSetting(createUserSettingResponse(user.getSetting())) // 사용자 설정
				.build();
	}

	// "날씨 변화"
	private ReportResponse.WeatherChange calculateWeatherChange(DailyWeather today, DailyWeather yesterday) {
		double tempDifference = Double.parseDouble(today.getTmp()) - Double.parseDouble(yesterday.getTmp());

		double todayWindSpeed = Double.parseDouble(today.getWindVector());
		double yesterdayWindSpeed = Double.parseDouble(yesterday.getWindVector());

		// TODO : 일단 15% 변화를 기준
		int windChangeStatus;
		if (todayWindSpeed > yesterdayWindSpeed * 1.15) {
			windChangeStatus = 1;  // 증가
		} else if (todayWindSpeed < yesterdayWindSpeed * 0.85) {
			windChangeStatus = -1;  // 감소
		} else {
			windChangeStatus = 0;  // 비슷
		}

		return new ReportResponse.WeatherChange(tempDifference, windChangeStatus);
	}

	// "유저들의 답변"
	private ReportResponse.TemperaturePerception analyzeTemperaturePerception(Season season, double actualTemp) {

		// 현재 계절에 따른, 온도 범주 설정
		double tempRange = getTempRangeBySeason(season);
		double lowerTemp = actualTemp - tempRange;
		double upperTemp = actualTemp + tempRange;

		// 최근 18개월 동안의 설문 중 설정된 온도 범위 내의 응답들을 조회
		LocalDateTime startDate = LocalDateTime.now().minusMonths(18);
		List<Survey> relevantSurveys = surveyRepository.findByTempRangeAndDateAfter(lowerTemp, upperTemp, startDate);

		if (relevantSurveys.isEmpty()) {
			return new ReportResponse.TemperaturePerception(null, 0);
		}

		// 각 응답(1~5 level) 집계
		Map<Integer, Long> answerCounts = relevantSurveys.stream()
				.collect(Collectors.groupingBy(Survey::getAns, Collectors.counting()));

		// 가장 많은 응답 찾기
		Map.Entry<Integer, Long> mostCommon = answerCounts.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.orElseThrow(() -> new CustomException(WeatherErrorCode.WEATHER_URI_ERROR));

		// 백분율 계산
		int totalResponses = relevantSurveys.size();
		int percentage = (int) ((mostCommon.getValue() * 100) / totalResponses);

		return new ReportResponse.TemperaturePerception(mostCommon.getKey(), percentage);
	}

	// TODO : 계절에 따라 범주 다르게 설정 [해당 온도 차의 범주에 설문한 사람들을 대상으로 통계]
	private double getTempRangeBySeason(Season season) {
		switch (season) {
			case SUMMER:
				return 1.5;
			case WINTER:
				return 2.5;
			case SPRING_AUTUMN:
				return 2.0;
			default:
				return 2.0;
		}
	}

	// "날씨 세부 사항" View 구성을 위한 사용자 설정
	private ReportResponse.UserSetting createUserSettingResponse(Setting userSetting) {
		return new ReportResponse.UserSetting(
				userSetting.isPrecipitation(),
				userSetting.isWind(),
				userSetting.isDust()
		);
	}

	/*
	--------------------- 조언(ADVICE) ---------------------
	 */
	private List<String> generateWeatherAdvices(UserMedian userMedian, Season season, DailyWeather today, DailyWeather yesterday) {
		List<String> advices = new ArrayList<>();

		int todayTemp = Integer.parseInt(today.getTmp());
		int todayPop = Integer.parseInt(today.getPop());
		int todayHumidity = Integer.parseInt(today.getHumidity());
		double todayWindSpeed = Double.parseDouble(today.getWindVector());

		// 1. 사용자의 개인화된 체감 온도 레벨 분석
		int personalizedTempLevel = getPersonalizedTemperatureLevel(todayTemp, userMedian);
		advices.add(String.valueOf(personalizedTempLevel));

		// 2. 강수 확률에 대한 조언 메소드
		addPrecipitationAdvice(todayPop, advices, season);

		// 3. 계절별 조언
		addSeasonalAdvice(todayTemp, todayHumidity, todayWindSpeed, season, advices);

		// 4. 습도 관련 조언
		addHumidityAdvice(todayHumidity, advices);

		// 5. 바람 관련 조언
		addWindAdvice(todayWindSpeed, advices);

		// 6. 어제와 비교한 조언
		addComparisonAdvice(todayTemp, yesterday, advices);

		// 7. 일교차 관련 조언
		addTemperatureDifferenceAdvice(today, advices);

		return advices;
	}

	// "조언" 처음에 들어갈 [현재 기온이 사용자의 어떤 파트에 해당되는 지]
	private int getPersonalizedTemperatureLevel(int todayTemp, UserMedian userMedian) {
		if (todayTemp < userMedian.getMedianOf1And2()) {
			return 1;
		} else if (todayTemp < userMedian.getMedianOf2And3()) {
			return 2;
		} else if (todayTemp < userMedian.getMedianOf3And4()) {
			return 3;
		} else if (todayTemp < userMedian.getMedianOf4And5()) {
			return 4;
		} else {
			return 5;
		}
	}

	private void addPrecipitationAdvice(int todayPop, List<String> advices, Season season) {
		String precipitationMessage = season == Season.WINTER ? "오늘은 비 또는 눈이 올 확률이" : "오늘은 비가 올 확률이";
		if (todayPop > 70) {
			advices.add(precipitationMessage + " 매우 높습니다. 우산을 꼭 챙기세요.");
		} else if (todayPop > 50) {
			advices.add(precipitationMessage + " 있습니다. 우산을 챙기세요.");
		}
	}

	private void addSeasonalAdvice(int todayTemp, int todayHumidity, double todayWindSpeed, Season season, List<String> advices) {
		switch (season) {
			case SUMMER:
				if (todayTemp > 30) {
					advices.add("오늘은 매우 더운 날씨입니다. 야외 활동을 자제하고 수분 섭취를 늘리세요.");
				} else if (todayTemp > 25) {
					advices.add("오늘은 더운 날씨입니다. 시원하게 입으세요.");
				}

				double heatIndex = CalculateUtil.calculateHeatIndex(todayTemp, todayHumidity);
				if (heatIndex > 38) {
					advices.add("체감 온도가 매우 높습니다. 더위에 주의하세요.");
				} else if (heatIndex > 32) {
					advices.add("체감 온도가 높습니다. 충분한 수분 섭취가 필요합니다.");
				}
				break;

			case WINTER:
				if (todayTemp < 0) {
					advices.add("오늘은 매우 추운 날씨입니다. 보온에 신경 쓰고, 체온을 유지하세요.");
				} else if (todayTemp < 10) {
					advices.add("오늘은 쌀쌀합니다. 따뜻하게 입으세요.");
				}

				double windChill = CalculateUtil.calculateWindChill(todayTemp, todayWindSpeed);
				if (windChill < 0) {
					advices.add("체감 온도가 매우 낮습니다. 외출 시 충분한 보온이 필요합니다.");
				}
				break;

			case SPRING_AUTUMN:
				if (todayTemp > 25) {
					advices.add("오늘은 다소 더운 날씨입니다. 시원하게 입으세요.");
				} else if (todayTemp < 10) {
					advices.add("오늘은 쌀쌀한 날씨입니다. 겉옷을 챙기세요.");
				}

				double discomfortIndex = CalculateUtil.calculateDiscomfortIndex(todayTemp, todayHumidity);
				if (discomfortIndex > 80) {
					advices.add("불쾌지수가 높습니다. 외출 시 주의하세요.");
				}
				break;
		}
	}

	private void addHumidityAdvice(int todayHumidity, List<String> advices) {
		if (todayHumidity > 60) {
			advices.add("오늘은 습도가 매우 높습니다. 불쾌지수가 높을 수 있으니 주의하세요.");
		} else if (todayHumidity < 40) {
			advices.add("오늘은 건조합니다. 충분한 수분 섭취를 권장합니다.");
		}
	}

	private void addWindAdvice(double todayWindSpeed, List<String> advices) {
		if (todayWindSpeed > 12) {
			advices.add("오늘은 매우 강한 바람이 불고 있습니다. 외출 시 주의하세요.");
		} else if (todayWindSpeed > 7) {
			advices.add("오늘은 바람이 강하게 붑니다. 외출 시 주의하세요.");
		}
	}

	private void addComparisonAdvice(int todayTemp, DailyWeather yesterday, List<String> advices) {
		int yesterdayTemp = Integer.parseInt(yesterday.getTmp());
		if (todayTemp - yesterdayTemp > 5) {
			advices.add("어제보다 기온이 많이 올랐습니다. 옷차림에 유의하세요.");
		} else if (yesterdayTemp - todayTemp > 5) {
			advices.add("어제보다 기온이 많이 떨어졌습니다. 건강에 유의하세요.");
		}
	}

	private void addTemperatureDifferenceAdvice(DailyWeather today, List<String> advices) {
		float tempDiff = Float.valueOf(today.getTempMax()) - Float.valueOf(today.getTempMin());
		//int tempDiff = Integer.parseInt(today.getTempMax()) - Integer.parseInt(today.getTempMin());
		if (tempDiff > 10.0) {
			advices.add("오늘은 일교차가 큽니다. 겉옷을 챙기세요.");
		}
	}

}
