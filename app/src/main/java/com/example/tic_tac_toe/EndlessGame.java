package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EndlessGame extends AppCompatActivity implements TextureView.SurfaceTextureListener{

    private TextureView endlessGameVideo;
    private MediaPlayer mediaPlayer;
    private int currentVideoPosition = 0;

    ImageView[] imageViews = new ImageView[9];
    ImageButton stpOrCon;
    boolean isStop = false;
    int currentPlayer = 0;

    int playerScore = 0;
    int aiScore = 0;

    private TextView bilateralScore;
    private ImageView blurredView;
    private EndlessAiMove aiMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endless_game);

        aiMove = new EndlessAiMove();
        aiMove.GameStart();

        for (int i = 0; i < 9; i++) {
            int resID = getResources().getIdentifier("imageView" + i, "id", getPackageName());
            imageViews[i] = findViewById(resID);
            SetClick(imageViews[i], i);
        }

        blurredView = findViewById(R.id.blurView);
        bilateralScore = findViewById(R.id.bilateralScore);
        //设置初始文本
        SetScore(":");

        ControlStatus();

        //加载视频背景
        endlessGameVideo = findViewById(R.id.endlessGameVideo);
        endlessGameVideo.setSurfaceTextureListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //customHandler.removeCallbacks(updatedTimerThread);
    }

    @Override
    public void onBackPressed() {//按下退出键操作
        if (!isStop){
            stpOrCon.setImageResource(R.drawable.orange_stp);
            isStop = true;
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
                        stpOrCon.setImageResource(R.drawable.orange_stp);
                        isStop = true;
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


    // 启用暂停按钮
    public void onPauseButtonClicked() {
        EndlessPauseFragment endlessPauseFragment = new EndlessPauseFragment(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.pauseView, endlessPauseFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //游戏胜利
    public void GameVictory(){
        VictoryFragment victoryFragment = new VictoryFragment(this,bilateralScore.getText().toString());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.pauseView,victoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //游戏失败
    public void GameLose(){
        LoseFragment loseFragment = new LoseFragment(this,bilateralScore.getText().toString());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.pauseView,loseFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //游戏平局
    public void GameDraw(){
        DrawFragment drawFragment = new DrawFragment(this,bilateralScore.getText().toString());
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
        int[][] winPosition = aiMove.getWinOrLose();

        //胜利数组不为空，有玩家或ai达成了三连
        if (winPosition != null){
            //Toast.makeText(this, "Tie", Toast.LENGTH_SHORT).show();
            for (int[] pos : winPosition)
            {
                imageViews[pos[0] * 3 + pos[1]].setImageResource(0);//清空图像
            }

            aiMove.clearWinningPositions(winPosition);//清空获胜的数组值

            //根据获胜的数字为玩家或AI加分
            if (aiMove.playerOrAi == 1){
                playerScore++;
                if (playerScore > aiScore){
                    SetScore(">");
                }else if (playerScore < aiScore){
                    SetScore("<");
                }else {
                    SetScore(":");
                }
            }else if (aiMove.playerOrAi == 2){
                aiScore++;
                if (playerScore > aiScore){
                    SetScore(">");
                }else if (playerScore < aiScore){
                    SetScore("<");
                }else {
                    SetScore(":");
                }
            }
            aiMove.playerOrAi = 3;//加完分后将值重置为3
        }

        if (!hasEmptyPosition()) {
            stpOrCon.setImageResource(R.drawable.orange_stp);
            isStop = true;
            showBlurredScreen();

            if (playerScore > aiScore) {
                GameVictory();
            } else if (playerScore < aiScore) {
                GameLose();
            } else {
                GameDraw();
            }
        }
    }

    public void SetScore(String string){

        SpannableString spannableString = new SpannableString(playerScore + string + aiScore);

        ForegroundColorSpan playerColor = new ForegroundColorSpan(getResources().getColor(R.color.playerColor));
        spannableString.setSpan(playerColor, 0, String.valueOf(playerScore).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ForegroundColorSpan aiColor = new ForegroundColorSpan(getResources().getColor(R.color.aiColor));
        spannableString.setSpan(aiColor, String.valueOf(playerScore).length() + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        bilateralScore.setText(spannableString);
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
            if (position != -1) {
                imageViews[position].setImageResource(R.drawable.red);
                currentPlayer = (currentPlayer + 1) % 2;
            }
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
        if (endlessGameVideo.isAvailable()) {
            initializeMediaPlayer(endlessGameVideo.getSurfaceTexture());
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
