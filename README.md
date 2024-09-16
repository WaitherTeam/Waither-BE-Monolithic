
![banner](https://github.com/WaitherTeam/Waither-BE/assets/98632435/ddf26618-3c35-4bb5-b6f5-e8f3359fb8ed)

# Waither<img src="https://github.com/WaitherTeam/Waither-BE/assets/98632435/0df2a67b-7ebf-460b-9d54-574b5650d734" align=left width=120>

> 나만의 기상 비서, Waither

'Waither'는 사용자 개인 맞춤형 날씨 정보 알림 서비스입니다.

<br><br><br><br>
# ⛓️ Server Architecture
![Waither Project Server Architecture - Monolithic](https://github.com/user-attachments/assets/e60a83a0-b434-4eb5-94d5-923d676bd4e3)


<br><br>
# 🔍 Using API
- <a href="https://www.data.go.kr/data/15084084/openapi.do">기상청_단기예보 ((구)_동네예보) 조회서비스</a> <br>
- <a href="https://www.data.go.kr/data/15073861/openapi.do">한국환경공단_에어코리아_대기오염정보</a> <br>
- <a href="https://www.data.go.kr/data/15085289/openapi.do">기상청_꽃가루농도위험지수 조회서비스(3.0)</a> <br>
- <a href="https://www.data.go.kr/data/15000415/openapi.do">기상청_기상특보 조회서비스</a> <br>
- <a href="https://www.data.go.kr/data/15043565/openapi.do">기상청_태풍정보 조회서비스</a> <br>
<br><br>
# 🗄️ Project Architecture
```
├── apiGateway-service # HTTP Gateway
├── config-service # Configuration Management
├── Eureka # Spring Cloud Eureka
├── noti-service # Notification Service
│       └── com.waither.notiService
├── user-service  # User Service
│       └── com.waither.userService
├── weather-service
│       └── com.waither.weatherService
```
<br><br>
#  ✏️Commit Message Convention
| Emoticon | Commit Type | Desc |
| --- | --- | --- |
|  ✨  | feat | 새로운 기능 추가 |
| 🐛  | fix | 버그 수정 |
| 📝 | docs | 문서 수정 (md 파일) |
| ♻️  | refactor | 코드 리팩토링 |
| 💄  | style | 코드 formatting, 세미콜론 누락, 코드 자체의 변경이 없는 경우 |
| ✅  | test | 테스트 코드, 리팩토링 테스트 코드 추가 |
| 🚀  | chore | 패키지 매니저 수정 (Dockerfile, gradle, sh, yml) |
| 🚑  | !hotfix | 급하게 치명적인 버그를 고쳐야 하는 경우 |
