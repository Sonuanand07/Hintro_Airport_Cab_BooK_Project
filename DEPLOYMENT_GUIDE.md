# DEPLOYMENT GUIDE - Smart Airport Ride Pooling Backend

## Table of Contents
1. [Quick Start](#quick-start)
2. [Local Development](#local-development)
3. [Docker Deployment](#docker-deployment)
4. [Production Deployment](#production-deployment)
5. [Kubernetes Deployment](#kubernetes-deployment)
6. [Troubleshooting](#troubleshooting)
7. [Performance Tuning](#performance-tuning)
8. [Monitoring](#monitoring)

## Quick Start

### Option 1: Docker Compose (Recommended for beginners)
```bash
# Clone repository
cd d:\Backend_Hintro_Project

# Start everything with one command
docker compose up --build

# Wait for output: "Started AirportPoolingApplication in X seconds"

# Access the application
# API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

**What it sets up**:
- PostgreSQL 15 on port 5432
- Redis 7 on port 6379
- Spring Boot application on port 8080
- Automatic database migrations via Flyway

### Option 2: Local Setup
```bash
# Prerequisites: Java 17, Maven 3.9+, PostgreSQL 15, Redis 7

# 1. Start PostgreSQL (ensure it's running)
# Windows: net start postgresql-x64-15
# Linux/Mac: brew services start postgresql

# 2. Start Redis
# Windows: redis-server.exe
# Linux/Mac: redis-server

# 3. Clone and run
cd d:\Backend_Hintro_Project
mvn clean spring-boot:run

# Application ready at http://localhost:8080
```

---

## Local Development

### Setup Environment

**Windows Prerequisites**:
```powershell
# Install chocolatey (if not installed)
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Java 17
choco install openjdk17 -y

# Install Maven
choco install maven -y

# Install PostgreSQL
choco install postgresql15 -y
# During install, set password for postgres user

# Install Redis
choco install redis-64 -y
```

**Linux (Ubuntu) Prerequisites**:
```bash
# Update package manager
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install Maven
sudo apt install maven -y

# Install PostgreSQL
sudo apt install postgresql postgresql-contrib -y

# Install Redis
sudo apt install redis-server -y
```

### Database Setup (Local Development)

**Create Database**:
```sql
-- Connect as postgres or admin user
psql -U postgres

-- In psql:
CREATE DATABASE airport_pooling;
\q
```

**Verify Flyway Migrations**:
```bash
# After first application run, verify tables exist
psql -U postgres -d airport_pooling -c "\dt"

# Should show tables: passengers, cabs, ride_requests, ride_groups, ride_group_members
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=RoutePlannerTest

# Run tests with coverage
mvn clean test jacoco:report

# View coverage report
# Open target/site/jacoco/index.html in browser
```

### Development Configuration

**Create `application-dev.yml`**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/airport_pooling_dev
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update  # Auto-update for development
    show-sql: true
  devtools:
    restart:
      enabled: true

logging:
  level:
    root: INFO
    com.hintro: DEBUG
```

**Run with dev profile**:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

---

## Docker Deployment

### Build Docker Image

```bash
# Build image
docker build -t airport-pooling:1.0 .

# Tag for registry
docker tag airport-pooling:1.0 myregistry.azurecr.io/airport-pooling:1.0

# Push to registry
docker push myregistry.azurecr.io/airport-pooling:1.0
```

### Run Container Standalone

```bash
# Start PostgreSQL container
docker run -d \
  --name airport-postgres \
  -e POSTGRES_DB=airport_pooling \
  -e POSTGRES_PASSWORD=secure_pass_123 \
  -p 5432:5432 \
  postgres:15

# Start Redis container
docker run -d \
  --name airport-redis \
  -p 6379:6379 \
  redis:7

# Start application container
docker run -d \
  --name airport-pooling-app \
  -p 8080:8080 \
  --link airport-postgres:postgres \
  --link airport-redis:redis \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/airport_pooling \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=secure_pass_123 \
  -e REDIS_HOST=redis \
  airport-pooling:1.0

# Check logs
docker logs -f airport-pooling-app
```

### Using Docker Compose

**`docker-compose.yml` in project root**:
```yaml
version: "3.9"
services:
  postgres:
    image: postgres:15
    container_name: airport-pooling-postgres
    environment:
      POSTGRES_DB: airport_pooling
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres123
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7
    container_name: airport-pooling-redis
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: airport-pooling-app
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/airport_pooling
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres123
      REDIS_HOST: redis
      REDIS_PORT: 6379
      SPRING_PROFILES_ACTIVE: docker
    ports:
      - "8080:8080"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 5s
      retries: 3

  nginx:
    image: nginx:latest
    container_name: airport-pooling-nginx
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    ports:
      - "80:80"
    depends_on:
      - app

volumes:
  pgdata:
```

**Start everything**:
```bash
docker compose up -d

# View logs
docker compose logs -f app

# Stop everything
docker compose down

# Clean up volumes
docker compose down -v
```

---

## Production Deployment

### Pre-Deployment Checklist

- [ ] Application builds successfully: `mvn clean package`
- [ ] All tests pass: `mvn test`
- [ ] Docker image builds: `docker build -t app:latest .`
- [ ] Security scanning done: `docker scan app:latest`
- [ ] Environment variables are secure
- [ ] Database backups are configured
- [ ] SSL/TLS certificates are valid
- [ ] Load balancer health checks configured
- [ ] Monitoring and logging are set up

### 1. AWS EC2 Deployment

**Launch EC2 Instance**:
```bash
# Using AWS CLI
aws ec2 run-instances \
  --image-id ami-0c55b159cbfafe1f0 \
  --instance-type t3.medium \
  --key-name my-key \
  --security-groups airport-pooling-sg \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=airport-pooling}]'
```

**Configure Security Group**:
```bash
# SSH
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp \
  --port 22 \
  --cidr 0.0.0.0/0

# HTTP
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0

# HTTPS
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp \
  --port 443 \
  --cidr 0.0.0.0/0
```

**Connect to Instance**:
```bash
ssh -i my-key.pem ec2-user@<instance-public-ip>

# Update system
sudo yum update -y
sudo yum install java-17-amazon-corretto -y

# Install Docker
sudo amazon-linux-extras install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user
```

**Deploy Application**:
```bash
# Clone repository
git clone <repo-url>
cd airport-pooling

# Using Docker
docker pull myregistry/airport-pooling:1.0
docker run -d \
  -p 80:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://rds-endpoint:5432/airport_pooling \
  -e SPRING_DATASOURCE_USERNAME=admin \
  -e SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD} \
  myregistry/airport-pooling:1.0
```

### 2. Azure App Service Deployment

```bash
# Create resource group
az group create \
  --name airport-pooling-rg \
  --location eastus

# Create App Service plan
az appservice plan create \
  --name airport-pooling-plan \
  --resource-group airport-pooling-rg \
  --sku B2 \
  --is-linux

# Create web app
az webapp create \
  --resource-group airport-pooling-rg \
  --plan airport-pooling-plan \
  --name airport-pooling-app \
  --runtime "JAVA|17-java17"

# Configure connection strings
az webapp config connection-string set \
  --resource-group airport-pooling-rg \
  --name airport-pooling-app \
  --settings \
    PostgresConnection="Server=<db-host>;Database=airport_pooling;User Id=admin;Password=${DB_PASSWORD};" \
    RedisConnection="<redis-host>:6379"

# Deploy from local JAR
az webapp deploy \
  --resource-group airport-pooling-rg \
  --name airport-pooling-app \
  --src-path target/airport-pooling-0.1.0.jar \
  --type jar
```

### 3. Google Cloud Run Deployment

```bash
# Set project
gcloud config set project airport-pooling-project

# Build and push image
gcloud builds submit --tag gcr.io/airport-pooling-project/airport-pooling

# Deploy to Cloud Run
gcloud run deploy airport-pooling \
  --image gcr.io/airport-pooling-project/airport-pooling \
  --platform managed \
  --region us-central1 \
  --memory 512Mi \
  --cpu 1 \
  --set-env-vars SPRING_DATASOURCE_URL=jdbc:postgresql://cloudsql-proxy:5432/airport_pooling \
  --allow-unauthenticated
```

---

## Kubernetes Deployment

### Prerequisites
```bash
# Install kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/windows/amd64/kubectl.exe"

# Install Helm (optional, for package management)
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```

### Create Kubernetes Resources

**1. Namespace**:
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: airport-pooling
```

**2. ConfigMap**:
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: airport-pooling-config
  namespace: airport-pooling
data:
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres:5432/airport_pooling"
  REDIS_HOST: "redis"
  REDIS_PORT: "6379"
```

**3. Secret**:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: airport-pooling-secret
  namespace: airport-pooling
type: Opaque
stringData:
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: your-secure-password
  SPRING_DATASOURCE_PASSWORD_B64: <base64-encoded-password>
```

**4. PostgreSQL StatefulSet**:
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
  namespace: airport-pooling
spec:
  serviceName: postgres
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_DB
          value: airport_pooling
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: airport-pooling-secret
              key: SPRING_DATASOURCE_PASSWORD
        volumeMounts:
        - name: pgdata
          mountPath: /var/lib/postgresql/data
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
  volumeClaimTemplates:
  - metadata:
      name: pgdata
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 10Gi
---
apiVersion: v1
kind: Service
metadata:
  name: postgres
  namespace: airport-pooling
spec:
  clusterIP: None
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
```

**5. Redis Deployment**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis
  namespace: airport-pooling
spec:
  replicas: 1
  selector:
    matchLabels:
      app: redis
  template:
    metadata:
      labels:
        app: redis
    spec:
      containers:
      - name: redis
        image: redis:7
        ports:
        - containerPort: 6379
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
---
apiVersion: v1
kind: Service
metadata:
  name: redis
  namespace: airport-pooling
spec:
  selector:
    app: redis
  ports:
  - port: 6379
    targetPort: 6379
```

**6. Application Deployment**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: airport-pooling
  namespace: airport-pooling
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: airport-pooling
  template:
    metadata:
      labels:
        app: airport-pooling
    spec:
      containers:
      - name: app
        image: myregistry.azurecr.io/airport-pooling:1.0
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
        envFrom:
        - configMapRef:
            name: airport-pooling-config
        - secretRef:
            name: airport-pooling-secret
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/ready
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
          failureThreshold: 3
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
---
apiVersion: v1
kind: Service
metadata:
  name: airport-pooling
  namespace: airport-pooling
spec:
  type: LoadBalancer
  selector:
    app: airport-pooling
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
```

**Apply to Cluster**:
```bash
# Create namespace
kubectl create namespace airport-pooling

# Apply configurations
kubectl apply -f configmap.yaml
kubectl apply -f secret.yaml
kubectl apply -f postgres.yaml
kubectl apply -f redis.yaml
kubectl apply -f deployment.yaml

# Verify deployment
kubectl get pods -n airport-pooling
kubectl get services -n airport-pooling
kubectl logs -n airport-pooling -l app=airport-pooling

# Access application
# Get LoadBalancer IP
kubectl get service airport-pooling -n airport-pooling
# Access at http://<EXTERNAL-IP>
```

---

## Troubleshooting

### Application Won't Start

```bash
# Check logs
docker logs airport-pooling-app
# or
kubectl logs -n airport-pooling -l app=airport-pooling

# Common issues:
# 1. Database not reachable
# 2. Redis not running
# 3. Port already in use
# 4. Insufficient memory
```

### Database Connection Issues

```bash
# Test PostgreSQL connection
psql -h localhost -U postgres -d airport_pooling -c "SELECT COUNT(*) FROM passengers;"

# For Docker
docker exec airport-pooling-postgres psql -U postgres -d airport_pooling -c "\dt"

# Check connection pooling
kubectl exec -n airport-pooling <pod-name> -- curl http://localhost:8080/actuator/metrics/hikaricp.connections
```

### High Memory Usage

```bash
# Check memory metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.usage

# Restart with heap settings
docker run -e JAVA_OPTS="-Xmx512m -Xms256m" airport-pooling:1.0

# For Kubernetes, update deployment resources:
kubectl set resources deployment airport-pooling \
  -n airport-pooling \
  --limits=memory=1Gi,cpu=1000m \
  --requests=memory=512Mi,cpu=500m
```

### Slow Queries

```bash
# Enable slow query log in PostgreSQL
docker exec airport-pooling-postgres psql -U postgres << EOF
ALTER SYSTEM SET log_statement = 'all';
ALTER SYSTEM SET log_duration = on;
SELECT pg_reload_conf();
EOF

# Check slow queries
docker exec airport-pooling-postgres tail -f /var/log/postgresql/postgresql.log
```

---

## Performance Tuning

### Application Tuning

**`application-prod.yml`**:
```yaml
server:
  tomcat:
    threads:
      max: 500
      min-spare: 50
    max-connections: 2000
    accept-count: 100
    connection-timeout: 30000
  compression:
    enabled: true
    min-response-size: 1024

spring:
  datasource:
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  jpa:
    hibernate:
      jdbc:
        batch_size: 20
        fetch_size: 100

cache:
  redis:
    time-to-live: 600000  # 10 minutes
```

### Database Tuning

```sql
-- PostgreSQL optimization
ALTER SYSTEM SET shared_buffers = '4GB';
ALTER SYSTEM SET effective_cache_size = '12GB';
ALTER SYSTEM SET work_mem = '100MB';
ALTER SYSTEM SET main_wal_level = 'replica';
ALTER SYSTEM SET max_connections = 500;

SELECT pg_reload_conf();

-- Create indexes for hot queries
CREATE INDEX CONCURRENTLY idx_requests_pending_recent 
ON ride_requests(status, desired_pickup_time DESC) 
WHERE status = 'PENDING';

-- Analyze tables for query planner
ANALYZE ride_requests;
ANALYZE ride_groups;
```

### Redis Optimization

```bash
docker run -d \
  -p 6379:6379 \
  redis:7 redis-server \
  --maxmemory 2gb \
  --maxmemory-policy allkeys-lru \
  --save 60 1000 \
  --appendonly yes
```

---

## Monitoring

### Health Checks

```bash
# Application health
curl http://localhost:8080/actuator/health

# Detailed health
curl http://localhost:8080/actuator/health?group=liveness
curl http://localhost:8080/actuator/health?group=readiness
```

### Metrics

```bash
# List all metrics
curl http://localhost:8080/actuator/metrics

# Specific metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.usage
curl http://localhost:8080/actuator/metrics/system.cpu.usage
curl http://localhost:8080/actuator/metrics/http.server.requests
```

### Prometheus Integration

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'airport-pooling'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
```

### ELK Stack (Elasticsearch, Logstash, Kibana)

```yaml
# Add to pom.xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

```yaml
# application.yml
spring:
  sleuth:
    sampler:
      probability: 1.0
  zipkin:
    base-url: http://localhost:9411
```

---

## Final Verification

Before considering the application production-ready, verify:

```bash
✓ Application starts without errors
✓ Database migrations run automatically
✓ All 11 API endpoints functional
✓ Swagger UI accessible
✓ Health checks passing
✓ Metrics available
✓ Logs are structured and informative
✓ Performance under load is acceptable
✓ Security headers are present
✓ CORS is properly configured
✓ Rate limiting is effective
✓ Database backups scheduled
✓ Monitoring alerts configured
✓ Incident response procedures documented
```

Ready for production deployment!
