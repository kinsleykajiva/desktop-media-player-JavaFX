package africa.jopen;


import africa.jopen.database.SqliteManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;



public class BaseApplication extends Application  {
    private double offsetX,offsetY;
    private Scene scene;
    public static File outputFile;



    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage)throws Exception {
        SqliteManager.getInstance().createTableMyMedia();
       // SqliteManager.getInstance().createTableRecentMedia();
        SqliteManager.getInstance().createTablePlaylist();
        SqliteManager.getInstance().createTablePlaylistsMedia();
       // SqliteManager.getInstance().createTableMediaMyMediaPlaylist();
        SqliteManager.getInstance().createTableEqualizer();

        SqliteManager.getInstance().initEqualizer();
        SqliteManager.getInstance().getEqualizer();

        Parent root = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
        scene = new Scene(root, null);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setFullScreenExitHint("");
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/app-icon.png").toExternalForm()));

        primaryStage.getScene().getRoot().setEffect(new DropShadow());
        primaryStage.getScene().setFill(Color.TRANSPARENT);
        scene.setOnMousePressed(event -> {
            offsetX = event.getSceneX();
            offsetY = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX()-offsetX);
            primaryStage.setY(event.getScreenY()-offsetY);
        });
        primaryStage.show();
    }
}
