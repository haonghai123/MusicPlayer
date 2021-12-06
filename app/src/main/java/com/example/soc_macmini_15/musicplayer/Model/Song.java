package com.example.soc_macmini_15.musicplayer.Model;

public class Song {

    private String title;
    private String subTitle;
    private String path;
    private boolean isFav;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Song(String title, String subTitle, String path, boolean isFav) {
        this.title = title;
        this.subTitle = subTitle;
        this.path = path;
        this.isFav = isFav;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }


}
