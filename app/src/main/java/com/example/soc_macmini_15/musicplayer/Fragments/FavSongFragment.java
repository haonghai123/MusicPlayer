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
import com.example.soc_macmini_15.musicplayer.Adapter.TestAdapter;
import com.example.soc_macmini_15.musicplayer.Model.Song;
import com.example.soc_macmini_15.musicplayer.R;

import java.util.ArrayList;

public class FavSongFragment extends ListFragment {

    private TestAdapter testAdapter;

    public ArrayList<Song> song;
    public ArrayList<Song> newList;

    private ListView listView;

    private createDataParsed createDataParsed;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        FavSongFragment tabFragment = new FavSongFragment();
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
        testAdapter = new TestAdapter(context);
        testAdapter.createDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        setContent();
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    public void setContent() {
        boolean searchedList = false;
        song = new ArrayList<>();
        newList = new ArrayList<>();
        testAdapter.open();
        song = testAdapter.getAllFavorites();
        testAdapter.close();
        SongAdapter adapter = new SongAdapter(getContext(), song);
        if (!createDataParsed.queryText().equals("")) {
            adapter = onQueryTextChange();
            adapter.notifyDataSetChanged();
            searchedList = true;
        } else {
            searchedList = false;
        }

        listView.setAdapter(adapter);
        final boolean finalSearchedList = searchedList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                if (!finalSearchedList) {
                    createDataParsed.onDataPass(song.get(position).getTitle(), song.get(position).getSubTitle(), song.get(position).getPath(), song.get(position).isFav());
                    createDataParsed.fullSongList(song, position);
                } else {
                    createDataParsed.onDataPass(newList.get(position).getTitle(),newList.get(position).getSubTitle(), newList.get(position).getPath(),newList.get(position).isFav());
                    createDataParsed.fullSongList(newList, position);
                }
            }
        });

    }


    public interface createDataParsed {
        public void onDataPass(String title, String subTitle, String path, boolean isFav);

        public void fullSongList(ArrayList<Song> songList, int position);

        public int getPosition();

        public String queryText();
    }

    public SongAdapter onQueryTextChange() {
        String text = createDataParsed.queryText();
        for (Song songs : song) {
            String title = songs.getTitle().toLowerCase();
            if (title.contains(text)) {
                newList.add(songs);
            }
        }
        return new SongAdapter(getContext(), newList);

    }



}
