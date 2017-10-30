package com.compassl.anji.songs_ssw;

import android.app.Activity;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.util.Util;
import com.compassl.anji.songs_ssw.db.SongInfo;
import com.compassl.anji.songs_ssw.util.InitialTool;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG";

    private FragmentForPlaying frag1;
    private static final int SONG_ACCOUNT=9;

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

        LitePal.getDatabase();
        SongInfo songInfo = DataSupport.findLast(SongInfo.class);
        if(songInfo==null || songInfo.getNewId()!=SONG_ACCOUNT){
            InitialTool.initSongInfo(this);
        }
        List<SongInfo> songInfoList = DataSupport.findAll(SongInfo.class);
        for (int i=0;i<songInfoList.size();i++){
            Log.d(TAG, songInfoList.get(i).getNewId()+"");
        }




        frag1 = new FragmentForPlaying();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_frameview,frag1);
        transaction.commit();



    }


}
