package com.pokrasko.rssreader2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by pokrasko on 10.11.14.
 */
public class FeedResultReceiver extends ResultReceiver {
    Context context;
    public static final int OK = 0;
    public static final int FEED_EXISTS_ERROR = 1;
    public static final int EXCEPTION_ERROR = 2;

    public FeedResultReceiver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    protected void onReceiveResult(int code, Bundle bundle) {
        String message = "You're fucking cheater";
        switch (code) {
            case OK:
                Log.i("ResultReceiver", "heh, it's ok");
                Class current = context.getClass();
                Log.i("ResultReceiver", "class is " + current.toString());
                if (current == FeedActivity.class) {
                    Log.i("ResultReceiver", "you've got to FeedActivity");
                    ((FeedActivity) context).onUpdatedFeed();
                } else if (current == PostActivity.class) {
                    Log.i("ResultReceiver", "you've got to PostActivity");
                    ((PostActivity) context).onUpdatedFeed();
                }
                return;
            case FEED_EXISTS_ERROR:
                message = "This feed is already in the list";
                break;
            case EXCEPTION_ERROR:
                message = bundle.getString("error");
                break;
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
