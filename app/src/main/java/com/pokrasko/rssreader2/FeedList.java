package com.pokrasko.rssreader2;

import java.util.ArrayList;

/**
 * Created by pokrasko on 10.11.14.
 */
public class FeedList {
    private ArrayList<Feed> feeds;

    public FeedList() {
        feeds = new ArrayList<Feed>();
    }

    public Feed get(int i) {
        return feeds.get(i);
    }

    public int size() {
        return feeds.size();
    }

    public void add(Feed feed) {
        feeds.add(feed);
    }

    public void remove(int i) {
        feeds.remove(i);
    }
}
