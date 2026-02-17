# API TESTING AND USAGE GUIDE

## Overview
This guide provides complete examples for testing all 11 API endpoints of the Smart Airport Ride Pooling system.

---

## Prerequisites

1. **Application Running**:
   ```bash
   docker compose up --build
   # OR
   mvn spring-boot:run
   ```

2. **Available Tools**:
   - `curl` (included with most systems)
   - Postman (import `postman_collection.json`)
   - Browser (for Swagger UI)

3. **Swagger UI Access**:
   - http://localhost:8080/swagger-ui.html
   - http://localhost:8080/api-docs (OpenAPI JSON)

---

## Test Scenarios

### Scenario 1: Complete Service Request (Single Passenger from Home to Airport)

**Step 1: Create Passenger**
```bash
curl -X POST http://localhost:8080/api/passengers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "phone": "+1-555-0001"
  }'
```
**Response**:
```json
{
  "id": 1,
  "name": "Alice Johnson",
  "phone": "+1-555-0001"
}
```

**Step 2: Register Cab**
```bash
curl -X POST http://localhost:8080/api/cabs \
  -H "Content-Type: application/json" \
  -d '{
    "plateNumber": "NYC-5001",
    "seatCapacity": 4,
    "luggageCapacity": 4
  }'
```
**Response**:
```json
{
  "id": 1,
  "plateNumber": "NYC-5001",
  "seatCapacity": 4,
  "luggageCapacity": 4,
  "status": "AVAILABLE"
}
```

**Step 3: Create Ride Request (TO_AIRPORT)**
```bash
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": 1,
    "direction": "TO_AIRPORT",
    "pickupLat": 40.7128,
    "pickupLng": -74.0060,
    "dropoffLat": 40.6413,
    "dropoffLng": -73.7781,
    "seatsRequired": 1,
    "luggageUnits": 1,
    "maxDetourPct": 25.0,
    "desiredPickupTime": "2024-02-17T15:30:00Z"
  }'
```
**Response**:
```json
{
  "id": 1,
  "passengerId": 1,
  "direction": "TO_AIRPORT",
  "status": "PENDING",
  "pickupLat": 40.7128,
  "pickupLng": -74.0060,
  "dropoffLat": 40.6413,
  "dropoffLng": -73.7781,
  "seatsRequired": 1,
  "luggageUnits": 1,
  "maxDetourPct": 25.0,
  "fareEstimate": 30.56
}
```

**Step 4: Get Request Details**
```bash
curl http://localhost:8080/api/requests/1
```

**Step 5: Trigger Matching**
```bash
curl -X POST http://localhost:8080/api/match/run
```
**Response**:
```json
{
  "processedCount": 1,
  "matchedCount": 1,
  "createdGroupCount": 1
}
```

**Step 6: Get Ride Group**
```bash
curl http://localhost:8080/api/groups/1
```
**Response**:
```json
{
  "id": 1,
  "direction": "TO_AIRPORT",
  "status": "OPEN",
  "totalSeats": 1,
  "totalLuggage": 1,
  "routeDistanceKm": 20.8,
  "cabId": null,
  "members": [
    {
      "requestId": 1,
      "passengerId": 1,
      "directDistanceKm": 20.8,
      "sharedDistanceKm": 20.8,
      "detourPct": 0.0,
      "stopOrder": 0
    }
  ]
}
```

---

### Scenario 2: Multiple Passengers Shared Ride (Pooling with Discount)

**Step 1-3: Create 2 Passengers and Cab**
```bash
# Passenger 2
curl -X POST http://localhost:8080/api/passengers \
  -H "Content-Type: application/json" \
  -d '{"name": "Bob Smith", "phone": "+1-555-0002"}'

# Passenger 3
curl -X POST http://localhost:8080/api/passengers \
  -H "Content-Type: application/json" \
  -d '{"name": "Carol Davis", "phone": "+1-555-0003"}'

# Cab (already created, ID=1)
```

