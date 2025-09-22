# --- 1단계: 빌드 ---
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# gradlew 권한 문제 방지
COPY gradlew .
RUN chmod +x gradlew

# 나머지 파일 복사
COPY . .

# Jar 빌드
RUN ./gradlew clean bootJar -x test --no-daemon

# --- 2단계: 실행 ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]