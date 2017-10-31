package com.compassl.anji.songs_ssw;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.compassl.anji.songs_ssw.db.SongInfo;
import com.compassl.anji.songs_ssw.util.HttpUtil;
import com.compassl.anji.songs_ssw.util.InitialTool;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener,RvAdapter.OnItemClickListenerRV{

    private static final int SONG_ACCOUNT=9;
    private static final int SEEK_BAR_UPDATE = 1;
    private static final int MODE_LIST_LOOP = 1;
    private static final int MODE_SINGLE_LOOP = 2;
    private static final int MODE_RANDOM = 3;
    private final int LY_PAGE=1;
    private final int BS_PAGE=2;
    private static int currentPage=1;
    private static int MODE = 1;


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
    private List<Song> songList = new ArrayList<>();
    private RvAdapter adapter;
    private RecyclerView rv;
    private SeekBar sb_song_play_progress;
    private TextView tv_display_time_current;
    private TextView tv_display_time_total;
    private ImageView iv_background;

    private int index=1;
    private float touchDownX;  // 手指按下的X坐标
    private float touchUpX;  //手指松开的X坐标
    private int total_time;
    private int current_time;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private Handler handler = new Handler(){

        public void handleMessage(Message message){
            try{
                sb_song_play_progress.setMax(mediaPlayer.getDuration());
            }catch (Exception e){e.printStackTrace();}
            switch (message.what){
                case SEEK_BAR_UPDATE:
                    try {
                        sb_song_play_progress.setProgress(mediaPlayer.getCurrentPosition());
                    }catch (Exception e ){e.printStackTrace();}
                    break;
                default:
                    break;
            }
            tv_display_time_current.setText(getDisplayTime(mediaPlayer.getCurrentPosition()));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        iv_background = (ImageView) findViewById(R.id.iv_background);
        loadBingPic();
        vf_ly_bs = (ViewFlipper) findViewById(R.id.vf_ly_bs);
        bt_previous = (ImageButton) findViewById(R.id.bt_previous);
        bt_play_pause = (ImageButton) findViewById(R.id.bt_play_and_pause);
        bt_next = (ImageButton) findViewById(R.id.bt_next);
        bt_mode = (ImageButton) findViewById(R.id.bt_mode);
        bt_list = (ImageButton) findViewById(R.id.bt_list);
        fbt_home = (FloatingActionButton) findViewById(R.id.fbt_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_choose_song);
        tv_bs = (TextView) findViewById(R.id.tv_bacground_story_view);
        tv_ly = (TextView) findViewById(R.id.tv_lyrics_view);
        tv_bs.getPaint().setFakeBoldText(true);
        tv_ly.getPaint().setFakeBoldText(true);
        //tv_ly.setMovementMethod(ScrollingMovementMethod.getInstance());
        //tv_bs.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_display_time_total = (TextView) findViewById(R.id.tv_display_time_total);
        tv_display_time_current = (TextView) findViewById(R.id.tv_display_time_current);
        tv_display_time_current.getPaint().setFakeBoldText(true);
        tv_display_time_total.getPaint().setFakeBoldText(true);

/*
        SharedPreferences pref = getSharedPreferences("bingPic",MODE_PRIVATE);
        String bingPic = pref.getString("bingPic","");
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(iv_background);
        }else {
            loadBingPic();
        }
*/
        sb_song_play_progress = (SeekBar) findViewById(R.id.sb_song_progress);
        sb_song_play_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(100);
                    }catch (Exception e){e.printStackTrace();}
                    Message message = new Message();
                    message.what = SEEK_BAR_UPDATE;
                    handler.sendMessage(message);
                }
            }
        }).start();

        initList();
        rv = (RecyclerView) findViewById(R.id.rv_for_choose);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        rv.setHasFixedSize(true);
        adapter = new RvAdapter(songList);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListenerRV(this);

        bt_previous.setOnClickListener(this);
        bt_play_pause.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        bt_mode.setOnClickListener(this);
        bt_list.setOnClickListener(this);
        fbt_home.setOnClickListener(this);
        vf_ly_bs.setOnTouchListener(this);

        changeSong(1);
        bt_play_pause.setImageResource(R.drawable.play);

    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bingPic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(bingPic).into(iv_background);
                    }
                });
            }
        });
    }

    private void changeSong(int i) {
        index=i;
        mediaPlayer.reset();

        if (MODE==MODE_RANDOM) {
            int temp = new Random().nextInt(SONG_ACCOUNT)+1;
            index = (temp==index)?temp+1:temp;
            index = (index>SONG_ACCOUNT)?1:index;
        }

        switch (index){
            case 1:
                tv_ly.setText(R.string.lyc_01);
                tv_bs.setText(R.string.bs_01);
                mediaPlayer=MediaPlayer.create(this,R.raw.ssw01aqjx);
                break;
            case 2:
                tv_ly.setText(R.string.lyc_02);
                tv_bs.setText(R.string.bs_02);
                mediaPlayer=MediaPlayer.create(this,R.raw.ssw02ssw);
                break;
            case 3:
                tv_ly.setText(R.string.lyc_03);
                tv_bs.setText(R.string.bs_03);
                mediaPlayer=MediaPlayer.create(this,R.raw.ssw03zxl);
                break;
            case 4:
                tv_ly.setText(R.string.lyc_04);
                tv_bs.setText(R.string.bs_04);
                mediaPlayer=MediaPlayer.create(this,R.raw.ssw04xty);
                break;
            case 5:
                tv_ly.setText(R.string.lyc_05);
                tv_bs.setText(R.string.bs_05);
                mediaPlayer=MediaPlayer.create(this,R.raw.ssw05yzwwdg);
                break;
            case 6:
                tv_ly.setText(R.string.lyc_06);
                tv_bs.setText(R.string.bs_06);
                mediaPlayer=MediaPlayer.create(this,R.raw.ssw06yyc);
                break;
            case 7:
                tv_ly.setText(R.string.lyc_07);
                tv_bs.setText(R.string.bs_07);
                mediaPlayer=MediaPlayer.create(this,R.raw.ssw07ylq);
                break;
            case 8:
                tv_ly.setText(R.string.lyc_08);
                tv_bs.setText(R.string.bs_08);
                mediaPlayer=MediaPlayer.create(this,R.raw.ssw08xc);
                break;
            case 9:
                tv_ly.setText(R.string.lyc_09);
                tv_bs.setText(R.string.bs_09);
                mediaPlayer=MediaPlayer.create(this,R.raw.ssw09bjlnxt);
                break;
        }
        switch (MODE){
            case MODE_LIST_LOOP:
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        index = (index>=SONG_ACCOUNT)?0:index;
                        changeSong(++index);
                        mediaPlayer.start();
                    }
                });
                break;
            case MODE_RANDOM:
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        int temp = new Random().nextInt(SONG_ACCOUNT)+1;
                        index = (temp==index)?temp+1:temp;
                        index = (index>SONG_ACCOUNT)?1:index;
                        changeSong(index);
                        Log.d("DEBUG", index+"");
                        mediaPlayer.start();
                    }
                });
                break;
            default:
                break;
        }
        tv_display_time_total.setText(getDisplayTime(mediaPlayer.getDuration()));
        bt_play_pause.setImageResource(R.drawable.pause);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_previous:
                if (index==1){
                    changeSong(SONG_ACCOUNT);
                }else {
                    changeSong(--index);
                }
                mediaPlayer.start();
                break;
            case R.id.bt_play_and_pause:
                if (!mediaPlayer.isPlaying()){
                    bt_play_pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }else {
                    bt_play_pause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                Log.d(TAG, "onClick: ");
                break;
            case R.id.bt_next:
                if (index==SONG_ACCOUNT){
                    changeSong(1);
                }else {
                    changeSong(++index);
                }
                mediaPlayer.start();
                break;
            case R.id.bt_mode:
                if (MODE==MODE_LIST_LOOP){
                    MODE = MODE_SINGLE_LOOP;
                    mediaPlayer.setLooping(true);
                    bt_mode.setImageResource(R.drawable.single);
                }else if (MODE == MODE_SINGLE_LOOP){
                    MODE = MODE_RANDOM;
                    mediaPlayer.setLooping(false);
                    bt_mode.setImageResource(R.drawable.random_play);
                }else if (MODE == MODE_RANDOM){
                    MODE = MODE_LIST_LOOP;
                    mediaPlayer.setLooping(false);
                    bt_mode.setImageResource(R.drawable.allplay);
                }
                break;
            case R.id.bt_list:
                break;
            case R.id.fbt_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }

    }


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
                vf_ly_bs.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in));
                vf_ly_bs.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.push_right_out));
                // 显示上一屏的View
                vf_ly_bs.showPrevious();
                // 从右往左，看后一个View
                currentPage=LY_PAGE;
            } else if (touchDownX - touchUpX > 100 && currentPage==LY_PAGE) {
                //显示下一屏的动画
                vf_ly_bs.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.push_left_in));
                vf_ly_bs.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.push_left_out));
                // 显示下一屏的View
                vf_ly_bs.showNext();
                currentPage=BS_PAGE;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onItemClickRV(int position) {
        index=position+1;
        drawerLayout.closeDrawers();
        changeSong(index);
        mediaPlayer.start();
    }

    private String getDisplayTime(int time) {
        int min = time/60000;
        int sec = time%60000/1000;
        String min_string;
        String sec_string;
        if (min/10<1){
            min_string = "0"+min;
        }else {
            min_string = ""+min;
        }
        if (sec/10<1){
            sec_string = "0"+sec;
        }else {
            sec_string = ""+sec;
        }
        return min_string+":"+sec_string;
    }

    private void initList() {
        songList.clear();
        //1
        Song song1 = new Song(getResources().getString(R.string.one),
                R.drawable.ssw01aqjx);
        songList.add(song1);
        //2
        Song song2 = new Song(getResources().getString(R.string.two),
                R.drawable.ssw02ssw);
        songList.add(song2);
        //3
        Song song3 = new Song(getResources().getString(R.string.three),
                R.drawable.ssw03zxl);
        songList.add(song3);
        //4
        Song song4 = new Song(getResources().getString(R.string.four),
                R.drawable.ssw04xty);
        songList.add(song4);
        //5
        Song song5 = new Song(getResources().getString(R.string.five),
                R.drawable.ssw05yzwwdg);
        songList.add(song5);
        //6
        Song song6 = new Song(getResources().getString(R.string.six),
                R.drawable.ssw06yyc);
        songList.add(song6);
        //7
        Song song7 = new Song(getResources().getString(R.string.seven),
                R.drawable.ssw07ylq);
        songList.add(song7);
        //8
        Song song8 = new Song(getResources().getString(R.string.eight),
                R.drawable.ssw08xc);
        songList.add(song8);
        //9
        Song song9 = new Song(getResources().getString(R.string.nine),
                R.drawable.ssw09bjlnxt);
        songList.add(song9);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
