package com.pokrasko.rssreader2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by pokrasko on 10.11.14.
 */
public class FeedLoader extends AsyncTaskLoader<FeedList> {

    public FeedLoader(Context context) {
        super(context);
    }

    @Override
    public FeedList loadInBackground() {
        FeedList list = new FeedList();
        Cursor cursor = getContext().getContentResolver().query(FeedContentProvider.CONTENT_FEEDS_URI,
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getInt(0);
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            String url = cursor.getString(3);
            list.add(new Feed(id, title, description, url));

            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
