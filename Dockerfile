# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Build the app (skip tests for faster build)
RUN mvn -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built jar from the build stage
COPY --from=build /workspace/target/*.jar /app/app.jar

# Copy CSV so ExamService can read it by filename from working directory
COPY ["students.wlu.ca-Spring 2025 Waterloo Final Examination Schedule.csv", "/app/"]

# Render (and many PaaS) provide PORT; Spring reads server.port=${PORT:8080}
ENV JAVA_OPTS=""

EXPOSE 8080

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
