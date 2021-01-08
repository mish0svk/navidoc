package com.example.navidoc.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navidoc.R;
import com.example.navidoc.activities.ARCameraActivity;
import com.example.navidoc.adapters.Place;
import com.example.navidoc.database.DAO;
import com.example.navidoc.database.DatabaseHelper;
import com.example.navidoc.database.Doctor;
import com.example.navidoc.database.History;
import com.example.navidoc.database.Node;

import java.io.Serializable;
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
    private AppCompatActivity context;

    private AlertDialog.Builder builder;

    public static AbstractDialog getInstance()
    {
        if (instance == null)
        {
            instance = new AbstractDialog();
        }

        return instance;
    }

    public AbstractDialog newBuilderInstance(AppCompatActivity context)
    {
        builder = new AlertDialog.Builder(context);
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        dao = db.dao();
        this.context = context;

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

    public AbstractDialog sePositiveButton(String currentLocation, Place place)
    {
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            addNewHistory();

            List<Doctor> tmp = dao.getDoctorsByName(place.getDoctorsName());
            Intent intent = null;
            if (Objects.requireNonNull(tmp).size() > 0)
            {
                Doctor doctor = tmp.get(0);
                if (doctor.getBeacon_unique_id()  == null || doctor.getBeacon_unique_id().isEmpty())
                {
                    MessageToast.makeToast(this.context, R.string.unavailable_location, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    doctor.setHistory_id(dao.getLastHistory().getHistory_ID());
                    dao.updatedDoctor(doctor);
                    Path path = getShortestPath(dao.getNodeByUniqueId(currentLocation), doctor.getName());
                    if (path != null)
                    {
                        intent = new Intent(builder.getContext(), ARCameraActivity.class);
                        intent.putExtra("path", path);
                        intent.putExtra("list", (Serializable) path.getHops());
                    }

                }
            }

            if (intent != null)
            {
                builder.getContext().startActivity(intent);
            }
        });

        return instance;
    }

    public AbstractDialog setNegativeButton(AppCompatActivity context)
    {
        builder.setNegativeButton(R.string.no, (dialog, which) -> MessageToast.makeToast(context, R.string.button_no_clicked, Toast.LENGTH_SHORT).show());

        return instance;
    }

    public AbstractDialog setPositiveButtonForMain(String currentLocation, String destination, List<Doctor> doctors)
    {
        builder.setPositiveButton(R.string.yes, (dialog, which) ->
        {
            addNewHistory();

            Intent intent = null;
            if (Objects.requireNonNull(doctors).size() > 0)
            {
                Doctor doctor = doctors.get(0);
                if (doctor.getBeacon_unique_id()  == null || doctor.getBeacon_unique_id().isEmpty())
                {
                    MessageToast.makeToast(this.context, R.string.unavailable_location, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    doctor.setHistory_id(dao.getLastHistory().getHistory_ID());
                    dao.updatedDoctor(doctor);
                    Path path = getShortestPath(dao.getNodeByUniqueId(currentLocation), destination);
                    if (path != null)
                    {
                        intent = new Intent(builder.getContext(), ARCameraActivity.class);
                        intent.putExtra("path", path);
                        intent.putExtra("list", (Serializable) path.getHops());
                    }

                }
            }

            if (intent != null)
            {
                builder.getContext().startActivity(intent);
            }

        });

        return instance;
    }

    private Path getShortestPath(Node currentLocation, String destination)
    {
        Node destinationLocation = dao.getNodeByUniqueId(destination);
        Dijkstra dijkstra = new Dijkstra(dao, currentLocation);
        Graph graph = dijkstra.calculateShortestPathFromSource();
        String destinationUniqueId = dao.getDoctorsByName(destination).get(0).getBeacon_unique_id();

        NodeGraph node = null;
        for (NodeGraph nodeGraph: graph.getNodes())
        {
            if (nodeGraph.getName().equals(destinationUniqueId))
            {
                node = nodeGraph;
            }
        }

        if (node != null)
        {

            int distance = 0;
            for (NodeGraph nodeGraph: node.getShortestPath())
            {
                distance += nodeGraph.getDistance();
            }

            NodeGraph tmp = new NodeGraph(destinationUniqueId);
            tmp.setDistance(node.getDistance() - distance);
            node.getShortestPath().add(tmp);

            return new Path(node);
        }
        else
        {
            MessageToast.makeToast(context, "something went wrong", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void addNewHistory()
    {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
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
