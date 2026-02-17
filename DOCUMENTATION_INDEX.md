# 📚 DOCUMENTATION INDEX

## Quick Navigation Guide

For different purposes, start here:

---

## 🚀 **I WANT TO START THE APPLICATION**

**→ Read**: `DEPLOYMENT_GUIDE.md` - "Quick Start" section

**One command**:
```bash
docker compose up --build
```

**Then access**:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

---

## ✅ **I WANT TO VERIFY ALL REQUIREMENTS ARE MET**

**→ Read**: `COMPLETE_REQUIREMENTS_CHECKLIST.md`

This document provides:
- ✅ All 8 functional requirements verified
- ✅ All 6 deliverables documented
- ✅ Code locations for each implementation
- ✅ Complexity analysis for algorithms
- ✅ Example calculations and scenarios

---

## 🧪 **I WANT TO TEST THE APIS**

**→ Read**: `API_TESTING_GUIDE.md`

This document provides:
- 5 complete end-to-end scenarios with curl commands
- All 11 API endpoints with examples
- Expected responses
- Error handling samples
- Postman collection import instructions

**Quick test**:
```bash
curl -X POST http://localhost:8080/api/passengers \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","phone":"+1-555-0000"}'
```

---

## 🚀 **I WANT TO DEPLOY TO PRODUCTION**

**→ Read**: `DEPLOYMENT_GUIDE.md`

Covers:
- Docker deployment
- AWS EC2 setup
- Azure App Service
- Google Cloud Run
- Kubernetes deployment (with manifests)
- Performance tuning
- Monitoring setup
- Troubleshooting

---

## 📋 **I WANT TO SUBMIT THIS PROJECT**

**→ Read**: `SUBMISSION_GUIDE.md`

Covers:
- Git repository setup
- Verification against requirements
- Pre-submission checklist
- What makes this submission strong
- Final verification steps

---

## 🏗️ **I WANT TO UNDERSTAND THE ARCHITECTURE**

**→ Read**: `README.md`

Covers:
- High-level architecture diagram
- Low-level design with class diagram
- Tech stack overview
- All API endpoints listed
- Assumptions documented

---

## 📊 **I WANT A SUMMARY OF WHAT'S COMPLETED**

**→ Read**: `PROJECT_COMPLETION_SUMMARY.md`

Covers:
- All requirements verification matrix
- What was accomplished
- Files added/modified
- Quick reference guides
- Submission readiness checklist

---

## 📮 **I WANT TO TEST WITH POSTMAN**

**→ Use**: `postman_collection.json`

**Steps**:
1. Download and install Postman
2. Click "Import" → select `postman_collection.json`
3. All 11 endpoints are ready to test
4. Pre-configured sample request bodies

---

## 🔧 **I WANT TO SETUP LOCAL DEVELOPMENT**

**→ Read**: `DEPLOYMENT_GUIDE.md` - "Local Development" section

**Prerequisites**:
- Java 17
- Maven
- PostgreSQL 15
- Redis 7

**Then**:
```bash
mvn spring-boot:run
```

---

## 🐳 **I WANT TO UNDERSTAND DOCKER SETUP**

**→ Read**: `DEPLOYMENT_GUIDE.md` - "Docker Deployment" section

**Files involved**:
- `Dockerfile` - Application image
- `docker-compose.yml` - Complete stack (PostgreSQL, Redis, App)

**Start everything**:
```bash
docker compose up --build
```

---

## 📝 **I WANT TO UNDERSTAND THE CODE**

**Start with**:
1. `src/main/java/com/hintro/airportpooling/AirportPoolingApplication.java` - Entry point
2. `src/main/java/com/hintro/airportpooling/controller/` - All REST endpoints
3. `src/main/java/com/hintro/airportpooling/service/MatchingService.java` - Core algorithm
4. `src/main/java/com/hintro/airportpooling/service/RoutePlanner.java` - Route optimization
5. `src/main/resources/db/migration/V1__init.sql` - Database schema

---

## 🔍 **I WANT TO CHECK IMPLEMENTATION DETAILS**

**Matching Algorithm**:
- Class: `RoutePlanner.java`
- Complexity: O(G * K²) where G = groups, K ≤ 4
- Reference: `COMPLETE_REQUIREMENTS_CHECKLIST.md` - DSA section

**Pricing Formula**:
- Class: `PricingService.java`
- Formula: (base + perKm * distance) * surge * (1 - discount)
- Reference: `COMPLETE_REQUIREMENTS_CHECKLIST.md` - Pricing section

**Concurrency Safety**:
- File: `MatchingService.java` lines 75-90
- Mechanism: Pessimistic locks + optimistic versioning
- Reference: `COMPLETE_REQUIREMENTS_CHECKLIST.md` - Concurrency section

**Database Indexing**:
- File: `src/main/resources/db/migration/V1__init.sql`
- Strategy: 5 indexes on hot queries
- Reference: `COMPLETE_REQUIREMENTS_CHECKLIST.md` - Database section

---

## 💾 **I WANT TO UNDERSTAND THE DATABASE**

**→ Read**: `COMPLETE_REQUIREMENTS_CHECKLIST.md` - Database Schema section

**Tables**:
- `passengers` - Rider information
- `cabs` - Vehicle availability
- `ride_requests` - Booking requests
- `ride_groups` - Shared cab groups
- `ride_group_members` - Group membership

