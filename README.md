# SimCar Project

## 프로젝트 소개
- 중고차 쇼핑몰 프로젝트 서버 부분

## 기술 스택
### Backend
- Java 21
- Spring Boot 3.4.1
- Spring Data JPA
- H2 Database / MySQL

### Build Tools
- Gradle 8.11.1

### Documentation
- Springdoc OpenAPI (Swagger) 2.3.0

## 시작하기
### 요구사항
- JDK 21
- Gradle 8.11.1+
- MySQL (선택사항)

### 설치 및 실행
```bash
# 프로젝트 클론
git clone https://github.com/your-username/simcar.git

# 프로젝트 디렉토리로 이동
cd simcar

# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# API 문서
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs