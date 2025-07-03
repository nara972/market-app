FROM openjdk:17-alpine

RUN apk add --no-cache findutils

WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
COPY gradle gradle

RUN ./gradlew build || true

COPY build/libs/*.jar app.jar

# 🔽 디버깅 옵션을 외부에서 전달 가능하게 변경
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]