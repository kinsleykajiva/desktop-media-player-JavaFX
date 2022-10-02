package africa.jopen.utils;

import java.io.File;
import java.nio.file.Paths;

public class SystemEnvironment {
    public static final String OS = System.getProperty("os.name").toLowerCase();
    public static boolean isLinux() {
        return OS.contains("linux");
    }
    public static boolean isWindowsXP() {
        return OS.contains("windows xp");
    }
    public static boolean isWindows() {
        return OS.contains("windows");
    }
    public static boolean isMac() {
        return OS.contains("mac");
    }
    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }
    public static File getUserHome() {
        return new File(getUserHomePath());
    }
    public static String getUserDataHomePath() {
        if(isWindowsXP())
            return System.getenv("APPDATA");
        if(isWindows())
            return System.getenv("LOCALAPPDATA");
        if(isLinux())
            return Paths.get(getUserHomePath(), ".local/share").toString();

        return getUserHomePath();
    }
    public static String getUserConfigHomePath() {
        if(isLinux())
            return Paths.get(getUserHomePath(), ".config").toString();

        return getUserHomePath();
    }

}
