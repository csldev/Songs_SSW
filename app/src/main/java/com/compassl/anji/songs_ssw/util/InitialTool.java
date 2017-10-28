package com.compassl.anji.songs_ssw.util;

import android.content.Context;

import com.compassl.anji.songs_ssw.R;
import com.compassl.anji.songs_ssw.db.SongInfo;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/10/28.
 */
public class InitialTool {

    public static void initSongInfo(Context context){

        LitePal.getDatabase();
        DataSupport.deleteAll(SongInfo.class,"newId>?","0");

        //add information

        //1
        SongInfo s1 = new SongInfo(1,
                context.getResources().getString(R.string.one),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.lyc_01),
                context.getResources().getString(R.string.bs_01));
        s1.save();


        //2
        SongInfo s2 = new SongInfo(2,
                context.getResources().getString(R.string.two),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.lyc_02),
                context.getResources().getString(R.string.bs_02));
        s2.save();

        //3
        SongInfo s3 = new SongInfo(3,
                context.getResources().getString(R.string.three),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.lyc_03),
                context.getResources().getString(R.string.bs_03));
        s3.save();

        //4
        SongInfo s4 = new SongInfo(4,
                context.getResources().getString(R.string.four),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.lyc_04),
                context.getResources().getString(R.string.bs_04));
        s4.save();

        //5
        SongInfo s5 = new SongInfo(5,
                context.getResources().getString(R.string.five),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.lyc_05),
                context.getResources().getString(R.string.bs_05));
        s5.save();

        //1
        SongInfo s6 = new SongInfo(6,
                context.getResources().getString(R.string.six),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.lyc_06),
                context.getResources().getString(R.string.bs_06));
        s6.save();

        //7
        SongInfo s7 = new SongInfo(7,
                context.getResources().getString(R.string.seven),
                context.getResources().getString(R.string.wl)+" "+context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.lyc_07),
                context.getResources().getString(R.string.bs_07));
        s7.save();

        //8
        SongInfo s8 = new SongInfo(8,
                context.getResources().getString(R.string.eight),
                context.getResources().getString(R.string.wl),
                context.getResources().getString(R.string.csl),
                context.getResources().getString(R.string.lyc_08),
                context.getResources().getString(R.string.bs_08));
        s8.save();

        //9
        SongInfo s9 = new SongInfo(9,
                context.getResources().getString(R.string.nine),
                context.getResources().getString(R.string.wl),
                context.getResources().getString(R.string.wl),
                context.getResources().getString(R.string.lyc_09),
                context.getResources().getString(R.string.bs_09));
        s9.save();

    }

}