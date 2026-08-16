# ===== 1단계: 빌드 =====
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Gradle wrapper와 설정 파일 먼저 복사 (레이어 캐싱 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew
# 의존성 다운로드
RUN ./gradlew dependencies --no-daemon

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ===== 2단계: 실행 =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 컨테이너 타임존을 KST로 고정.
# alpine 기본 타임존은 UTC라, 설정이 없으면 LocalDateTime.now()가 UTC 시각을 반환한다.
# (출석 시각·세션 시각이 실제보다 9시간 이르게 저장/응답되던 원인)
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone
ENV TZ=Asia/Seoul

# 빌드 결과물만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
