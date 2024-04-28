package com.example.afillmore_weight_tracking_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

/**
 * Database helper class that provides methods to interact with the SQLite database.
 * This class handles the creation, update, and management of database tables and records for users and weights.
 * It includes methods for adding, checking, updating, and deleting users and weight records, as well as managing goal weights.
 */

public class UserDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "application.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor for the database helper where the database name and version are specified.
     * @param context The context through which to access the database.
     */
    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create our Database tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "goal_weight DECIMAL" +
                ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_WEIGHT_TABLE = "CREATE TABLE weights (" +
                "weight_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "date DATE," +
                "weight DECIMAL," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")";
        db.execSQL(CREATE_WEIGHT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
    /**
     * Adds a new user to the database.
     * @param username The username of the new user.
     * @param password The password for the new user.
     * @return true if the user was successfully added, false otherwise.
     */

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long result = db.insert("users", null, values);
        db.close();
        return result != -1;
    }

    /**
     * Checks whether a user exists in the database with the given username and password.
     * @param username The username to check.
     * @param password The password associated with the username.
     * @return true if the user exists, otherwise false.
     */
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users",
                new String[] {"username", "password"},
                "username=? AND password=?",
                new String[] {username, password}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    /**
     * Retrieves the last ten weights recorded for a given user.
     * @param userId The user ID for whom to retrieve the weight records.
     * @return A list of WeightData objects representing the last ten weights of the user.
     */
    public List<WeightData> getLastTenWeights(int userId) {
        List<WeightData> weights = new ArrayList<>();
        if (userId == -1) {
            Log.e("WeightDisplayActivity", "Invalid user ID");
            return weights;  // Return empty list if user ID is invalid
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("weights", new String[]{"weight_id", "user_id", "date", "weight"},
                "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, "date DESC", "10");

        if (cursor != null) {
            try {
                int idIndex = cursor.getColumnIndex("weight_id");
                int dateIndex = cursor.getColumnIndex("date");
                int weightIndex = cursor.getColumnIndex("weight");

                // Check if any index is -1, which means the column doesn't exist in the cursor.
                if (idIndex == -1 || dateIndex == -1 || weightIndex == -1) {
                    Log.e("UserDbHelper", "One or more column indices are invalid.");
                    return weights;  // Return empty list if any column index is invalid
                }

                while (cursor.moveToNext()) {
                    int id = cursor.getInt(idIndex);
                    String date = cursor.getString(dateIndex);
                    float weight = cursor.getFloat(weightIndex);
                    weights.add(new WeightData(id, date, weight));
                }
            } finally {
                cursor.close();  // Ensure cursor is always closed
            }
        }
        db.close();
        return weights;
    }



    /**
     * Adds a new weight record for a user.
     * @param userId The ID of the user for whom the weight is recorded.
     * @param date The date of the weight record.
     * @param weight The weight of the user on the recorded date.
     * @return true if the record was successfully added, false otherwise.
     */
    public boolean addWeight(int userId, String date, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("date", date);
        values.put("weight", weight);
        long result = db.insert("weights", null, values);
        db.close();
        return result != -1;
    }

    /**
     * Sets a goal weight for a user.
     * @param userId The ID of the user for whom to set the goal weight.
     * @param goalWeight The goal weight to be set.
     * @return true if the goal weight was successfully updated, false otherwise.
     */
    public boolean setGoalWeight(int userId, float goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goal_weight", goalWeight);
        int rowsAffected = db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }

    /**
     * Gets the current goal weight of a user.
     * @param userId The user ID whose goal weight is to be retrieved.
     * @return The goal weight of the user.
     */
    public float getGoalWeight(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"goal_weight"}, "id = ?", new String[]{String.valueOf(userId)}, null, null, null);
        float goalWeight = -1; // Default or error value if goal weight is not set or column is missing

        if (cursor != null && cursor.moveToFirst()) {
            int goalWeightIndex = cursor.getColumnIndex("goal_weight");
            if (goalWeightIndex != -1) { // Check if the column index is valid
                goalWeight = cursor.getFloat(goalWeightIndex);
            } else {
                // Log an error or handle the case where the column doesn't exist
                Log.e("UserDbHelper", "Goal weight column not found in the database.");
            }
            cursor.close();
        }
        db.close();
        return goalWeight;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"}, "username=?", new String[]{username}, null, null, null);
        int userId = -1;  // Default to -1 if user is not found

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex("id");
                    if (idIndex != -1) {  // Ensure index is valid
                        userId = cursor.getInt(idIndex);
                    } else {
                        Log.e("UserDbHelper", "Column 'id' was not found.");
                    }
                }
            } finally {
                cursor.close();  // Ensure the cursor is closed
            }
        }
        db.close();
        return userId;
    }


    /**
     * Deletes a weight record by its ID.
     * @param id The ID of the weight record to delete.
     * @return true if the weight was successfully deleted, false otherwise.
     */
    public boolean deleteWeight(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("weights", "weight_id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    /**
     * Updates the weight for a specific weight record.
     * @param id The ID of the weight record to update.
     * @param newWeight The new weight to be set for the record.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateWeight(int id, float newWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("weight", newWeight);
        int rowsAffected = db.update("weights", values, "weight_id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }
}
