package com.pokrasko.rssreader2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by pokrasko on 09.11.14.
 */
public class FeedContentProvider extends ContentProvider {
    private static final int FEEDS = 0;
    private static final int FEED_ID = 1;
    private static final int POSTS = 2;

    private static final String ID_FIELD = "id";
    private static final String TITLE_FIELD = "title";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String URL_FIELD = "url";
    public static final String FEED_ID_FIELD = "feed_id";

    private static final String AUTHORITY = "com.pokrasko.rssreader2";
    private static final String FEEDS_PATH = "feeds";
    private static final String POSTS_PATH = "posts";
    public static final Uri CONTENT_FEEDS_URI = Uri.parse("content://" +
        AUTHORITY + "/" + FEEDS_PATH);
    public static final Uri CONTENT_POSTS_URI = Uri.parse("content://" +
        AUTHORITY + "/" + POSTS_PATH);

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI(AUTHORITY, FEEDS_PATH, FEEDS);
        matcher.addURI(AUTHORITY, POSTS_PATH, POSTS);
        matcher.addURI(AUTHORITY, FEEDS_PATH + "/#", FEED_ID);
    }

    static String DB_NAME = "feeds.db";
    static int DB_VERSION = 1;
    private static final String FEEDS_TABLE = "feeds";
    private static final String POSTS_TABLE = "posts";
    private FeedDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new FeedDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return Integer.toString(matcher.match(uri));
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;
        int uriType = matcher.match(uri);
        switch (uriType) {
            case FEEDS:
                id = db.insert(FEEDS_TABLE, null, values);
                break;
            case POSTS:
                id = db.insert(POSTS_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Bad URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, "" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = matcher.match(uri);
        switch (uriType) {
            case FEEDS:
                queryBuilder.setTables(FEEDS_TABLE);
                break;
            case FEED_ID:
                queryBuilder.setTables(FEEDS_TABLE);
                queryBuilder.appendWhere("id=" + uri.getLastPathSegment());
                break;
            case POSTS:
                queryBuilder.setTables(POSTS_TABLE);
                break;
            default:
                throw new IllegalArgumentException("Bad URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int uriType = matcher.match(uri);

        int updatedRows = 0;
        switch (uriType) {
            case FEED_ID:
                String feedId = uri.getLastPathSegment();
                updatedRows = db.update(FEEDS_TABLE, values, ID_FIELD + "=" + feedId, null);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int uriType = matcher.match(uri);

        int deletedRows = 0;
        switch (uriType) {
            case FEED_ID:
                String feedId = uri.getLastPathSegment();
                deletedRows = db.delete(FEEDS_TABLE, ID_FIELD + "=" + feedId, null);
                break;
            case POSTS:
                deletedRows = db.delete(POSTS_TABLE, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    public class FeedDBHelper extends SQLiteOpenHelper {
        public FeedDBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL("CREATE TABLE " + FEEDS_TABLE +
                " (" + ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + TITLE_FIELD + " TEXT NOT NULL" +
                ", " + DESCRIPTION_FIELD + " TEXT NOT NULL" +
                ", " + URL_FIELD + " TEXT NOT NULL UNIQUE);");
            db.execSQL("CREATE TABLE " + POSTS_TABLE +
                " (" + ID_FIELD + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + TITLE_FIELD + " TEXT NOT NULL" +
                ", " + DESCRIPTION_FIELD + " TEXT NOT NULL" +
                ", " + URL_FIELD + " TEXT NOT NULL" +
                ", " + FEED_ID_FIELD + " INTEGER REFERENCES feeds(id) ON DELETE CASCADE);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int o, int n) {
            db.execSQL("DROP TABLE IF EXISTS feeds");
            db.execSQL("DROP TABLE IF EXISTS posts");
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int o, int n) {
            db.execSQL("DROP TABLE IF EXISTS feeds");
            db.execSQL("DROP TABLE IF EXISTS posts");
            onCreate(db);
        }
    }
}
