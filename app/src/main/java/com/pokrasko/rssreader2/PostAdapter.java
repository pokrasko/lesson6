package com.pokrasko.rssreader2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by pokrasko on 11.11.14.
 */
public class PostAdapter extends BaseAdapter {
    private PostList list;

    public PostAdapter(PostList list) {
        this.list = list;
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_item, viewGroup, false);
        }
        final Post post = list.get(i);

        TextView title = (TextView) view.findViewById(R.id.postTitleView);
        final WebView description = (WebView) view.findViewById(R.id.postDescriptionView);
        Button fullButton = (Button) view.findViewById(R.id.fullButton);

        title.setText(post.getTitle());
        description.loadData(post.getDescription(), "text/html; charset=utf-8", null);

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.changeVisibility();
                if (post.isVisible()) {
                    description.setVisibility(View.VISIBLE);
                } else {
                    description.setVisibility(View.GONE);
                }
            }
        });

        fullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PostActivity) viewGroup.getContext()).showMore(post.getTitle(), post.getUrl());
            }
        });

        return view;
    }
}
