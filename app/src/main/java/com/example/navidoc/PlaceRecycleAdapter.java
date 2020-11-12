package com.example.navidoc;

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

import java.util.List;

public class PlaceRecycleAdapter extends RecyclerView.Adapter<PlaceRecycleAdapter.ViewHolder>
{
    private List<Place> places;
    private OnPlaceListener onPlaceListener;
    private static final String TAG = "PlaceRecycleAdapter";

    public PlaceRecycleAdapter(List<Place> places, OnPlaceListener onPlaceListener)
    {
        this.places = places;
        this.onPlaceListener = onPlaceListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.place_item, parent, false);

        return new ViewHolder(view, this.onPlaceListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Place place = this.places.get(position);
        holder.titleView.setText(place.getAmbulance());
        holder.subtitleView.setText(String.format("%s, %s", place.getDoctorsName(), place.getDepartment()));
    }

    @Override
    public int getItemCount()
    {
        return this.places.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView titleView;
        private TextView subtitleView;
        private OnPlaceListener onPlaceListener;
        private ConstraintLayout mainLayout;
        private ConstraintLayout navigateLayout;
        private ImageButton favouriteButton;
        private ImageButton infoButton;
        private ImageButton navigateButton;

        public ViewHolder(@NonNull View itemView, OnPlaceListener onPlaceListener)
        {
            super(itemView);

            this.titleView = itemView.findViewById(R.id.title_item);
            this.subtitleView = itemView.findViewById(R.id.subtitle_item);
            this.onPlaceListener = onPlaceListener;
            this.navigateLayout = itemView.findViewById(R.id.item_navigate_layout);
            this.mainLayout = itemView.findViewById(R.id.item_main_layout);
            this.favouriteButton = itemView.findViewById(R.id.item_favourite);
            this.infoButton = itemView.findViewById(R.id.item_info);
            this.navigateButton = itemView.findViewById(R.id.navigate_item);

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
                case R.id.item_main_layout:
                case R.id.title_item:
                case R.id.subtitle_item:
                case R.id.item_info:
                    onPlaceListener.onPlaceClick(getAdapterPosition());
                    break;
                case R.id.item_navigate_layout:
                case R.id.navigate_item:
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
