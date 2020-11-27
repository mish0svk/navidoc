package com.example.navidoc.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navidoc.R;

public class MessageToast
{
    public static Toast makeToast(AppCompatActivity context, String text, int length)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_layout, context.findViewById(R.id.toast_layout_root));
        TextView textView = layout.findViewById(R.id.toast);
        textView.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(length);
        toast.setView(layout);

        return toast;
    }

    public static Toast makeToast(AppCompatActivity context, int id, int length)
    {
        return makeToast(context, context.getResources().getString(id), length);
    }
}
