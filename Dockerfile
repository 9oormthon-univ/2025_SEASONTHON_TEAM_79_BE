# --- Runtime image (JRE 24) ---
FROM eclipse-temurin:24-jre
WORKDIR /app

# Gradle 빌드 산출물 경로
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 운영환경에서 Spring은 환경변수로도 설정 가능 (server.env는 SERVER_ENV 로 매핑)
# 기본값은 비워두고, docker run/compose에서 주입
ENV TZ=Asia/Seoul

# 내부 포트(앱에서 server.port=2000이면 2000, 기본 8080이면 8080)
# EXPOSE는 문서용이지만 관례상 적어둠. 실제 바인딩은 -p/compose가 결정.
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]