package com.pokrasko.rssreader2;

import java.util.ArrayList;

/**
 * Created by pokrasko on 11.11.14.
 */
public class PostList {
    private ArrayList<Post> list;

    public PostList() {
        list = new ArrayList<Post>();
    }

    public Post get(int i) {
        return list.get(i);
    }

    public int size() {
        return list.size();
    }

    public void add(Post post) {
        list.add(post);
    }
}
