package com.example.navidoc.Databse;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DAO
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDepartment(Department department);

    @Update
    void updateDepartment(Department department);

    @Delete
    void deleteDepartment(Department department);

    @Query("DELETE FROM department")
    void deleteAllDepartments();

    @Query("SELECT * FROM department")
    List<Department> getAllDepartments();

    @Query("SELECT * FROM department WHERE ID = :departmentId")
    Department getDepartmentByID(int departmentId);

    @Query("SELECT * FROM department WHERE name LIKE '%' || :name || '%'")
    List<Department> getDepartmentsByName(String name);

    @Query("SELECT * FROM department WHERE floor = :floor")
    List<Department> getDepartmentsByFloor(int floor);

    @Query("SELECT department.ID FROM department WHERE name LIKE :name")
    int getDepartmentIdByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDoctor(Doctor doctor);

    @Update
    void updatedDoctor(Doctor doctor);

    @Delete
    void deleteDoctor(Doctor doctor);

    @Query("DELETE FROM doctor")
    void deleteAllDoctors();

    @Query("SELECT * FROM doctor")
    List<Doctor> getAllDoctors();

    @Query("SELECT * FROM doctor WHERE Doctor_ID = :id")
    Doctor getDoctorById(int id);

    @Query("SELECT * FROM doctor WHERE name LIKE '%' || :name || '%'")
    List<Doctor> getDoctorsByName(String name);

    @Query("SELECT * FROM doctor WHERE ambulance_name LIKE '%' || :ambulanceName || '%'")
    List<Doctor> getDoctorsByAmbulanceName(String ambulanceName);

    @Transaction
    @Query("SELECT * FROM department")
    List<DepartmentWithDoctors> getDepartmentWithDoctors();

    @Transaction
    @Query("SELECT doctor.* FROM doctor INNER JOIN department" +
            " ON doctor.department_id = department.ID WHERE department.name LIKE '%' || :name || '%'")
    List<Doctor> getDoctorsFromDepartmentByDepartmentName(String name);
}
