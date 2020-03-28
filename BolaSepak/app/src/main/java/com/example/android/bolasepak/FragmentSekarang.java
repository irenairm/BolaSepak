package com.example.android.bolasepak;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentSekarang extends Fragment {
    View view;
    private RecyclerView myRecyclerView;
    private List<TeamDetail> listTeamDetail;
    private String url = "http://134.209.97.218:5050/api/v1/json/1/eventsnext.php?id=";
    private RecyclerViewAdapter recyclerViewAdapter;
    private ImageView homeLogoImageView, awayLogoImageView;
    private String idTeam;
    private String idEvent;
    private Event event1;

    public FragmentSekarang() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.match_sekarang, container, false);
        myRecyclerView =  view.findViewById(R.id.recyclerViewSekarang);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            myRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        } else {
            myRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }

        idTeam = this.getArguments().getString("idTeam");
        url += idTeam;

        listTeamDetail = new ArrayList<>();

        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected){
            getData();
        } else {
            new SetEventTeam().execute(getActivity());
        }

        recyclerViewAdapter =  new RecyclerViewAdapter(getContext(), listTeamDetail);
        myRecyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
        return view;
    }


    private class SetDatabase extends AsyncTask<Context, Void, List<Event>>{
        @Override
        protected List<Event> doInBackground(Context... context) {
            EventRoomDatabase db = EventRoomDatabase.getDatabase(context[0]);
            List<Event> list = db.eventDao().getEvents();
            db.eventDao().insert(event1);
            list = db.eventDao().getEvents();
            return list;
        }
    }

    private class SetEventTeam extends AsyncTask<Context, Void, List<Event>>{
        @Override
        protected List<Event> doInBackground(Context... context) {
            EventRoomDatabase db = EventRoomDatabase.getDatabase(context[0]);
            List<Event> list = db.eventDao().getEvents();
            return list;
        }

        @Override
        protected void onPostExecute(List<Event> list) {
            super.onPostExecute(list);
            if(list != null){
                for (Event event : list){
                    if (event.getIdHome().equals(idTeam) || event.getIdAway().equals(idTeam)){
                        String strDate = event.getDate();
                        Date date1 = null;
                        try {
                            date1 = new SimpleDateFormat("dd MMMM yyyy").parse(strDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date2 = new Date();
                        if(date1.compareTo(date2) >= 0) {
                            TeamDetail team = new TeamDetail();
                            team.setTeamName1(event.getHomeName());
                            team.setTeamName2(event.getAwayName());
                            team.setScoreTeam1(event.getHomeScore());
                            team.setScoreTeam2(event.getAwayScore());
                            team.setIdEvent(event.getId());
                            team.setMatchDate(event.getDate());
                            team.setPhotoTeam1(R.drawable.ic_launcher_background);
                            team.setPhotoTeam2(R.drawable.ic_launcher_background);
                            listTeamDetail.add(team);
                        }
                    }
                }
                recyclerViewAdapter.addData(listTeamDetail);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listTeamDetail = new ArrayList<>();
    }

    private void getData() {
        final Date d = new Date();
        Log.d("EWWWH",url);
        boolean isConnected = true;

        if (isConnected){
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray events = response.getJSONArray("events");
                        if (response.length() > 0) {
                            for (int i = 0; i < events.length(); i++) {
                                try {
                                    JSONObject jsonObject = events.getJSONObject(i);
                                    TeamDetail team = new TeamDetail();
                                    team.setTeamName1(jsonObject.getString("strHomeTeam"));
                                    team.setTeamName2(jsonObject.getString("strAwayTeam"));
                                    team.setScoreTeam1("-");
                                    team.setScoreTeam2("-");
                                    team.setIdEvent(jsonObject.getString("idEvent"));
                                    String date = jsonObject.getString("dateEvent");
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date date_match = formatter.parse(date);
                                        formatter = new SimpleDateFormat("dd MMMM yyyy");
                                        date = formatter.format(date_match);
                                        team.setMatchDate(date);
                                    } catch (Exception e) {
                                        Log.e("Error", e.getMessage());
                                        e.printStackTrace();
                                    }
                                    team.setPhotoTeam1(R.drawable.ic_launcher_background);
                                    team.setPhotoTeam2(R.drawable.ic_launcher_background);
                                    if (team.getMatchDate()!=null) {
                                        listTeamDetail.add(team);
                                        String id,name,homeName,awayName,homeScore,awayScore,homeDetail,awayDetail,homeShot,awayShot,city,idHome,idAway;
                                        id = jsonObject.getString("idEvent");
                                        name = jsonObject.getString("strEvent");
                                        homeName = jsonObject.getString("strHomeTeam");
                                        awayName = jsonObject.getString("strAwayTeam");
                                        homeScore = jsonObject.getString("intHomeScore");
                                        awayScore = jsonObject.getString("intAwayScore");
                                        homeDetail = jsonObject.getString("strHomeGoalDetails");
                                        awayDetail = jsonObject.getString("strAwayGoalDetails");
                                        homeShot = jsonObject.getString("intHomeShots");
                                        awayShot = jsonObject.getString("intAwayShots");
                                        city = jsonObject.getString("strCity");
                                        idHome = jsonObject.getString("idHomeTeam");
                                        idAway = jsonObject.getString("idAwayTeam");

                                        event1 = new Event(id,name,homeName,awayName,homeScore,awayScore,homeDetail,awayDetail,homeShot,awayShot,date,city,idHome,idAway);
                                        new SetDatabase().execute(getActivity());
                                    }
                                } catch (JSONException e) {
                                    Log.e("Error", e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("ERRRRROR", e.getMessage());
                    }
                    recyclerViewAdapter.addData(listTeamDetail);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley", error.toString());

                }
            });
            Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
        }
        else{
//           Not connected, ambil dari database
        }
//      End of function
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
