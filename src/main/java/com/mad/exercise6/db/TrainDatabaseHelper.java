package com.mad.exercise6.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mad.exercise6.model.Train;

import java.util.ArrayList;

/**
 * Created by Andrew on 7/09/2016.
 * A helper class to create and update tables within the train database
 * Uses the singleton pattern and keeps a static reference to itself
 */
public class TrainDatabaseHelper extends SQLiteOpenHelper {

    //Stores static reference to a single database helper instance to prevent
    //opening the database more times than it is closed
    private static TrainDatabaseHelper sDatabaseHelperInstance;

    //Debug Constants
    private static final String TRAIN_RETRIEVAL_TAG = "get_trains";

    private static final String DATABASE_NAME = "trains_database";
    private static final String TRAIN_INFORMATION_TABLE_NAME = "train_info_table";
    private static final int DATABASE_VERSION = 1;

    //SQL LANGUAGE CONSTANTS
    private static final String KEY_ID = "id";
    private static final String ID_PROPERTIES = "INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String KEY_PLATFORM = "platform";
    private static final String PLATFORM_PROPERTIES = "TEXT";
    private static final String KEY_ARRIVAL_TIME = "arrival_time";
    private static final String ARRIVAL_TIME_PROPERTIES = "INTEGER";
    private static final String KEY_STATUS = "status";
    private static final String STATUS_PROPERTIES = "TEXT";
    private static final String KEY_DESTINATION = "destination";
    private static final String DESTINATION_PROPERTIES = "TEXT";
    private static final String KEY_DESTINATION_TIME = "destination_time";
    private static final String DESTINATION_TIME_PROPERTIES = "TEXT";

    private static final String[] ALL_COLUMN_NAMES = {KEY_ID, KEY_PLATFORM, KEY_ARRIVAL_TIME,
            KEY_STATUS, KEY_DESTINATION, KEY_DESTINATION_TIME};

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TRAIN_INFORMATION_TABLE_NAME;
    private static final String DELETE_ALL = "DELETE FROM " + TRAIN_INFORMATION_TABLE_NAME;

    public static synchronized TrainDatabaseHelper getInstance(Context context) {
        if (sDatabaseHelperInstance == null) {
            sDatabaseHelperInstance = new TrainDatabaseHelper(context.getApplicationContext());
        }

        return sDatabaseHelperInstance;
    }

    private TrainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * Called on the creation of the database
     * Sets up the database with the constants in this class
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTrainInformationTable = "CREATE TABLE " + TRAIN_INFORMATION_TABLE_NAME + "(" +
                KEY_ID + " " + ID_PROPERTIES + "," + KEY_PLATFORM + " " + PLATFORM_PROPERTIES +
                "," + KEY_ARRIVAL_TIME + " " + ARRIVAL_TIME_PROPERTIES + "," + KEY_STATUS + " " +
                STATUS_PROPERTIES + "," + KEY_DESTINATION + " " + DESTINATION_PROPERTIES + "," +
                KEY_DESTINATION_TIME + " " + DESTINATION_TIME_PROPERTIES + ")";

        sqLiteDatabase.execSQL(createTrainInformationTable);

    }

    /**
     * Called when the version number of the database changes
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //Drop the table if it exists
        sqLiteDatabase.execSQL(DROP_TABLE);
        //Create the table
        onCreate(sqLiteDatabase);
    }

    /**
     * Adds a given train to the database
     * @param train
     */
    public void addTrain(Train train) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PLATFORM, train.getPlatform());
        values.put(KEY_ARRIVAL_TIME, train.getArrivalTime());
        values.put(KEY_STATUS, train.getStatus());
        values.put(KEY_DESTINATION, train.getDestination());
        values.put(KEY_DESTINATION_TIME, train.getDestinationTime());
        database.insert(TRAIN_INFORMATION_TABLE_NAME, null, values);
        database.close();
    }

    /**
     * Returns a train from the given row in the database
     * @param id
     * @return train
     */
    public Train readRow(int id) {

        SQLiteDatabase database = this.getReadableDatabase();

        //Write the query
        Cursor cursor = database.query(TRAIN_INFORMATION_TABLE_NAME, ALL_COLUMN_NAMES,
                " " + KEY_ID + " = ?", new String[] {String.valueOf(id)},null,null,null,null);

        //Check to see if the id given exists
        if (cursor != null) {
            cursor.moveToFirst();
        }

        //Build the train object
        Train train = new Train(cursor.getString(1),
                Integer.parseInt(cursor.getString(2)), cursor.getString(3),
                cursor.getString(4), cursor.getString(5));

        train.setId(Integer.parseInt(cursor.getString(0)));

        Log.d(TRAIN_RETRIEVAL_TAG, train.toString());

        return train;
    }

    /**
     * Retrieves all of the trains stored in the database in the form of an array list
     * @return allTrains
     */
    public ArrayList<Train> readAllTrains() {
        ArrayList<Train> allTrains = new ArrayList<Train>();

        String query = "SELECT * FROM " + TRAIN_INFORMATION_TABLE_NAME;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        //Look through each row and build up the list of trains
        Train train = null;
        if (cursor.moveToFirst()) {
            do {
                train = new Train(cursor.getString(1),
                        Integer.parseInt(cursor.getString(2)), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5));

                train.setId(Integer.parseInt(cursor.getString(0)));

                allTrains.add(train);
            } while (cursor.moveToNext());
        }
        Log.d(TRAIN_RETRIEVAL_TAG, allTrains.toString());

        return allTrains;
    }

    /**
     * Updates a particular train if any of the values of that train have changed
     * @param train
     * @return
     */
    public int updateTrain(Train train) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLATFORM,  train.getPlatform());
        values.put(KEY_ARRIVAL_TIME, train.getArrivalTime());
        values.put(KEY_STATUS, train.getStatus());
        values.put(KEY_DESTINATION, train.getDestination());
        values.put(KEY_DESTINATION_TIME, train.getDestinationTime());

        int success = database.update(TRAIN_INFORMATION_TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(train.getId())});

        database.close();
        return success;
    }

    /**
     * Clears the table of all records
     */
    public void deleteAll() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(DELETE_ALL);
        database.close();
    }

}
