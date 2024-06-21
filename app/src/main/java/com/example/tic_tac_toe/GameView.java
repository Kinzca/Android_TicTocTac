package com.example.tic_tac_toe;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class GameView extends AppCompatActivity implements TextureView.SurfaceTextureListener{

    private TextureView gameVideo;
    private MediaPlayer mediaPlayer;
    private int currentVideoPosition = 0;

    ImageView[] imageViews = new ImageView[9];
    ImageButton stpOrCon;
    boolean isStop = false;
    int currentPlayer = 0;

    private TextView timerTextView;
    private long startTime = 0L;
    private final Handler customHandler = new Handler();
    long timeMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private ImageView blurredView;
    private aiMove aiMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);

        aiMove = new aiMove();
        aiMove.GameStart();

        for (int i = 0; i < 9; i++) {
            int resID = getResources().getIdentifier("imageView" + i, "id", getPackageName());
            imageViews[i] = findViewById(resID);
            SetClick(imageViews[i], i);
        }

        timerTextView = findViewById(R.id.clockwise);
        startTime = System.currentTimeMillis();
        customHandler.postDelayed(updatedTimerThread, 0);

        blurredView = findViewById(R.id.blurView);
        ControlStatus();

        //加载视频背景
        gameVideo = findViewById(R.id.gameVideo);
        gameVideo.setSurfaceTextureListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customHandler.removeCallbacks(updatedTimerThread);
    }

    @Override
    public void onBackPressed() {//按下退出键操作
        if (!isStop){
            stpOrCon.setImageResource(R.drawable.green_stp);
            isStop = true;
            pauseTimer();
            showBlurredScreen();
            onPauseButtonClicked();
        }


    }


    public void ControlStatus() {
        stpOrCon = findViewById(R.id.stpOrCon);
        if (stpOrCon != null) {
            stpOrCon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isStop) {
                        stpOrCon.setImageResource(R.drawable.green_stp);
                        isStop = true;
                        pauseTimer();
                        showBlurredScreen();
                        onPauseButtonClicked();
                    }
//                    else {
//                        stpOrCon.setImageResource(R.drawable.green_con);
//                        isStop = false;
//                        resumeTimer();
//                        hideBlurredScreen();
//                    }
                }
            });
        } else {
            Log.e("ControlStatus", "stpOrCon is null");
        }
    }

    private final Runnable updatedTimerThread = new Runnable() {
        @Override
        public void run() {
            timeMilliseconds = System.currentTimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeMilliseconds;
            int seconds = (int) (updatedTime / 1000);
            int showSeconds = seconds % 60;
            int minutes = seconds / 60;
            timerTextView.setText(String.format("%02d:%02d", minutes, showSeconds));
            customHandler.postDelayed(this, 1000);
        }
    };

    private void pauseTimer() {
        timeSwapBuff += timeMilliseconds;
        customHandler.removeCallbacks(updatedTimerThread);
        timeMilliseconds = 0L;
    }

    void resumeTimer() {
        startTime = System.currentTimeMillis();
        customHandler.postDelayed(updatedTimerThread, 0);
    }

    private void resetTimer() {
        startTime = System.currentTimeMillis();
        timeSwapBuff = 0L;
        customHandler.removeCallbacks(updatedTimerThread);
        timerTextView.setText("00:00");
    }

    // 启用暂停按钮
    public void onPauseButtonClicked() {
        PauseFragment pauseFragment = new PauseFragment(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.pauseView, pauseFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //游戏胜利
    public void GameVictory(){
        VictoryFragment victoryFragment = new VictoryFragment(this,timerTextView.getText().toString());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.pauseView,victoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //游戏失败
    public void GameLose(){
        LoseFragment loseFragment = new LoseFragment(this,timerTextView.getText().toString());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.pauseView,loseFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //游戏平局
    public void GameDraw(){
        DrawFragment drawFragment = new DrawFragment(this,timerTextView.getText().toString());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.pauseView,drawFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private Bitmap takeScreenshot() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    // 模糊屏幕
    private void showBlurredScreen() {
        Bitmap screenshot = takeScreenshot();
        Bitmap blurredBitmap = BlurBuilder.blur(this, screenshot); // 使用你的模糊算法
        blurredView.setImageBitmap(blurredBitmap);
        blurredView.setVisibility(View.VISIBLE);
    }

    // 隐藏模糊屏幕
    void hideBlurredScreen() {
        blurredView.setVisibility(View.GONE);
    }

    // 下棋
    public void SetClick(final ImageView imageView, final int position) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable() == null && !isStop && hasEmptyPosition()) {
                    imageView.setImageResource(R.drawable.green);
                    aiMove.PlayerMove(position / 3, position % 3);

                    currentPlayer = (currentPlayer + 1) % 2;

                    GameTurn();
                }
            }
        });
    }

    //返回最终结果
    public void WinOrLose(){
        int result = aiMove.getWinOrLose();

        if (!hasEmptyPosition() && result != 0 && result != 1){//平局
            //Toast.makeText(this, "Tie", Toast.LENGTH_SHORT).show();

            stpOrCon.setImageResource(R.drawable.green_stp);
            isStop = true;
            pauseTimer();
            showBlurredScreen();
            GameDraw();
        } else if (result == 0){//胜利
            //Toast.makeText(this, "Victory", Toast.LENGTH_SHORT).show();

            stpOrCon.setImageResource(R.drawable.green_stp);
            isStop = true;
            pauseTimer();
            showBlurredScreen();
            GameVictory();
        } else if (result == 1){//失败
            //Toast.makeText(this, "Lose", Toast.LENGTH_SHORT).show();
            
            stpOrCon.setImageResource(R.drawable.green_stp);
            isStop = true;
            pauseTimer();
            showBlurredScreen();
            GameLose();
        }
    }

    public boolean hasEmptyPosition() {
        for (ImageView imageView : imageViews) {
            if (imageView.getDrawable() == null) {
                return true;
            }
        }
        return false;
    }

    //有空位Ai行动
    // 将 aiMove() 方法重命名为 performAIMove() 或其他不会与成员变量冲突的名称
    public void performAIMove() {
        if (hasEmptyPosition()) {
            int position = aiMove.AIMove();
            imageViews[position].setImageResource(R.drawable.red);
            currentPlayer = (currentPlayer + 1) % 2;
        }
    }


    public void GameTurn() {

        if (isStop) {
            return;
        }
        if (currentPlayer == 0) {
            // Player's turn
        } else {
            performAIMove();
        }

        WinOrLose();
    }

    @Override
    public void onSurfaceTextureAvailable(android.graphics.SurfaceTexture surface, int width, int height) {
        initializeMediaPlayer(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(android.graphics.SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(android.graphics.SurfaceTexture surface) {
        if (mediaPlayer != null) {
            currentVideoPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(android.graphics.SurfaceTexture surface) {
    }

    private void initializeMediaPlayer(android.graphics.SurfaceTexture surface) {
        if (mediaPlayer == null) {
            Surface videoSurface = new Surface(surface);
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.gamevideo));
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
        if (gameVideo.isAvailable()) {
            initializeMediaPlayer(gameVideo.getSurfaceTexture());
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
