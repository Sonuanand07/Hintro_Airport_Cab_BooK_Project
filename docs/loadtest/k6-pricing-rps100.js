import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  scenarios: {
    steady_rps: {
      executor: "constant-arrival-rate",
      rate: 100,
      timeUnit: "1s",
      duration: "2m",
      preAllocatedVUs: 200,
      maxVUs: 1000
    }
  },
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<300"]
  }
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";

export default function () {
  const res = http.get(`${BASE_URL}/api/pricing/estimate?direction=TO_AIRPORT&pickupLat=40.7580&pickupLng=-73.9855&dropoffLat=40.6413&dropoffLng=-73.7781&groupSize=2`);
  check(res, {
    "status is 200": (r) => r.status === 200
  });
  sleep(0.1);
}
