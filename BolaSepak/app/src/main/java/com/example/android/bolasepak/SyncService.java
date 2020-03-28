package com.example.android.bolasepak;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.text.format.DateUtils.DAY_IN_MILLIS;


public class SyncService extends Service {
    private List<Pair<String,String>> pairList = new ArrayList<Pair<String,String>>();
    private Handler mHandler;
    // default interval for syncing data
    public static final long DEFAULT_SYNC_INTERVAL = 24*60*60*1000;
    private Context mContext;

    public SyncService(Context context){
        mContext = context;
    }

    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            syncData();
            // Repeat this runnable code block again every ... min

            mHandler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the Handler object
        mHandler = new Handler();
        // Execute a runnable task as soon as possible
        mHandler.post(runnableService);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private synchronized void syncData() {
        final Date d = new Date();
        // call your rest service here
        final String urlEvent = "https://www.thesportsdb.com/api/v1/json/1/eventsnextleague.php?id=4328";

        JsonObjectRequest eventRequest = new JsonObjectRequest
                (Request.Method.GET, urlEvent, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray events = response.getJSONArray("events");
                            Log.d("endd-events", Integer.toString(events.length()));
                            for (int i = 0; i < events.length(); i++){
                                JSONObject event = events.getJSONObject(i);
                                String idHome,idAway,date;
                                idHome = event.getString("idHomeTeam");
                                idAway = event.getString("idAwayTeam");
                                date = event.getString("dateEvent");
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date1 = sdf.parse(date);
                                    if (d.compareTo(date1) < 0) {
                                        pairList.add(new Pair<String, String>(idHome, idAway));
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            Log.d("pair 2st",Integer.toString(pairList.size()));
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


        Volley.newRequestQueue(mContext).add(eventRequest);

    }

    public List<Pair<String, String>> getList(){
        return pairList;
    }
}