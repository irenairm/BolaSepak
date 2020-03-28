package com.example.android.bolasepak;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TeamDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Team team);

    @Query("DELETE FROM team_table")
    void deleteAll();

    @Query("SELECT * from team_table")
    List<Team> getTeams();

    @Query("SELECT name from team_table WHERE id = :id")
    String getName(String id);

}

