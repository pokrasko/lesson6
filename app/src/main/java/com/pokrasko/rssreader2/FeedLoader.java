package com.pokrasko.rssreader2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by pokrasko on 10.11.14.
 */
public class FeedLoader extends AsyncTaskLoader<FeedList> {
    private static final String[] fullProjection = {"id", "title", "description", "url"};

    public FeedLoader(Context context) {
        super(context);
    }

    @Override
    public FeedList loadInBackground() {
        FeedList list = new FeedList();
        Cursor cursor = getContext().getContentResolver().query(FeedContentProvider.CONTENT_FEEDS_URI,
                fullProjection, null, null, null);

        while (!cursor.isAfterLast()) {
            long id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
            list.add(new Feed(id, title, description, url));

            cursor.moveToNext();
        }

        return list;
    }
}
