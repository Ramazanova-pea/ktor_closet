FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY closet-server-all.jar /app/app.jar

#COPY .env /app/.env
COPY application.yaml /app/application.yaml

##storage for images

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
