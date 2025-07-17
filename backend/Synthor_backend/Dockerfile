# 1. 베이스 이미지로 Java 17 버전을 사용합니다. (프로젝트의 Java 버전에 맞게 변경 가능)
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리를 설정합니다.
WORKDIR /app

# 3. Gradle 래퍼 파일을 먼저 복사하여 Gradle 종속성을 캐시할 수 있도록 합니다.
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# 4. gradlew 파일에 실행 권한을 부여합니다.
RUN chmod +x ./gradlew

# 5. Gradle 종속성을 다운로드합니다.
RUN ./gradlew dependencies

# 5. 소스 코드를 복사합니다.
COPY src ./src

# 6. 애플리케이션을 빌드합니다. 테스트는 건너뜁니다.
RUN ./gradlew build -x test

# 7. 애플리케이션 실행 포트를 8080으로 노출합니다.
EXPOSE 8080

# 8. 최종적으로 빌드된 JAR 파일을 실행합니다.
CMD ["java", "-jar", "build/libs/Synthor_backend-0.0.1-SNAPSHOT.jar"]
