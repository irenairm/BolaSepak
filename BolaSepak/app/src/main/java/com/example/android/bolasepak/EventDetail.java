package com.example.android.bolasepak;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventDetail extends AppCompatActivity {

    String idEvent;
    String idHome,idAway;
    TextView dateTextView, weatherTextView, shotsTextView, goalsTextView;
    TextView homeScoreTextView, awayScoreTextView;
    TextView homeShotsTextView, awayShotsTextView;
    TextView homeGoalsTextView, awayGoalsTextView;
    TextView homeNameTextView, awayNameTextView;
    ImageView homeLogoImageView, awayLogoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent intent = getIntent();
        idEvent = intent.getData().toString();

        dateTextView = (TextView) findViewById(R.id.date_text);
        homeScoreTextView = (TextView) findViewById(R.id.home_score_text);
        awayScoreTextView = (TextView) findViewById(R.id.away_score_text);
        homeShotsTextView = (TextView) findViewById(R.id.home_shots_text);
        awayShotsTextView = (TextView) findViewById(R.id.away_shots_text);
        homeGoalsTextView = (TextView) findViewById(R.id.home_goal_details_text);
        awayGoalsTextView = (TextView) findViewById(R.id.away_goal_details_text);
        homeNameTextView = (TextView) findViewById(R.id.home_name_text);
        awayNameTextView = (TextView) findViewById(R.id.away_name_text);
        homeLogoImageView = (ImageView) findViewById(R.id.home_logo_image);
        awayLogoImageView = (ImageView) findViewById(R.id.away_logo_image);
        weatherTextView = (TextView) findViewById(R.id.weather_text);
        shotsTextView = (TextView) findViewById(R.id.shots_text);
        goalsTextView = (TextView) findViewById(R.id.goals_text);

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected){
            final RequestQueue queue = Volley.newRequestQueue(this);
            String url ="http://134.209.97.218:5050/api/v1/json/1/lookupevent.php?id=" + idEvent;

            JsonObjectRequest eventRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject events = response.getJSONArray("events").getJSONObject(0);

                                String id,name,homeName,awayName,homeScore,awayScore,homeDetail,awayDetail,homeShot,awayShot,date,city;
                                id = events.getString("idEvent");
                                name = events.getString("strEvent");
                                homeName = events.getString("strHomeTeam");
                                awayName = events.getString("strAwayTeam");
                                homeScore = events.getString("intHomeScore");
                                awayScore = events.getString("intAwayScore");
                                homeDetail = events.getString("strHomeGoalDetails");
                                awayDetail = events.getString("strAwayGoalDetails");
                                homeShot = events.getString("intHomeShots");
                                awayShot = events.getString("intAwayShots");
                                date = events.getString("dateEvent");
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                try {
                                    Date date_match = formatter.parse(date);
                                    formatter = new SimpleDateFormat("dd MMMM yyyy");
                                    date = formatter.format(date_match);
                                } catch (Exception e) {
                                    Log.e("Error", e.getMessage());
                                    e.printStackTrace();
                                }
                                city = events.getString("strCity");
                                idHome = events.getString("idHomeTeam");
                                idAway = events.getString("idAwayTeam");

                                Event event1 = new Event(id,name,homeName,awayName,homeScore,awayScore,homeDetail,awayDetail,homeShot,awayShot,date,city,idHome,idAway);

                                updateUI(event1);

                                String homeUrl ="https://www.thesportsdb.com/api/v1/json/1/lookupteam.php?id=" + idHome;

                                JsonObjectRequest homeRequest = new JsonObjectRequest
                                        (Request.Method.GET, homeUrl, null, new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONObject teams = response.getJSONArray("teams").getJSONObject(0);
                                                    if (!teams.isNull("strTeamLogo")){
                                                        new DownloadImageTask(homeLogoImageView)
                                                                .execute(teams.getString("strTeamLogo"));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                error.printStackTrace();
                                            }
                                        });

                                queue.add(homeRequest);

                                String awayUrl ="https://www.thesportsdb.com/api/v1/json/1/lookupteam.php?id=" + idAway;

                                JsonObjectRequest awayRequest = new JsonObjectRequest
                                        (Request.Method.GET, awayUrl, null, new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONObject teams = response.getJSONArray("teams").getJSONObject(0);
                                                    if (!teams.isNull("strTeamLogo")){
                                                        new DownloadImageTask(awayLogoImageView)
                                                                .execute(teams.getString("strTeamLogo"));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                error.printStackTrace();
                                            }
                                        });

                                queue.add(awayRequest);

                                if (events.isNull("intHomeScore") && !events.isNull("strCity")){

                                    String apiKey = "35b47bfa07e38b50587ebf0527a0942b";

                                    String urlWeather ="http://api.openweathermap.org/data/2.5/weather?q="+events.getString("strCity")+"&APPID="+apiKey;

                                    JsonObjectRequest weatherRequest = new JsonObjectRequest
                                            (Request.Method.GET, urlWeather, null, new Response.Listener<JSONObject>() {

                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        JSONObject weather = response.getJSONArray("weather").getJSONObject(0);
                                                        String main = weather.getString("main");
                                                        weatherTextView.setText(main);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    error.printStackTrace();
                                                }
                                            });

                                    queue.add(weatherRequest);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });

            queue.add(eventRequest);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Gak ada internet",
                    Toast.LENGTH_LONG).show();

            new SetEventData().execute(getApplicationContext());
        }

        homeLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),TeamDetailMain.class);
                intent.setData(Uri.parse(idHome));
                startActivity(intent);

            }
        });

        awayLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),TeamDetailMain.class);
                intent.setData(Uri.parse(idAway));
                startActivity(intent);
            }
        });

    }

    void updateUI(Event event){
        dateTextView.setText(event.getDate());
        if (!event.getHomeScore().equals("null")){
            homeScoreTextView.setText(event.getHomeScore());
            awayScoreTextView.setText(event.getAwayScore());
            if (!event.getHomeShot().equals("null")){
                homeShotsTextView.setText(event.getHomeShot());
                awayShotsTextView.setText(event.getAwayShot());
            }
            if (!event.getHomeGoalDetail().equals("null")){
                String goalDetails = "";
                String[] homeDetails = event.getHomeGoalDetail().split(";");
                for (int i=0; i<homeDetails.length; i++){
                    String[] temp = homeDetails[i].split(":");
                    if (temp.length > 1) {
                        goalDetails += temp[1].trim() + " ";
                    }
                    goalDetails += temp[0] + "\n";
                }
                homeGoalsTextView.setText(goalDetails);

                String goalAwayDetails = "";
                String[] awayDetails = event.getAwayGoalDetail().split(";");
                for (int i=0; i<awayDetails.length; ++i){
                    String[] temp = awayDetails[i].split(":");
                    if (temp.length > 1) {
                        goalAwayDetails += temp[1].trim() + " ";
                    }
                    goalAwayDetails += temp[0] + "\n";
                }
                awayGoalsTextView.setText(goalAwayDetails);
            }
            weatherTextView.setVisibility(View.GONE);
        } else {
            homeShotsTextView.setVisibility(View.GONE);
            awayShotsTextView.setVisibility(View.GONE);
            homeGoalsTextView.setVisibility(View.GONE);
            awayGoalsTextView.setVisibility(View.GONE);
            shotsTextView.setVisibility(View.GONE);
            goalsTextView.setVisibility(View.GONE);
        }

        homeNameTextView.setText(event.getHomeName());
        awayNameTextView.setText(event.getAwayName());
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

    private class SetEventData extends AsyncTask<Context, Void, List<Event>>{
        @Override
        protected List<Event> doInBackground(Context... context) {

            List<Event> list = null;
            EventRoomDatabase db = EventRoomDatabase.getDatabase(context[0]);
            list = db.eventDao().getEvents();
            return list;
        }

        @Override
        protected void onPostExecute(List<Event> list) {
            super.onPostExecute(list);
            if(list != null){
                for (Event event : list){
                    if (event.getId().equals(idEvent)){
                        updateUI(event);
                        idHome = event.getIdHome();
                        idAway = event.getIdAway();
                    }
                }
            }
        }
    }

}
