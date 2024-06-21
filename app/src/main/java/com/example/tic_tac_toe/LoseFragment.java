package com.example.tic_tac_toe;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
public class LoseFragment extends Fragment {

    private AppCompatActivity appCompatActivity;
    private ImageButton yes;
    private ImageButton no;
    private String time;
    private TextView timeView;

    public LoseFragment(AppCompatActivity appCompatActivity,String time) {
        this.appCompatActivity = appCompatActivity;
        this.time = time;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 解析fragment的布局文件
        View rootView = inflater.inflate(R.layout.fragment_lose, container, false);

        yes = rootView.findViewById(R.id.loseYes);
        no = rootView.findViewById(R.id.loseNo);
        timeView = rootView.findViewById(R.id.timeView); // 修改这里，初始化 timeView

        SetText();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof GameView)
                {
                    // 使用 getActivity() 获取 Context
                    Intent intent = new Intent(getActivity(), GameView.class);
                    getActivity().startActivity(intent);
                    getActivity().finish(); // 结束当前的活动
                }
                else{
                    // 使用 getActivity() 获取 Context
                    Intent intent = new Intent(getActivity(), EndlessGame.class);
                    getActivity().startActivity(intent);
                    getActivity().finish(); // 结束当前的活动
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回主页面
                getActivity().finish();
            }
        });

        return rootView;
    }

    public void SetText(){
        //这里的TimeView就是结算界面显示的文字（由于第一次写时没考虑到注释一下以免忘记）
        if (getActivity() instanceof GameView){
            timeView.setText("用时：" + time);
        }else{
            timeView.setText(time);
        }
    }
}
