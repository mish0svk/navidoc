package com.example.navidoc;

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

import com.example.navidoc.Databse.DAO;
import com.example.navidoc.Databse.DatabaseHelper;
import com.example.navidoc.Databse.Department;
import com.example.navidoc.Databse.Doctor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceSearchAdapter extends ArrayAdapter<Place>
{
    private Context context;
    private List<Place> places;
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
        @SuppressLint("ViewHolder")
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

    private class PlaceFilter extends Filter
    {
        private PlaceSearchAdapter placeSearchAdapter;
        public List<Place> filteredPlaces;
        private Context context;


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

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            this.placeSearchAdapter.places.clear();
            placeSearchAdapter.places.addAll((List<Place>) results.values);
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
