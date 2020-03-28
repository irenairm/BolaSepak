package com.example.android.bolasepak;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView homeName, date, homeScore, awayScore, awayName;
        private final ImageView homeLogo, awayLogo;

        private EventViewHolder(View itemView) {
            super(itemView);
            homeName = itemView.findViewById(R.id.team1);
            awayName = itemView.findViewById(R.id.team2);
            homeScore = itemView.findViewById(R.id.scoreTeam1);
            awayScore = itemView.findViewById(R.id.scoreTeam2);
            date = itemView.findViewById(R.id.matchDate);
            homeLogo = itemView.findViewById(R.id.teamPicture1);
            awayLogo = itemView.findViewById(R.id.teamPicture2);
        }
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

    private final LayoutInflater mInflater;
    private List<Event> mEvents; // Cached copy of words

    EventListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.matchdetail, parent, false);
        return new EventViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        final Event current = mEvents.get(position);
        holder.homeName.setText(current.getHomeName());
        holder.awayName.setText(current.getAwayName());
        if (!current.getHomeScore().equals("null")){
            holder.homeScore.setText(current.getHomeScore());
            holder.awayScore.setText(current.getAwayScore());
        } else {
            holder.homeScore.setText("-");
            holder.awayScore.setText("-");
        }
        holder.date.setText(current.getDate());

        ConnectivityManager cm =
                (ConnectivityManager) holder.homeName.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        String idEvent = current.getId();

        if (isConnected){
            final RequestQueue queue = Volley.newRequestQueue(holder.homeName.getContext());
            String url ="https://www.thesportsdb.com/api/v1/json/1/lookupevent.php?id=" + idEvent;

            JsonObjectRequest eventRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject events = response.getJSONArray("events").getJSONObject(0);

                                String idHome, idAway;

                                idHome = events.getString("idHomeTeam");
                                idAway = events.getString("idAwayTeam");

                                String homeUrl ="https://www.thesportsdb.com/api/v1/json/1/lookupteam.php?id=" + idHome;

                                JsonObjectRequest homeRequest = new JsonObjectRequest
                                        (Request.Method.GET, homeUrl, null, new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONObject teams = response.getJSONArray("teams").getJSONObject(0);
                                                    if (!teams.isNull("strTeamLogo")){
                                                        new EventListAdapter.DownloadImageTask(holder.homeLogo)
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
                                                        new EventListAdapter.DownloadImageTask(holder.awayLogo)
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        v.getContext(),
                        current.getId(),
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(v.getContext(), EventDetail.class);
                intent.setData(Uri.parse(current.getId()));
                v.getContext().startActivity(intent);
            }
        });
    }

    void setEvents(List<Event> events){
        mEvents = events;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mEvents != null)
            return mEvents.size();
        else return 0;
    }

}