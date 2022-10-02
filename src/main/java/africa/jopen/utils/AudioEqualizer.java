package africa.jopen.utils;

import africa.jopen.database.SqliteManager;

import java.util.ArrayList;
import java.util.Arrays;

public class AudioEqualizer {
    //SINGLETON
    private static AudioEqualizer instance = null;
    private AudioEqualizer() {
        currentPresetIndex = 0;
        presetsNames = new ArrayList<>(Arrays.asList("Flat", "Acoustic", "Electronic", "Pop", "Rock", "Bass Boosted", "Custom"));
        presetsValues = new ArrayList<>(Arrays.asList(  new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{5, 5, 4, 1, 2, 2, 3, 4, 3, 2},
                new int[]{4, 4, 1, 0, -2, 2, 1, 1, 4, 5},
                new int[]{-2, -1, 0, 2, 4, 4, 2, 0, -1, -2},
                new int[]{5, 4, 3, 1, 0, -1, 1, 3, 4, 5},
                new int[]{6, 5, 4, 3, 1, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

    }
    public static AudioEqualizer getInstance(){
        if (instance==null)
            instance = new AudioEqualizer();
        return instance;
    }
    //END SINGLETON

    private int currentPresetIndex;
    private ArrayList<String> presetsNames;
    private ArrayList<int[]> presetsValues;

    public int getCurrentPresetIndex() {
        return currentPresetIndex;
    }

    public void setCurrentPresetIndex(int currentPresetIndex) {
        this.currentPresetIndex = currentPresetIndex;
    }

    public ArrayList<String> getPresetsNames() {
        return presetsNames;
    }

    public void setPresetsNames(ArrayList<String> presetsNames) {
        this.presetsNames = presetsNames;
    }

    public ArrayList<int[]> getPresetsValues() {
        return presetsValues;
    }

    public void setPresetsValues(ArrayList<int[]> presetsValues) {
        this.presetsValues = presetsValues;
    }

    public void saveCustomPreset(int[] values){
        SqliteManager.getInstance().setEqualizer(values);
        presetsValues.remove(presetsValues.size()-1);
        presetsValues.add(values);
    }

}
