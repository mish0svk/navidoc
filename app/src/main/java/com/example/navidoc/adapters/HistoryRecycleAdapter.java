package com.example.navidoc.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navidoc.R;

import java.util.List;

public class HistoryRecycleAdapter extends RecyclerView.Adapter<HistoryRecycleAdapter.ViewHolder>
{
    private final List<Place> places;
    private final OnPlaceListener onPlaceListener;
    private static final String TAG = "PlaceRecycleAdapter";

    public HistoryRecycleAdapter(List<Place> places, OnPlaceListener onPlaceListener)
    {
        this.places = places;
        this.onPlaceListener = onPlaceListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.place_item_history, parent, false);

        return new ViewHolder(view, this.onPlaceListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Place place = this.places.get(position);
        String title = place.getAmbulance();
        String date = place.getDate();
        if (title.length() > 13)
        {
            holder.titleView.setTextSize(14);
        }

        holder.titleView.setText(title);
        holder.dateView.setText((date));
        holder.subtitleView.setText(String.format("%s, %s", place.getDoctorsName(), place.getDepartment()));

        if (place.isFavourite() == 1)
        {
            holder.favouriteButton.setBackgroundResource(R.drawable.ic_full_star);
        }
        else
        {
            holder.favouriteButton.setBackgroundResource(R.drawable.ic_star);
        }
    }

    @Override
    public int getItemCount()
    {
        return this.places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final TextView titleView;
        private final TextView subtitleView;
        private final TextView dateView;
        private final OnPlaceListener onPlaceListener;
        private final ImageButton favouriteButton;

        public ViewHolder(@NonNull View itemView, OnPlaceListener onPlaceListener)
        {
            super(itemView);

            this.titleView = itemView.findViewById(R.id.title_item);
            this.subtitleView = itemView.findViewById(R.id.subtitle_item);
            this.dateView = itemView.findViewById(R.id.date_item);
            this.onPlaceListener = onPlaceListener;
            ConstraintLayout navigateLayout = itemView.findViewById(R.id.item_navigate_layout);
            ConstraintLayout mainLayout = itemView.findViewById(R.id.item_history_layout);
            this.favouriteButton = itemView.findViewById(R.id.item_favourite);
            ImageButton infoButton = itemView.findViewById(R.id.item_info);
            ImageButton navigateButton = itemView.findViewById(R.id.navigate_item);

            mainLayout.setOnClickListener(this);
            navigateLayout.setOnClickListener(this);
            titleView.setOnClickListener(this);
            subtitleView.setOnClickListener(this);
            favouriteButton.setOnClickListener(this);
            infoButton.setOnClickListener(this);
            navigateButton.setOnClickListener(this);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.item_history_layout:
                case R.id.title_item:
                case R.id.subtitle_item:
                case R.id.item_info:
                    onPlaceListener.onPlaceClick(getAdapterPosition());
                    break;
                case R.id.item_navigate_layout:
                case R.id.navigate_item:
                case R.id.navigate:
                    onPlaceListener.onNavigateClick(getAdapterPosition());
                    break;
                case R.id.item_favourite:
                    onPlaceListener.onFavouriteClick(getAdapterPosition());
                    break;
                default:
                    Log.d(TAG, "onClick: Unknown id");
                    break;
            }

        }
    }

}
