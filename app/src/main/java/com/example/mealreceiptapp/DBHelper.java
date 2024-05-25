package com.example.mealreceiptapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MealDB.db";
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Database creation script if needed
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Database upgrade script if needed
    }

    // Copy the database from assets to the application's data directory
    public void copyDatabaseFromAssets() {
        try {
            InputStream inputStream = mContext.getAssets().open(DATABASE_NAME);
            String outputPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
            OutputStream outputStream = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
