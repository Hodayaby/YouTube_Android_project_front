package com.example.myyoutube;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.PostViewHolder> implements Filterable {

    // Listener interface to handle post filtering results
    public interface PostsAdapterListener {
        void onPostsFiltered(int count);
    }

    // ViewHolder class to hold the views for each post item
    public class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView videoTitle;
        private final TextView videoDetails;
        private final ImageView ivPic;
        private final ImageView channelImage;

        // Constructor to initialize the views in each item
        private PostViewHolder(View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoDetails = itemView.findViewById(R.id.videoDetails);
            ivPic = itemView.findViewById(R.id.thumbnail);
            channelImage = itemView.findViewById(R.id.channelImage);
        }
    }

    private final LayoutInflater mInflater;
    private List<Post> posts; // List to hold current posts
    private List<Post> postsFull; // List to hold all posts (for filtering)
    private PostsAdapterListener listener; // Listener to notify about filter results

    // Constructor for initializing the adapter and listener
    public PostsListAdapter(Context context, PostsAdapterListener listener) {
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    // Create a new ViewHolder for each item
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.posts_layout, parent, false);
        return new PostViewHolder(itemView);
    }

    // Bind the data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        if (posts != null) {
            final Post current = posts.get(position);
            holder.videoTitle.setText(current.getContent());
            holder.videoDetails.setText(current.getAuthor() + " • " + current.getViews() + " • " + current.getUploadTime());
            holder.ivPic.setImageURI(Uri.parse(current.getImageUri()));
            holder.channelImage.setImageResource(current.getChannelImage());

            // Add click listener to each item
            holder.itemView.setOnClickListener(v -> {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, VideoViewActivity.class);
                intent.putExtra("videoTitle", current.getContent());
                intent.putExtra("videoAuthor", current.getAuthor());
                intent.putExtra("videoViews", current.getViews());
                intent.putExtra("videoUploadTime", current.getUploadTime());
                intent.putExtra("videoPic", current.getImageUri());
                intent.putExtra("videoChannelImage", current.getChannelImage());
                intent.putExtra("videoUri", current.getVideoUri());
                context.startActivity(intent);
            });
        }
    }

    // Return the total number of items in the adapter
    @Override
    public int getItemCount() {
        if (posts != null)
            return posts.size();
        else return 0;
    }

    // Set the list of posts and notify the adapter
    public void setPosts(List<Post> s) {
        posts = s;
        postsFull = new ArrayList<>(s); // Copy for filtering purposes
        notifyDataSetChanged();
    }

    // Return the filter for the posts
    @Override
    public Filter getFilter() {
        return postFilter;
    }

    // Filter class to filter posts based on search input
    private Filter postFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Post> filteredList = new ArrayList<>();

            // Check if the search query is empty
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(postsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Post post : postsFull) {
                    // Filter posts by content or author
                    if (post.getContent().toLowerCase().contains(filterPattern) || post.getAuthor().toLowerCase().contains(filterPattern)) {
                        filteredList.add(post);
                    }
                }
            }

            // Return the filtered results
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            posts.clear();
            posts.addAll((List) results.values);
            // Notify listener about the filtered results
            if (listener != null) {
                listener.onPostsFiltered(posts.size());
            }
            notifyDataSetChanged();
        }
    };
}
