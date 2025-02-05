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
package de.embl.cba.elastix.wrapper.elastix;

import bdv.util.*;
import de.embl.cba.elastix.commandline.ElastixCaller;
import de.embl.cba.elastix.commandline.settings.ElastixSettings;
import de.embl.cba.elastix.utils.Utils;
import de.embl.cba.elastix.wrapper.BdvManager;
import de.embl.cba.elastix.wrapper.StagingManager;
import de.embl.cba.elastix.wrapper.elastix.parameters.DefaultElastixParametersCreator;
import de.embl.cba.elastix.wrapper.elastix.parameters.ElastixParameters;
import de.embl.cba.elastix.wrapper.transformix.TransformixWrapper;
import de.embl.cba.elastix.wrapper.transformix.TransformixWrapperSettings.OutputModality;
import ij.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static de.embl.cba.elastix.wrapper.StagingManager.*;

public class ElastixWrapper
{
    private ElastixWrapperSettings settings;
    private StagingManager stagingManager;

    public ElastixWrapper( ElastixWrapperSettings settings )
    {
        this.settings = settings;
        this.stagingManager = new StagingManager( settings );
    }

    public void runElastix()
    {
        processSettings();
        stagingManager.createOrEmptyWorkingDir();

        if (!stageImages()) {
            Utils.logErrorAndExit(settings, "There was an issue staging the images.\n " +
                    "Maybe the temporary working directory could not be generated.");
            return;
        }

        setMovingImageParameters();
        createElastixParameterFile();
        ElastixSettings elastixSettings = new ElastixSettings( settings );
        new ElastixCaller( elastixSettings ).callElastix();
    }

    private void processSettings()
    {
        if ( ! settings.elastixDirectory.endsWith( File.separator ) )
            settings.elastixDirectory += File.separator;

        if ( ! settings.tmpDir.endsWith( File.separator ) )
            settings.tmpDir += File.separator;
    }

    private boolean stageImages()
    {
        settings.stagedFixedImageFilePaths = stagingManager.stageImageAsMhd(
                settings.fixedImageFilePath,
                ELASTIX_FIXED_IMAGE_NAME );

        settings.stagedMovingImageFilePaths = stagingManager.stageImageAsMhd(
                settings.movingImageFilePath,
                ELASTIX_MOVING_IMAGE_NAME );

        if ( ! settings.fixedMaskPath.equals( "" ) )
            settings.stagedFixedMaskFilePaths = stagingManager.stageImageAsMhd(
                    settings.fixedMaskPath,
                    ELASTIX_FIXED_MASK_IMAGE_NAME );

        if ( ! settings.movingMaskPath.equals( "" ) )
            settings.stagedMovingMaskFilePaths = stagingManager.stageImageAsMhd(
                    settings.movingMaskPath,
                    ELASTIX_MOVING_MASK_IMAGE_NAME );

        setFixedToMovingChannel();

        return true;
    }

    private void setFixedToMovingChannel() {
        if ( settings.fixedToMovingChannel.size() == 0 )
        {
            // use all channels for registration
            for ( int c = 0; c < settings.stagedFixedImageFilePaths.size(); c++ )
                settings.fixedToMovingChannel.put( c, c );
        }
    }

    private void setMovingImageParameters() {
        ImagePlus imp = stagingManager.openImage( settings.movingImageFilePath );
        settings.movingImageBitDepth = imp.getBitDepth();
    }

    private void createElastixParameterFile()
    {
        settings.parameterFilePath = stagingManager.getDefaultParameterFilePath();
        System.out.println( "Parameter list type: " + settings.elastixParametersStyle );
        ElastixParameters parameters =
                new DefaultElastixParametersCreator( settings ).getElastixParameters( settings.elastixParametersStyle );

        if ( parameters == null ) {
            Utils.logErrorAndExit( settings, "Parameter file could not be created - image bit depth might not be supported" );
        }

        parameters.writeParameterFile( settings.parameterFilePath );
    }

    /**
     * Shows the fixed, moving and transformed moving images
     * in BigDataViewer.
     *
     * @return {@code Bdv} BigDataViewer handle, enabling, e.g., bdv.close()
     */
    public Bdv reviewResults()
    {
        TransformixWrapper transformixWrapper = createTransformixWrapper( OutputModality.Save_as_tiff );
        transformixWrapper.transformImagesAndHandleOutput();

        BdvManager bdvManager = new BdvManager();
        showFixedImagesInBdv( bdvManager );
        showMovingImagesInBdv( bdvManager );
        return showTransformedImagesInBdv( bdvManager, transformixWrapper );
    }

    public void reviewResultsInImageJ()
    {
        TransformixWrapper transformixWrapper = createTransformixWrapper( OutputModality.Show_images );
        showInputImagePlus();
        transformixWrapper.transformImagesAndHandleOutput();
    }

    public void createTransformedImagesAndSaveAsTiff()
    {
        TransformixWrapper transformixWrapper = createTransformixWrapper( OutputModality.Save_as_tiff );
        transformixWrapper.transformImagesAndHandleOutput();
    }

    private TransformixWrapper createTransformixWrapper( OutputModality outputModality ) {
        settings.outputModality = outputModality;
        settings.transformationFilePath = stagingManager.getDefaultTransformationFilePath();
        settings.outputFile = new File( stagingManager.getPath( "transformed" ) );
        return new TransformixWrapper( settings );
    }

    private Bdv showMovingImagesInBdv( BdvManager bdvManager )
    {
        Bdv bdv = null;
        for ( int index : settings.fixedToMovingChannel.values() )
        {
            String baseName = new File( settings.stagedMovingImageFilePaths.get(index) ).getName();
            bdv = bdvManager.showMetaImageInBdv( settings.tmpDir, baseName );
        }
        return bdv;
    }

    private Bdv showFixedImagesInBdv( BdvManager bdvManager )
    {
        Bdv bdv = null;
        for ( int index : settings.fixedToMovingChannel.keySet() )
        {
            String baseName = new File( settings.stagedFixedImageFilePaths.get(index) ).getName();
            bdv = bdvManager.showMetaImageInBdv( settings.tmpDir, baseName );
        }
        return bdv;
    }

    private Bdv showTransformedImagesInBdv( BdvManager bdvManager, TransformixWrapper transformixWrapper )
    {
        Bdv bdv = null;
        for ( int index : settings.fixedToMovingChannel.values() )
        {
            bdv = transformixWrapper.showTransformedImage( bdvManager, index );
        }
        return bdv;
    }

    private BdvStackSource showFixedInBdv( BdvManager bdvManager )
    {
        final ImagePlus templateImp = IJ.openImage( settings.fixedImageFilePath );
        return bdvManager.showImagePlusInBdv( templateImp );
    }

    public void showTransformationFile()
    {
        IJ.open( stagingManager.getDefaultTransformationFilePath() );
    }

    public void saveTransformationFile()
    {
        final File transformation =
                new File( stagingManager.getDefaultTransformationFilePath() );

        File copied = new File( settings.transformationOutputFilePath );

        try
        {
            FileUtils.copyFile( transformation, copied);
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void showInputImagePlus( )
    {
        ImagePlus fixed;

        fixed = IJ.openImage( settings.fixedImageFilePath );

        fixed.show();

        fixed.setTitle( "fixed" );

        // TODO: The macro recording does not work when using IJ.run(..) inside the plugin
        // if ( fixed.getNChannels() > 1 ) IJ.run("Split Channels" );
    }
}
