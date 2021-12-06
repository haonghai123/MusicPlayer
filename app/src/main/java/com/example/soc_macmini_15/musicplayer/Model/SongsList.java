package com.example.soc_macmini_15.musicplayer.Model;

public class SongsList {
    private String title;
    private String subTitle;
    private String path;
    private boolean isFav;
    private String PlayListName;

    public SongsList(String title, String subTitle, String path, boolean isFav, String playListName) {
        this.title = title;
        this.subTitle = subTitle;
        this.path = path;
        this.isFav = isFav;
        PlayListName = playListName;
    }

    public void setPlayListName(String playListName) {
        PlayListName = playListName;
    }

    public String getPlayListName() {
        return PlayListName;
    }

    public String getTitle() {
        return title;
    }
}
