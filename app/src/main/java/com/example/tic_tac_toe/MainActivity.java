package com.example.tic_tac_toe;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    private int currentVideoPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //进行初始化函数操作
        OneGame();
        EndlessGame();
        QuitGame();

        //加载视频背景
        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
    }

    ImageButton startOneGame;
    ImageButton startEndlessGame;
    ImageButton quitGame;

    //单局界面切换
    private void OneGame() {
        startOneGame = findViewById(R.id.oneGame);
        startOneGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameView.class);
                startActivity(intent);
            }
        });
    }

    //无尽模式界面切换
    private void EndlessGame() {
        startEndlessGame = findViewById(R.id.endlessGame);
        startEndlessGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EndlessGame.class);
                startActivity(intent);
            }
        });
    }

    //退出游戏
    private void QuitGame() {
        quitGame = findViewById(R.id.quitGame);
        quitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出游戏
                finish();

            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull android.graphics.SurfaceTexture surface, int width, int height) {
        initializeMediaPlayer(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull android.graphics.SurfaceTexture surface, int width, int height) {
        // handle size changes if necessary
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull android.graphics.SurfaceTexture surface) {
        if (mediaPlayer != null) {
            currentVideoPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull android.graphics.SurfaceTexture surface) {
        // handle updates if necessary
    }

    private void initializeMediaPlayer(android.graphics.SurfaceTexture surface) {
        if (mediaPlayer == null) {
            Surface videoSurface = new Surface(surface);
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mainview));
                mediaPlayer.setSurface(videoSurface);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.seekTo(currentVideoPosition);
                        mediaPlayer.start();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mediaPlayer.setSurface(new Surface(surface));
            mediaPlayer.seekTo(currentVideoPosition);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (textureView.isAvailable()) {
            initializeMediaPlayer(textureView.getSurfaceTexture());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            currentVideoPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
