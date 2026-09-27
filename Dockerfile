# =========================================================
# LOUKY BANK - CreditFlow
# Dockerfile multi-stage pour Spring Boot
# =========================================================

# ---------- Stage 1 : Build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copier le pom.xml et telecharger les dependances (cache Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et builder
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Stage 2 : Runtime ----------
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Creer un utilisateur non-root pour la securite
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Port par defaut (Railway/Render injectent $PORT)
EXPOSE 8080

# Configuration JVM optimisee pour un environnement contraint
ENV JAVA_OPTS="-Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError"
ENV PORT=8080

# Lancer l'application avec le port injecte
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]