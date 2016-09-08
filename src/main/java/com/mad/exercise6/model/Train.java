package com.mad.exercise6.model;

/**
 * Created by Andrew on 2/09/2016.
 * A POJO class to hold information on a single train
 */
public class Train {

    private static final int INITIAL_ID = -1;

    private int mId;
    private String mPlatform;
    private int mArrivalTime;
    private String mStatus;
    private String mDestination;
    private String mDestinationTime;

    /**
     * Constructor method for a single train. Takes in all data about a train
     * and stores it in the class member variables
     * @param platform
     * @param arrivalTime
     * @param status
     * @param destination
     * @param destinationTime
     */
    public Train(String platform, int arrivalTime, String status, String destination,
                 String destinationTime) {
        mPlatform = platform;
        mArrivalTime = arrivalTime;
        mStatus = status;
        mDestination = destination;
        mDestinationTime = destinationTime;
        mId = INITIAL_ID;
    }

    /**
     * Getter method for the platform
     * @return platform
     */
    public String getPlatform() {
        return mPlatform;
    }

    /**
     * Setter method for the platform
     * @param platform
     */
    public void setPlatform(String platform) {
        mPlatform = platform;
    }

    /**
     * Getter method for the arrival time
     * @return arrivalTime
     */
    public int getArrivalTime() {
        return mArrivalTime;
    }

    /**
     * Setter method for the arrival time
     * @param arrivalTime
     */
    public void setArrivalTime(int arrivalTime) {
        mArrivalTime = arrivalTime;
    }

    /**
     * Getter method for the status of the train
     * @return status
     */
    public String getStatus() {
        return mStatus;
    }

    /**
     * Setter method for the status of the train
     * @param status
     */
    public void setStatus(String status) {
        mStatus = status;
    }

    /**
     * Getter method for the destination of the train
     * @return destination
     */
    public String getDestination() {
        return mDestination;
    }

    /**
     * Setter method for the destination of the train
     * @param destination
     */
    public void setDestination(String destination) {
        mDestination = destination;
    }

    /**
     * Getter method for the destination time of the train
     * @return destinationTime
     */
    public String getDestinationTime() {
        return mDestinationTime;
    }

    /**
     * Setter method for the destination time of the train
     * @param destinationTime
     */
    public void setDestinationTime(String destinationTime) {
        mDestinationTime = destinationTime;
    }

    /**
     * Method that sets the id of the train to correspond with its position in a database
     * @param id
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * Retrieves the id of the train as labelled in the database
     * @return
     */
    public int getId() {
        return mId;
    }

    /**
     * Override debug method to print the status of a train to a string
     * @return
     */
    @Override
    public String toString() {
        return "ID: " + mId + ", Platform: " + mPlatform + ", Arrival Time: " + mArrivalTime + ", Status: " +
                mStatus + ", Destination " + mDestination + ", Destination Time " +
                mDestinationTime + "\n";
    }
}
