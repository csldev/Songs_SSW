package com.compassl.anji.songs_ssw.util;

import android.content.Context;
import android.content.pm.ProviderInfo;

import com.compassl.anji.songs_ssw.R;
import com.compassl.anji.songs_ssw.Song;
import com.compassl.anji.songs_ssw.db.SongInfo;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/28.
 */
public class InitialTool {


    public static List<Song> initSongChoose(Context context){
            List<Song> songList = new ArrayList<>();
            songList.clear();
            //1
            Song song1 = new Song(context.getResources().getString(R.string.one),
                    R.drawable.ssw01aqjx);
            songList.add(song1);
            //2
            Song song2 = new Song(context.getResources().getString(R.string.two),
                    R.drawable.ssw02ssw);
            songList.add(song2);
            //3
            Song song3 = new Song(context.getResources().getString(R.string.three),
                    R.drawable.ssw03zxl);
            songList.add(song3);
            //4
            Song song4 = new Song(context.getResources().getString(R.string.four),
                    R.drawable.ssw04xty);
            songList.add(song4);
            //5
            Song song5 = new Song(context.getResources().getString(R.string.five),
                    R.drawable.ssw05yzwwdg);
            songList.add(song5);
            //6
            Song song6 = new Song(context.getResources().getString(R.string.six),
                    R.drawable.ssw06yyc);
            songList.add(song6);
            //7
            Song song7 = new Song(context.getResources().getString(R.string.seven),
                    R.drawable.ssw07ylq);
            songList.add(song7);
            //8
            Song song8 = new Song(context.getResources().getString(R.string.eight),
                    R.drawable.ssw08xc);
            songList.add(song8);
            //9
            Song song9 = new Song(context.getResources().getString(R.string.nine),
                    R.drawable.ssw09bjlnxt);
            songList.add(song9);
        return songList;
    }



//    public static void initSongInfo(Context context){
//
//        LitePal.getDatabase();
//        DataSupport.deleteAll(SongInfo.class,"newId>?","0");
//
//        //add information
//
//        //1
//        SongInfo s1 = new SongInfo(1,
//                context.getResources().getString(R.string.one),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.lyc_01),
//                context.getResources().getString(R.string.bs_01));
//        s1.save();
//
//
//        //2
//        SongInfo s2 = new SongInfo(2,
//                context.getResources().getString(R.string.two),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.lyc_02),
//                context.getResources().getString(R.string.bs_02));
//        s2.save();
//
//        //3
//        SongInfo s3 = new SongInfo(3,
//                context.getResources().getString(R.string.three),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.lyc_03),
//                context.getResources().getString(R.string.bs_03));
//        s3.save();
//
//        //4
//        SongInfo s4 = new SongInfo(4,
//                context.getResources().getString(R.string.four),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.lyc_04),
//                context.getResources().getString(R.string.bs_04));
//        s4.save();
//
//        //5
//        SongInfo s5 = new SongInfo(5,
//                context.getResources().getString(R.string.five),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.lyc_05),
//                context.getResources().getString(R.string.bs_05));
//        s5.save();
//
//        //1
//        SongInfo s6 = new SongInfo(6,
//                context.getResources().getString(R.string.six),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.lyc_06),
//                context.getResources().getString(R.string.bs_06));
//        s6.save();
//
//        //7
//        SongInfo s7 = new SongInfo(7,
//                context.getResources().getString(R.string.seven),
//                context.getResources().getString(R.string.wl)+" "+context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.lyc_07),
//                context.getResources().getString(R.string.bs_07));
//        s7.save();
//
//        //8
//        SongInfo s8 = new SongInfo(8,
//                context.getResources().getString(R.string.eight),
//                context.getResources().getString(R.string.wl),
//                context.getResources().getString(R.string.csl),
//                context.getResources().getString(R.string.lyc_08),
//                context.getResources().getString(R.string.bs_08));
//        s8.save();
//
//        //9
//        SongInfo s9 = new SongInfo(9,
//                context.getResources().getString(R.string.nine),
//                context.getResources().getString(R.string.wl),
//                context.getResources().getString(R.string.wl),
//                context.getResources().getString(R.string.lyc_09),
//                context.getResources().getString(R.string.bs_09));
//        s9.save();
//
//    }

}