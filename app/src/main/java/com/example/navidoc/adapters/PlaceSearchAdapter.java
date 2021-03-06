package com.example.navidoc.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.navidoc.database.DAO;
import com.example.navidoc.database.DatabaseHelper;
import com.example.navidoc.database.Department;
import com.example.navidoc.database.Doctor;
import com.example.navidoc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceSearchAdapter extends ArrayAdapter<Place>
{
    private final Context context;
    private final List<Place> places;
    private final int LIMIT;

    public PlaceSearchAdapter(@NonNull Context context, @NonNull List<Place> places)
    {
        super(context, R.layout.autocomplete_search_place_layout, places);
        this.context = context;
        this.places = places;
        this.LIMIT = 5;
    }

    @Override
    public int getCount()
    {
        return Math.min(this.LIMIT, this.places.size());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        @SuppressLint({"ViewHolder", "InflateParams"})
        View view = LayoutInflater.from(context).inflate(R.layout.autocomplete_search_place_layout, null);
        Place place = places.get(position);
        TextView doctorsName = view.findViewById(R.id.doctors_name);
        TextView department = view.findViewById(R.id.department);
        TextView ambulance = view.findViewById(R.id.ambulance);
        doctorsName.setText(place.getDoctorsName());
        ambulance.setText(place.getAmbulance());
        department.setText(place.getDepartment());

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter()
    {
        return new PlaceFilter(this, context);
    }

    private static class PlaceFilter extends Filter
    {
        private final PlaceSearchAdapter placeSearchAdapter;
        public List<Place> filteredPlaces;
        private final Context context;


        public PlaceFilter(PlaceSearchAdapter placeSearchAdapter, Context context)
        {
            this.placeSearchAdapter = placeSearchAdapter;
            this.filteredPlaces = new ArrayList<>();
            this.context = context;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            this.placeSearchAdapter.places.clear();
            FilterResults filterResults = new FilterResults();

            if (constraint != null && constraint.length() > 0)
            {
                DatabaseHelper db = DatabaseHelper.getInstance(context);
                List<Doctor> doctors = new ArrayList<>();
                List<Place> tmpPlaces = new ArrayList<>();
                DAO dao = db.dao();

                doctors.addAll(dao.getDoctorsByAmbulanceName(constraint.toString()));
                doctors.addAll(dao.getDoctorsByName(constraint.toString()));
                doctors.addAll(dao.getDoctorsFromDepartmentByDepartmentName(constraint.toString()));
                doctors = doctors.stream().distinct().collect(Collectors.toList());

                doctors.forEach(doctor -> {
                    Department department = dao.getDepartmentByID(doctor.getDepartment_id());

                    tmpPlaces.add(new Place(doctor.getAmbulance_name(), department.getName(), department.getFloor(),
                            doctor.getName(), doctor.getStart_time(), doctor.getEnd_time(),
                            doctor.getPhone_number(), doctor.getWeb_site(), doctor.getIsFavorite()));
                });

                filterResults.values = tmpPlaces;
                filterResults.count = tmpPlaces.size();
            }
            else
            {
                filterResults.values = new ArrayList<Place>();
                filterResults.count = 0;
            }

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            this.placeSearchAdapter.places.clear();
            if (results.values == null)
            {
                placeSearchAdapter.places.addAll(new ArrayList<>());
            }
            else
            {
                placeSearchAdapter.places.addAll((List<Place>) results.values);
            }
            placeSearchAdapter.notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue)
        {
            Place place = (Place) resultValue;

            return place.getDoctorsName();
        }
    }
}
