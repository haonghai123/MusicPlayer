package com.example.soc_macmini_15.musicplayer.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.soc_macmini_15.musicplayer.Adapter.SongAdapter;
import com.example.soc_macmini_15.musicplayer.Model.Song;
import com.example.soc_macmini_15.musicplayer.R;

import java.util.ArrayList;

public class CurrentSongFragment extends ListFragment {

    public ArrayList<Song> song = new ArrayList<>();

    private ListView listView;

    private createDataParsed createDataParsed;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        CurrentSongFragment tabFragment = new CurrentSongFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParsed = (createDataParsed) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        //songsList = new ArrayList<>();
        setContent();
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    public void setContent() {
        if (createDataParsed.getSong() != null)
            song.add(createDataParsed.getSong());

        SongAdapter adapter = new SongAdapter(getContext(), song);

        if (song.size() > 1)
            if (createDataParsed.getPlaylistFlag()) {
                song.clear();
            }

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                createDataParsed.onDataPass(song.get(position).getTitle(), song.get(position).getSubTitle(), song.get(position).getPath(), song.get(position).isFav());
                createDataParsed.fullSongList(song, position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return true;
            }
        });
    }

    public interface createDataParsed {
        public void onDataPass(String title, String subTitle, String path, boolean isFav);

        public void fullSongList(ArrayList<Song> songList, int position);

        public Song getSong();

        public boolean getPlaylistFlag();
    }


}
