package com.example.android.bolasepak;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<TeamDetail> mData;

    public RecyclerViewAdapter(Context mContext, List<TeamDetail> mData) {
        this.mContext = mContext;
        this.mData = mData;
        notifyItemChanged(0,mData.size());
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.matchdetail,parent,false);
        MyViewHolder vHolder = new MyViewHolder(v);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.tv_team1.setText(mData.get(position).getTeamName1());
        holder.tv_team2.setText(mData.get(position).getTeamName2());
        holder.tv_matchDate.setText(mData.get(position).getMatchDate());
        holder.tv_scoreTeam1.setText(mData.get(position).getScoreTeam1());
        holder.tv_scoreTeam2.setText(mData.get(position).getScoreTeam2());
        //holder.tv_teamPicture1.setImageResource(mData.get(position).getPhotoTeam1());
        //holder.tv_teamPicture2.setImageResource(mData.get(position).getPhotoTeam2());
        ConnectivityManager cm =
                (ConnectivityManager) holder.tv_team1.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        String idEvent = mData.get(position).getIdEvent();

        if (isConnected){
            final RequestQueue queue = Volley.newRequestQueue(holder.tv_team1.getContext());
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
                                                        new RecyclerViewAdapter.DownloadImageTask(holder.tv_teamPicture1)
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
                                                        new RecyclerViewAdapter.DownloadImageTask(holder.tv_teamPicture2)
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
                        mData.get(position).getIdEvent(),
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(v.getContext(), EventDetail.class);
                intent.setData(Uri.parse(mData.get(position).getIdEvent()));
                v.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_team1;
        private TextView tv_team2;
        private TextView tv_matchDate;
        private TextView tv_scoreTeam1;
        private TextView tv_scoreTeam2;
        private ImageView tv_teamPicture1;
        private ImageView tv_teamPicture2;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv_team1 = (TextView) itemView.findViewById(R.id.team1);
            tv_team2 = (TextView) itemView.findViewById(R.id.team2);
            tv_matchDate = (TextView) itemView.findViewById(R.id.matchDate);
            tv_scoreTeam1 = (TextView) itemView.findViewById(R.id.scoreTeam1);
            tv_scoreTeam2 = (TextView) itemView.findViewById(R.id.scoreTeam2);
            tv_teamPicture1 = (ImageView) itemView.findViewById(R.id.teamPicture1);
            tv_teamPicture2 = (ImageView) itemView.findViewById(R.id.teamPicture2);

        }
    }
    public void addData(List<TeamDetail> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }



}
