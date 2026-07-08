package com.example.mealplannerapp.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mealplannerapp.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class FitnessDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_detail);

        String title = getIntent().getStringExtra("title");
        String image = getIntent().getStringExtra("image");
        String duration = getIntent().getStringExtra("duration");
        String video = getIntent().getStringExtra("video");
        String benefits = getIntent().getStringExtra("benefits");
        String description = getIntent().getStringExtra("description");

        ImageView imageView = findViewById(R.id.detailImage);
        TextView txtTitle = findViewById(R.id.detailTitle);
        TextView txtDuration = findViewById(R.id.detailDuration);
        TextView txtBenefits = findViewById(R.id.detailBenefits);
        TextView txtDescription = findViewById(R.id.detailDescription);

        YouTubePlayerView youTubePlayerView =
                findViewById(R.id.youtubePlayer);

        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(
                new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(
                            com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {

                        youTubePlayer.loadVideo(video, 0);
                    }
                });

        txtTitle.setText(title);
        txtDuration.setText(duration);
        txtBenefits.setText(benefits);
        txtDescription.setText(description);

        Glide.with(this)
                .load(image)
                .into(imageView);
    }
}
