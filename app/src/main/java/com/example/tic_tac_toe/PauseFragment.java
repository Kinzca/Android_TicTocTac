package com.example.tic_tac_toe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PauseFragment extends Fragment {
    private final GameView gameView;
    private ImageButton yes;
    private ImageButton no;

    public PauseFragment(GameView gameView) {
        this.gameView = gameView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 解析fragment的布局文件
        View rootView = inflater.inflate(R.layout.fragment_pause, container, false);

        yes = rootView.findViewById(R.id.yes);
        no = rootView.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回主页面
                getActivity().finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭此pause界面
                requireFragmentManager().beginTransaction().remove(PauseFragment.this).commit();
                //调用GameView中的开启暂停方法
                requireFragmentManager().beginTransaction().remove(PauseFragment.this).commit();
                gameView.resumeTimer(); // 调用GameView中的resumeTimer方法
                gameView.isStop = false;
                gameView.resumeTimer();
                gameView.hideBlurredScreen();
                gameView.stpOrCon.setImageResource(R.drawable.green_con);
            }
        });

        return rootView;
    }
}
