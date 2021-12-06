package com.example.soc_macmini_15.musicplayer.Model;

import java.util.ArrayList;

public class PlayList {
    private String name;
    private int size;

    public PlayList(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
}
