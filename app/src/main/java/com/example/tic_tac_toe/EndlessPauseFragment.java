package com.example.tic_tac_toe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class EndlessPauseFragment extends Fragment {

    private final EndlessGame endlessGame;
    private ImageButton yes;
    private ImageButton no;

    public EndlessPauseFragment(EndlessGame endlessGame) {
        this.endlessGame = endlessGame;
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
                requireFragmentManager().beginTransaction().remove(EndlessPauseFragment.this).commit();
                //调用GameView中的开启暂停方法
                requireFragmentManager().beginTransaction().remove(EndlessPauseFragment.this).commit();
                //gameView.resumeTimer(); // 调用GameView中的resumeTimer方法
                endlessGame.isStop = false;
                //gameView.resumeTimer();
                endlessGame.hideBlurredScreen();
                endlessGame.stpOrCon.setImageResource(R.drawable.green_con);
            }
        });

        return rootView;
    }
}