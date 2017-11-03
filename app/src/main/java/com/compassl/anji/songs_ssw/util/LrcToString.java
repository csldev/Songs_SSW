package com.compassl.anji.songs_ssw.util;

import android.content.Context;
import android.util.Log;

import com.compassl.anji.songs_ssw.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/11/2.
 */
public class LrcToString {

    public static String getLrcToString(Context context,String fileName) throws IOException {
        InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        String lrc = new String(buffer);
        is.close();
        return lrc;
    }

    public static String getLrcInfo(String allLrc){
        //Matcher m = Pattern.compile("\\[(\\d{1,2}):(\\d{1,2}).(\\d{1,2})\\]").matcher(allLrc);
        Matcher m = Pattern.compile("\\[(\\d*):(\\d*).(\\d*)\\]").matcher(allLrc);
        int i;
        if (m.find()){
            i= m.start();
        }else {
            return "无歌词信息";
        }
        String str = allLrc.substring(0,i-1);
        return "\r\n\r\n\r\n"+str.replace("[ti:","歌曲：").replace("[ar:","作曲：").replace("[al:","专辑：").replace("[by:","作词：")
                .replaceAll("]","");
    }

}