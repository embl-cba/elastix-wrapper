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
package de.embl.cba.elastix.commandline;

import de.embl.cba.elastix.commandline.settings.ElastixSettings;
import de.embl.cba.elastix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ElastixCaller {

    public static final String ELASTIX = "elastix";
    public static final String FIXED = "f";
    public static final String MOVING = "m";

    ElastixSettings settings;
    String executableShellScript;

    public ElastixCaller( ElastixSettings settings ) {
        this.settings = settings;
        executableShellScript = new ExecutableShellScriptCreator( ELASTIX, settings ).createExecutableShellScript();
    }

    public void callElastix()
    {
        settings.logService.info( "Running elastix... (please wait)" );

        List< String > args = createElastixCallArgs();

        Utils.executeCommand( args, settings.logService );

        settings.logService.info( "...done!" );
    }

    private List< String > createElastixCallArgs( )
    {
        List<String> args = new ArrayList<>();
        args.add( executableShellScript );
        args.add( "-out" );
        args.add( settings.tmpDir );

        addImagesAndMasksToArguments( args );

        args.add( "-p" );
        args.add( settings.parameterFilePath );
        args.add( "-threads" );
        args.add( "" + settings.numWorkers );

        if ( settings.initialTransformationFilePath != null && !settings.initialTransformationFilePath.equals( "" ) )
        {
            args.add( "-t0" );
            args.add( settings.initialTransformationFilePath );
        }

        return args;
    }

    private void addImagesAndMasksToArguments( List< String > args )
    {
        addImagesToArguments( args, FIXED, settings.fixedImageFilePaths );

        addImagesToArguments( args, MOVING, settings.movingImageFilePaths );

        if ( settings.fixedMaskFilePaths != null )
            addImagesToArguments( args, "fMask", settings.fixedMaskFilePaths );

        if ( settings.movingMaskFilePaths != null )
            addImagesToArguments( args, "mMask", settings.movingMaskFilePaths );
    }

    private void addImagesToArguments( List< String > args,
                                       String fixedOrMoving,
                                       ArrayList< String > filePaths )
    {
        int elastixChannelIndex = 0;
        for ( String filePath : filePaths ) {
            if (filePaths.size() == 1)
                args.add("-" + fixedOrMoving);
            else
                args.add("-" + fixedOrMoving + elastixChannelIndex);

            args.add(filePath);

            elastixChannelIndex++;
        }
    }



}
