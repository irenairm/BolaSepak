package com.example.android.bolasepak;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "team_sub_table")
public class TeamSub {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String mId;

    public TeamSub(@NonNull String id) {
        this.mId = id;
    }

    public String getId(){return this.mId;}

}