FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
COPY . .
RUN ./mvnw clean verify
RUN java -Djarmode=layertools -jar target/app.jar extract

FROM adoptopenjdk:11-jre-hotspot
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]