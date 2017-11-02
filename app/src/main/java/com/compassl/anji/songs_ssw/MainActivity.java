package com.compassl.anji.songs_ssw;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
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
import com.bumptech.glide.load.resource.file.FileDecoder;
import com.bumptech.glide.util.Util;
import com.compassl.anji.songs_ssw.db.SongInfo;
import com.compassl.anji.songs_ssw.util.HttpUtil;
import com.compassl.anji.songs_ssw.util.InitialTool;
import com.compassl.anji.songs_ssw.util.LrcToString;
import com.compassl.anji.songs_ssw.util.MathUtil;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import me.wcy.lrcview.LrcView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,RvAdapter.OnItemClickListenerRV{

    private static final int SONG_ACCOUNT=9;
    private static final int SEEK_BAR_UPDATE = 1;
    private static final int MODE_LIST_LOOP = 1;
    private static final int MODE_SINGLE_LOOP = 2;
    private static final int MODE_RANDOM = 3;
    private static final int MODE_PLAY_BY_ORDER = 4;
    private static final int CURRENT_LY = 1;
    private static final int CURRENT_BS = 2;
    private static int currentPage=CURRENT_LY;
    private static int MODE = MODE_LIST_LOOP;

    private int index=1;

    private ViewFlipper vf_ly_bs;
    private ImageButton bt_previous;
    private ImageButton bt_play_pause;
    private ImageButton bt_next;
    private ImageButton bt_mode;
    private ImageButton bt_ly_bs;
    private FloatingActionButton fbt_home;
    private DrawerLayout drawerLayout;
    private MyTextView tv_bs;
    private List<Song> songList = new ArrayList<>();
    private RvAdapter adapter;
    private RecyclerView rv;
    private SeekBar sb_song_play_progress;
    private TextView tv_display_time_current;
    private TextView tv_display_time_total;
    private ImageView iv_background;
    private MyLrcView lv_ly;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    //Handler 类，处理子线程发出的请求
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
                    lv_ly.updateTime(mediaPlayer.getCurrentPosition());
                    break;
                default:
                    break;
            }
            tv_display_time_current.setText(MathUtil.getDisplayTime(mediaPlayer.getCurrentPosition()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置融合通知栏
        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        //设置背景图案
        iv_background = (ImageView) findViewById(R.id.iv_background);
        SharedPreferences prefs = getSharedPreferences("bingPic",MODE_PRIVATE);
        String bingPic = prefs.getString("bingPic",null);
        Glide.with(this).load(bingPic).into(iv_background);
        if(isNewDay() || bingPic == null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                loadBingPic();
            }
        }

        //加载各类控件
        vf_ly_bs = (ViewFlipper) findViewById(R.id.vf_ly_bs);
        bt_previous = (ImageButton) findViewById(R.id.bt_previous);
        bt_play_pause = (ImageButton) findViewById(R.id.bt_play_and_pause);
        bt_next = (ImageButton) findViewById(R.id.bt_next);
        bt_mode = (ImageButton) findViewById(R.id.bt_mode);
        bt_ly_bs = (ImageButton) findViewById(R.id.bt_ly_bs);
        fbt_home = (FloatingActionButton) findViewById(R.id.fbt_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_choose_song);
        tv_bs = (MyTextView) findViewById(R.id.tv_bacground_story_view);
        rv = (RecyclerView) findViewById(R.id.rv_for_choose);
        lv_ly = (MyLrcView) findViewById(R.id.lv_ly);
        tv_display_time_total = (TextView) findViewById(R.id.tv_display_time_total);
        tv_display_time_current = (TextView) findViewById(R.id.tv_display_time_current);
        sb_song_play_progress = (SeekBar) findViewById(R.id.sb_song_progress);

        //为按钮设置监听事件
        bt_previous.setOnClickListener(this);
        bt_play_pause.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        bt_mode.setOnClickListener(this);
        bt_ly_bs.setOnClickListener(this);
        fbt_home.setOnClickListener(this);


        //歌词控件界面的属性设置
        lv_ly.setLabel("no lyrics");
        lv_ly.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
                return true;
            }
        });
        lv_ly.setmOnTouchListenerM(new MyLrcView.OnTouchListenerM() {
            @Override
            public void onTouchEventM(String direction) {
                if ("previous".equals(direction) && currentPage == CURRENT_BS){
                    vf_ly_bs.setInAnimation(MainActivity.this,R.anim.push_right_in);
                    vf_ly_bs.setOutAnimation(MainActivity.this,R.anim.push_right_out);
                    vf_ly_bs.showPrevious();
                    currentPage = CURRENT_LY;
                }
                if ("next".equals(direction) && currentPage == CURRENT_LY){
                    vf_ly_bs.setInAnimation(MainActivity.this,R.anim.push_left_in);
                    vf_ly_bs.setOutAnimation(MainActivity.this,R.anim.push_left_out);
                    vf_ly_bs.showNext();
                    currentPage = CURRENT_BS;
                }
            }
        });

        //为背景文案的TextView设置属性
        tv_bs.getPaint().setFakeBoldText(true);
        tv_bs.setOntouchListenerM(new MyTextView.onTouchListenerM() {
            @Override
            public void onTouch(String direction) {
                if ("previous".equals(direction) && currentPage == CURRENT_BS){
                    vf_ly_bs.setInAnimation(MainActivity.this,R.anim.push_right_in);
                    vf_ly_bs.setOutAnimation(MainActivity.this,R.anim.push_right_out);
                    vf_ly_bs.showPrevious();
                    currentPage = CURRENT_LY;
                }else if ("next".equals(direction) && currentPage == CURRENT_LY){
                    vf_ly_bs.setInAnimation(MainActivity.this,R.anim.push_left_in);
                    vf_ly_bs.setOutAnimation(MainActivity.this,R.anim.push_left_out);
                    vf_ly_bs.showNext();
                    currentPage = CURRENT_BS;
                }
            }
            @Override
            public void onTouch(int Y) {
                tv_bs.scrollBy(0,Y);
            }
            @Override
            public void resetY() {
                tv_bs.scrollTo(0, 0);
            }
            @Override
            public void setToBottom(int Y) {
                tv_bs.scrollTo(0,Y);
            }

        });

        //为显示歌曲总时间和当前进度的TextView设置属性，使文字加粗
        tv_display_time_current.getPaint().setFakeBoldText(true);
        tv_display_time_total.getPaint().setFakeBoldText(true);

        //歌曲进度显示条设置拖动监听器和子线程操作
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

        //加载拉动菜单中recyclerview布局的适配器
        songList=InitialTool.initSongChoose(MainActivity.this);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        rv.setHasFixedSize(true);
        adapter = new RvAdapter(songList);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListenerRV(this);

        //切换到第一首歌的播放界面
        changeSong(1);

        //切换歌曲后，由于是第一次打开，歌曲不进行自动播放，而在
        //changSong(int index)方法中的末尾会把该按钮设为暂停图案，故在此处需要手动设置播放图案。
        bt_play_pause.setImageResource(R.drawable.play );
    }

    //方法：检验是否为新的一天，若是，需要重新从网上更新背景图片
    private boolean isNewDay() {
        SharedPreferences prefs = getSharedPreferences("date",MODE_PRIVATE);
        int year = prefs.getInt("year",0);
        int day_of_year=prefs.getInt("day_of_year",0);
        Calendar date = Calendar.getInstance();
        int year_now = date.get(Calendar.YEAR);
        int day_of_year_now = date.get(Calendar.DAY_OF_YEAR);
        return !( year_now==year && day_of_year_now==day_of_year );
    }

    //方法：从网上更新背景图片
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
                SharedPreferences prefs = getSharedPreferences("bingPic",MODE_PRIVATE);
                prefs.edit().putString("todayPic",bingPic).apply();
            }
        });
    }

    //方法：转换歌曲
    private void changeSong(int i) {
        index=i;
        mediaPlayer.reset();
        if (MODE == MODE_RANDOM) {
            int temp = new Random().nextInt(SONG_ACCOUNT)+1;
            index = (temp==index)?temp+1:temp;
            index = (index>SONG_ACCOUNT)?1:index;
        }
        if (MODE == MODE_PLAY_BY_ORDER && index == 1){
            onDestroy();
        }
        String index_str = (index>9)?index+"":"0"+index;
        //加载音乐
        try {
            String fileName_song = "ssw"+index_str+".mp3";
            AssetFileDescriptor fd = getAssets().openFd(fileName_song);
            mediaPlayer.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),fd.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //装载歌词
        String fileName = "ssw"+index_str+".txt";
        try {
            lv_ly.loadLrc(LrcToString.getLrcToString(MainActivity.this,fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //装载文案
        String fileName_bs = "ssw_bs_"+index_str+".txt";
        try {
            tv_bs.setText(LrcToString.getLrcToString(MainActivity.this,fileName_bs));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                index = (index>=SONG_ACCOUNT)?0:index;
                changeSong(++index);
                mediaPlayer.start();
            }
        });
        tv_display_time_total.setText(MathUtil.getDisplayTime(mediaPlayer.getDuration()));
        bt_play_pause.setImageResource(R.drawable.pause);
    }

    //方法：歌词与文案图标的切换
    private void changeShow() {
        if (currentPage == CURRENT_LY){
            currentPage = CURRENT_BS;
            bt_ly_bs.setImageResource(R.drawable.ly);
            Toast.makeText(MainActivity.this,"显示背景文案",Toast.LENGTH_SHORT).show();
            vf_ly_bs.setInAnimation(this,R.anim.push_left_in);
            vf_ly_bs.setOutAnimation(this,R.anim.push_left_out);
            vf_ly_bs.showNext();
        }else {
            currentPage = CURRENT_LY;
            bt_ly_bs.setImageResource(R.drawable.bs);
            Toast.makeText(MainActivity.this,"显示歌词",Toast.LENGTH_SHORT).show();
            vf_ly_bs.setInAnimation(this,R.anim.push_right_in);
            vf_ly_bs.setOutAnimation(this,R.anim.push_right_out);
            vf_ly_bs.showPrevious();
        }
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
                    Toast.makeText(MainActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
                }else if (MODE == MODE_SINGLE_LOOP){
                    MODE = MODE_RANDOM;
                    mediaPlayer.setLooping(false);
                    bt_mode.setImageResource(R.drawable.random_play);
                    Toast.makeText(MainActivity.this,"随机播放",Toast.LENGTH_SHORT).show();
                }else if (MODE == MODE_RANDOM){
                    MODE = MODE_PLAY_BY_ORDER;
                    mediaPlayer.setLooping(false);
                    bt_mode.setImageResource(R.drawable.play_by_order);
                    Toast.makeText(MainActivity.this,"顺序播放",Toast.LENGTH_SHORT).show();
                }else if(MODE == MODE_PLAY_BY_ORDER){
                    MODE = MODE_LIST_LOOP;
                    mediaPlayer.setLooping(false);
                    bt_mode.setImageResource(R.drawable.allplay);
                    Toast.makeText(MainActivity.this,"列表循环",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_ly_bs:
                changeShow();
                break;
            case R.id.fbt_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
    }

    @Override
    public void onItemClickRV(int position) {
        index=position+1;
        drawerLayout.closeDrawers();
        changeSong(index);
        mediaPlayer.start();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("退出");
        builder.setMessage("确认要退出应用吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDestroy();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Calendar date = Calendar.getInstance();
        SharedPreferences prefs = getSharedPreferences("date",MODE_PRIVATE);
        prefs.edit().putInt("year",date.get(Calendar.YEAR)).putInt("day_of_year",date.get(Calendar.DAY_OF_YEAR)).apply();
        finish();
    }
}
