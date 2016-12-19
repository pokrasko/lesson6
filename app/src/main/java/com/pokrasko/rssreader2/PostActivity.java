package com.pokrasko.rssreader2;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class PostActivity extends ListActivity implements LoaderManager.LoaderCallbacks<PostList> {
    private long feedId;
    private String title;
    private String description;
    private FeedResultReceiver receiver;

    TextView descriptionView;
    ListView listView;
    TextView emptyView;
    Button refreshButton;

    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        feedId = intent.getLongExtra("feed_id", -1);
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");

        receiver = new FeedResultReceiver(new Handler(), this);

        listView = (ListView) findViewById(android.R.id.list);
        descriptionView = (TextView) findViewById(R.id.descriptionView);
        emptyView = (TextView) findViewById(R.id.noPosts);
        refreshButton = (Button) findViewById(R.id.refreshButton);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });

        setTitle(title);
        descriptionView.setText(description);
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);

        adapter = new PostAdapter(new PostList());
        setListAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);

        onRefresh();
    }


    @Override
    public Loader<PostList> onCreateLoader(int i, Bundle bundle) {
        return new PostLoader(this, feedId);
    }

    @Override
    public void onLoadFinished(Loader<PostList> loader, PostList list) {
        if (list.size() == 0) {
            listView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        adapter = new PostAdapter(list);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        adapter = new PostAdapter(new PostList());
        setListAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_feed:
                Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void onRefresh() {
        if (FeedUpdater.running) {
            return;
        }
        Intent intent = new Intent(this, FeedUpdater.class);
        intent.putExtra("feed_id", feedId);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("receiver", receiver);
        startService(intent);
    }

    void onUpdatedFeed() {
        getLoaderManager().restartLoader(0, null, this);
    }

    public void showMore(String title, String url) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}
