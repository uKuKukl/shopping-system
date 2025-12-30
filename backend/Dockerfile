FROM ccr.ccs.tencentyun.com/library/maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY maven-settings.xml /root/.m2/settings.xml
COPY src ./src
RUN mvn -s /root/.m2/settings.xml -q -DskipTests package

FROM ccr.ccs.tencentyun.com/library/eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
