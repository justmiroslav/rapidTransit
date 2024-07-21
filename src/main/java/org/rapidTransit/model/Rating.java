package org.rapidTransit.model;

public class Rating {
    private int ratingId;
    private final long tripId;
    private final long userId;
    private final int rating;
    private final String comment;

    public Rating(int ratingId, long tripId, long userId, int rating, String comment) {
        this.ratingId = ratingId;
        this.tripId = tripId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public long getTripId() {
        return tripId;
    }

    public long getUserId() {
        return userId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
