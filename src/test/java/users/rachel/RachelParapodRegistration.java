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
package users.rachel;

import bdv.util.Bdv;
import de.embl.cba.elastix.wrapper.elastix.ElastixWrapperSettings;
import de.embl.cba.elastix.wrapper.elastix.ElastixWrapper;
import de.embl.cba.elastix.wrapper.elastix.parameters.ElastixParameters;
import net.imagej.ImageJ;

public class RachelParapodRegistration
{
	public static void main( String[] args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		ElastixWrapperSettings settings = new ElastixWrapperSettings();

		settings.logService = ij.log();
		settings.elastixDirectory = "/Applications/elastix_macosx64_v4.8" ;
		settings.tmpDir = "/Users/tischer/Desktop/elastix-tmp";

		settings.fixedImageFilePath = "/Users/tischer/Documents/rachel-mellwig-em-prospr-registration/data/ganglion-segmentation/spem-seg-ganglion.tif";

		settings.movingImageFilePath = "/Users/tischer/Documents/rachel-mellwig-em-prospr-registration/data/ganglion-segmentation/fib-sem-seg-ganglion.tif";

		settings.fixedMaskPath = "/Users/tischer/Documents/rachel-mellwig-em-prospr-registration/data/ganglion-segmentation/spem-mask-ganglion.tif";

		settings.initialTransformationFilePath = "/Users/tischer/Documents/rachel-mellwig-em-prospr-registration/data/ganglion-segmentation/amira-transform.txt";

		settings.transformationType = ElastixParameters.TransformationType.Affine;
		settings.downSamplingFactors = "10 10 10; 2, 2, 2";
		// settings.movingMaskPath = "";
		// settings.bSplineGridSpacing = "50 50 50";
		settings.iterations = 1000;
		settings.spatialSamples = "10000; 10000";
		settings.channelWeights = new double[]{1.0, 1.0, 3.0, 1.0, 1.0};
		// settings.finalResampler = ElastixSettings.FINAL_RESAMPLER_LINEAR;

		final ElastixWrapper elastixWrapper = new ElastixWrapper( settings );
		elastixWrapper.runElastix();

		final Bdv bdv = elastixWrapper.reviewResults();
		//bdv.close();

		settings.logService.info( "Done!" );
	}

	private static String getImageFilePath( String relativePath )
	{
		return RachelParapodRegistration.class.getResource( relativePath ).getFile().toString();
	}

}