**Step 4: Create Multiple TO_AIRPORT Requests**
```bash
# Request 2 - pickupLat near Alice
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": 2,
    "direction": "TO_AIRPORT",
    "pickupLat": 40.7120,
    "pickupLng": -74.0050,
    "dropoffLat": 40.6413,
    "dropoffLng": -73.7781,
    "seatsRequired": 1,
    "luggageUnits": 1,
    "maxDetourPct": 30.0,
    "desiredPickupTime": "2024-02-17T15:35:00Z"
  }'

# Request 3
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": 3,
    "direction": "TO_AIRPORT",
    "pickupLat": 40.7135,
    "pickupLng": -74.0070,
    "dropoffLat": 40.6413,
    "dropoffLng": -73.7781,
    "seatsRequired": 1,
    "luggageUnits": 1,
    "maxDetourPct": 20.0,
    "desiredPickupTime": "2024-02-17T15:40:00Z"
  }'
```

**Step 5: Trigger Matching**
```bash
curl -X POST http://localhost:8080/api/match/run
```
**Response**:
```json
{
  "processedCount": 2,
  "matchedCount": 2,
  "createdGroupCount": 0
}
```
**Note**: Both matched to same group (created earlier)

**Step 6: View Optimized Group**
```bash
curl http://localhost:8080/api/groups/1
```
**Response** (multiple members):
```json
{
  "id": 1,
  "direction": "TO_AIRPORT",
  "status": "OPEN",
  "totalSeats": 3,
  "totalLuggage": 3,
  "routeDistanceKm": 22.5,
  "members": [
    {
      "requestId": 1,
      "stopOrder": 0,
      "directDistanceKm": 20.8,
      "sharedDistanceKm": 21.2,
      "detourPct": 1.92
    },
    {
      "requestId": 2,
      "stopOrder": 1,
      "directDistanceKm": 20.8,
      "sharedDistanceKm": 22.5,
      "detourPct": 8.17
    },
    {
      "requestId": 3,
      "stopOrder": 2,
      "directDistanceKm": 20.8,
      "sharedDistanceKm": 22.0,
      "detourPct": 5.77
    }
  ]
}
```

**Step 7: Check Pricing with Group Discount**

Single passenger (no discount):
```bash
curl "http://localhost:8080/api/pricing/estimate?direction=TO_AIRPORT&pickupLat=40.7128&pickupLng=-74.0060&dropoffLat=40.6413&dropoffLng=-73.7781&groupSize=1"
```
**Response**: `{"estimatedFare": 30.56, "distanceKm": 20.8}`

Three passengers (30% shared discount):
```bash
curl "http://localhost:8080/api/pricing/estimate?direction=TO_AIRPORT&pickupLat=40.7128&pickupLng=-74.0060&dropoffLat=40.6413&dropoffLng=-73.7781&groupSize=3"
```
**Response**: `{"estimatedFare": 21.39, "distanceKm": 20.8}`

**Savings**: ~$9 per person (30% discount)

---

### Scenario 3: FROM_AIRPORT Return Journey

**Step 1: Create FROM_AIRPORT Request**
```bash
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": 1,
    "direction": "FROM_AIRPORT",
    "pickupLat": 40.6413,
    "pickupLng": -73.7781,
    "dropoffLat": 40.7128,
    "dropoffLng": -74.0060,
    "seatsRequired": 1,
    "luggageUnits": 1,
    "maxDetourPct": 25.0,
    "desiredPickupTime": "2024-02-17T18:00:00Z"
  }'
```

**Step 2: Matching and Group Formation**
```bash
curl -X POST http://localhost:8080/api/match/run
```

**Step 3: View FROM_AIRPORT Group**
```bash
curl http://localhost:8080/api/groups/2
```

---

### Scenario 4: Cancellation

