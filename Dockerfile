FROM maven:3.9-amazoncorretto-19 AS BUILD
WORKDIR /app/

COPY pom.xml .
COPY src src
RUN mvn install -DskipTests

FROM amazoncorretto:19.0.1-al2 AS RUNTIME

WORKDIR /app/
COPY --from=BUILD /app/target/*.jar app.jar

ENTRYPOINT exec java $JAVA_OPTS -jar /app/app.jar