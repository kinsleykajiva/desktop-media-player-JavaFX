/* SPDX-License-Identifier: MIT */
package africa.jopen.utils;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public final class Utils {


    public static List<File>  foldersList = new ArrayList<>();

    public static WritableImage copyImage(Image source) {
        int height = (int) source.getHeight();
        int width = (int) source.getWidth();
        var reader = source.getPixelReader();
        var target = new WritableImage(width, height);
        var writer = target.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x, y, color);
            }
        }
        return target;
    }

    public static Color getDominantColor(Image image, double opacity) {
        int[] dominant = ColorThief.getColor(SwingFXUtils.fromFXImage(image, null));
        if (dominant == null || dominant.length != 3) { return Color.TRANSPARENT; }
        return Color.rgb(dominant[0], dominant[1], dominant[2], opacity);
    }

    public static String formatDuration(Duration duration) {
        long seconds = (long) duration.toSeconds();
        return seconds < 3600 ?
                String.format("%02d:%02d", (seconds % 3600) / 60, seconds % 60) :
                String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    /**
     * Run this Runnable in the JavaFX Application Thread. This method can be
     * called whether or not the current thread is the JavaFX Application
     * Thread.
     *
     * @param runnable The code to be executed in the JavaFX Application Thread.
     */
    public static void invoke (Runnable runnable) {
        if (isNull(runnable)) {
            return;
        }

        try {
            if (Platform.isFxApplicationThread()) {
                runnable.run();
            } else {
                Platform.runLater(runnable);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Force list refresh on.
     *
     * @param <T> the generic type
     * @param lsv the lsv
     */
    public static <T> void forceListRefreshOn (ListView<T> lsv) {
        ObservableList<T> items = lsv.getItems();
        lsv.setItems(null);
        lsv.setItems(items);
    }

    /**
     * Force table view refresh on.
     *
     * @param <T> the generic type
     * @param tbv the tbv
     */
    public static <T> void forceTableViewRefreshOn (TableView<T> tbv) {
        ObservableList<T> items = tbv.getItems();
        tbv.setItems(null);
        tbv.setItems(items);
    }
}
