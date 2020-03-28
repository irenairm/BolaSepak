package com.example.android.bolasepak;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TeamDetailMain extends AppCompatActivity implements View.OnClickListener {
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private List<TeamDetail> listTeamDetail = new ArrayList<>();
    private ImageView teamPict;
    private TextView teamName;
    private Button btn;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    private  static final int NOTIFICATION_ID = 0;
    private String team_Name;
    private String idTeam;
    private List<Pair<String,String>> pairList = new ArrayList<Pair<String,String>>();
    SyncService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarteam);
        viewPager = (ViewPager) findViewById(R.id.viepagerID);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        FragmentSekarang fragSkrg = new FragmentSekarang();
        FragmentSebelum fragSblm = new FragmentSebelum();
        adapter.addFragment(fragSkrg, "SEKARANG");
        adapter.addFragment(fragSblm, "SEBELUM");

        teamPict = (ImageView) findViewById(R.id.teamPicture);
        teamName = (TextView) findViewById(R.id.teamName);

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(this);

        Intent intent = getIntent();
        idTeam = intent.getData().toString();

        service = new SyncService(this);
        service.onStartCommand(intent, Service.START_FLAG_REDELIVERY,200);
        //android.os.SystemClock.sleep(30000);
        //pairList = service.getList();

        Log.d("pair-tag", "ini harusnya sebelum check");

        Log.d("pair -1st",Integer.toString(pairList.size()));

        //new CheckSubscribed().execute(getApplicationContext());
        new GetData().execute(getApplicationContext());

        Bundle bundle = new Bundle();
        bundle.putString("idTeam",idTeam);
        fragSblm.setArguments(bundle);
        fragSkrg.setArguments(bundle);

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected){
            getDataTeam();
        } else {
            new SetTeamData().execute(getApplicationContext());
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        createNotificationChannel();

        Log.d("pair 0st",Integer.toString(pairList.size()));

    }

    private class GetData extends AsyncTask<Context, Void, Integer>{
        @Override
        protected Integer doInBackground(Context... context) {
            while (service.getList().size()==0){

            }

            return service.getList().size();
        }

        @Override
        protected void onPostExecute(Integer size) {
            super.onPostExecute(size);
            Log.d("pair post", Integer.toString(size));
            new CheckSubscribed().execute(getApplicationContext());
        }
    }

    private class SetTeamData extends AsyncTask<Context, Void, String>{
        @Override
        protected String doInBackground(Context... context) {
            EventRoomDatabase db = EventRoomDatabase.getDatabase(context[0]);
            String name = db.teamDao().getName(idTeam);
            Log.d("endd-name", name);
            return name;
        }

        @Override
        protected void onPostExecute(String name) {
            super.onPostExecute(name);
            if(name != null){
                teamName.setText(name);
            }
        }
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.button){
            if(btn.getText()=="SUBSCRIBE"){
                btn.setText("SUBSCRIBED");
                btn.setBackgroundColor(Color.GRAY);
//                subscribeTeam(v);

                new SetSubscribed().execute(getApplicationContext());

            }
            else{
                btn.setText("SUBSCRIBE");
                btn.setBackgroundColor(Color.GRAY);

                new UnSubscribed().execute(getApplicationContext());

            }
        }
    }

    private class SetSubscribed extends AsyncTask<Context, Void, List<TeamSub>>{
        @Override
        protected List<TeamSub> doInBackground(Context... context) {
            EventRoomDatabase db = EventRoomDatabase.getDatabase(context[0]);
            db.teamSubDao().insert(new TeamSub(idTeam));
            List<TeamSub> list = db.teamSubDao().getTeamSubs();
            return list;
        }

        @Override
        protected void onPostExecute(List<TeamSub> list) {
            super.onPostExecute(list);
            if(list != null){
                Log.d("endd-tsize", Integer.toString(list.size()));
                for (TeamSub team : list){
                    Log.d("endd-list", team.getId());
                    for (Pair<String, String> pairs : service.getList()) {
                        if (pairs.first.equals(team.getId()) || pairs.second.equals(team.getId())){
//                            Intent intent = new Intent(TeamDetailMain.this,Broadcast.class);
//                            PendingIntent pendingIntent = PendingIntent.getBroadcast(TeamDetailMain.this,0,intent,0);
//                            Log.d("tesuto","tesuto");
//                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                            Log.d("tesuto", "alarm dibuat");
//                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),24*60*60*1000,pendingIntent);
//                            Log.d("tesuto", "pending intent dibuat");
                            AlarmManager alarmMgr = (AlarmManager)TeamDetailMain.this.getSystemService(TeamDetailMain.this.ALARM_SERVICE);
                            Intent intent = new Intent(TeamDetailMain.this, BroadcastReceiver.class);
                            PendingIntent alarmIntent = PendingIntent.getBroadcast(TeamDetailMain.this, 0, intent, 0);

// Set the alarm to start at 8:30 a.m.
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 25);
                            Log.d("tesuto", "alarm dibuat");

// setRepeating() lets you specify a precise custom interval--in this case,
// 20 minutes.
                            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                    1000 *1 , alarmIntent);
                        }
                    }
                }
            }
        }

    }

    private class CheckSubscribed extends AsyncTask<Context, Void, List<TeamSub>>{
        @Override
        protected List<TeamSub> doInBackground(Context... context) {
            Log.d("pair-tag", "ini saat check");
            EventRoomDatabase db = EventRoomDatabase.getDatabase(context[0]);
            List<TeamSub> list = db.teamSubDao().getTeamSubs();
            Log.d("endd-tsize", Integer.toString(list.size()));
            return list;
        }

        @Override
        protected void onPostExecute(List<TeamSub> list) {
            super.onPostExecute(list);
            if(list != null){
                for (TeamSub team : list){
                    if (team.getId().equals(idTeam)){
                        btn.setText("SUBSCRIBED");
                    }
                    Log.d("pair lst",Integer.toString(service.getList().size()));
                    for (Pair<String, String> pairs : service.getList()) {
                        if (pairs.first.equals(team.getId()) || pairs.second.equals(team.getId())){
//                            Intent intent = new Intent(TeamDetailMain.this,Broadcast.class);
//                            PendingIntent pendingIntent = PendingIntent.getBroadcast(TeamDetailMain.this,0,intent,0);
//                            Log.d("tesuto","tesuto");
//                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                            Log.d("tesuto", "alarm dibuat");
//                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),86400000,pendingIntent);
//                            Log.d("tesuto", "pending intent dibuat");
                            AlarmManager alarmMgr = (AlarmManager)getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
                            Intent intent = new Intent(getApplicationContext(), BroadcastReceiver.class);
                            PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

// Set the alarm to start at 8:30 a.m.
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 28);
                            Log.d("tesuto", "alarm dibuat");

// setRepeating() lets you specify a precise custom interval--in this case,
// 20 minutes.
                            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                    1000 *1 , alarmIntent);
                            Log.d("tesuto", "pending intent dibuat");
                        }
                    }
                }
            }
        }
    }


    private class UnSubscribed extends AsyncTask<Context, Void, List<TeamSub>>{
        @Override
        protected List<TeamSub> doInBackground(Context... context) {
            EventRoomDatabase db = EventRoomDatabase.getDatabase(context[0]);
            db.teamSubDao().deleteTeam(idTeam);
            List<TeamSub> list = db.teamSubDao().getTeamSubs();
            Log.d("endd-tsize", Integer.toString(list.size()));
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
            return list;
        }

        @Override
        protected void onPostExecute(List<TeamSub> list) {
            super.onPostExecute(list);
            if(list != null){
                Log.d("endd-tsize", Integer.toString(list.size()));
                for (TeamSub team : list){
                    Log.d("endd-list", team.getId());
                }
            }
        }


    }

    public void createNotificationChannel(){
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,"Subscribe Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Match Notification");
            mNotifyManager.createNotificationChannel(notificationChannel);

        }
    }

    public void getDataTeam(){
        String url = "http://134.209.97.218:5050/api/v1/json/1/lookupteam.php?id=" + idTeam;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray events = response.getJSONArray("teams");
                            try {
                                JSONObject jsonObject = events.getJSONObject(0);
                                teamName.setText(jsonObject.getString("strTeam"));
                                if (!jsonObject.isNull("strTeamLogo")){
                                    new TeamDetailMain.DownloadImageTasks(teamPict)
                                            .execute(jsonObject.getString("strTeamLogo"));
                                }
                            } catch (JSONException e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }

                } catch (JSONException e) {
                    Log.e("ERRRRRORZZZZZZ", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private class DownloadImageTasks extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTasks(ImageView bmImage) {
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


