package com.example.cst2335_graphicalinterfaceprogramming;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Albums {
    @SerializedName("album")
    @Expose
    private List<Album> albums = null;

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbum(List<Album> albums) {
        this.albums = albums;
    }
}
