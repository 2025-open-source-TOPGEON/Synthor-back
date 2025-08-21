# Synthor Backend

Synthor 백엔드 프로젝트는 AI와 NLP 기술을 활용하여 사용자의 요청에 따라 완성도 높은 데이터를 생성하는 API 서버입니다.

## ✨ 주요 기능 (Features)

- **AI 기반 데이터 생성**: 사용자가 요청한 필드와 조건에 맞춰 AI가 실제와 유사한 데이터를 생성합니다.
- **자연어 처리(NLP) 연동**: 자연어 요청을 분석하여 데이터 생성에 필요한 파라미터를 추출합니다.
- **RESTful API**: 데이터 생성을 위한 표준 RESTful API 엔드포인트를 제공합니다.
- **컨테이너화 지원**: Docker를 통해 어떤 환경에서든 쉽고 빠르게 배포하고 실행할 수 있습니다.

## 🛠️ 기술 스택 (Tech Stack)

- **Language**: Java 17
- **Framework**: Spring Boot
- **Build Tool**: Gradle
- **Containerization**: Docker

## 🚀 시작하기 (Getting Started)

### 사전 요구사항 (Prerequisites)

- Java (JDK) 17 이상
- Gradle 8.x

### 설치 및 실행 (Installation & Run)

1.  **저장소 복제 (Clone the repository)**
    ```bash
    git clone https://github.com/your-username/Synthor_backend.git
    cd Synthor_backend
    ```

2.  **환경 설정 (Configuration)**
    `src/main/resources/application.properties` 파일에 필요한 설정을 추가합니다. 특히 외부 AI API를 사용하는 경우, API 키와 같은 민감한 정보를 필수로 설정해야 합니다.
    ```properties
    # application.properties
    server.port=8080

    # AI API Key (Example)
    # api.gemini.key=YOUR_API_KEY
    ```

3.  **프로젝트 빌드 (Build the project)**
    ```bash
    ./gradlew build
    ```

4.  **애플리케이션 실행 (Run the application)**
    ```bash
    ./gradlew bootRun
    ```
    애플리케이션이 시작되면 `http://localhost:8080` 에서 접속할 수 있습니다.

## 🐳 Docker로 실행하기 (Running with Docker)

### Docker 이미지 빌드

```bash
docker build -t synthor-backend .
```

### Docker 컨테이너 실행

```bash
docker run -p 8080:8080 synthor-backend
```

## 📁 프로젝트 구조 (Project Structure)

```
.
├── src/main/java/com/synthor/backend
│   ├── config          # 애플리케이션 설정 (WebConfig 등)
│   ├── controller      # API 요청을 처리하는 컨트롤러
│   ├── dto             # 데이터 전송 객체 (Request/Response)
│   └── service         # 핵심 비즈니스 로직을 담는 서비스
├── src/main/resources
│   └── application.properties # 애플리케이션 설정 파일
├── build.gradle        # Gradle 빌드 스크립트
└── Dockerfile          # Docker 이미지 생성을 위한 설정 파일
```
