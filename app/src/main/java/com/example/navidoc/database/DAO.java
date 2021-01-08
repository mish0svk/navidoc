package com.example.navidoc.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@SuppressWarnings("unused")
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

    @Query("SELECT * FROM doctor WHERE history_id != 0")
    List<Doctor> getAllDoctorsWithHistory();

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

    @Query("SELECT * FROM doctor WHERE isFavorite = :favourite")
    List<Doctor> getDoctorsByFavourite(int favourite);

    @Query("SELECT * FROM doctor WHERE history_id != 0 ORDER BY history_id DESC")
    List<Doctor> getNewestDoctorsByHistory();

    @Query("SELECT * FROM doctor WHERE history_id != 0 ORDER BY history_id ASC")
    List<Doctor> getOldestDoctorsByHistory();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistory(History history);

    @Update
    void updatedHistory(History history);

    @Delete
    void deleteHistory(History history);

    @Query("DELETE FROM history")
    void deleteAllHistories();

    @Query("SELECT * FROM history")
    List<History> getAllHistories();

    @Query("SELECT * FROM history ORDER BY history_id DESC LIMIT 1")
    History getLastHistory();

    @Query("SELECT * FROM history ORDER BY date DESC")
    History getLastHistories();

    @Query("SELECT * FROM history WHERE History_ID = :id")
    History getHistoryById(int id);

    @Transaction
    @Query("SELECT * FROM history")
    List<DoctorsHistory> getDoctorsHistory();

    @Query("SELECT * FROM doctor WHERE beacon_unique_id = :uniqueId")
    Doctor getDoctorByBeaconUniqueId(String uniqueId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNode(Node node);

    @Update
    void updateNode(Node node);

    @Delete
    void deleteNode(Node node);

    @Query("DELETE FROM node")
    void deleteAllNodes();

    @Query("SELECT * FROM node WHERE uniqueId = :uniqueId")
    Node getNodeByUniqueId(String uniqueId);

    @Transaction
    @Query("SELECT * FROM node WHERE uniqueId = :uniqueId")
    NodeWithNeighborNodes getNodeWithNeighborsByUniqueId(String uniqueId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNeighborNode(NeighborNode node);

    @Update
    void updateNeighborNode(NeighborNode node);

    @Delete
    void deleteNeighborNode(NeighborNode node);

    @Query("DELETE FROM neighbornode")
    void deleteAllNeighborNodes();

    @Transaction
    @Query("SELECT * FROM node")
    List<NodeWithNeighborNodes> getNodesWithNeighbors();

    @Transaction
    @Query("SELECT * FROM neighbornode WHERE nodeId = :nodeId")
    List<NeighborNode> getNeighborNodesByNodeId(int nodeId);
}
