package com.pokrasko.rssreader2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
/*import android.view.Menu;
import android.view.MenuItem;*/
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class FeedActivity extends ListActivity implements LoaderManager.LoaderCallbacks<FeedList> {
    ListView listView;
    TextView emptyView;
    Dialog dialog;

    private FeedAdapter adapter;
    private FeedResultReceiver receiver;

    public static final int FEED_ADD_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        dialog = createDialog();

        listView = (ListView) findViewById(R.id.feedListView);
        emptyView = (TextView) findViewById(R.id.noFeeds);

        receiver = new FeedResultReceiver(new Handler(), this);

        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);

        adapter = new FeedAdapter(new FeedList());
        setListAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Feed feed = (Feed) adapter.getItem(position);
                long feedId = feed.getId();
                String title = feed.getTitle();
                String description = feed.getDescription();
                String url = feed.getUrl();

                Intent intent = new Intent(getBaseContext(), PostActivity.class);
                intent.putExtra("feed_id", feedId);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public Loader<FeedList> onCreateLoader(int i, Bundle bundle) {
        return new FeedLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<FeedList> loader, FeedList list) {
        if (adapter.getCount() == 0) {
            listView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        adapter = new FeedAdapter(list);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        adapter = new FeedAdapter(new FeedList());
    }


    private void addFeed(String URL) {
        Intent intent = new Intent(this, FeedUpdater.class);
        intent.putExtra("new_feed", true);
        intent.putExtra("url", URL);
        intent.putExtra("receiver", receiver);
        startService(intent);
    }

    private Dialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.feed_add_dialog, null))
               .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String URL = ((EditText) findViewById(R.id.newFeedURL)).getText().toString();
                       addFeed(URL);
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
        return builder.create();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, FEED_ADD_ID, 0, R.string.add_feed);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == FEED_ADD_ID) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Feed feed = (Feed) adapter.getItem(acmi.position);
            long feedId = feed.getId();

            Uri uri = Uri.parse(FeedContentProvider.CONTENT_FEEDS_URI + "/" + feedId);
            getContentResolver().delete(uri, null, null);
            adapter.remove(acmi.position);
            return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_feed) {
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void onUpdatedFeed() {
        getLoaderManager().initLoader(0, null, this);
    }
}
