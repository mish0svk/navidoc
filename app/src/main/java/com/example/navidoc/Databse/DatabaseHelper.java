package com.example.navidoc.Databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HospitalDatabase";

    //Department table
    private static final String TABLE_DEPARTMENT = "Departments";
    private static final String KEY_DEP_ID = "Department_Id";
    private static final String KEY_DEP_NAME = "Department_name";
    private static final String KEY_DEP_FLOOR = "Floor";

    // Doctor table
    private static final String TABLE_DOCTOR= "Doctors_new";
    private static final String KEY_DOC_ID = "Doctor_Id";
    private static final String KEY_DOC_NAME = "Doctor_name";
    private static final String KEY_DOC_AMBULANCE = "Doctor_ambulance";
    private static final String KEY_DOC_PHONE = "Doctor_phone";
    private static final String KEY_DOC_EMAIL = "Doctor_EMAIL";
    private static final String KEY_DOC_DOOR = "Doctor_door_number";
    private static final String KEY_DOC_WEB = "Doctor_web_site";
    private static final String KEY_DOC_START = "Doctor_start_time";
    private static final String KEY_DOC_END = "Doctor_end_time";
    private static final String KEY_DOC_DEPARTMENT = "Doctor_department";
    private static final String KEY_DOC_FAVORITE = "Doctor_favorite";
    /*KEY_DOC_ID, KEY_DOC_NAME, KEY_DOC_AMBULANCE, KEY_DOC_DOOR,
    KEY_DOC_PHONE, KEY_DOC_EMAIL, KEY_DOC_WEB, KEY_DOC_START,
    KEY_DOC_END, KEY_DOC_FAVORITE, KEY_DOC_DEPARTMENT*/


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    String CREATE_TABLE_DEP = "create table "
            + TABLE_DEPARTMENT + " ("
            + KEY_DEP_ID + " INTEGER primary key, "
            + KEY_DEP_NAME + " TEXT not null, "
            + KEY_DEP_FLOOR + " INTEGER" + ")";

    String CREATE_TABLE_DOC = "create table "
            + TABLE_DOCTOR + " ("
            + KEY_DOC_ID + " integer primary key autoincrement, "
            + KEY_DOC_NAME + " text not null, "
            + KEY_DOC_AMBULANCE + " text not null, "
            + KEY_DOC_DOOR + " text not null, "
            + KEY_DOC_PHONE + " text not null, "
            + KEY_DOC_EMAIL + " text not null, "
            + KEY_DOC_WEB + " text not null, "
            + KEY_DOC_START + " datetime not null, "
            + KEY_DOC_END + " datetime not null, "
            + KEY_DOC_FAVORITE + " integer default 0, "
            + KEY_DOC_DEPARTMENT + " INTEGER" + ")";


    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables

        db.execSQL(CREATE_TABLE_DEP);
        db.execSQL(CREATE_TABLE_DOC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPARTMENT);
        // create new tables
        onCreate(db);
    }

    public void addDepartment(Department department) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DEP_NAME, department.getName());
        values.put(KEY_DEP_FLOOR, department.getFloor());

        // Inserting Row
        db.insert(TABLE_DEPARTMENT, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public void addDoctor(Doctor doctor, int department) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DOC_NAME, doctor.getName());
        values.put(KEY_DOC_AMBULANCE, doctor.getAmbulance_name());
        values.put(KEY_DOC_DOOR, doctor.getDoor_number());
        values.put(KEY_DOC_PHONE, doctor.getPhone_number());
        values.put(KEY_DOC_EMAIL, doctor.getEmail());
        values.put(KEY_DOC_WEB, doctor.getWeb_site());
        values.put(KEY_DOC_START, doctor.getStart_time());
        values.put(KEY_DOC_END, doctor.getEnd_time());
        values.put(KEY_DOC_FAVORITE, doctor.getIsFavorite());
        values.put(KEY_DOC_DEPARTMENT, department);

        // Inserting Row
        db.insert(TABLE_DOCTOR, null, values);
        db.close();
    }

    public Department getDepartment(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DEPARTMENT, new String[]{KEY_DEP_ID,
                        KEY_DEP_NAME, KEY_DEP_FLOOR}, KEY_DEP_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Department department = new Department(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), Integer.parseInt(cursor.getString(2)));
        // return contact
        return department;
    }

    public List<Department> getAllDepartments() {
        List<Department> departmentList = new ArrayList<Department>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEPARTMENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Department department = new Department();
                department.setID(Integer.parseInt(cursor.getString(0)));
                department.setName(cursor.getString(1));
                department.setFloor(Integer.parseInt(cursor.getString(2)));
                // Adding contact to list
                departmentList.add(department);
            } while (cursor.moveToNext());
        }

        // return contact list
        return departmentList;
    }

    public Doctor getDoctor(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DOCTOR, new String[]{KEY_DOC_ID, KEY_DOC_NAME, KEY_DOC_AMBULANCE, KEY_DOC_DOOR,
                        KEY_DOC_PHONE, KEY_DOC_EMAIL, KEY_DOC_WEB, KEY_DOC_START,
                        KEY_DOC_END, KEY_DOC_FAVORITE, KEY_DOC_DEPARTMENT}, KEY_DOC_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Doctor doctor = new Doctor(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(4),
                cursor.getString(5),
                Integer.parseInt(cursor.getString(3)),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                Integer.parseInt(cursor.getString(9)));
        // return contact
        return doctor;
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctorList = new ArrayList<Doctor>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DOCTOR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Doctor doctor = new Doctor();
                doctor.setDoctor_ID(Integer.parseInt(cursor.getString(0)));
                doctor.setName(cursor.getString(1));
                doctor.setAmbulance_name(cursor.getString(2));
                doctor.setPhone_number(cursor.getString(4));
                doctor.setEmail(cursor.getString(5));
                doctor.setDoor_number(Integer.parseInt(cursor.getString(3)));
                doctor.setWeb_site(cursor.getString(6));
                doctor.setStart_time(cursor.getString(7));
                doctor.setEnd_time(cursor.getString(8));
                doctor.setIsFavorite(Integer.parseInt(cursor.getString(9)));
                // Adding contact to list
                doctorList.add(doctor);
            } while (cursor.moveToNext());
        }
        return  doctorList;
    }

    // return id of doctor department
    public int getDoctorDepartment(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DOCTOR, new String[]{KEY_DOC_ID, KEY_DOC_NAME, KEY_DOC_AMBULANCE, KEY_DOC_DOOR,
                        KEY_DOC_PHONE, KEY_DOC_EMAIL, KEY_DOC_WEB, KEY_DOC_START,
                        KEY_DOC_END, KEY_DOC_FAVORITE, KEY_DOC_DEPARTMENT}, KEY_DOC_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return Integer.parseInt(cursor.getString(10));
    }

    public List<Doctor> getDoctorsOnDepartment(int departmentID)
    {
        List<Doctor> doctorList = new ArrayList<Doctor>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DOCTOR + " WHERE "
                + KEY_DOC_DEPARTMENT + " = " + departmentID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Doctor doctor = new Doctor();
                doctor.setDoctor_ID(Integer.parseInt(cursor.getString(0)));
                doctor.setName(cursor.getString(1));
                doctor.setAmbulance_name(cursor.getString(2));
                doctor.setPhone_number(cursor.getString(4));
                doctor.setEmail(cursor.getString(5));
                doctor.setDoor_number(Integer.parseInt(cursor.getString(3)));
                doctor.setWeb_site(cursor.getString(6));
                doctor.setStart_time(cursor.getString(7));
                doctor.setEnd_time(cursor.getString(8));
                doctor.setIsFavorite(Integer.parseInt(cursor.getString(9)));
                // Adding contact to list
                doctorList.add(doctor);
            } while (cursor.moveToNext());
        }

        return  doctorList;
    }

}

