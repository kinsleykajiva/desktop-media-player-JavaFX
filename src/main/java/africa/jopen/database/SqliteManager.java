package africa.jopen.database;


import africa.jopen.models.MyMedia;
import africa.jopen.models.MyPlayListMedia;
import africa.jopen.models.MyPlayLists;
import africa.jopen.utils.AudioEqualizer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class SqliteManager {
    private Connection connection;
    private static SqliteManager instance = null;
    private final String url = "jdbc:sqlite:desktop-media-player.db";
    private SqliteManager(){connect();}
    public static SqliteManager getInstance(){
        if (instance==null)
            instance = new SqliteManager();
        return instance;
    }
    public boolean connect(){
        try {
            connection = DriverManager.getConnection(url);
            if(connection != null && !connection.isClosed()) {
                return true;
            }
        }catch (SQLException e){
           e.printStackTrace();
           System.err.println(e.getMessage());
        }
        return false;
    } public boolean disconnect(){
        try {
            if(connection!=null && !connection.isClosed()) {
                connection.close();
                return  true;
            }
        }catch (SQLException e){
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return  false;
    }
    public String getUrl() {return url;}
    public boolean addMedia(MyMedia myMedia){
        try {
            if(connection != null && myMedia!=null && !connection.isClosed()&&!isPresent("path",myMedia.getPath(),"media")) {
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO media " +
                        "(path,title,artist,genre,album,image,length,year)" +
                        " VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
                stmt.setString(1, myMedia.getPath());
                stmt.setString(2, myMedia.getTitle());
                stmt.setString(3, myMedia.getArtist());
                stmt.setString(4, myMedia.getGenre());
                stmt.setString(5, myMedia.getAlbum());
                stmt.setBytes(6, myMedia.getImageUrl());
                stmt.setString(7, myMedia.getLength());
                stmt.setString(8, myMedia.getYear());
                stmt.execute();
                stmt.close();
                return true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean addPlayList(MyPlayLists playLists){
        try {
            if(connection != null && playLists!=null && !connection.isClosed()&&!isPresent("title",playLists.title(),"playlists")) {
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO playlists VALUES( ?);");
                stmt.setString(1, playLists.title());
                stmt.execute();
                stmt.close();
                return true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean addMediaToPlayList(MyPlayListMedia media){
        try {
            if(connection != null && media!=null && !connection.isClosed()&&!isPresent("title",media.playlistId()+"","playlistsMedia")) {
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO playlistsMedia VALUES(?, ?);");
                stmt.setInt(1, media.playlistId());
                stmt.setInt(2, media.mediaId());
                stmt.execute();
                stmt.close();
                return true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ObservableList<MyMedia> Library = FXCollections.observableArrayList();
    private ObservableList<MyPlayLists> PlayLists = FXCollections.observableArrayList();
    private ObservableList<MyPlayListMedia> PlayListsMedia = FXCollections.observableArrayList();
    public ObservableList<MyMedia> getMusicLibrary() {
        return Library;
    }

    public ObservableList<MyPlayLists> getPlayLists() {
        return PlayLists;
    }
    public ObservableList<MyPlayListMedia   > getPlayListsMedia() {
        return PlayListsMedia;
    }
    public void readMyMedia(String filter){
        try {
            if(connection != null&&!connection.isClosed()) {
                String query = "select * from media;";
                if (!filter.isEmpty()) {
                    query = "select * from media where "+filter+"=true";
                }
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {

                        Library.add(new MyMedia(rs.getString("Title"), rs.getString("Artist"),
                                rs.getString("Album"), rs.getString("Genre"), rs.getString("Path")
                                , rs.getString("Length"), rs.getString("Year"), rs.getBytes("Image"),
                                rs.getInt("id")));

                }
                stmt.close();
                rs.close();
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
     public void readMyPlayLists(){
        try {
            if(connection != null&&!connection.isClosed()) {
                String query = "select * from playlists;";
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {

                    PlayLists.add(
                                new MyPlayLists(rs.getInt("id"), rs.getString("title")
                                ));

                }
                stmt.close();
                rs.close();
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
     public void readMyPlayListMedia(){
        try {
            if(connection != null&&!connection.isClosed()) {
                String query = "select * from playlistsMedia;";
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    PlayListsMedia.add(
                                new MyPlayListMedia(rs.getInt("id"), rs.getInt("playlistId") , rs.getInt("mediaId")
                                ));
                }
                stmt.close();
                rs.close();
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void createTableMyMedia(){
        try {
            String query =
                    "CREATE TABLE IF NOT EXISTS media(" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "path VARCHAR(255)," +
                            "title VARCHAR(100)," +
                            "artist VARCHAR(100)," +
                            "genre VARCHAR(100)," +
                            "album VARCHAR(100)," +
                            "image blob ," +
                            "length VARCHAR(10)," +
                            "year VARCHAR(10)" +
                            ");";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void createTablePlaylist(){
        try {
            String query =
                    """
CREATE TABLE IF NOT EXISTS playlists(
   id INTEGER PRIMARY KEY AUTOINCREMENT,
   title VARCHAR(100)
);
""";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void createTablePlaylistsMedia(){
        try {
            String query =
                    """
CREATE TABLE IF NOT EXISTS playlistsMedia(
   id INTEGER PRIMARY KEY AUTOINCREMENT,
   playlistId INTEGER,
   mediaId INTEGER
);
""";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private boolean isPresent(final String object,final String key, final String tab){
        try {
            if (connection != null && !connection.isClosed()) {
                String query = "SELECT * FROM "+tab+" WHERE "+object+"=?;";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1,key);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {return  true;}
                stmt.close();
            }
        }catch (SQLException e){}
        return  false;
    }
    public void createTableEqualizer(){
        try {
            String query =
                    "CREATE TABLE IF NOT EXISTS Equalizer(" +
                            "Hz32 INTEGER," +
                            "Hz64 INTEGER," +
                            "Hz125 INTEGER," +
                            "Hz250 INTEGER," +
                            "Hz500 INTEGER," +
                            "Hz1000 INTEGER," +
                            "Hz2000 INTEGER," +
                            "Hz4000 INTEGER," +
                            "Hz8000 INTEGER,"+
                            "Hz16000 INTEGER," +
                            "Key VARCHAR(3));" ;
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        }catch (SQLException e){}
    }
    public boolean initEqualizer(){
        try {
            if(connection != null &&!connection.isClosed()&&!isPresent("Key","key","Equalizer")) {
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO Equalizer VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
                stmt.setInt(1,0);
                stmt.setInt(2,0);
                stmt.setInt(3,0);
                stmt.setInt(4,0);
                stmt.setInt(5,0);
                stmt.setInt(6,0);
                stmt.setInt(7,0);
                stmt.setInt(8,0);
                stmt.setInt(9,0);
                stmt.setInt(10,0);
                stmt.setString(11,"key");
                stmt.execute();
                stmt.close();
                return true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setEqualizer(int []vett){
        try {
            if(connection != null &&!connection.isClosed()) {
                PreparedStatement stmt = connection.prepareStatement("UPDATE Equalizer SET " +
                        "Hz32=?," +
                        "Hz64=?," +
                        "Hz125=?," +
                        "Hz250=?," +
                        "Hz500=?," +
                        "Hz1000=?," +
                        "Hz2000=?," +
                        "Hz4000=?," +
                        "Hz8000=?," +
                        "Hz16000=?  WHERE Key=key ;");
                for(int i = 0; i < 10; ++i)
                    stmt.setInt(i+1, vett[i]);

                stmt.execute();
                stmt.close();
                return true;
            }
        }catch (SQLException e) {}
        return false;
    }
    public boolean getEqualizer(){
        try {
            String query = "select * from Equalizer where Key=key;";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            int []settings=new int[10];
            if(rs.next()){
                settings[0]=rs.getInt("Hz32");
                settings[1]=rs.getInt("Hz64");
                settings[2]=rs.getInt("Hz125");
                settings[3]=rs.getInt("Hz250");
                settings[4]=rs.getInt("Hz500");
                settings[5]=rs.getInt("Hz1000");
                settings[6]=rs.getInt("Hz2000");
                settings[7]=rs.getInt("Hz4000");
                settings[8]=rs.getInt("Hz8000");
                settings[9]=rs.getInt("Hz16000");
            }
            AudioEqualizer.getInstance().getPresetsValues().remove(AudioEqualizer.getInstance().getPresetsNames().size()-1);
            AudioEqualizer.getInstance().getPresetsValues().add(settings);
            return true;
        }catch (SQLException e){}
        return false;
    }
}
