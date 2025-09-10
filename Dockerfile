# TODO: 이거 되는지 확인

# Java 24 지원을 위한 베이스 이미지 (현재 프리뷰)
FROM openjdk:24-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 시스템 패키지 업데이트 및 필수 도구 설치
RUN apt-get update && apt-get install -y \
    curl \
    netcat-openbsd \
    && rm -rf /var/lib/apt/lists/*

# Gradle 빌드 결과물 복사
COPY build/libs/*.jar app.jar

# 로그 디렉토리 생성 (log4j2 설정 고려)
RUN mkdir -p /app/logs

# 애플리케이션 포트 노출
EXPOSE 8080

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms512m -Xmx1024m \
    -Djava.net.preferIPv4Stack=true \
    -XX:+UseG1GC \
    -XX:+PrintGCDetails \
    -XX:+PrintGCTimeStamps"

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]