**Step 1: Create Request**
```bash
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": 1,
    "direction": "TO_AIRPORT",
    "pickupLat": 40.7128,
    "pickupLng": -74.0060,
    "dropoffLat": 40.6413,
    "dropoffLng": -73.7781,
    "seatsRequired": 2,
    "luggageUnits": 3,
    "maxDetourPct": 15.0,
    "desiredPickupTime": "2024-02-17T20:00:00Z"
  }'
```

**Step 2: Cancel Before Matching**
```bash
curl -X POST http://localhost:8080/api/requests/<request-id>/cancel
```
**Response**:
```json
{
  "requestId": 5,
  "finalStatus": "CANCELLED"
}
```

**Step 3: Verify Status**
```bash
curl http://localhost:8080/api/requests/<request-id>
```
Response will show `"status": "CANCELLED"`

---

### Scenario 5: Constraint Violations

**Scenario 5a: Exceeding Group Seat Capacity**
```bash
# Create request requiring 5 seats (group max is 4)
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": 1,
    "direction": "TO_AIRPORT",
    "pickupLat": 40.7128,
    "pickupLng": -74.0060,
    "dropoffLat": 40.6413,
    "dropoffLng": -73.7781,
    "seatsRequired": 5,
    "luggageUnits": 1,
    "maxDetourPct": 25.0,
    "desiredPickupTime": "2024-02-17T15:30:00Z"
  }'
```
**Response**: `HTTP 400 Validation Error`

**Scenario 5b: Excessive Detour Tolerance**
```bash
# Create request with small detour tolerance
# But route has higher detour
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{
    "passengerId": 1,
    "direction": "TO_AIRPORT",
    "pickupLat": 40.7128,
    "pickupLng": -74.0060,
    "dropoffLat": 40.6413,
    "dropoffLng": -73.7781,
    "seatsRequired": 1,
    "luggageUnits": 1,
    "maxDetourPct": 2.0,
    "desiredPickupTime": "2024-02-17T15:30:00Z"
  }'
```
**Response**: Request created but will NOT match to groups with higher detour
- When matching occurs, plan is rejected if detour > 2%
- Request remains PENDING until suitable group found or timeout

---

## All API Endpoints Reference

### 1. Passengers

#### Create Passenger
```bash
POST /api/passengers
Content-Type: application/json

{
  "name": "string",
  "phone": "string (unique)"
}
```

#### Get Passenger
```bash
GET /api/passengers/{id}
```

### 2. Cabs

#### Create Cab
```bash
POST /api/cabs
Content-Type: application/json

{
  "plateNumber": "string (unique)",
  "seatCapacity": integer,
  "luggageCapacity": integer
}
```

#### List Cabs
```bash
GET /api/cabs
```

### 3. Ride Requests

#### Create Ride Request
```bash
POST /api/requests
Content-Type: application/json

{
  "passengerId": long,
  "direction": "TO_AIRPORT | FROM_AIRPORT",
  "pickupLat": double,
  "pickupLng": double,
  "dropoffLat": double,
  "dropoffLng": double,
  "seatsRequired": integer,
  "luggageUnits": integer,
  "maxDetourPct": double,
  "desiredPickupTime": "ISO 8601 timestamp"
}
```

#### Get Ride Request
```bash
GET /api/requests/{id}
```

#### Cancel Ride Request
```bash
POST /api/requests/{id}/cancel
```

### 4. Ride Groups

#### Get Ride Group with Members
```bash
GET /api/groups/{id}

Response includes:
- Group details (status, route distance, etc.)
- List of members with:
  - Passenger info
  - Stop order in route
  - Direct distance
  - Shared distance
  - Detour percentage
```

#### List All Ride Groups
```bash
GET /api/groups
```

### 5. Matching & Pricing

#### Run Matching Batch (Manual Trigger)
```bash
POST /api/match/run

Response:
{
  "processedCount": integer,
  "matchedCount": integer,
  "createdGroupCount": integer
}
```

