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
package de.embl.cba.elastix.utils;

import de.embl.cba.elastix.logging.IJLazySwingLogger;
import de.embl.cba.elastix.logging.Logger;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by tischi on 11/04/17.
 */
public class MonitorThreadPoolStatus {

    private static Logger logger = new IJLazySwingLogger();

    public static void showProgressAndWaitUntilDone(List<Future> futures,
                                                    String message,
                                                    int updateFrequencyMilliseconds) {
        int done = 0;
        while( done != futures.size() )
        {
            done = 0;
            for ( Future f : futures )
            {
                if (f.isDone() ) done++;
            }

            logger.progress( message,  done + "/" + futures.size() );

            try {
                Thread.sleep(updateFrequencyMilliseconds);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        }

    }

}
