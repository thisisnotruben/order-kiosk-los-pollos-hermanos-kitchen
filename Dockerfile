from eclipse-temurin:latest

RUN ./gradlew build
COPY ./build/libs/kitchen-1.0.0.jar .

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/kitchen-1.0.0.jar"]
