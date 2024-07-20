package org.rapidTransit.model;

public class Route {
    private final int id;
    private final String departureCity;
    private final String arrivalCity;
    private float travelTime;

    public Route(int routeId, String departureCity, String arrivalCity, float travelTime) {
        this.id = routeId;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.travelTime = travelTime;
    }

    public int getId() {
        return id;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public float getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(float travelTime) {
        this.travelTime = travelTime;
    }
}
