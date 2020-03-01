package com.geforce.vijai.healthpartner.ui.exercise;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geforce.vijai.healthpartner.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    List<YoutubeVideos> youtubeVideoList;

    public VideoAdapter(List<YoutubeVideos> youtubeVideoList) {
        this.youtubeVideoList = youtubeVideoList;
    }
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext()).inflate(R.layout.video_view, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        //holder.videoWeb.getSettings().setJavaScriptEnabled(true);
        //if(youtubeVideoList.get(position).getVideoUrl()!=null && isValidUrl(youtubeVideoList.get(position).getVideoUrl()))
        //{
          //  Log.i("from adapter",youtubeVideoList.get(position).getVideoUrl());
            //holder.videoWeb.setVisibility(View.VISIBLE);
            holder.videoWeb.loadData( youtubeVideoList.get(position).getVideoUrl(), "text/html" , "utf-8" );
        //}
        //else{
          //  holder.videoWeb.setVisibility(View.GONE);
        //}

    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        if(m.matches())
            return true;
        else
            return false;
    }
    @Override
    public int getItemCount() {
        return youtubeVideoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{

        WebView videoWeb;

        public VideoViewHolder(View itemView) {
            super(itemView);

            videoWeb = (WebView) itemView.findViewById(R.id.videoWebView);

            videoWeb.getSettings().setJavaScriptEnabled(true);
            videoWeb.setWebChromeClient(new WebChromeClient() {

            } );
        }
    }
}
