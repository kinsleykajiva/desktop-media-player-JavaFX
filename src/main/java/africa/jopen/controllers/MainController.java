package africa.jopen.controllers;

import africa.jopen.database.SqliteManager;
import africa.jopen.models.MyMedia;
import africa.jopen.utils.ContextMenuHandler;
import africa.jopen.utils.Utils;
import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideOutLeft;
import com.jfoenix.controls.JFXSlider;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


import static africa.jopen.utils.Player.getInstance;
import static java.lang.String.format;


public class MainController {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public ObservableList<MyMedia> temp = FXCollections.observableArrayList();
    @FXML
    public TableView<MyMedia> tableViewPlaylist;

    @FXML
    public TableColumn<MyMedia, String> album1,artist1,genre1,length1,title1,year1;


    @FXML
    private AnchorPane drawerPane, mainAnchorPanee;
    @FXML
    public BorderPane sliderPane;
    @FXML
    public Button btnAddPlayList;

    @FXML
    public ImageView imgBtnClose, imgBtnFullScreen ,imgAddFile ,imgAddFolder;

    @FXML
    public StackPane stackpaneInfrontView;
    @FXML
    public ImageView imgBtnMinimize;
    @FXML
    public HBox hboxWindowsControlls ,hboxSelectFiles;
    @FXML
    public VBox vboxRecents, vboxSettings, vboxVideos, vboxNavBar, vboxPlayLists, vboxAudios;

    @FXML
    public ListView<File> listView;
    @FXML
    public Label labSoundIcon,labAlbum ,labMusicName,labTotalTime ,labPlayedTime ,labMusicSinger ,labPlay,labPlayLast,labPlayNext ,labPlayModeIcon ,labPlayListCount /*loop counter*/;
    @FXML
    public JFXSlider mediaSlider;
    @FXML
    public JFXSlider volumeSlider;
    private int loopingCounts = 0;
    @FXML
    public ProgressBar progressBarProgress;
    private Timeline showTimeLineAnimation, hideTimeLineAnimation;
    private ContextMenuHandler contextMenuHandler;
    private Runnable runnableLoadCacchedMusic = ()->{
        SqliteManager.getInstance().readMyMedia("");
        Utils.invoke(()->{

            if(!SqliteManager.getInstance().getMusicLibrary().isEmpty()){
                onDoneImportFiles();
                tableViewPlaylist.setItems(SqliteManager.getInstance().getMusicLibrary());
            }else{

                drawerPane.setVisible(false);
                tableViewPlaylist.setVisible(false);
                stackpaneInfrontView.setVisible(false);
            }
        });
    };
    private void setMediaSlider() {
        mediaSlider.setValue(getInstance().getCurrentMediaTime());
    }

    private void playpause() {
        if(getInstance().getIsRunning()){
            getInstance().pauseMedia();
        }else{
            getInstance().resume();
        }

        switchPlayPauseIcon();
    }
    private int lastSelctedPosition = 0;
    private void next() {
        lastSelctedPosition++;
        if(lastSelctedPosition > SqliteManager.getInstance().getMusicLibrary().size()) {
            lastSelctedPosition =0;
        }
        if(getInstance().isMediaLoaded()){
            getInstance().stop();
        }
        MyMedia myMedia=  SqliteManager.getInstance().getMusicLibrary().get(lastSelctedPosition);
        tableViewPlaylist.getSelectionModel().select(lastSelctedPosition);
        getInstance().createMedia(myMedia);
    }
    private void prev() {
        lastSelctedPosition--;
        if(lastSelctedPosition < 0) {
            lastSelctedPosition =0;
        }
        if(getInstance().isMediaLoaded()){
            getInstance().stop();
        }
        MyMedia myMedia=  SqliteManager.getInstance().getMusicLibrary().get(lastSelctedPosition);
        tableViewPlaylist.getSelectionModel().select(lastSelctedPosition);
        getInstance().createMedia(myMedia);
    }
    private void setMediaSliderEnd() {
        mediaSlider.setMax(getInstance().getEndMediaTime());
    }
    public void switchPlayPauseIcon() {
        if (getInstance().getIsRunning()) {
            var res = MainController.class.getResource("/images/pause@32-px.png");
            var img = new ImageView(new Image(String.valueOf(res)));
            labPlay.setGraphic(img);
        }else {
            var res = MainController.class.getResource("/images/play@32-px.png");
            var img = new ImageView(new Image(String.valueOf(res)));
            labPlay.setGraphic(img);
        }
    }

