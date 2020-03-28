package com.example.android.bolasepak;

/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;

    private EventViewModel mEventViewModel;

    EditText editTextSearch;

    RequestQueue queue;

    String idHome, idAway, homeName, awayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextSearch = findViewById(R.id.editTextSearch);

        RecyclerView recyclerViewEvent = findViewById(R.id.recyclerview);

        final EventListAdapter adapterEvent = new EventListAdapter(this);
        recyclerViewEvent.setAdapter(adapterEvent);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerViewEvent.setLayoutManager(new GridLayoutManager(this, 1));
        } else {
            recyclerViewEvent.setLayoutManager(new GridLayoutManager(this, 2));
        }

        //recyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected){
            queue = Volley.newRequestQueue(this);
            editTextSearch.addTextChangedListener(watchInput);
            getMatch("");
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    Integer.toString(adapterEvent.getItemCount()),
                    Toast.LENGTH_LONG).show();
        }

        // Get a new or existing ViewModel from the ViewModelProvider.
        mEventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.

        mEventViewModel.getAllEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable final List<Event> events) {
                // Update the cached copy of the words in the adapter.
                adapterEvent.setEvents(events);
            }
        });

    }

    private TextWatcher watchInput = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            String message = editTextSearch.getText().toString();
            //clearEventList();
            if (message.length() == 0){
                getMatch("");
            } else {
                getMatch(message);
            }
        };
    };

    private void getMatch(String s) {
        String urlEvent = "http://134.209.97.218:5050/api/v1/json/1/searchevents.php?e=" + s;

        JsonObjectRequest eventRequest = new JsonObjectRequest
                (Request.Method.GET, urlEvent, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray events = response.getJSONArray("event");
                            for (int i = 0; i < events.length(); i++){
                                JSONObject event = events.getJSONObject(i);
                                if (!event.isNull("idSoccerXML")){
                                    String id,name,homeScore,awayScore,homeDetail,awayDetail,homeShot,awayShot,date,city;
                                    id = event.getString("idEvent");
                                    name = event.getString("strEvent");
                                    homeName = event.getString("strHomeTeam");
                                    awayName = event.getString("strAwayTeam");
                                    homeScore = event.getString("intHomeScore");
                                    awayScore = event.getString("intAwayScore");
                                    homeDetail = event.getString("strHomeGoalDetails");
                                    awayDetail = event.getString("strAwayGoalDetails");
                                    homeShot = event.getString("intHomeShots");
                                    awayShot = event.getString("intAwayShots");
                                    date = event.getString("dateEvent");
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date date_match = formatter.parse(date);
                                        formatter = new SimpleDateFormat("dd MMMM yyyy");
                                        date = formatter.format(date_match);
                                    } catch (Exception e) {
                                        Log.e("Error", e.getMessage());
                                        e.printStackTrace();
                                    }
                                    city = event.getString("strCity");
                                    idHome = event.getString("idHomeTeam");
                                    idAway = event.getString("idAwayTeam");

                                    Event event1 = new Event(id,name,homeName,awayName,homeScore,awayScore,homeDetail,awayDetail,homeShot,awayShot,date,city,idHome,idAway);
                                    mEventViewModel.insert(event1);

                                    new SetTeamData().execute(getApplicationContext());

                                    Log.d("endd", "ini habis set team");
                                }
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
    }

    private class SetTeamData extends AsyncTask<Context, Void, List<Team>> {
        @Override
        protected List<Team> doInBackground(Context... context) {
            List<Team> list = null;
            EventRoomDatabase db = EventRoomDatabase.getDatabase(context[0]);
            db.teamDao().insert(new Team(idHome, homeName));
            Log.d("endd-team1", idHome);
            db.teamDao().insert(new Team(idAway, awayName));
            Log.d("endd-team2", idAway);
            list = db.teamDao().getTeams();
            Log.d("endd-size-team", Integer.toString(list.size()));
            return list;
        }

        @Override
        protected void onPostExecute(List<Team> list) {
            super.onPostExecute(list);
            if(list != null){
                Log.d("endd-size-team-post", Integer.toString(list.size()));
                for (Team team : list){
                    Log.d("endd-team", team.getName());
                }
            }
        }
    }
}
