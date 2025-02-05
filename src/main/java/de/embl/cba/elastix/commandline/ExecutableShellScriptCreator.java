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

import de.embl.cba.elastix.commandline.settings.Settings;
import de.embl.cba.elastix.utils.Utils;
import ij.IJ;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static de.embl.cba.elastix.utils.Utils.saveStringToFile;
import static org.scijava.util.PlatformUtils.*;

public class ExecutableShellScriptCreator {

    String elastixOrTransformix;
    Settings settings;

    public ExecutableShellScriptCreator( String elastixOrTransformix, Settings settings) {
        this.elastixOrTransformix = elastixOrTransformix;
        this.settings = settings;
    }

    public String createExecutableShellScript()
    {
        if ( isMac() || isLinux() )
        {
            String executablePath = settings.tmpDir
                    + File.separator + "run_" + elastixOrTransformix + ".sh";

            String binaryPath = settings.elastixDirectory + File.separator + "bin" + File.separator + elastixOrTransformix;

            if( ! new File( binaryPath ).exists() )
                Utils.logErrorAndExit( settings, "Elastix file does not exist: " + binaryPath );

            String shellScriptText = getScriptText();

            saveStringToFile( shellScriptText, executablePath );

            makeExecutable( executablePath );

            return executablePath;

        }
        else if ( isWindows() )
        {
            setElastixSystemPathForWindowsOS();

            String binaryPath = settings.elastixDirectory + File.separator + elastixOrTransformix + ".exe";

            if ( ! new File( binaryPath ).exists() )
                Utils.logErrorAndExit( settings, "Elastix file does not exist: " + binaryPath );

            return binaryPath;
        }
        else
        {
            Utils.logErrorAndExit( settings, "Could not detect operating system!" );
            return null;
        }

    }

    private String getScriptText()
    {
        String shellScriptText = "";
        shellScriptText += "#!/bin/bash\n";
        shellScriptText += "ELASTIX_PATH=" + settings.elastixDirectory + "\n";

        if ( isMac() )
        {
            shellScriptText += "export DYLD_LIBRARY_PATH=$ELASTIX_PATH/lib/\n";
        }
        else if ( isLinux() )
        {
            shellScriptText += "export LD_LIBRARY_PATH=$ELASTIX_PATH/lib/\n";
        }

        shellScriptText += "$ELASTIX_PATH/bin/" + elastixOrTransformix +" $@\n";
        return shellScriptText;
    }

    private void setElastixSystemPathForWindowsOS()
    {
        ProcessBuilder pb = new ProcessBuilder();
        Map<String, String> env = pb.environment();
        env.put( "PATH", settings.elastixDirectory + ":$PATH");
    }

    private void makeExecutable( String executablePath )
    {
        try
        {
            Utils.waitOneSecond();
            Runtime.getRuntime().exec("chmod +x " + executablePath );
            Utils.waitOneSecond();
        }
        catch ( IOException e )
        {
            IJ.log( "Could not make file executable: " + executablePath );
            e.printStackTrace();
        }
    }
}
