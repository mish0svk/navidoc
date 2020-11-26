package com.example.navidoc.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.navidoc.R;
import com.example.navidoc.adapters.Place;
import com.example.navidoc.database.DAO;
import com.example.navidoc.database.DatabaseHelper;
import com.example.navidoc.database.Doctor;
import com.example.navidoc.database.History;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class AbstractDialog
{
    private static final String TAG = "TAG";
    private static AbstractDialog instance;
    private DAO dao;

    private AlertDialog.Builder builder;

    public static AbstractDialog getInstance()
    {
        if (instance == null)
        {
            instance = new AbstractDialog();
        }

        return instance;
    }

    public AbstractDialog newBuilderInstance(Context context)
    {
        builder = new AlertDialog.Builder(context);
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        dao = db.dao();

        return instance;
    }

    public AlertDialog.Builder getBuilder()
    {
        return builder;
    }

    public AbstractDialog setTitle(int title)
    {
        builder.setTitle(title);

        return instance;
    }

    public AbstractDialog setTitle(String title)
    {
        builder.setTitle(title);

        return instance;
    }

    public AbstractDialog setMessage(int message)
    {
        builder.setMessage(message);

        return instance;
    }

    public AbstractDialog setMessage(String message)
    {
        builder.setMessage(message);

        return instance;
    }

    public AbstractDialog sePositiveButton(Place place)
    {
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            addNewHistory();

            List<Doctor> tmp = dao.getDoctorsByName(place.getDoctorsName());
            if (Objects.requireNonNull(tmp).size() > 0)
            {
                Doctor doctor = tmp.get(0);
                doctor.setHistory_id(dao.getLastHistory().getHistory_ID());
                dao.updatedDoctor(doctor);
            }
        });

        return instance;
    }

    public AbstractDialog setNegativeButton(Context context)
    {
        builder.setNegativeButton(R.string.no, (dialog, which) -> Toast.makeText(context, R.string.button_no_clicked, Toast.LENGTH_SHORT).show());

        return instance;
    }

    public AbstractDialog setPositiveButtonForMain(List<Doctor> doctors)
    {
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            // Do something when user clicked the Yes button
            // Set the TextView visibility GONE
            addNewHistory();

            if (Objects.requireNonNull(doctors).size() > 0)
            {
                Doctor doctor1 = doctors.get(0);
                doctor1.setHistory_id(dao.getLastHistory().getHistory_ID());
                dao.updatedDoctor(doctor1);
            }

        });

        return instance;
    }

    @SuppressLint("SimpleDateFormat")
    public void addNewHistory()
    {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        String[] arrSplit = strDate.split(" ");
        String time = arrSplit[1];
        String date1 = arrSplit[0];
        Log.d(TAG, "Datetime: " + date1);
        Log.d(TAG, "Datetime: " + time);
        History history = new History(date1,time);

        dao.insertHistory(history);
    }
}
