package sample.controllers;

import java.io.File;

public class Songs {
    File title;
    String album;
    String artist;

    public File getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public void setTitle(File title) {
        this.title = title;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }
}
