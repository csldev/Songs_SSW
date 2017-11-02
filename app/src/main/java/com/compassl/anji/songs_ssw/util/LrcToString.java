package com.compassl.anji.songs_ssw.util;

import android.content.Context;

import com.compassl.anji.songs_ssw.R;

import java.io.IOException;
import java.io.InputStream;

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
}