FROM adoptopenjdk:11-jre-hotspot as builder
WORKDIR application
COPY . .
RUN ./mvnw clean verify
RUN java -Djarmode=layertools -jar target/app.jar extract

FROM adoptopenjdk:11-jre-hotspot
WORKDIR application
COPY --from=builder application/target/dependencies/ ./
COPY --from=builder application/target/spring-boot-loader/ ./
COPY --from=builder application/target/snapshot-dependencies/ ./
COPY --from=builder application/target/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]