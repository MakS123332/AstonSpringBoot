version: '3.8'

services:
  # База данных для user-service
  postgres:
    image: postgres:15-alpine
    container_name: postgres-db
    environment:
      POSTGRES_DB: userdb
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-db/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - microservices-net
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U user -d userdb"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Kafka и Zookeeper
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
    networks:
      - microservices-net
    healthcheck:
      test: ["CMD-SHELL", "echo stat | nc localhost 2181 || exit 1"]
      interval: 10s
      timeout: 5s
      retries: 5

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    container_name: kafka
    depends_on:
      zookeeper:
        condition: service_healthy
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
    ports:
      - "9092:9092"
    networks:
      - microservices-net
    healthcheck:
      test: ["CMD-SHELL", "kafka-topics --bootstrap-server kafka:9092 --list || exit 1"]
      interval: 10s
      timeout: 5s
      retries: 10

  # Service Discovery (Eureka)
  eureka-server:
    image: openjdk:17-jdk-slim
    container_name: eureka-server
    ports:
      - "8761:8761"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      JAVA_OPTS: "-Xmx256m -Xms128m"
    volumes:
      - ./eureka-server/target/eureka-server-1.0.0.jar:/app.jar
    command: java -jar /app.jar
    networks:
      - microservices-net
    depends_on:
      - postgres
      - kafka
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5

  # External Configuration Server
  config-server:
    image: openjdk:17-jdk-slim
    container_name: config-server
    ports:
      - "8888:8888"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      GIT_URI: "https://github.com/your-username/config-repo.git"
      JAVA_OPTS: "-Xmx256m -Xms128m"
    volumes:
      - ./config-server/target/config-server-1.0.0.jar:/app.jar
    command: java -jar /app.jar
    networks:
      - microservices-net
    depends_on:
      eureka-server:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8888/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5

  # API Gateway
  api-gateway:
    image: openjdk:17-jdk-slim
    container_name: api-gateway
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_CLOUD_CONFIG_URI: http://config-server:8888
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://eureka-server:8761/eureka/
      JAVA_OPTS: "-Xmx256m -Xms128m"
    volumes:
      - ./api-gateway/target/api-gateway-1.0.0.jar:/app.jar
    command: java -jar /app.jar
    networks:
      - microservices-net
    depends_on:
      config-server:
        condition: service_healthy
      eureka-server:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5

  # User Service
  user-service:
    image: openjdk:17-jdk-slim
    container_name: user-service
    ports:
      - "8082:8082"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_CLOUD_CONFIG_URI: http://config-server:8888
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://eureka-server:8761/eureka/
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/userdb
      SPRING_DATASOURCE_USERNAME: user
      SPRING_DATASOURCE_PASSWORD: password
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      JAVA_OPTS: "-Xmx512m -Xms256m"
    volumes:
      - ./user-service/target/user-service-1.0.0.jar:/app.jar
    command: java -jar /app.jar
    networks:
      - microservices-net
    depends_on:
      postgres:
        condition: service_healthy
      kafka:
        condition: service_healthy
      config-server:
        condition: service_healthy
      eureka-server:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8082/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 10

  # Notification Service
  notification-service:
    image: openjdk:17-jdk-slim
    container_name: notification-service
    ports:
      - "8081:8081"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_CLOUD_CONFIG_URI: http://config-server:8888
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://eureka-server:8761/eureka/
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      SPRING_MAIL_HOST: smtp.gmail.com
      SPRING_MAIL_PORT: 587
      SPRING_MAIL_USERNAME: ${MAIL_USERNAME:-test@example.com}
      SPRING_MAIL_PASSWORD: ${MAIL_PASSWORD:-password}
      JAVA_OPTS: "-Xmx512m -Xms256m"
    volumes:
      - ./notification-service/target/notification-service-1.0.0.jar:/app.jar
    command: java -jar /app.jar
    networks:
      - microservices-net
    depends_on:
      kafka:
        condition: service_healthy
      config-server:
        condition: service_healthy
      eureka-server:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 10

volumes:
  postgres_data:

networks:
  microservices-net:
    driver: bridge