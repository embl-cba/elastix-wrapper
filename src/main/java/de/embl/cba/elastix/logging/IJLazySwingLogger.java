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
package de.embl.cba.elastix.logging;

import ij.IJ;
import javax.swing.*;

public class IJLazySwingLogger implements Logger {

    private boolean showDebug = false;

    public IJLazySwingLogger() {
    }

    @Override
    public void setShowDebug(boolean showDebug)
    {
        this.showDebug = showDebug;
    }

    @Override
    public boolean isShowDebug()
    {
        return ( showDebug );
    }


    @Override
    public void info(String message){
        ijLazySwingLog(String.format("[INFO]: %s", message));
    }

    @Override
    public void progress( String message, String progress )
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                String[] logs = IJ.getLog().split("\n");
                if ( logs[logs.length-1].contains(message) )
                {
                    IJ.log(String.format("\\Update:[PROGRESS]: %s %s", message, progress));
                }
                else
                {
                    IJ.log(String.format("[PROGRESS]: %s %s", message, progress));
                }
            }
        });
    }

    @Override
    public void error(String _message){
        IJ.showMessage(String.format("[ERROR]: %s", _message));
    }

    @Override
    public void warning(String _message){
        ijLazySwingLog(String.format("[WARNING]: %s", _message));
    }

    @Override
    public void debug(String _message){
        if ( showDebug )
        {
            ijLazySwingLog(String.format("[DEBUG]: %s", _message));
        }
    }


    private void ijLazySwingLog(String message)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                IJ.log(message);
            }
        });
    }

}
