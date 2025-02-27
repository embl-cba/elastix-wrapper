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
package de.embl.cba.elastix.wrapper;

import de.embl.cba.elastix.commandline.settings.Settings;
import de.embl.cba.elastix.utils.Utils;
import de.embl.cba.metaimage_io.MetaImage_Writer;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.Duplicator;

import java.io.File;
import java.util.ArrayList;

public class StagingManager {

    public static final String STAGING_FILE_TYPE = "mhd";
    public static final String ELASTIX_FIXED_IMAGE_NAME = "fixed";
    public static final String ELASTIX_MOVING_IMAGE_NAME = "moving";
    public static final String ELASTIX_FIXED_MASK_IMAGE_NAME = "fixedMask";
    public static final String ELASTIX_MOVING_MASK_IMAGE_NAME = "movingMask";

    public static final String MHD = ".mhd";
    public static final String RAW = ".raw";

    public static final String ELASTIX_OUTPUT_FILENAME = "result.0";
    public static final String TRANSFORMIX_INPUT_FILENAME = "to_be_transformed";
    public static final String TRANSFORMIX_OUTPUT_FILENAME = "result";

    private Settings settings;
    private ArrayList< String > elastixTmpFilenames;

    public StagingManager ( Settings settings ) {
        this.settings = settings;
    }

    public void createOrEmptyWorkingDir()
    {
        settings.logService.info( "Temporary directory is: " + settings.tmpDir );

        setElastixTmpFilenames();

        File directory = new File( settings.tmpDir );

        if (! directory.exists() )
            directory.mkdir();
        else
            for ( String filename : elastixTmpFilenames )
            {
                final File file = new File( settings.tmpDir, filename );
                if ( file.exists() )
                    file.delete();
            }
    }

    private void setElastixTmpFilenames()
    {
        final ArrayList< String > elastixTmpFilenameStumps = new ArrayList<>();
        elastixTmpFilenameStumps.add( ELASTIX_FIXED_IMAGE_NAME );
        elastixTmpFilenameStumps.add( ELASTIX_MOVING_IMAGE_NAME );
        elastixTmpFilenameStumps.add( ELASTIX_MOVING_MASK_IMAGE_NAME );
        elastixTmpFilenameStumps.add( ELASTIX_FIXED_MASK_IMAGE_NAME );
        elastixTmpFilenameStumps.add( ELASTIX_OUTPUT_FILENAME  );
        elastixTmpFilenameStumps.add( TRANSFORMIX_OUTPUT_FILENAME );
        elastixTmpFilenameStumps.add( TRANSFORMIX_INPUT_FILENAME );

        elastixTmpFilenames = new ArrayList<>();
        for ( String filenameStump : elastixTmpFilenameStumps )
            addTmpImage( filenameStump );

        for ( int c = 0; c < 10; c++ )
            for ( String filenameStump : elastixTmpFilenameStumps )
                addTmpImage( getChannelFilename( filenameStump, c ) );
    }

    private void addTmpImage( String filename )
    {
        elastixTmpFilenames.add( filename + MHD );
        elastixTmpFilenames.add( filename + RAW );
    }

    public ArrayList< String > stageImageAsMhd( String imagePath, String filename )
    {
        ImagePlus imp = openImage( imagePath );

        int nChannels = imp.getNChannels();

        if ( nChannels > 1 )
        {
            return stageMultiChannelImagePlusAsMhd( imp, filename );
        }
        else
        {
            ArrayList< String > filenames = new ArrayList<>();
            filenames.add( stageImagePlusAsMhd( imp, filename ) );
            return filenames;
        }
    }

    private String stageImagePlusAsMhd( ImagePlus imp, String filename )
    {
        if ( filename.contains( ELASTIX_FIXED_MASK_IMAGE_NAME )
                || filename.contains( ELASTIX_MOVING_MASK_IMAGE_NAME ) )
            Utils.convertToMask( imp, 0.1F );

        MetaImage_Writer writer = new MetaImage_Writer();
        String filenameWithExtension = filename + MHD;
        settings.logService.info( "Staging image as mhd: " + filenameWithExtension );
        writer.save( imp, settings.tmpDir, filenameWithExtension );
        return getPath( filenameWithExtension );
    }

    private ArrayList< String > stageMultiChannelImagePlusAsMhd( ImagePlus imp, String filename )
    {
        ArrayList< String > filePaths = new ArrayList<>( );

        for ( int channelIndex = 0; channelIndex < imp.getNChannels(); ++channelIndex )
        {
            ImagePlus channelImage = getChannel( imp, channelIndex );

            filePaths.add(
                    stageImagePlusAsMhd(
                            channelImage, getChannelFilename( filename, channelIndex ) ) );
        }

        return filePaths;
    }

    private String getChannelFilename( String filename, int channelIndex )
    {
        return filename + "-C" + channelIndex;
    }

    private ImagePlus getChannel( ImagePlus imp, int channel )
    {
        Duplicator duplicator = new Duplicator();

        return duplicator.run(
                imp,
                channel + 1,
                channel + 1,
                1,
                imp.getNSlices(),
                1,
                1 );
    }

    private boolean checkChannelNumber( int nChannelsFixedImage, int nChannelsMovingImage )
    {

        if ( nChannelsFixedImage != nChannelsMovingImage )
        {
            Utils.logErrorAndExit( settings, "Number of channels " +
                    "in fixed and moving image do not match." );
            return false;
        }
        return true;
    }

    public ImagePlus openImage ( String imagePath ) {
        ImagePlus imp = IJ.openImage( imagePath );

        if ( imp == null )
        {
            System.err.println( "[ERROR] The image could not be loaded: "
                    + imagePath );
            if ( settings.headless )
                System.exit( 1 );
        }

        return imp;
    }

    public String getDefaultParameterFilePath()
    {
        return getPath( "elastix_parameters.txt" );
    }

    public String getDefaultTransformationFilePath()
    {
        return getPath( "TransformParameters.0.txt" );
    }

    public String getPath( String fileName )
    {
        return settings.tmpDir + File.separator + fileName;
    }
}
