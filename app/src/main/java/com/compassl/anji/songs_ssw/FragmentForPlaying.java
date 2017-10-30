package com.compassl.anji.songs_ssw;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * Created by Administrator on 2017/10/29.
 */
public class FragmentForPlaying extends Fragment implements View.OnClickListener{

    private final int LY_PAGE=1;
    private final int BS_PAGE=2;
    private static int currentPage=1;

    private ViewFlipper vf_ly_bs;
    private ImageButton bt_previous;
    private ImageButton bt_play_pause;
    private ImageButton bt_next;
    private ImageButton bt_mode;
    private ImageButton bt_list;
    private FloatingActionButton fbt_home;
    private DrawerLayout drawerLayout;
    private TextView tv_ly;
    private TextView tv_bs;

    private float touchDownX;  // 手指按下的X坐标
    private float touchUpX;  //手指松开的X坐标

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_playing,container,false);
        vf_ly_bs = (ViewFlipper) view.findViewById(R.id.vf_ly_bs);
        bt_previous = (ImageButton) view.findViewById(R.id.bt_previous);
        bt_play_pause = (ImageButton) view.findViewById(R.id.bt_play_and_pause);
        bt_next = (ImageButton) view.findViewById(R.id.bt_next);
        bt_mode = (ImageButton) view.findViewById(R.id.bt_mode);
        bt_list = (ImageButton) view.findViewById(R.id.bt_list);
        fbt_home = (FloatingActionButton) view.findViewById(R.id.fbt_menu);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.dl_choose_song);

        tv_bs = (TextView) view.findViewById(R.id.tv_bacground_story_view);
        tv_ly = (TextView) view.findViewById(R.id.tv_lyrics_view);
        //tv_ly.setMovementMethod(new ScrollingMovementMethod());
        //tv_bs.setMovementMethod(new ScrollingMovementMethod());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bt_previous.setOnClickListener(this);
        bt_play_pause.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        bt_mode.setOnClickListener(this);
        bt_list.setOnClickListener(this);
        fbt_home.setOnClickListener(this);

        vf_ly_bs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 取得左右滑动时手指按下的X坐标
                    touchDownX = event.getX();
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 取得左右滑动时手指松开的X坐标
                    touchUpX = event.getX();
                    // 从左往右，看前一个View
                    if (touchUpX - touchDownX > 100 && currentPage==BS_PAGE) {
                            // 显示上一屏动画
                            vf_ly_bs.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_right_in));
                            vf_ly_bs.setOutAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.push_right_out));
                            // 显示上一屏的View
                            vf_ly_bs.showPrevious();
                            // 从右往左，看后一个View
                            currentPage=LY_PAGE;

                    } else if (touchDownX - touchUpX > 100 && currentPage==LY_PAGE) {
                        //显示下一屏的动画
                        vf_ly_bs.setInAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.push_left_in));
                        vf_ly_bs.setOutAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.push_left_out));
                        // 显示下一屏的View
                        vf_ly_bs.showNext();
                        currentPage=BS_PAGE;
                    }
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_previous:
                Toast.makeText(getActivity(),"here",Toast.LENGTH_SHORT).show();
                break;

            case R.id.bt_play_and_pause:
                break;
            case R.id.bt_next:
                break;
            case R.id.bt_mode:
                break;
            case R.id.bt_list:
                break;
            case R.id.fbt_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }


    }
}