# Synthor Backend

Synthor 백엔드 프로젝트는 AI와 NLP 기술을 활용하여 사용자의 요청에 따라 완성도 높은 데이터를 생성하는 API 서버입니다.

## ✨ 주요 기능 (Features)

- **AI 기반 데이터 생성**: 사용자가 요청한 필드와 조건에 맞춰 AI가 실제와 유사한 데이터를 생성합니다.
- **자연어 처리(NLP) 연동**: 자연어 요청을 분석하여 데이터 생성에 필요한 파라미터를 추출합니다.
- **RESTful API**: 데이터 생성을 위한 표준 RESTful API 엔드포인트를 제공합니다.
- **컨테이너화 지원**: Docker를 통해 어떤 환경에서든 쉽고 빠르게 배포하고 실행할 수 있습니다.

## 🚀 Live API & Documentation

프로젝트를 직접 실행하지 않고도 아래 링크에서 실시간으로 API를 테스트하고 문서를 확인할 수 있습니다.

**[Live API 바로가기 (Swagger UI)](https://synthor-back.onrender.com/swagger-ui/index.html#/data-generation-controller)**

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

    # External AI API URLs (example)
    ai.api.auto-generate.url=https://synthor-ai.onrender.com/api/fields/auto-generate
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
│   ├── config
│   │   └── WebConfig.java                  # 웹 관련 설정 (CORS 정책 등)
│   ├── controller
│   │   └── DataGenerationController.java   # 데이터 생성 API 요청을 처리하는 컨트롤러
│   ├── dto
│   │   ├── AiApiAutoGenerateResponse.java  # AI API 자동 생성 응답 DTO
│   │   ├── AiApiRequest.java               # AI API 요청 DTO
│   │   ├── AiApiResponse.java              # AI API 응답 DTO
│   │   ├── DataGenerationRequest.java      # 클라이언트의 데이터 생성 요청 DTO
│   │   ├── FieldDetail.java                # 필드 상세 정보를 담는 DTO
│   │   └── FieldRequest.java               # 개별 필드 요청을 담는 DTO
│   └── service
│       ├── AiApiService.java               # 외부 AI API와 통신하는 서비스
│       ├── DataFormattingService.java      # 생성된 데이터의 포맷팅을 담당하는 서비스
│       ├── DataGenerationService.java      # 데이터 생성 핵심 비즈니스 로직을 처리하는 서비스
│       └── NlpService.java                 # 자연어 처리(NLP)를 담당하는 서비스
├── src/main/resources
│   └── application.properties              # 애플리케이션 설정 파일
├── build.gradle                            # Gradle 빌드 스크립트
└── Dockerfile                              # Docker 이미지 생성을 위한 설정 파일

```



## 📊 지원하는 데이터 타입 (Supported Data Types)

다음은 Synthor API에서 지원하는 데이터 타입 목록입니다. `type` 필드에 아래 값을 사용하여 원하는 종류의 데이터를 생성할 수 있습니다.

| 카테고리 | 타입 (type) | 설명 |
| :--- | :--- | :--- |
| **개인정보 (한글)** | `korean_full_name` | 한국인 이름 (성+이름) |
| | `korean_first_name` | 한국인 이름 |
| | `korean_last_name` | 한국인 성 |
| | `korean_gender` | 성별 (남자, 여자) |
| | `korean_gender_with_non_binary` | 성별 (남자, 여자, 그 외) |
| **개인정보 (영문)** | `full_name` | 영문 이름 (성+이름) |
| | `first_name` | 영문 이름 |
| | `last_name` | 영문 성 |
| | `gender` | 성별 (Male, Female) |
| | `gender_with_non_binary` | 성별 (Male, Female, Non-binary) |
| **인터넷/기술** | `username` | 사용자 아이디 |
| | `password` | 비밀번호 (최소 8자, 대/소문자, 숫자, 특수문자 포함) |
| | `email_address` | 이메일 주소 |
| | `domain_name` | 도메인 주소 |
| | `url` | 전체 URL 주소 |
| | `user_agent` | 웹 브라우저 User Agent 문자열 |
| | `ip_v4_address` | IPv4 주소 |
| | `ip_v6_address` | IPv6 주소 |
| | `mac_address` | MAC 주소 |
| **앱/디바이스** | `app_name` | 애플리케이션 이름 |
| | `app_version` | 앱 버전 (e.g., 1.2.3) |
| | `app_bundle_id` | 앱 번들 ID (e.g., com.example.app) |
| | `device_model` | 디바이스 모델명 |
| | `device_brand` | 디바이스 제조사 |
| | `device_os` | 디바이스 운영체제 |
| **주소 (한글)** | `korean_address` | 대한민국 전체 주소 |
| | `korean_address_line_2` | 대한민국 상세 주소 (e.g., 101동 101호) |
| | `korean_city` | 대한민국 시/도 |
| | `korean_state` | 대한민국 도 |
| | `korean_postal_code` | 대한민국 우편번호 |
| **주소 (영문)** | `address` | 영문 전체 주소 |
| | `country` | 국가 이름 |
| | `address_line_2` | 영문 상세 주소 (e.g., Apt. 101) |
| | `street_address` | 영문 도로명 주소 |
| | `city` | 도시 이름 |
| | `state` | 주 이름 |
| | `postal_code` | 우편번호 |
| | `latitude` | 위도 |
| | `longitude` | 경도 |
| **회사/상업 (한글)** | `korean_company_name` | 한국 회사명 |
| | `korean_job_title` | 직책 (e.g., 팀장, 개발자) |
| | `korean_department_corporate` | 부서 (e.g., 인사팀, 개발팀) |
| | `korean_department_retail` | 판매 품목 (e.g., 의류, 가전) |
| | `korean_product_name` | 상품명 |
| | `korean_product_category` | 상품 카테고리 |
| | `korean_product_description` | 상품 설명 |
| | `korean_catch_phrase` | 캐치프레이즈 |
| **회사/상업 (영문)** | `company_name` | 영문 회사명 |
| | `job_title` | 직책 |
| | `department_corporate` | 부서 |
| | `department_retail` | 판매 품목 |
| | `product_name` | 상품명 |
| | `product_category` | 상품 카테고리 |
| | `product_description` | 상품 설명 |
| | `catch_phrase` | 캐치프레이즈 |
| | `product_price` | 상품 가격 |
| **금융** | `credit_card_number` | 신용카드 번호 |
| | `credit_card_type` | 신용카드 종류 (e.g., VISA) |
| | `bank_name` | 은행 이름 |
| | `bank_routing_number` | 은행 라우팅 번호 |
| | `iban` | IBAN 계좌번호 |
| | `swift_bic` | SWIFT/BIC 코드 |
| | `currency` | 통화 이름 |
| **기타** | `number` | 숫자 (범위 지정 가능) |
| | `datetime` | 날짜/시간 (범위 및 포맷 지정 가능) |
| | `time` | 시간 (범위 및 포맷 지정 가능) |
| | `phone_number` | 전화번호 (포맷 지정 가능) |
| | `avatar` | 아바타 이미지 URL |
| | `korean_color` | 색상 (한글) |
| | `color` | 색상 (영문) |
| | `language` | 언어 이름 |
| | `paragraphs` | 문단 (개수 지정 가능) |

```

## 💻 API 사용 예시 (API Usage Example)

Synthor API는 두 가지 주요 데이터 생성 방식을 제공합니다. 사용자가 데이터 구조를 직접 정의하는 **수동 생성**과, AI가 자연어 프롬프트로부터 데이터 구조를 추론하는 **AI 자동 생성** 방식입니다.

### 1. 수동 생성 (Manual Generation)

사용자가 `fields` 배열을 통해 데이터의 구조(필드명, 타입, 제약조건 등)를 직접 명시하여 요청합니다.

**cURL 요청 예시 (`/api/data/manual-generate`)**

```bash
curl -X 'POST' \
  'http://localhost:8081/api/data/manual-generate?format=json' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "count": 100,
  "fields": [
    {
      "name": "userPassword",
      "prompt": "타입을 full name 으로 바꿔줘",
      "type": "password",
      "constraints": {},
      "nullablePercent": 50
    },
    {
      "name": "userEmail",
      "prompt": "비밀번호는 최소 10자 이상이고 숫자와 특수문자가 포함되어야 해",
      "type": "email_address",
      "constraints": {},
      "nullablePercent": 0
    },
    {
      "name": "userCountry",
      "type": "country",
      "prompt": "",
      "constraints": {
        "options": [
          "South Korea",
          "USA",
          "Japan",
          "China"
        ]
      },
      "nullablePercent": 30
    }
  ]
}'
```

**Request Body 설명**

- `count`: 생성할 데이터 행(row)의 개수를 지정합니다.
- `fields`: 생성할 데이터 필드에 대한 정보를 담는 배열입니다.
    - `name` (String): 생성될 데이터의 필드명(key)입니다.
    - `type` (String): 생성할 데이터의 종류입니다. (지원하는 데이터 타입 표 참고)
    - `prompt` (String, Optional): 필드에 대한 자연어 설명입니다. AI가 이 설명을 분석하여 `type`과 `constraints`를 자동으로 수정할 수 있습니다.
    - `constraints` (Object, Optional): 데이터 생성에 대한 세부 제약 조건입니다.
    - `nullablePercent` (Integer, Optional): 이 필드가 `null` 값을 가질 확률 (0-100)입니다.

### 2. AI 자동 생성 (AI-Powered Generation)

자연어 프롬프트 하나만 전달하면, AI가 데이터의 전체 구조(필드, 타입, 제약조건)를 추론하여 자동으로 데이터를 생성합니다. 필드 구조를 전혀 모를 때 매우 유용합니다.

**cURL 요청 예시 (`/api/data/ai-generate`)**

```bash
curl -X 'POST' \
  'http://localhost:8080/api/data/ai-generate?format=json' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '"쇼핑몰에서 사용자 등록을 위한 정보 100개"'
```

**Request Body 설명**

- 요청 Body에는 생성하고 싶은 데이터에 대한 설명을 자연어 문자열로 전달합니다. 데이터 개수를 포함하면 `count`가 자동으로 설정됩니다.

**응답 예시 (JSON format)**

AI가 "쇼핑몰에서 사용자 등록을 위한 정보"라는 프롬프트를 해석하여 아래와 같이 `사용자_ID`, `비밀번호`, `이름`, `이메일`, `휴대폰_번호`, `주소` 등의 필드를 포함한 데이터를 생성합니다.

```json
{
  "count": 6,
  "fields": [
    {
      "name": "full_name",
      "type": "full_name",
      "constraints": {},
      "nullablePercent": 0
    },
    {
      "name": "email",
      "type": "email_address",
      "constraints": {},
      "nullablePercent": 0
    },
    {
      "name": "password",
      "type": "password",
      "constraints": {
        "minimum_length": 8
      },
      "nullablePercent": 0
    },
    {
      "name": "address",
      "type": "address",
      "constraints": {},
      "nullablePercent": 0
    },
    {
      "name": "phone",
      "type": "phone",
      "constraints": {},
      "nullablePercent": 0
    },
    {
      "name": "birth_date",
      "type": "datetime",
      "constraints": {
        "format": "yyyy-mm-dd"
      },
      "nullablePercent": 0
    }
  ]
}
```