#### Estimate Fare
```bash
GET /api/pricing/estimate?direction={direction}&pickupLat={lat}&pickupLng={lng}&dropoffLat={lat}&dropoffLng={lng}&groupSize={size}

Parameters:
- direction: TO_AIRPORT | FROM_AIRPORT
- pickupLat, pickupLng: Pickup coordinates
- dropoffLat, dropoffLng: Dropoff coordinates (or airport hub)
- groupSize: Number of passengers in group (1+)

Response:
{
  "estimatedFare": double,
  "distanceKm": double
}
```

---

## Error Handling

### 400 Bad Request
```bash
# Invalid input or validation failure
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{"passengerId": 1}'  # Missing required fields

# Response:
{
  "error": "Validation failed"
}
```

### 404 Not Found
```bash
# Request non-existent resource
curl http://localhost:8080/api/passengers/9999

# Response:
{
  "error": "Passenger not found"
}
```

### 200 OK
All successful operations return HTTP 200 with response body

---

## Performance Testing

### Load Testing with Apache Bench
```bash
# Create 1000 requests (10 concurrent)
ab -n 1000 -c 10 http://localhost:8080/actuator/health

# Results show:
# - Requests per second
# - Mean response time
# - Longest response time
```

### Concurrent Matching Test
```bash
# Create multiple requests quickly
for i in {1..100}; do
  curl -X POST http://localhost:8080/api/requests \
    -H "Content-Type: application/json" \
    -d '{
      "passengerId": '"$((i % 10))"',
      "direction": "TO_AIRPORT",
      "pickupLat": 40.7128,
      "pickupLng": -74.0060,
      "dropoffLat": 40.6413,
      "dropoffLng": -73.7781,
      "seatsRequired": 1,
      "luggageUnits": 1,
      "maxDetourPct": 25.0,
      "desiredPickupTime": "2024-02-17T15:30:00Z"
    }' &
done

# Wait all complete
wait

# Trigger matching
curl -X POST http://localhost:8080/api/match/run

# Check metrics
curl http://localhost:8080/actuator/metrics/http.server.requests
```

---

## Monitoring Endpoints

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Detailed Health
```bash
curl http://localhost:8080/actuator/health/details
```

### Memory Metrics
```bash
curl http://localhost:8080/actuator/metrics/jvm.memory.usage
```

### HTTP Metrics
```bash
curl http://localhost:8080/actuator/metrics/http.server.requests
```

### Database Connection Pool
```bash
curl http://localhost:8080/actuator/metrics/hikaricp.connections
```

---

## Data Validation Rules

| Field | Validation | Notes |
|-------|-----------|-------|
| `phone` | UNIQUE | Must be globally unique |
| `plateNumber` | UNIQUE | Must be globally unique |
| `seatsRequired` | 1-4 | Individual request limit |
| `luggageUnits` | 0-4 | Per person luggage |
| `maxDetourPct` | 0-100 | Percentage above direct distance |
| `desiredPickupTime` | ISO 8601 | Must be future timestamp |
| `coordinates` | -180 to 180 lat/lng | Valid geographic coordinates |

---

## Tips for Testing

1. **Use Swagger UI for Interactive Testing**:
   - Open http://localhost:8080/swagger-ui.html
   - All endpoints interactive
   - Try it out buttons pre-fill responses

2. **Save Postman Collection**:
   - Import `postman_collection.json`
   - Pre-configured requests
   - Environment variables for URLs

3. **Monitor in Real Time**:
   - Open `docker logs -f airport-pooling-app`
   - Watch database queries and transaction logs

4. **Verify Database State**:
   ```bash
   # Connect to PostgreSQL
   psql -h localhost -U postgres -d airport_pooling
   
   # List tables
   \dt
   
   # Query data
   SELECT * FROM ride_requests;
   SELECT * FROM ride_groups;
   SELECT * FROM ride_group_members;
   ```

Ready to test!
