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
package de.embl.cba.elastix.commandline.settings;

import de.embl.cba.elastix.wrapper.elastix.ElastixWrapperSettings;

import java.util.ArrayList;

public class ElastixSettings extends Settings {

    public String parameterFilePath;
    public String initialTransformationFilePath;
    // if size >1 will do multi-image registration
    // Order of fixed, moving and masks must be the same
    public ArrayList<String> fixedImageFilePaths;
    public ArrayList<String> movingImageFilePaths;
    public ArrayList<String> fixedMaskFilePaths;
    public ArrayList<String> movingMaskFilePaths;

    public ElastixSettings() {}

    public ElastixSettings( ElastixWrapperSettings settings ) {
        logService = settings.logService;
        elastixDirectory = settings.elastixDirectory;
        tmpDir = settings.tmpDir;
        numWorkers = settings.numWorkers;
        headless = settings.headless;
        parameterFilePath = settings.parameterFilePath;
        initialTransformationFilePath = settings.initialTransformationFilePath;

        fixedImageFilePaths = new ArrayList<>();
        fixedMaskFilePaths = new ArrayList<>();
        movingImageFilePaths = new ArrayList<>();
        movingMaskFilePaths = new ArrayList<>();

        for ( int fixedChannelIndex : settings.fixedToMovingChannel.keySet() ) {
            int movingChannelIndex = settings.fixedToMovingChannel.get(fixedChannelIndex);
            fixedImageFilePaths.add( settings.stagedFixedImageFilePaths.get(fixedChannelIndex) );
            if (settings.stagedFixedMaskFilePaths != null ) {
                fixedMaskFilePaths.add(settings.stagedFixedMaskFilePaths.get(fixedChannelIndex));
            }
            movingImageFilePaths.add( settings.stagedMovingImageFilePaths.get(movingChannelIndex) );
            if ( settings.stagedMovingMaskFilePaths != null ) {
                movingMaskFilePaths.add(settings.stagedMovingMaskFilePaths.get(movingChannelIndex));
            }
        }
    }
}
