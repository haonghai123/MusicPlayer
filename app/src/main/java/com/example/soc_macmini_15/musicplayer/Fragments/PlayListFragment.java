package com.example.soc_macmini_15.musicplayer.Fragments;


import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soc_macmini_15.musicplayer.Activity.MainActivity;
import com.example.soc_macmini_15.musicplayer.Adapter.PlayListAdapter;
import com.example.soc_macmini_15.musicplayer.Adapter.SongAdapter;
import com.example.soc_macmini_15.musicplayer.Adapter.TestAdapter;
import com.example.soc_macmini_15.musicplayer.Adapter.ViewPagerAdapter;
import com.example.soc_macmini_15.musicplayer.DB.PlayListDBHelper;
import com.example.soc_macmini_15.musicplayer.DB.SongsListDBHelper;
import com.example.soc_macmini_15.musicplayer.Interfaces.OnBackPressedListener;
import com.example.soc_macmini_15.musicplayer.Model.PlayList;
import com.example.soc_macmini_15.musicplayer.Model.Song;
import com.example.soc_macmini_15.musicplayer.Model.SongsList;
import com.example.soc_macmini_15.musicplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PlayListFragment extends ListFragment implements OnBackPressedListener {


    private static ContentResolver contentResolver1;

    public ArrayList<PlayList> playlist;
    public ArrayList<PlayList> newPlayList;
    public ArrayList<Song> songslist;
    public ArrayList<Song> newSongsList;
    public ArrayList<Song> searchSongslist;
    public ArrayList<Song> newSearchSongsList;
    private PlayListDBHelper playListDBHelper;
    private SongsListDBHelper songsListDBHelper;
    private ListView listView;
    private Button btnAdd;
    private createDataParse createDataParse;
    private createDataParsed createDataParsed;
    private ContentResolver contentResolver;
    public static int whichView = 0;
    public static String name;
    private String searchText = "";
    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        PlayListFragment tabFragment = new PlayListFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playListDBHelper = new PlayListDBHelper(getContext());
        songsListDBHelper = new SongsListDBHelper(getContext());
        setHasOptionsMenu(true);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParse = (createDataParse) context;
        createDataParsed = (createDataParsed) context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playlist_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).setOnBackPressedListener(this);
        listView = view.findViewById(R.id.list_playlist);
        btnAdd = view.findViewById(R.id.btnAdd);
        contentResolver = contentResolver1;
        setPlaylistContent();
    }


    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    public void setPlaylistContent() {
        whichView = 0;
        btnAdd.setText("Tạo Playlist mới");
        boolean searchedList = false;
        playlist = new ArrayList<>();
        newPlayList = new ArrayList<>();
        playlist = playListDBHelper.getAllPlaylists();
        PlayListAdapter adapter = new PlayListAdapter(getContext(), playlist);
        if (!searchText.equals("")) {
            adapter = onQueryTextChangePlaylist();
            adapter.notifyDataSetChanged();
            searchedList = true;
        } else {
            searchedList = false;
        }
        createDataParse.getLength(playlist.size());
        listView.setAdapter(adapter);

        final boolean finalSearchedList = searchedList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                if (!finalSearchedList) {
                    createDataParse.onDataPass(playlist.get(position).getName(), playlist.get(position).getSize());
                    createDataParse.fullPlayList(playlist, position);
                    setSongslistContent(playlist.get(position).getName());
                } else {
                    createDataParse.onDataPass(newPlayList.get(position).getName(), newPlayList.get(position).getSize());
                    createDataParse.fullPlayList(newPlayList, position);
                    setSongslistContent(newPlayList.get(position).getName());
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (!finalSearchedList) {
                        deletePlaylist(position, finalSearchedList);
                    }
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    return true;
                } catch (SQLException e) {
                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "You clicked :\n" , Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Playlist Name");
                final EditText input = new EditText(getContext());
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if(playListDBHelper.checkPlaylistExist(input.getText().toString())>0){
                                Toast.makeText(getContext(), "Playlist already exists", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                playListDBHelper.addPlayList(input.getText().toString());
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Throwable e) {
                            Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                        } finally {
                            setPlaylistContent();
                        }


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }

    public void setSongslistContent(String name) {
        whichView = 1;
        this.name=name;
        btnAdd.setText("Thêm bài");
        boolean searchedList = false;
        songslist = new ArrayList<>();
        newSongsList = new ArrayList<>();
        songslist = songsListDBHelper.getAllSongsList(name);
        SongAdapter adapter = new SongAdapter(getContext(), songslist);
        if (!searchText.equals("")) {
            adapter = onQueryTextChangeSongsList();
            adapter.notifyDataSetChanged();
            searchedList = true;
        } else {
            searchedList = false;
        }
        createDataParsed.getLength(songslist.size());
        listView.setAdapter(adapter);

        final boolean finalSearchedList = searchedList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                if (!finalSearchedList) {
                    createDataParsed.onDataPass(songslist.get(position).getTitle(), songslist.get(position).getSubTitle(), songslist.get(position).getPath(), songslist.get(position).isFav());
                    createDataParsed.fullSongList(songslist, position);
                } else {
                    createDataParsed.onDataPass(newSongsList.get(position).getTitle(), newSongsList.get(position).getSubTitle(), newSongsList.get(position).getPath(), newSongsList.get(position).isFav());
                    createDataParsed.fullSongList(newSongsList, position);
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    deleteSongslist(position, name, finalSearchedList);
                    playListDBHelper.decreaseSize(name);
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    return true;
                } catch (SQLException e) {
                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSearchSongs(name);
            }
        });

    }

    public void setSearchSongs(String name) {
        whichView = 2;
        btnAdd.setText("Quay lại");
        boolean searchedList = false;
        searchSongslist = new ArrayList<>();
        newSearchSongsList = new ArrayList<>();
        searchSongslist = songsListDBHelper.getSearchedSongsToAdd(name);
        SongAdapter adapter = new SongAdapter(getContext(), searchSongslist);
        if (!searchText.equals("")) {
            adapter = onQueryTextChangeSearchSongs();
            adapter.notifyDataSetChanged();
            searchedList = true;
        } else {
            searchedList = false;
        }
        createDataParsed.getLength(searchSongslist.size());
        listView.setAdapter(adapter);

        final boolean finalSearchedList = searchedList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if(finalSearchedList){
                        songsListDBHelper.addSongsList(newSearchSongsList.get(position), name);
                    }
                    else{
                        songsListDBHelper.addSongsList(searchSongslist.get(position), name);
                    }

                    playListDBHelper.increaseSize(name);
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                } finally {
                    setSongslistContent(name);
                }
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();


            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "You clicked :\n" , Toast.LENGTH_SHORT).show();
                doBack();
            }
        });

    }

    public PlayListAdapter onQueryTextChangePlaylist() {
        String text = searchText.toLowerCase();
        for (PlayList songs : playlist) {
            String title = songs.getName().toLowerCase();
            if (title.contains(text)) {
                newPlayList.add(songs);
            }
        }
        return new PlayListAdapter(getContext(), newPlayList);
    }

    public SongAdapter onQueryTextChangeSongsList() {
        String text = searchText.toLowerCase();
        for (Song songs : songslist) {
            String title = songs.getTitle().toLowerCase();
            if (title.contains(text)) {
                newSongsList.add(songs);
            }
        }
        return new SongAdapter(getContext(), newSongsList);

    }
    public SongAdapter onQueryTextChangeSearchSongs() {
        String text = searchText.toLowerCase();
        for (Song songs : searchSongslist) {
            String title = songs.getTitle().toLowerCase();
            if (title.contains(text)) {
                newSearchSongsList.add(songs);
            }
        }
        return new SongAdapter(getContext(), newSearchSongsList);

    }

    private void deletePlaylist(final int position, boolean isSearch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to delete this playlist ?")
                .setCancelable(true)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if(!isSearch) {
                                playListDBHelper.deletePlayList(playlist.get(position).getName());
                            }
                            else{
                                playListDBHelper.deletePlayList(newPlayList.get(position).getName());
                            }
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        } catch (SQLException e) {
                            Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                        } finally {
                            setPlaylistContent();
                        }

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteSongslist(final int position, String name, boolean isSearch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to removed this ?")
                .setCancelable(true)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if(!isSearch) {
                                songsListDBHelper.deleteSongsList(songslist.get(position).getTitle(), name);
                            }
                            else{
                                songsListDBHelper.deleteSongsList(newSongsList.get(position).getTitle(), name);
                            }
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        } catch (SQLException e) {
                            Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                        } finally {
                            setSongslistContent(name);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void doBack() {
        setPlaylistContent();
    }


    private Menu menu;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
//        this.menu = menu;
        inflater.inflate(R.menu.action_bar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            //searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchText = newText;
                    if (whichView == 0) {
                        setPlaylistContent();
                    }
                    if (whichView == 1) {
                        setSongslistContent(name);
                    }
                    if (whichView == 2) {
                        setSearchSongs(name);
                    }
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
            super.onCreateOptionsMenu(menu, inflater);
        }


    }

    public interface createDataParse {
        public void onDataPass(String name, int size);

        public void fullPlayList(ArrayList<PlayList> songList, int position);

        //public String queryText();

        public void currentSong(PlayList playList);

        public void getLength(int length);

    }

    public interface createDataParsed {
        public void onDataPass(String title, String subTitle, String path, boolean isFav);

        public void fullSongList(ArrayList<Song> songList, int position);

        //public String queryText();

        public void currentSong(Song song);

        public void getLength(int length);
    }
}
