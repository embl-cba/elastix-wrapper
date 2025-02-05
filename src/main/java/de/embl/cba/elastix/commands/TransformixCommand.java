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
package de.embl.cba.elastix.commands;

import de.embl.cba.elastix.wrapper.transformix.TransformixWrapperSettings;
import de.embl.cba.elastix.wrapper.transformix.TransformixWrapperSettings.OutputModality;
import de.embl.cba.elastix.utils.Utils;
import de.embl.cba.elastix.wrapper.transformix.TransformixWrapper;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;

@Plugin(type = Command.class, menuPath = "Plugins>Registration>Elastix>Transformix" )
public class TransformixCommand implements Command
{

    @Parameter
    public LogService logService;

    @Parameter( label = "Elastix installation directory",
            style = "directory" )
    public File elastixDirectory;

    @Parameter( label = "Temporary directory for intermediate files", style = "directory" )
    public File tmpDir = new File( System.getProperty("java.io.tmpdir") );

    @Parameter( label = "Image" )
    public File inputImageFile;

    @Parameter( label = "Transformation" )
    public File transformationFile;

    @Parameter( label = "Output modality", choices = {
            TransformixWrapperSettings.OUTPUT_MODALITY_SHOW_IMAGES,
            TransformixWrapperSettings.OUTPUT_MODALITY_SAVE_AS_TIFF,
            TransformixWrapperSettings.OUTPUT_MODALITY_SAVE_AS_BDV
    } )
    public String outputModality;

    @Parameter( label = "Output file", style = "save", required = false )
    public File outputFile;

    @Parameter( label = "Number of threads" )
    int numThreads = 1;

    public void run()
    {
        runTransformix();
    }

    private void runTransformix()
    {
        TransformixWrapperSettings settings = getSettingsFromUI();
        TransformixWrapper transformixWrapper = new TransformixWrapper( settings );
        transformixWrapper.runTransformix();
    }

    private TransformixWrapperSettings getSettingsFromUI()
    {
        TransformixWrapperSettings settings = new TransformixWrapperSettings();
        settings.logService = logService;
        settings.elastixDirectory = elastixDirectory.toString();
        settings.tmpDir = tmpDir.toString();
        settings.movingImageFilePath = inputImageFile.toString();
        settings.transformationFilePath = transformationFile.toString();
        settings.numWorkers = numThreads;

        switch ( outputModality ) {
            case TransformixWrapperSettings.OUTPUT_MODALITY_SHOW_IMAGES:
                settings.outputModality = OutputModality.Show_images;
                break;
            case TransformixWrapperSettings.OUTPUT_MODALITY_SAVE_AS_TIFF:
                settings.outputModality = OutputModality.Save_as_tiff;
                break;
            case TransformixWrapperSettings.OUTPUT_MODALITY_SAVE_AS_BDV:
                settings.outputModality = OutputModality.Save_as_bdv;
                break;
        }

        settings.outputFile = outputFile;

        if ( !settings.outputModality.equals( OutputModality.Show_images ) )
            if ( outputFile == null )
                Utils.logErrorAndExit( settings,"Please specify an output file.");

        return settings;
    }
}
