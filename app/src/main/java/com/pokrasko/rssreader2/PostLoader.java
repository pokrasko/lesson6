package com.pokrasko.rssreader2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by pokrasko on 11.11.14.
 */
public class PostLoader extends AsyncTaskLoader<PostList> {
    private final String selection;
    private static final String[] projection = {"id", "title", "description", "url"};

    public PostLoader(Context context, long feedId) {
        super(context);
        selection = FeedContentProvider.FEED_ID_FIELD + "=" + feedId;
    }

    @Override
    public PostList loadInBackground() {
        PostList list = new PostList();
        Cursor cursor = getContext().getContentResolver().query(FeedContentProvider.CONTENT_POSTS_URI,
                projection, selection, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getInt(0);
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            String url = cursor.getString(3);
            list.add(new Post(id, title, description, url));

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
