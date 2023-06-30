package com.thirumalaivasa.vehiclemanagement.Models;

public class FuelApiData {


    private String cityId;
    private String cityName;
    private String stateId;
    private String stateName;
    private String countryId;
    private String countryName;
    private String applicableOn;
    private Fuel fuel;

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getStateId() {
        return stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public String getCountryId() {
        return countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getApplicableOn() {
        return applicableOn;
    }

    public Fuel getFuel() {
        return fuel;
    }

    public static class Fuel {
        private FuelType petrol;
        private FuelType diesel;
        private FuelType lpg;
        private FuelType cng;

        public FuelType getPetrol() {
            return petrol;
        }

        public FuelType getDiesel() {
            return diesel;
        }

        public FuelType getLpg() {
            return lpg;
        }

        public FuelType getCng() {
            return cng;
        }

        public static class FuelType {
            private double retailPrice;
            private double retailPriceChange;
            private String retailPriceChangeInterval;
            private String retail;

            public double getRetailPrice() {
                return retailPrice;
            }

            public double getRetailPriceChange() {
                return retailPriceChange;
            }

            public String getRetailPriceChangeInterval() {
                return retailPriceChangeInterval;
            }

            public String getRetail() {
                return retail;
            }
        }
    }
}