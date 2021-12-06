package com.example.soc_macmini_15.musicplayer.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.soc_macmini_15.musicplayer.DB.SongsListDBHelper;
import com.example.soc_macmini_15.musicplayer.Model.PlayList;
import com.example.soc_macmini_15.musicplayer.Model.Song;
import com.example.soc_macmini_15.musicplayer.R;

import java.util.ArrayList;

public class PlayListAdapter extends ArrayAdapter<PlayList> implements Filterable {

    private Context mContext;
    private ArrayList<PlayList> playLists = new ArrayList<>();
    SongsListDBHelper songsListDBHelper;
    public PlayListAdapter(Context mContext, ArrayList<PlayList> playLists) {
        super(mContext, 0, playLists);
        this.mContext = mContext;
        this.playLists = playLists;
        songsListDBHelper=new SongsListDBHelper(mContext);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.playlist_items, parent, false);
        }

        PlayList currentSong = playLists.get(position);
        TextView tvTitle = listItem.findViewById(R.id.tv_music_name);
        TextView tvSubtitle = listItem.findViewById(R.id.tv_music_subtitle);
        ImageView imageView=listItem.findViewById(R.id.iv_music_list);
        tvTitle.setText(currentSong.getName());
        tvSubtitle.setText(currentSong.getSize()+" bài hát");
        imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        return listItem;
    }
}
