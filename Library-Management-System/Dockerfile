# Use Java 21 (VERY IMPORTANT for Spring Boot 4)
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy project
COPY . .

# Build project
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Run app
CMD ["java", "-jar", "target/Library-Management-System-0.0.1-SNAPSHOT.jar"]