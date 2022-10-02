package africa.jopen.utils;


import africa.jopen.controllers.MainController;
import africa.jopen.models.MyMedia;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.util.Timer;
import java.util.TimerTask;

public class Player {

    private int skipMilliseconds = 10000;
    private Timer timer;
    private TimerTask timerTask;

    private Double volume = 100.0 ;

    private TimerTask task;
    private boolean runningTimer;
    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean loopMode;
    private double unmuteVolumeValue=100;
    private double rate=1;

    private SimpleDoubleProperty currentMediaTime = new SimpleDoubleProperty(0);
    private SimpleDoubleProperty endMediaTime = new SimpleDoubleProperty(0);
    private SimpleStringProperty artistName = new SimpleStringProperty();
    private SimpleStringProperty mediaName = new SimpleStringProperty();
    private SimpleBooleanProperty mediaLoaded = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isRunning = new SimpleBooleanProperty(false);

    private static Player instance = null;

    private Player() {    }
    public static Player getInstance(){
        if (instance==null) {
            instance = new Player();
        }
        return instance;
    }
    private Label imageArtLabel;
    public void setRate(double rate) {
        this.rate = rate;
    }
    public double getUnmuteVolumeValue() {
        return unmuteVolumeValue;
    }
    public void setUnmuteVolumeValue(double unmuteVolumeValue) {
        this.unmuteVolumeValue = unmuteVolumeValue;
    }

    public boolean isMediaLoaded() {
        return mediaLoaded.get();
    }
    public void setMediaLoaded(boolean mediaLoaded) { this.mediaLoaded.set(mediaLoaded);}
    public SimpleBooleanProperty mediaLoadedProperty() {
        return mediaLoaded;
    }

    public void setMediaName(String mediaName) {
        this.mediaName.set(mediaName);
    }
    public String getMediaName() {
        return mediaName.get();
    }
    public SimpleStringProperty mediaNameProperty() {
        return mediaName;
    }

    public SimpleBooleanProperty isRunningProperty() {
        return isRunning;
    }
    public boolean getIsRunning() { return isRunning.get(); }

    public double getCurrentMediaTime() { return currentMediaTime.get(); }
    public void setCurrentMediaTime(double currentMediaTime) { this.currentMediaTime.set(currentMediaTime); }
    public SimpleDoubleProperty currentMediaTimeProperty() { return currentMediaTime; }

    public void setEndMediaTime(double endMediaTime) { this.endMediaTime.set(endMediaTime); }
    public double getEndMediaTime() { return endMediaTime.get(); }
    public SimpleDoubleProperty endMediaTimeProperty() { return endMediaTime; }

    public String getArtistName() { return artistName.get(); }
    public SimpleStringProperty artistNameProperty() { return artistName; }
    public void setArtistName(String artistName) { this.artistName.set(artistName); }



    public void setImageArtLabel(Label imageArtLabel) {
        this.imageArtLabel = imageArtLabel;
    }

    public boolean isLoopMode() { return loopMode; }
    public void setLoopMode(boolean loopMode) { this.loopMode = loopMode; }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    private MyMedia currentMedia ;

    public void createMedia(MyMedia myMedia){
        currentMedia = myMedia;
        try {
            media = new Media(myMedia.getPath());
            mediaPlayer = new MediaPlayer(media);

            Utils.invoke(()->{
                mediaName.set(myMedia.getTitle());
                artistName.set(myMedia.getArtist());
                if(myMedia.getImageUrl() != null) {
                    var img = new ImageView(new Image(new ByteArrayInputStream(myMedia.getImageUrl())));
                    img.setFitWidth(46);
                    img.setFitHeight(44);
                    this.imageArtLabel.setGraphic(img);
                }else{
                    var res = MainController.class.getResource("/images/audios@32-px.png");
                    var img = new ImageView(new Image(String.valueOf(res)));
                    img.setFitWidth(46);
                    img.setFitHeight(44);
                    this.imageArtLabel.setGraphic(img);
                }
            });
            playMedia();
        } catch (MediaException mediaException) {
            mediaException.printStackTrace();
        }
    }
    public void cancelTimer(){
        timer.cancel();
    }

    public void playMedia(){

        if(media != null){
            mediaLoaded.set(true);
            setMediaLoaded(true);
            mediaPlayer.setVolume(volume);
            mediaPlayer.setRate(rate);
            mediaPlayer.play();
            isRunning.set(true);
            for(int i = 0; i < mediaPlayer.getAudioEqualizer().getBands().size(); ++i)
                mediaPlayer.getAudioEqualizer().getBands().get(i).setGain(AudioEqualizer.getInstance().getPresetsValues().get(AudioEqualizer.getInstance().getCurrentPresetIndex())[i]);
            if(rate!=1){
                mediaPlayer.setRate(rate);
            }
            beginTimer();
        }
    }
    public void beginTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                   setCurrentMediaTime(mediaPlayer.getCurrentTime().toSeconds());
                    setEndMediaTime(mediaPlayer.getTotalDuration().toSeconds());
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask,0,1000);

    }
    public void pauseMedia(){
        if(media != null){
            mediaPlayer.pause();
            isRunning.set(false);


        }
    }
    public void resume() {
        if(media != null) {
            isRunning.set(true);
            mediaPlayer.play();
        }
    }
    public void changePosition(double position){
        mediaPlayer.seek(Duration.seconds(position));
    }

    public void tenSecondBack() {
        mediaPlayer.seek(Duration.millis((mediaPlayer.getCurrentTime().toMillis()-skipMilliseconds)));
    }

    public void tenSecondForward() {
        mediaPlayer.seek(Duration.millis((mediaPlayer.getCurrentTime().toMillis()+skipMilliseconds)));
    }

    public void stop(){
        mediaPlayer.dispose();
        mediaLoaded.set(false);
        currentMediaTime.set(0);
        endMediaTime.set(0);
        isRunning.set(false);
        mediaName.set("");
        artistName.set("");
        cancelTimer();
    }

    public void repeat() {
        if(!loopMode)
            loopMode = true;
        else
            loopMode = false;
    }

    public void setVolume(double v) {
        volume=v;
        mediaPlayer.setVolume(v);
    }


}
