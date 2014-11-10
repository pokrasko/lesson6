package com.pokrasko.rssreader2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by pokrasko on 10.11.14.
 */
public class FeedAdapter extends BaseAdapter {
    private FeedList list;

    public FeedAdapter(FeedList list) {
        this.list = list;
    }

    public void add(Feed feed) {
        list.add(feed);
        notifyDataSetChanged();
    }

    public void remove(int i) {
        list.remove(i);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, final ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_item, viewGroup, false);
        }
        final Feed feed = list.get(i);

        TextView title = (TextView) view.findViewById(R.id.feedTitle);
        TextView description = (TextView) view.findViewById(R.id.feedDescription);
        title.setText(feed.getTitle());
        description.setText(feed.getDescription());

        return view;
    }
}
