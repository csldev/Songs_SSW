package com.compassl.anji.songs_ssw;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/28.
 */
public class FragmentForChoose extends Fragment {

    private List<Song> songList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_choose,container,false);
        initList();
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv_for_choose);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        RvAdapter adapter = new RvAdapter(songList);
        rv.setAdapter(adapter);

        return view;
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

}

