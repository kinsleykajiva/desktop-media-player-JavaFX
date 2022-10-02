package africa.jopen.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateNewPlaylist implements Initializable {

    private String previousName="";

    @FXML
    private Button ButtonImageChange;

    @FXML
    private Button btnSave;
    @FXML
    private Label labelErrore;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField textTitle;

    void onImageChange(ActionEvent event) {
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose the Cover");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image File","*.png","*.jpg");
            fileChooser.getExtensionFilters().add(extFilter);
            imageView.setImage(new Image(fileChooser.showOpenDialog(new Stage()).getPath()));
           // SubStageHandler.getInstance().setUpdated(true);

        }catch(Exception exception){
        }

    }


    void onSave(ActionEvent event) {
        // TODO: 04/07/2022 non far inserire caratteri speciali?
        String text = textTitle.getText().trim();
       /* if(findName(text)){
            int index= PlaylistCollection.getInstance().getPlaylistWidthName(previousName);
            PlaylistCollection.getInstance().getPlayListsCollections().get(index).setName(text);
            PlaylistCollection.getInstance().getPlayListsCollections().get(index).setImage(imageView.getImage().getUrl());
            DatabaseManager.getInstance().changePlaylist(text,previousName,imageView.getImage().getUrl());
            PlaylistCollection.getInstance().setUpdatePlaylist(true);
            PlaylistCollection.getInstance().getPlayListsCollections().get(index).setInitialized(true);
            ((Node)(event.getSource())).getScene().getWindow().hide();
        }*/

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

     //   textTitle.setText(SubStageHandler.getInstance().getPlaylistName());
        previousName=textTitle.getText();
//        imageView.setImage(new Image(PlaylistCollection.getInstance().getPlayListsCollections().get(PlaylistCollection.getInstance().returnPlaylist(previousName)).getImage()));
        textTitle.setFocusTraversable(false);
        btnSave.setDisable(false);

        imageView.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
            if(!ButtonImageChange.isVisible()){}
              //  setButtonChange(newValue);
        });

        ButtonImageChange.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
            if(ButtonImageChange.isVisible()){}
               // setButtonChange(newValue);
        });

        textTitle.textProperty().addListener(new ChangeListener<String>() {//che fa qui?
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length()>25) {
                    newValue=newValue.substring(0,newValue.length()-1);
                    textTitle.setText(newValue);
                }
                /*if(!findName(newValue.trim())){ error();}
                else {labelErrore.setVisible(false);}
                SubStageHandler.getInstance().setUpdated(true);*/

            }
        });

       // SceneHandler.getInstance().scaleTransition(btnSave);
    }
    private void error(){
        labelErrore.setText("Playlist name already in use");
        labelErrore.setVisible(true);
    }
    private boolean findName(String name){
        if(name.equals(""))
            return false;
       /* for(int pos=0;pos<PlaylistCollection.getInstance().getPlayListsCollections().size();pos++){
            if(PlaylistCollection.getInstance().getPlayListsCollections().get(pos).getName().equals(name)&&!previousName.equals(name)) {
                error();
                return false;
            }
        }*/
        return true;
    }

    private void setButtonChange(boolean visible){
        ButtonImageChange.setVisible(visible);
    }
}
