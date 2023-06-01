package com.m_learning.feature.general;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;

import com.m_learning.databinding.ActivityImageFullScreenBinding;
import com.m_learning.utils.MediaData;
import com.m_learning.utils.ToolUtils;

public class ImageFullScreenActivity extends AppCompatActivity {

    String type = "";

    private MediaController mediacontroller;

    private MediaData mediaData;
    private ActivityImageFullScreenBinding binding;
    private String title;

    public static Intent newInstance(Context context, MediaData mediaData, String title) {
        Intent intent = new Intent(context, ImageFullScreenActivity.class);
        intent.putExtra("mediaData", mediaData);
        intent.putExtra("title", title);
        return intent;
    }

    private void getIntentData() {
        mediaData = (MediaData) getIntent().getSerializableExtra("mediaData");
        title = getIntent().getStringExtra("title");
        type = mediaData.getType();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityImageFullScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getIntentData();
        binding.tvTitle.setText(title);
        binding.ibtnBack.setOnClickListener(view1 -> onBackPressed());
        if (mediaData.getType().equals("image")) {
            binding.videoView.setVisibility(View.GONE);
            binding.imgPlay.setVisibility(View.GONE);
            binding.image.setVisibility(View.VISIBLE);
            ToolUtils.setImgProgress(this, mediaData.getImagePath(), binding.image, binding.progress);
        } else {
            ToolUtils.setImgProgress(this, mediaData.getImagePath(), binding.image, binding.progress);
            binding.videoView.setVisibility(View.VISIBLE);
            binding.imgPlay.setVisibility(View.VISIBLE);
            binding.progress.setVisibility(View.GONE);
            binding.imgPlay.setOnClickListener(view1 -> {
                mediacontroller = new MediaController(this);
                mediacontroller.setAnchorView(binding.videoView);
                mediacontroller.setMediaPlayer(binding.videoView);
                binding.videoView.setMediaController(mediacontroller);

                Uri video = Uri.parse(mediaData.getVideoPath());
                binding.videoView.setMediaController(mediacontroller);
                binding.videoView.setVideoURI(video);

                binding.imgPlay.setVisibility(View.GONE);
                binding.progress.setVisibility(View.VISIBLE);
            });


            binding.videoView.setOnPreparedListener(mediaPlayer -> {
                binding.videoView.start();
                binding.progress.setVisibility(View.GONE);
                binding.imgPlay.setVisibility(View.GONE);
            });
            binding.imgPlay.performClick();

            binding.image.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediacontroller != null) {
            mediacontroller.hide();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
