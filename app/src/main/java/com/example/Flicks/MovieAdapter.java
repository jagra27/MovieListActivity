package com.example.Flicks;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Flicks.models.Config;
import com.example.Flicks.models.Movie;
import com.example.movielistactivity.R;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    //instance field
    //list of movies
    ArrayList<Movie> movies;
    Config config;
    // context field to be available for the adapter
    Context context;
    //initialization with the list

    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    //creates and inflates a new view

    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create the view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        // return a new ViewHolder
        return new ViewHolder(movieView);
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the movie data at its position
        Movie movie = movies.get(position);
        // populate the layout view with movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        // basically this is for whether or not the orientation is portrait or landscape
        boolean isPortait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        // give imageUrl a value of null first since its assignment is now if an if-else
        String imageUrl = null;

        if (isPortait){
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());

        }
        else {
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        /*
        now we need to get the correct placeholdder for the orientation, could use if else block but this is a different variation
        it is called a ternary expression
        */
        int placeholderId = isPortait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortait ? holder.ivPosterImage : holder.ivBackdropImage;

        // build url for poster image
        //now load the image using glide
        Glide.with(context)
                .load(imageUrl)
                .placeholder((placeholderId)) // if the image doesn't load it will show this as a placeholder
                .error(placeholderId)// if the image doesn't load at all it will show this
                .into(imageView);

    }
    // returns the total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    //create the viewholder as a static inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // track the view objects(for the layout of the UI)
        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(View itemView){
            super(itemView);
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropimage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);

        }
    }
}
