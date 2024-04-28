package com.example.afillmore_weight_tracking_app;

/**
 * Represents a single weight record. This class is used to encapsulate the details of a weight record,
 * including its unique ID, the date of the record, and the weight value. It provides a structured way
 * to pass weight data throughout the application, particularly between the database and the user interface.
 */
public class WeightData {
    private int id; // Unique identifier for the weight record
    private String date;
    private float weight;

    // Constructor
    public WeightData(int id, String date, float weight) {
        this.id = id;
        this.date = date;
        this.weight = weight;
    }

    // Getter for ID
    /**
     * Returns the unique identifier for this weight record.
     * @return the weight record's ID.
     */
    public int getId() {
        return id;
    }

    // Getters for other fields
    /**
     * Returns the date of this weight record.
     * @return the date as a string.
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the weight recorded on this date.
     * @return the weight value.
     */
    public float getWeight() {
        return weight;
    }
}



