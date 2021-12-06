package com.example.soc_macmini_15.musicplayer.Fragments;


import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
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

public class AllSongFragment extends ListFragment {


    private static ContentResolver contentResolver1;

    public ArrayList<Song> song;
    public ArrayList<Song> newList;

    private ListView listView;

    private createDataParse createDataParse;
    private ContentResolver contentResolver;
    private TestAdapter testAdapter;
    public static Fragment getInstance(int position, ContentResolver mcontentResolver) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        AllSongFragment tabFragment = new AllSongFragment();
        tabFragment.setArguments(bundle);
        contentResolver1 = mcontentResolver;
        return tabFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testAdapter=new TestAdapter(getContext());
        testAdapter.createDatabase();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParse = (createDataParse) context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        contentResolver = contentResolver1;
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
        song=testAdapter.getAllSongs();
        testAdapter.close();
        SongAdapter adapter = new SongAdapter(getContext(), song);
        if (!createDataParse.queryText().equals("")) {
            adapter = onQueryTextChange();
            adapter.notifyDataSetChanged();
            searchedList = true;
        } else {
            searchedList = false;
        }
        createDataParse.getLength(song.size());
        listView.setAdapter(adapter);

        final boolean finalSearchedList = searchedList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                if (!finalSearchedList) {
                    createDataParse.onDataPass(song.get(position).getTitle(), song.get(position).getSubTitle(), song.get(position).getPath(), song.get(position).isFav());
                    createDataParse.fullSongList(song, position);
                } else {
                    createDataParse.onDataPass(newList.get(position).getTitle(),newList.get(position).getSubTitle(), newList.get(position).getPath(),newList.get(position).isFav());
                    createDataParse.fullSongList(newList, position);
                }
            }
        });


    }




    public SongAdapter onQueryTextChange() {
        String text = createDataParse.queryText();
        for (Song songs : song) {
            String title = songs.getTitle().toLowerCase();
            if (title.contains(text)) {
                newList.add(songs);
            }
        }
        return new SongAdapter(getContext(), newList);

    }

    private void showDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.play_next))
                .setCancelable(true)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createDataParse.currentSong(song.get(position));
                        setContent();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public interface createDataParse {
        public void onDataPass(String title, String subTitle, String path, boolean isFav);

        public void fullSongList(ArrayList<Song> songList, int position);

        public String queryText();

        public void currentSong(Song song);
        public void getLength(int length);
    }

}
