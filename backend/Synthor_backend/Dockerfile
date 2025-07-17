# --- 1. 빌드(Build) 스테이지 ---
# Spring Boot 애플리케이션을 빌드하기 위한 환경을 설정합니다.
FROM openjdk:17-jdk-slim AS build

# 작업 디렉토리를 설정합니다.
WORKDIR /workspace

# Gradle 빌드에 필요한 파일들을 먼저 복사합니다.
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# gradlew에 실행 권한을 부여합니다.
RUN chmod +x ./gradlew

# 소스 코드를 복사합니다.
COPY src ./src

# 이전 빌드 아티팩트를 정리하고, 실행 가능한 JAR 파일을 빌드합니다.
RUN ./gradlew clean bootJar -x test


# --- 2. 실행(Runtime) 스테이지 ---
# 실제 애플리케이션을 실행할 환경을 설정합니다.
FROM openjdk:17-slim

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일만 복사해옵니다.
COPY --from=build /workspace/build/libs/*.jar app.jar

# 애플리케이션 실행 포트를 8080으로 노출합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]