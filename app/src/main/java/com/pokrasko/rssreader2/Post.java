package com.pokrasko.rssreader2;

/**
 * Created by pokrasko on 11.11.14.
 */
public class Post {
    private long id;
    private String title;
    private String description;
    private String url;
    private boolean visibility;

    public Post(long id, String title, String description, String url) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        visibility = false;
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

    public boolean isVisible() {
        return visibility;
    }

    void changeVisibility() {
        visibility = !visibility;
    }
}
