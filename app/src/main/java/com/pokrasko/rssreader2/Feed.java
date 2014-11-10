package com.pokrasko.rssreader2;

/**
 * Created by pokrasko on 10.11.14.
 */
public class Feed {
    private long id;
    private String title;
    private String description;
    private String url;

    public Feed(long id, String title, String description, String url) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}
