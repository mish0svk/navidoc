package com.example.navidoc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.res.ResourcesCompat.getColor;

public class PlaceListAdapter extends ArrayAdapter<Place>
{
    private final List<Place> places;
    private final Context context;
    private final int resource;

    public PlaceListAdapter(@NonNull Context context, int resource, @NonNull List<Place> objects)
    {
        super(context, resource, objects);
        this.places = new ArrayList<>(objects);
        this.context = context;
        this.resource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        convertView = layoutInflater.inflate(this.resource, parent, false);

        TextView titleView = convertView.findViewById(R.id.title_item);
        TextView subtitleView = convertView.findViewById(R.id.subtitle_item);
        String subtitle = this.places.get(position).getDoctorsName() + ", " + this.places.get(position).getDepartment();

        titleView.setText(this.places.get(position).getAmbulance());
        subtitleView.setText(subtitle);

        //convertView.setBackgroundColor(getColor(context.getResources(), R.color.black, context.getTheme()));

        return convertView;
    }
}
