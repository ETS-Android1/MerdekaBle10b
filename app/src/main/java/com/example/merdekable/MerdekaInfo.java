package com.example.merdekable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * This is MerdekaInfo class which shows video and information of Malaysia Independence Day.
 */
public class MerdekaInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merdeka_info);
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.videoclip);
        videoView.start();
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        TextView textview = (TextView) findViewById(R.id.textView);
        textview.setMovementMethod(new ScrollingMovementMethod());
    }
}