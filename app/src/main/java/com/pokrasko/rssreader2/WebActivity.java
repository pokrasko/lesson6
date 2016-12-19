package com.pokrasko.rssreader2;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by pokrasko on 11.11.14.
 */
public class WebActivity extends Activity {
    private WebView webView;
    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");

        setTitle(title);
        webView.loadUrl(url);
    }
}
