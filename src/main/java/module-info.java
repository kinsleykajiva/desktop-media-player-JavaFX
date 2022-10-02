module desktop.media.player {

    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.jfoenix;
    requires de.jensd.fx.glyphs.fontawesome;
    requires eventbus.java;
    requires MaterialFX;
    requires FXTrayIcon;
    requires org.json;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires java.net.http;
    requires org.jetbrains.annotations;
    requires java.logging;
    requires com.fasterxml.jackson.databind;
    requires org.controlsfx.controls;
    requires org.apache.commons.lang3;
    requires animated;
    requires AnimateFX;
    requires javafx.swing;
    requires java.sql;
    requires mp3agic;

    opens africa.jopen to javafx.fxml, javafx.graphics;
    opens africa.jopen.controllers to javafx.fxml, javafx.graphics;
    // opens desktop.media.player to javafx.base,javafx.fxml, javafx.graphics;
    opens africa.jopen.models to javafx.base ,javafx.fxml, javafx.graphics;
   // opens africa.jopen.utils to javafx.fxml, javafx.graphics;
    /*exports africa.jopen.events;
    exports africa.jopen.models;*/
}
