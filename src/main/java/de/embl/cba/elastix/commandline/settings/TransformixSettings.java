package de.embl.cba.elastix.commandline.settings;

import de.embl.cba.elastix.wrapper.transformix.TransformixWrapperSettings;

public class TransformixSettings extends Settings {
    public String transformationFilePath;
    public String movingImageFilePath;

    public TransformixSettings() {}

    public TransformixSettings( TransformixWrapperSettings settings, int movingFileIndex ) {
        logService = settings.logService;
        elastixDirectory = settings.elastixDirectory;
        tmpDir = settings.tmpDir;
        numWorkers = settings.numWorkers;
        headless = settings.headless;
        transformationFilePath = settings.transformationFilePath;
        movingImageFilePath = settings.stagedMovingImageFilePaths.get( movingFileIndex );
    }
}
