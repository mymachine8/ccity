package com.cargoexchange.cargocity.cargocity.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
public class CargoCityProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CargoCityDbHelper mOpenHelper;
    static final int LOCATION = 100;
    static final int LOCATION_WITH_ORDER = 101;

    private static final SQLiteQueryBuilder sLocationByOrderSettingQueryBuilder;

    static{
        sLocationByOrderSettingQueryBuilder = new SQLiteQueryBuilder();
        sLocationByOrderSettingQueryBuilder.setTables(
                CargoCityContract.LocationEntry.TABLE_NAME);
    }

    private static final String sOrderIdSelection =
            CargoCityContract.LocationEntry.TABLE_NAME+
                    "." + CargoCityContract.LocationEntry.COLUMN_ORDER_ID + " = ? ";

    private Cursor getLocationByOrderSetting(Uri uri, String[] projection, String sortOrder) {
        String orderId = CargoCityContract.LocationEntry.getOrderIdFromUri(uri);
        String[] selectionArgs;
        selectionArgs = new String[]{orderId};
        String selection = sOrderIdSelection;
        return sLocationByOrderSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new CargoCityDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "location with order"
            case LOCATION_WITH_ORDER: {
                retCursor = getLocationByOrderSetting(uri, projection, sortOrder);
                break;
            }
            // "location"
            case LOCATION: {
                retCursor = null;
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case LOCATION_WITH_ORDER:
                return CargoCityContract.LocationEntry.CONTENT_TYPE;
            case LOCATION:
                return CargoCityContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case LOCATION_WITH_ORDER: {
                long _id = db.insert(CargoCityContract.LocationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CargoCityContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LOCATION:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CargoCityContract.LocationEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }


    static UriMatcher buildUriMatcher() {
        return null;
    }
}