    private void execute (Runnable runnable) {
        try {
            executor.submit(runnable);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        title1.setCellValueFactory(new PropertyValueFactory<MyMedia,String>("title"));
        artist1.setCellValueFactory(new PropertyValueFactory<MyMedia,String>("artist"));
        album1.setCellValueFactory(new PropertyValueFactory<MyMedia,String>("album"));
        genre1.setCellValueFactory(new PropertyValueFactory<MyMedia,String>("genre"));
        year1.setCellValueFactory(new PropertyValueFactory<MyMedia,String>("year"));
        length1.setCellValueFactory(new PropertyValueFactory<MyMedia,String>("length"));

        showTimeLineAnimation = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(sliderPane.translateXProperty(), 0)));
        hideTimeLineAnimation = new Timeline(new KeyFrame(Duration.millis(300), new KeyValue(sliderPane.translateXProperty(), 300)));

        hideTimeLineAnimation.setOnFinished(event -> {
            stackpaneInfrontView.setVisible(false);
            drawerPane.setVisible(false);
        });

        // this is the default view or screen
        mainAnchorPanee.setStyle("-fx-background-image: null;-fx-background-color: rgba(47,46,54,0.89);");

        vboxPlayLists.setOnMouseClicked(e -> {
            if (drawerPane.isVisible()) {
                drawerPane.setVisible(false);
                stackpaneInfrontView.setVisible(false);
                showTimeLineAnimation.stop();
                hideTimeLineAnimation.play();
            } else {
                stackpaneInfrontView.setVisible(false);
                drawerPane.setVisible(true);
                hideTimeLineAnimation.stop();
                showTimeLineAnimation.play();
            }

        });
        imgAddFile.setOnMouseClicked(e -> {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter fileExtensions =
                    new FileChooser.ExtensionFilter(
                            "MP3 Files", "*.mp3", "*.wav", "*.flac");
            chooser.getExtensionFilters().add(fileExtensions);
            File f = chooser.showOpenDialog(currentStage());
        });
        imgAddFolder.setOnMouseClicked(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(currentStage());
            if(selectedDirectory != null){
                tableViewPlaylist.setVisible(true);
                hboxSelectFiles.setVisible(false);
                // save the folder files
               extractFilesFromFolder(selectedDirectory);

            }

        });

        getInstance().setImageArtLabel(labAlbum);
        labPlayModeIcon.setOnMouseClicked(e -> {

            loopingCounts++;
            labPlayListCount.setText("x"+loopingCounts);
            if(loopingCounts > 1){
                loopingCounts = 0;
            }
        });

        btnAddPlayList.setOnMouseClicked(e -> {
           // create playlst
            try{
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/views/new-playlist-view.fxml"));
            var scene = new Scene(loader.load(),400,240);
            var stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMinWidth(400);
            stage.setMinHeight(240);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {


                }
            });

            }catch(Exception exception){
                exception.printStackTrace();
            }
        });
        labPlay.setOnMouseClicked(e -> {
            playpause();
        });
        labPlayNext.setOnMouseClicked(e -> {
            next();
        });
        labPlayLast.setOnMouseClicked(e -> {
            prev() ;
        });

        imgBtnClose.setOnMouseClicked(e -> {
            Platform.exit();
            System.exit(0);
        });
        drawerPane.setOnMouseClicked(e -> {

            showTimeLineAnimation.stop();
            hideTimeLineAnimation.play();
        });

        imgBtnFullScreen.setOnMouseClicked(e -> {
            Stage stage = currentStage();
            stage.setFullScreen(!stage.isFullScreen());
            imgBtnFullScreen.setImage(new Image(getClass().getResource(stage.isFullScreen() ? "/images/exit-full-screen@32-px.png" : "/images/fullscreen@32-px.png").toString()));
        });
        imgBtnMinimize.setOnMouseClicked(e -> {
            Stage stage = currentStage();
            stage.setIconified(true);
        });
        var slideInLeft = new SlideInLeft(vboxNavBar);
        slideInLeft.setOnFinished(ef -> {
            vboxNavBar.setVisible(false);
        });
        mainAnchorPanee.setOnMouseMoved(e -> {
            if (!vboxNavBar.isVisible()) {
                vboxNavBar.setVisible(true);
            }
            if (!hboxWindowsControlls.isVisible()) {
                hboxWindowsControlls.setVisible(true);
            }

        });
        var slideOutLeft = new SlideOutLeft(vboxNavBar);

        slideInLeft.setOnFinished(ef -> vboxNavBar.setVisible(true));


        mainAnchorPanee.setOnMouseExited(e -> {

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(ee -> {
                vboxNavBar.setVisible(false);
                hboxWindowsControlls.setVisible(false);
            });
            pause.play();

        });

        execute(runnableLoadCacchedMusic);

        tableViewPlaylist.setRowFactory(tableView -> {
            final TableRow<MyMedia> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && !event.getButton().equals(MouseButton.SECONDARY)) {
                    MyMedia myMedia = row.getItem();
                    if (event.getClickCount() == 2) {
                        if (getInstance().isMediaLoaded()) {
                            getInstance().stop();
                        }
                        lastSelctedPosition = row.getIndex();
                        getInstance().createMedia(myMedia);



                        // PlayQueue.getInstance().setCurrentMedia(row.getIndex());
                    }
                } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                    if (!row.isEmpty()) {
                        MyMedia myMedia = row.getItem();
                          contextMenuHandler = new ContextMenuHandler(myMedia,"","playqueue",row.getIndex());
                          row.setContextMenu(contextMenuHandler);
                           row.getContextMenu();
                    }
                }
            });

            return row;
        });
        initMediaListerns();
    }

    private void initMediaListerns() {
        double percentage = 100.0;
        volumeSlider.setValue(50);
        mediaSlider.setValue(0);

        volumeSlider.setStyle("-track-color: linear-gradient(to right, tertiarySelectionColor " + percentage + "%, white " + percentage + ("%);"));
        mediaSlider.valueChangingProperty().addListener((observableValue, aBoolean, t1) -> getInstance().changePosition(mediaSlider.getValue()));
        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (getInstance().getMediaPlayer().isMute())
                getInstance().getMediaPlayer().setMute(false);
            if (volumeSlider.getValue() == 0) labSoundIcon.setDisable(true);
            else if (volumeSlider.getValue() > 50) labSoundIcon.setDisable(true);
            else labSoundIcon.setDisable(true);
            getInstance().setVolume(volumeSlider.getValue() * 0.01);

            double percentage1 = 100.0 * (newValue.doubleValue() - volumeSlider.getMin()) / (volumeSlider.getMax() - volumeSlider.getMin());
            volumeSlider.setStyle("-track-color: linear-gradient(to right, tertiarySelectionColor " + percentage1 + "%, white " + percentage1 + ("%);"));
        });
        mediaSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            double curr = getInstance().getCurrentMediaTime();
            if (Math.abs(curr - newValue.doubleValue()) > 1) {
                getInstance().changePosition(newValue.doubleValue());
                getInstance().cancelTimer();
                labPlayedTime.setText(formatTime(newValue.doubleValue()));
                getInstance().beginTimer();
            }
            double percentage12 = 100.0 * (newValue.doubleValue() - mediaSlider.getMin()) / (mediaSlider.getMax() - mediaSlider.getMin());
            mediaSlider.setStyle("-track-color: linear-gradient(to right, tertiarySelectionColor " + percentage12 + "%, white " + percentage12 + ("%);"));
        });
        getInstance().isRunningProperty().addListener(observable -> {
            switchPlayPauseIcon();
            labPlay.setTooltip(new Tooltip("Pause"));
        });
        getInstance().endMediaTimeProperty().addListener(observable -> Utils.invoke(() -> {
            setMediaSliderEnd();
            labTotalTime .setText(formatTime(getInstance().getEndMediaTime()));
        }));
        getInstance().currentMediaTimeProperty().addListener(observable -> {
            Utils.invoke(() -> {
                setMediaSlider();
                labPlayedTime.setText(formatTime(getInstance().getCurrentMediaTime()));
                if ((getInstance().getCurrentMediaTime() == getInstance().getEndMediaTime()) && getInstance().getEndMediaTime()!=0.0) {
                    if(loopingCounts > 0){
                        next(); // auto play next song
                    }

                }
            });

        });

        getInstance().mediaNameProperty().addListener(observable -> Platform.runLater(() -> labMusicName.setText(getInstance().getMediaName())));
        getInstance().artistNameProperty().addListener(observable -> Platform.runLater(() -> labMusicSinger.setText(getInstance().getArtistName())));
        getInstance().isRunningProperty().addListener(observable -> formatTime(getInstance().getCurrentMediaTime()));
        getInstance().isRunningProperty().addListener(observable -> mainAnchorPanee.setOnKeyPressed(keyEvent->{

            /*if (key == KeyCode.SPACE && Player.getInstance().isMediaLoaded()) { //Space	Play/pause
                btnPlayPause.requestFocus();
                playPauseHandler();
            } else if (key == KeyCode.S) {//S	Stop
                Player.getInstance().stop();
            } else if (key == KeyCode.N) {//N	Next track
                next();
            } else if (key == KeyCode.P) {//P	Previous track
                previous();
            } else if (key == KeyCode.L) { //L	Normal/loop/repeat
                btnRepeat.fire();
            } else if (key == KeyCode.T) {//T	Shuffle
                btnShuffle.fire();
            } else if (key == KeyCode.M) {//M	Mute
                muteUnmuteHandler();
            } else if (KeyCombo.skipBack.match(keyEvent)) {//ALT + LEFT  Skip back 10s
                skipBack();
            } else if (KeyCombo.skipForward.match(keyEvent)) {//ALT + RIGHT  Skip forward 10s
                skipForward();
            } else if (KeyCombo.volumeUp.match(keyEvent)) {//CTRL + UP  Volume up 10%
                controlsBox.requestFocus();
                volumeChange(10);
            } else if (KeyCombo.volumeDown.match(keyEvent)) {//CTRL + DOWN  Volume down 10%
                controlsBox.requestFocus();
                volumeChange(-10);
            } else if (KeyCombo.quit.match(keyEvent)) {//CTRL + Q  Quit application
                SceneHandler.getInstance().exit();
            }*/
        }));
    }

    /*public void onTimeUpdate(Duration newTime) {
        if(player.isPlaying()) {
            double totalSecs = newTime.toSeconds();
            l_timeTick.setText(TimeUtils.parseSecondsToTime(totalSecs));

            double progress = totalSecs / Player.getInstance().getMediaPlayer().getTotalDuration().toSeconds() * 100;
            mediaSlider.setValue(progress);
        }
    }*/
    private void volumeChange(int offset) {
        if (volumeSlider.getValue() + offset < 0)
            volumeSlider.setValue(0);
        else if (volumeSlider.getValue() + offset > 100)
            volumeSlider.setValue(100);
        else
            volumeSlider.setValue(volumeSlider.getValue() + offset);
    }

    void onDoneImportFiles(){
        hboxSelectFiles.setVisible(false);
        tableViewPlaylist.setVisible(true);
        stackpaneInfrontView.setVisible(false);
        drawerPane.setVisible(false);
        drawerPane.setDisable(false);// to remove click event so that the ares wont close
    }

    private  void extractFilesFromFolder(File selectedDirectory) {

        stackpaneInfrontView.setVisible(true);
        drawerPane.setVisible(true);
        drawerPane.setDisable(true);// to remove click event so that the ares wont close
        stackpaneInfrontView.setStyle("-fx-background-color: rgba(0,0,0,0.68);");

// start on another thread
        var loadingCacheTask = new Task<Void>(){
            @Override
            protected void updateProgress(double workDone, double max) {
                super.updateProgress(workDone, max);
                Utils.invoke(()->{
                progressBarProgress.setProgress(workDone);

                });
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                onDoneImportFiles();
                tableViewPlaylist.setItems(temp);
            }

            @Override
            protected void failed() {
                super.failed();
                onDoneImportFiles();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                onDoneImportFiles();

            }

            @Override
            protected Void call()  {
                ArrayList<File> directoryList = new ArrayList<>();
                directoryList.add(selectedDirectory);
                List<File> myFileList = new ArrayList<>();
                AtomicInteger count = new AtomicInteger();
                while(!directoryList.isEmpty()) {
                    selectedDirectory.listFiles(pathname -> {
                        count.getAndIncrement();
                        updateProgress(count.get(), directoryList.size());
                        if (pathname.isDirectory()) {
                            directoryList.add(pathname);
                            return true;
                        }
                        if (pathname.getName().toLowerCase().endsWith(".mp3") || pathname.getName().toLowerCase().endsWith(".wav")) {
                            myFileList.add(pathname);
                        }
                        return false;
                    });
                    directoryList.remove(0);
                }
                myFileList.forEach(file -> {
                    MyMedia myMedia = new MyMedia(file);
                    try {
                        Mp3File mp3file = new Mp3File(file);
                        if (mp3file.hasId3v2Tag()) {
                            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                            System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
                            myMedia.setLength(formatTime(mp3file.getLengthInSeconds() ));
                            System.out.println("Bitrate: " + mp3file.getBitrate() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
//                            myMedia.setBitrate(mp3file.getBitrate() );
                            System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
//                            myMedia.setSampleRate(mp3file.getSampleRate() );
                            System.out.println("Track: " + id3v2Tag.getTrack());
//                            myMedia.setTrack(id3v2Tag.getTrack());
                            System.out.println("Artist: " + id3v2Tag.getArtist());
                            myMedia.setArtist(id3v2Tag.getArtist());
                            System.out.println("Title: " + id3v2Tag.getTitle());
                            if(id3v2Tag.getTitle() != null){
                            myMedia.setTitle(id3v2Tag.getTitle());}
                            System.out.println("Album: " + id3v2Tag.getAlbum());
                            myMedia.setAlbum(id3v2Tag.getAlbum());
                            System.out.println("Year: " + id3v2Tag.getYear());
                            myMedia.setYear(id3v2Tag.getYear());
                            System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
                            myMedia.setGenre( id3v2Tag.getGenreDescription() );
                            System.out.println("Comment: " + id3v2Tag.getComment());
//                            myMedia.setComment(id3v2Tag.getComment());
                            System.out.println("Lyrics: " + id3v2Tag.getLyrics());
//                            myMedia.setLyrics(id3v2Tag.getLyrics());
                            System.out.println("Composer: " + id3v2Tag.getComposer());
//                            myMedia.setComposer(id3v2Tag.getComposer());
                            System.out.println("Publisher: " + id3v2Tag.getPublisher());
//                            myMedia.setPublisher(id3v2Tag.getPublisher());
                            System.out.println("Original artist: " + id3v2Tag.getOriginalArtist());
//                            myMedia.setOriginalArtist(id3v2Tag.getOriginalArtist());
                            System.out.println("Album artist: " + id3v2Tag.getAlbumArtist());
//                            myMedia.setAlbumArtist(id3v2Tag.getAlbumArtist());
                            System.out.println("Copyright: " + id3v2Tag.getCopyright());
//                            myMedia.setCopyright(id3v2Tag.getCopyright());
                            System.out.println("URL: " + id3v2Tag.getUrl());
//                            myMedia.setURL(id3v2Tag.getUrl());
                            System.out.println("Encoder: " + id3v2Tag.getEncoder());
//                            myMedia.setEncoder(id3v2Tag.getEncoder());
                            byte[] albumImageData = id3v2Tag.getAlbumImage();
                            if (albumImageData != null) {
                                myMedia.setImageUrl(albumImageData);
                                System.out.println("Have album image data, length: " + albumImageData.length + " bytes");
                                System.out.println("Album image mime type: " + id3v2Tag.getAlbumImageMimeType());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SqliteManager.getInstance().addMedia(myMedia);
                    temp.add(myMedia);
                });

                return null;
            }
        };
        execute(loadingCacheTask);

    }



    public String formatTime(double timeDouble) {
        if (timeDouble > 0) {
            int hh = (int) (timeDouble / 3600);
            int mm = (int) ((timeDouble % 3600) / 60);
            int ss = (int) ((timeDouble % 3600) % 60);

            return format("%02d:%02d:%02d", hh, mm, ss);
        }
        return "00:00:00";
    }
    private Stage currentStage() {
        return (Stage) drawerPane.getScene().getWindow();
    }

}
