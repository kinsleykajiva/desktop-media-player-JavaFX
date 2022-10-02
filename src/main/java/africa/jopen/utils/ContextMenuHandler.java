package africa.jopen.utils;


import africa.jopen.database.SqliteManager;
import africa.jopen.models.MyMedia;
import africa.jopen.models.MyPlayListMedia;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class ContextMenuHandler extends ContextMenu {
    private String source;
    private String playlistName;
    private int row;
    public ContextMenuHandler(MyMedia myMedia, String playlistName, String  source, int row) {
        super ();
        this.source = source;
        this.playlistName = playlistName;
        this.row = row;
       // MenuItem menuItem = new MenuItem();
        MenuItem menuItem1 = new MenuItem();


        menuItem1.setText("Delete");
        menuItem1.setOnAction(actionEvent -> {


        });
        this.getItems().add(new SeparatorMenuItem());

        SqliteManager.getInstance().readMyPlayLists();
        if(!SqliteManager.getInstance().getPlayLists().isEmpty()){
            SqliteManager.getInstance().getPlayLists().forEach(playLists -> {
                MenuItem tmp = new MenuItem(playLists.title());
                tmp.setOnAction(ev->{
                    SqliteManager.getInstance().addMediaToPlayList(new MyPlayListMedia(0,playLists.id(),myMedia.getId()));
                });
                this.getItems().add(tmp);
            });
        }






    }
}
