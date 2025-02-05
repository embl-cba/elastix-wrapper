/*-
 * #%L
 * Fiji distribution of ImageJ for the life sciences.
 * %%
 * Copyright (C) 2017 - 2025 EMBL
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package de.embl.cba.elastix.wrapper.transformix;

import de.embl.cba.elastix.commandline.settings.Settings;

import java.io.File;
import java.util.ArrayList;

public class TransformixWrapperSettings extends Settings {

    public enum OutputModality
    {
        Show_images,
        Save_as_tiff,
        Save_as_bdv
    }

    public static final String OUTPUT_MODALITY_SHOW_IMAGES
            = "Show images";
    public static final String OUTPUT_MODALITY_SAVE_AS_TIFF
            = "Save as Tiff";
    public static final String OUTPUT_MODALITY_SAVE_AS_BDV
            = "Save as BigDataViewer .xml/.h5";

    public TransformixWrapperSettings() {}

    public String transformationFilePath;

    // before staging
    public String movingImageFilePath = "";
    // after staging - in order of channels
    public ArrayList<String> stagedMovingImageFilePaths;

    public OutputModality outputModality;
    public File outputFile;
}
