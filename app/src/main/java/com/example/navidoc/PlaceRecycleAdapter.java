package com.example.navidoc;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaceRecycleAdapter extends RecyclerView.Adapter<PlaceRecycleAdapter.ViewHolder>
{
    private Context context;
    private List<Place> places;

    public PlaceRecycleAdapter(Context context, List<Place> places)
    {
        this.context = context;
        this.places = places;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.place_item, parent, false);

        return new ViewHolder(view);
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

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView titleView;
        private TextView subtitleView;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            this.titleView = itemView.findViewById(R.id.title_item);
            this.subtitleView = itemView.findViewById(R.id.subtitle_item);
        }
    }

}
