package com.example.android.bolasepak;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "team_table")
public class Team {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String mId;

    @ColumnInfo(name = "name")
    private String mName;

    public Team(@NonNull String id, String name) {
        this.mId = id;
        this.mName = name;
    }

    public String getId(){return this.mId;}

    public String getName(){return this.mName;}

}