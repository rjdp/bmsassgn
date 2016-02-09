package com.example.android.sunshine.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by A.K ABHI on 09-02-2016.
 */
public class PostsAdapter extends ArrayAdapter<Post> {

        private static class ViewHolder {
            TextView id;
            TextView userId;
            TextView title;
            TextView body;

        }

        public PostsAdapter(Context context, ArrayList<Post> Posts) {
            super(context, R.layout.item_post, Posts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Post post = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_post, parent, false);
                viewHolder.id = (TextView) convertView.findViewById(R.id.tvId);
                viewHolder.userId = (TextView) convertView.findViewById(R.id.tvUserId);
                viewHolder.title = (TextView) convertView.findViewById(R.id.tvTitle);
                viewHolder.body = (TextView) convertView.findViewById(R.id.tvBody);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.id.setText("#"+Integer.toString(post.id));
            viewHolder.userId.setText(Integer.toString(post.userId));
            viewHolder.title.setText(post.title);
            viewHolder.body.setText(post.body);
            // Return the completed view to render on screen
            return convertView;
        }
    }
