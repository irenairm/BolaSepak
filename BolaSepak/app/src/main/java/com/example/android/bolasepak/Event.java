package com.example.android.bolasepak;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "event_table")
public class Event {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "idEvent")
    private String mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "homeName")
    private String mHomeName;

    @ColumnInfo(name = "awayName")
    private String mAwayName;

    @ColumnInfo(name = "homeScore")
    private String mHomeScore;

    @ColumnInfo(name = "awayScore")
    private String mAwayScore;

    @ColumnInfo(name = "homeGoalDetail")
    private String mHomeGoalDetail;

    @ColumnInfo(name = "awayGoalDetail")
    private String mAwayGoalDetail;

    @ColumnInfo(name = "homeShot")
    private String mHomeShot;

    @ColumnInfo(name = "awayShot")
    private String mAwayShot;

    @ColumnInfo(name = "date")
    private String mDate;

    @ColumnInfo(name = "city")
    private String mCity;

    @ColumnInfo(name = "idHome")
    private String mIdHome;

    @ColumnInfo(name = "idAway")
    private String mIdAway;

    public Event(@NonNull String id, String name, String homeName, String awayName, String homeScore, String awayScore, String homeGoalDetail, String awayGoalDetail, String homeShot, String awayShot, String date, String city, String idHome, String idAway) {
        this.mId = id;
        this.mName = name;
        this.mHomeName = homeName;
        this.mAwayName = awayName;
        this.mHomeScore = homeScore;
        this.mAwayScore = awayScore;
        this.mHomeGoalDetail = homeGoalDetail;
        this.mAwayGoalDetail = awayGoalDetail;
        this.mHomeShot = homeShot;
        this.mAwayShot = awayShot;
        this.mDate = date;
        this.mCity = city;
        this.mIdHome = idHome;
        this.mIdAway = idAway;
    }

    public String getId(){return this.mId;}

    public String getName(){return this.mName;}

    public String getHomeName(){return this.mHomeName;}

    public String getAwayName(){return this.mAwayName;}

    public String getHomeScore(){return this.mHomeScore;}

    public String getAwayScore(){return this.mAwayScore;}

    public String getHomeGoalDetail(){return this.mHomeGoalDetail;}

    public String getAwayGoalDetail(){return this.mAwayGoalDetail;}

    public String getHomeShot(){return this.mHomeShot;}

    public String getAwayShot(){return this.mAwayShot;}

    public String getDate(){return this.mDate;}

    public String getCity(){return this.mCity;}

    public String getIdHome(){return this.mIdHome;}

    public String getIdAway(){return this.mIdAway;}
}