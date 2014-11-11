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

        while (!cursor.isAfterLast()) {
            long id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
            list.add(new Post(id, title, description, url));

            cursor.moveToNext();
        }

        return list;
    }
}
