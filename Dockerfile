# Stage 1 - Build Angular App
FROM node:22 AS ngbuild

WORKDIR /clientsrc

# Install Angular CLI and TypeScript globally
RUN npm i -g @angular/cli@19.2.1 typescript

# Copy Angular project files
COPY ./finalprojectfrontendclient/angular.json .
COPY ./finalprojectfrontendclient/package.json .
COPY ./finalprojectfrontendclient/tsconfig.json .
COPY ./finalprojectfrontendclient/tsconfig.app.json .
COPY ./finalprojectfrontendclient/ngsw-config.json .
COPY ./finalprojectfrontendclient/src src
COPY ./finalprojectfrontendclient/public public

# Fix SCSS references to CSS
RUN find src -type f -name "*.ts" -exec sed -i 's/\.scss/\.css/g' {} \;

# Install dependencies with force flag to handle version conflicts
RUN npm install --force

# Build the Angular application
RUN ng build --configuration production

# Stage 2 - Build Spring Boot App
FROM maven:3.9.9-eclipse-temurin-23 AS javabuild

WORKDIR /app

# Copy Spring Boot project files
COPY ./resume-ai-backend/pom.xml .
COPY ./resume-ai-backend/.mvn .mvn
COPY ./resume-ai-backend/mvnw .
COPY ./resume-ai-backend/src src
COPY ./resume-ai-backend/schema.sql .

# Grant executable permissions for mvnw
RUN chmod +x mvnw

# Copy Angular build output to Spring Boot static resources
COPY --from=ngbuild /clientsrc/dist/finalprojectfrontendclient/browser src/main/resources/static

# Verify that prompt files exist in resources directory
RUN echo "Checking for prompt files in resources directory"
RUN ls -la src/main/resources/

# Build the Spring Boot application
RUN ./mvnw package -DskipTests

# Stage 3 - Run the Application
FROM openjdk:23

WORKDIR /app

# Copy built JAR
COPY --from=javabuild /app/target/resume-ai-backend-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE ${PORT}

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]

# run
# docker build -t cihansifan/anyname:v0.0.1 .

# container
# docker run -p 8085:8080 cihansifan/anyname:v0.0.1