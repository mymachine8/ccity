package com.cargoexchange.cargocity.cargocity.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.cargoexchange.cargocity.cargocity.data.CargoCityContract.LocationEntry;
/**
 * Created by thespidy on 01/02/16.
 */
public class CargoCityDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "cargocity.db";

    public CargoCityDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY," +
                LocationEntry.COLUMN_ORDER_ID + " TEXT UNIQUE NOT NULL, " +
                LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