**Schema location**: `src/main/resources/db/migration/V1__init.sql`
**Sample data**: `src/main/resources/db/migration/V2__sample_data.sql`

---

## 🎯 **I WANT TO UNDERSTAND PRICING**

**→ Read**: `COMPLETE_REQUIREMENTS_CHECKLIST.md` - Pricing section

**Formula breakdown**:
```
Fare = (BasePrice + PerKm * Distance) * SurgeMultiplier * (1 - GroupDiscount)

Where:
- BasePrice = $5
- PerKm = $1.20
- SurgeMultiplier = 1.0 to 2.0 (based on demand)
- GroupDiscount = up to 30% (increases with group size)
```

**Examples**:
- 1 person, 20km: ~$30.56
- 3 people, same route: ~$21.39 (30% discount applied)

---

## ⚡ **I WANT TO UNDERSTAND PERFORMANCE**

**→ Read**: `COMPLETE_REQUIREMENTS_CHECKLIST.md` - Performance Analysis section

**Key metrics**:
- Matching algorithm: O(K²) with K ≤ 4 (typical: 10-50ms per request)
- Batch processing: 100 RPS sustainable
- Latency target: < 300ms (achievable)
- Concurrency: 10,000 users via horizontal scaling

**Optimization techniques**:
- Pessimistic locks for race condition safety
- Database connection pooling (HikariCP)
- Redis caching for hot data
- Strategic indexes on query patterns

---

## 🐛 **I HAVE A PROBLEM / GETTING AN ERROR**

**→ Read**: `DEPLOYMENT_GUIDE.md` - Troubleshooting section

**Common issues**:
- Application won't start
- Database connection issues
- High memory usage
- Slow queries

Each has diagnostic steps and solutions.

---

## 📞 **I WANT HELP SETTING UP**

**Step 1**: Read `DEPLOYMENT_GUIDE.md` - Quick Start
**Step 2**: Choose option (Docker or Local)
**Step 3**: Follow specific section
**Step 4**: Run verification steps
**Step 5**: See `API_TESTING_GUIDE.md` for first test

---

## 📊 **I WANT TO MONITOR THE APPLICATION**

**→ Read**: `DEPLOYMENT_GUIDE.md` - Monitoring section

**Available endpoints**:
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Memory: `http://localhost:8080/actuator/metrics/jvm.memory.usage`
- HTTP Metrics: `http://localhost:8080/actuator/metrics/http.server.requests`

---

## 🎓 **I WANT TO LEARN THE REQUIREMENTS**

**→ Read in order**:

1. `SUBMISSION_GUIDE.md` - 5 min overview
2. `README.md` - Architecture understanding  
3. `COMPLETE_REQUIREMENTS_CHECKLIST.md` - Detailed breakdown
4. `PROJECT_COMPLETION_SUMMARY.md` - Completion status

---

## 📱 **I WANT TO TRY THE APIs QUICKLY**

**Option 1: Browser**
```
http://localhost:8080/swagger-ui.html
```
Click "Try it out" on any endpoint

**Option 2: Postman**
- Import `postman_collection.json`
- Click Send

**Option 3: curl**
- See `API_TESTING_GUIDE.md` for examples

---

---

## 📚 All Documentation Files

| File | Purpose | Pages | Read Time |
|------|---------|-------|-----------|
| `PROJECT_COMPLETION_SUMMARY.md` | Quick overview | 4 | 5 min |
| `README.md` | Architecture & design | 8 | 10 min |
| `COMPLETE_REQUIREMENTS_CHECKLIST.md` | Requirements verification | 16 | 20 min |
| `DEPLOYMENT_GUIDE.md` | Setup & deployment | 18 | 30 min |
| `API_TESTING_GUIDE.md` | API examples & testing | 12 | 15 min |
| `SUBMISSION_GUIDE.md` | Submission instructions | 8 | 10 min |
| `postman_collection.json` | Interactive API testing | - | - |
| `DOCUMENTATION_INDEX.md` | This file | - | 5 min |

---

## 🎯 Recommended Reading Order

### For Evaluators (30 mins total)
1. `PROJECT_COMPLETION_SUMMARY.md` (5 min) - Overview
2. `README.md` (10 min) - Architecture
3. `API_TESTING_GUIDE.md` Scenario 1 (5 min) - See it work
4. `COMPLETE_REQUIREMENTS_CHECKLIST.md` (10 min) - Verify requirements

### For Developers (45 mins total)
1. `DEPLOYMENT_GUIDE.md` Quick Start (5 min)
2. `README.md` (10 min)
3. `API_TESTING_GUIDE.md` (15 min)
4. Explore code in IDE (15 min)

### For DevOps/Deployment (30 mins total)
1. `DEPLOYMENT_GUIDE.md` - All sections (20 min)
2. `docker-compose.yml` - Review config (5 min)
3. Reference `COMPLETE_REQUIREMENTS_CHECKLIST.md` - Performance section (5 min)

---

## ✅ Verification Checklist

Before submission, verify:
- [ ] Application starts: `docker compose up --build`
- [ ] Health check: `curl http://localhost:8080/actuator/health`
- [ ] Swagger works: `http://localhost:8080/swagger-ui.html`
- [ ] All docs readable and consistent
- [ ] Postman collection imports successfully
- [ ] Git repo initialized and pushed
- [ ] SUBMISSION_GUIDE.md checklist complete

---

**Ready to start?** → `DEPLOYMENT_GUIDE.md` - Quick Start section
