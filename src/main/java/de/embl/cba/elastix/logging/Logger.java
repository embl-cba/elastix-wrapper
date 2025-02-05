package de.embl.cba.elastix.logging;

public interface Logger {

    void setShowDebug(boolean showDebug);

    boolean isShowDebug();

    void info(String message);

    void progress(String message, String progress);

    void error(String message);

    void warning(String message);

    void debug(String message);
}
