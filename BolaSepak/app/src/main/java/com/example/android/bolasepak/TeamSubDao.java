package com.example.android.bolasepak;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TeamSubDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TeamSub teamSub);

    @Query("DELETE FROM team_sub_table WHERE id = :id")
    void deleteTeam(String id);

    @Query("SELECT * from team_sub_table")
    List<TeamSub> getTeamSubs();

}

