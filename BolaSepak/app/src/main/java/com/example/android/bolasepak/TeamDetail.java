package com.example.android.bolasepak;

public class TeamDetail {
    private String matchDate;
    private int PhotoTeam1;
    private int PhotoTeam2;
    private String TeamName1;
    private String TeamName2;
    private String scoreTeam1;
    private String scoreTeam2;
    private String idEvent;

    public TeamDetail(){}

    public TeamDetail(String teamName1, String teamName2,  int photoTeam1, int photoTeam2, String matchDate, String scoreTeam1, String scoreTeam2, String idEvent) {
        TeamName1 = teamName1;
        TeamName2 = teamName2;
        PhotoTeam1 = photoTeam1;
        PhotoTeam2 = photoTeam2;
        this.matchDate = matchDate;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.idEvent = idEvent;
    }

    public String getTeamName1() {
        return TeamName1;
    }

    public String getTeamName2() {
        return TeamName2;
    }


    public int getPhotoTeam1() {
        return PhotoTeam1;
    }

    public int getPhotoTeam2() {
        return PhotoTeam2;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public String getScoreTeam1() {
        return scoreTeam1;
    }

    public String getScoreTeam2() {
        return scoreTeam2;
    }

    public String getIdEvent(){ return idEvent; }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public void setPhotoTeam1(int photoTeam1) {
        this.PhotoTeam1 = photoTeam1;
    }

    public void setPhotoTeam2(int photoTeam2) {
        this.PhotoTeam2 = photoTeam2;
    }

    public void setTeamName1(String teamName1) {
        this.TeamName1 = teamName1;
    }

    public void setTeamName2(String teamName2) {
        this.TeamName2 = teamName2;
    }

    public void setScoreTeam1(String scoreTeam1) {
        this.scoreTeam1 = scoreTeam1;
    }

    public void setScoreTeam2(String scoreTeam2) {
        this.scoreTeam2 = scoreTeam2;
    }

    public void setIdEvent(String idEvent) {this.idEvent = idEvent;}
}


