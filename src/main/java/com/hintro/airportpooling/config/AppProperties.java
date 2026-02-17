package com.hintro.airportpooling.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pooling")
public class AppProperties {
    private Airport airport = new Airport();
    private int timeWindowMinutes = 15;
    private int maxSeatsPerGroup = 4;
    private int maxLuggagePerGroup = 4;
    private Matching matching = new Matching();
    private Pricing pricing = new Pricing();
    private Detour detour = new Detour();

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public int getTimeWindowMinutes() {
        return timeWindowMinutes;
    }

    public void setTimeWindowMinutes(int timeWindowMinutes) {
        this.timeWindowMinutes = timeWindowMinutes;
    }

    public int getMaxSeatsPerGroup() {
        return maxSeatsPerGroup;
    }

    public void setMaxSeatsPerGroup(int maxSeatsPerGroup) {
        this.maxSeatsPerGroup = maxSeatsPerGroup;
    }

    public int getMaxLuggagePerGroup() {
        return maxLuggagePerGroup;
    }

    public void setMaxLuggagePerGroup(int maxLuggagePerGroup) {
        this.maxLuggagePerGroup = maxLuggagePerGroup;
    }

    public Matching getMatching() {
        return matching;
    }

    public void setMatching(Matching matching) {
        this.matching = matching;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public Detour getDetour() {
        return detour;
    }

    public void setDetour(Detour detour) {
        this.detour = detour;
    }

    public static class Airport {
        private double lat = 40.6413;
        private double lng = -73.7781;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    public static class Matching {
        private int batchSize = 100;
        private long intervalMs = 1000;

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public long getIntervalMs() {
            return intervalMs;
        }

        public void setIntervalMs(long intervalMs) {
            this.intervalMs = intervalMs;
        }
    }

    public static class Pricing {
        private double baseFare = 5.0;
        private double perKm = 1.2;
        private double sharedDiscountPerExtra = 0.1;
        private double maxSharedDiscount = 0.3;
        private double surgeBase = 1.0;

        public double getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(double baseFare) {
            this.baseFare = baseFare;
        }

        public double getPerKm() {
            return perKm;
        }

        public void setPerKm(double perKm) {
            this.perKm = perKm;
        }

        public double getSharedDiscountPerExtra() {
            return sharedDiscountPerExtra;
        }

        public void setSharedDiscountPerExtra(double sharedDiscountPerExtra) {
            this.sharedDiscountPerExtra = sharedDiscountPerExtra;
        }

        public double getMaxSharedDiscount() {
            return maxSharedDiscount;
        }

        public void setMaxSharedDiscount(double maxSharedDiscount) {
            this.maxSharedDiscount = maxSharedDiscount;
        }

        public double getSurgeBase() {
            return surgeBase;
        }

        public void setSurgeBase(double surgeBase) {
            this.surgeBase = surgeBase;
        }
    }

    public static class Detour {
        private double defaultMaxPct = 25.0;

        public double getDefaultMaxPct() {
            return defaultMaxPct;
        }

        public void setDefaultMaxPct(double defaultMaxPct) {
            this.defaultMaxPct = defaultMaxPct;
        }
    }
}
