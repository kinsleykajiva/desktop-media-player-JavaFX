package africa.jopen.models;

import java.io.File;
import java.util.Objects;

public class MyMedia {
    private int id =0;
    private String title = "";
    private String artist = "N/A";
    private String album = "N/A";
    private String genre = "N/A";
    private String path = "";
    private String length="00:00:00";
    private String year="N/A";
    private byte[] imageUrl;
    private String delete="fa-close";

    public MyMedia(String title, String artist, String album, String genre, String path, String length, String year, byte[] imageUrl,int id) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.path = path;
        this.length = length;
        this.year = year;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public MyMedia() { }
    public MyMedia(File file) {
        path = file.toURI().toString();
        setTitle(file.getName());
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setAlbum(String album) { this.album = album; }
    public void setLength(String length) {
        this.length = length;
    }
    public void setYear(String year) { this.year = year; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getLength() { return length; }
    public String getYear() { return year; }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public byte[] getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(byte[] imageURL) {
        this.imageUrl = imageURL;
    }

    @Override
    public String toString() {
        return  title + '\'' + artist + '\'' +album + '\'' +genre + '\'' +path + '\'' +length +'\''+year ;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyMedia)) return false;
        MyMedia myMedia = (MyMedia) o;
        return getPath().equals(myMedia.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath());
    }
}
