CREATE TABLE passengers (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    phone TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE cabs (
    id BIGSERIAL PRIMARY KEY,
    plate_number TEXT NOT NULL UNIQUE,
    seat_capacity INT NOT NULL,
    luggage_capacity INT NOT NULL,
    status TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ride_groups (
    id BIGSERIAL PRIMARY KEY,
    direction TEXT NOT NULL,
    status TEXT NOT NULL,
    total_seats INT NOT NULL,
    total_luggage INT NOT NULL,
    max_seats INT NOT NULL,
    max_luggage INT NOT NULL,
    time_window_start TIMESTAMP NOT NULL,
    time_window_end TIMESTAMP NOT NULL,
    route_distance_km DOUBLE PRECISION NOT NULL,
    cab_id BIGINT NULL REFERENCES cabs(id),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ride_requests (
    id BIGSERIAL PRIMARY KEY,
    passenger_id BIGINT NOT NULL REFERENCES passengers(id),
    direction TEXT NOT NULL,
    pickup_lat DOUBLE PRECISION NOT NULL,
    pickup_lng DOUBLE PRECISION NOT NULL,
    dropoff_lat DOUBLE PRECISION NOT NULL,
    dropoff_lng DOUBLE PRECISION NOT NULL,
    seats_required INT NOT NULL,
    luggage_units INT NOT NULL,
    max_detour_pct DOUBLE PRECISION NOT NULL,
    desired_pickup_time TIMESTAMP NOT NULL,
    status TEXT NOT NULL,
    fare_estimate DOUBLE PRECISION NOT NULL,
    assigned_group_id BIGINT NULL REFERENCES ride_groups(id),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ride_group_members (
    id BIGSERIAL PRIMARY KEY,
    ride_group_id BIGINT NOT NULL REFERENCES ride_groups(id),
    ride_request_id BIGINT NOT NULL UNIQUE REFERENCES ride_requests(id),
    stop_order INT NOT NULL,
    direct_distance_km DOUBLE PRECISION NOT NULL,
    shared_distance_km DOUBLE PRECISION NOT NULL,
    detour_pct DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_requests_status_time ON ride_requests(status, desired_pickup_time);
CREATE INDEX idx_requests_direction_status ON ride_requests(direction, status);
CREATE INDEX idx_requests_assigned_group ON ride_requests(assigned_group_id);
CREATE INDEX idx_groups_status_direction ON ride_groups(status, direction);
CREATE INDEX idx_group_members_group ON ride_group_members(ride_group_id, stop_order);
