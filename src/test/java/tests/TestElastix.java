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
package tests;

import de.embl.cba.elastix.wrapper.elastix.ElastixWrapperSettings;
import de.embl.cba.elastix.wrapper.elastix.ElastixWrapper;
import de.embl.cba.elastix.wrapper.elastix.parameters.ElastixParameters;
import net.imagej.ImageJ;

import java.io.File;

public class TestElastix
{
	// FIXME:
	// 1. Make this test headless
	// 2. Add an actual test (assert)
	// 3. Make this also run in the GitHub actions

	//@Test
	public static void registerEulerSingleChannelImage()
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		ElastixWrapperSettings settings = new ElastixWrapperSettings();

		settings.logService = ij.log();
		settings.elastixDirectory = "/Applications/elastix-5.2.0-mac" ;
		settings.tmpDir = new File("src/test/resources/test-data/fluo01/tmp").getAbsolutePath();
		settings.transformationType = ElastixParameters.TransformationType.Euler;
		settings.fixedImageFilePath = new File("src/test/resources/test-data/fluo01/ellipsoid-horizontal-dxyz200nm.tif").getAbsolutePath();
		settings.movingImageFilePath = new File("src/test/resources/test-data/fluo01/ellipsoid-at45degrees-dxyz200nm.tif").getAbsolutePath();
		settings.downSamplingFactors = "10 10";
		settings.fixedMaskPath = "";
		settings.movingMaskPath = "";

		final ElastixWrapper elastixWrapper = new ElastixWrapper( settings );
		elastixWrapper.runElastix();

		// Bdv
		elastixWrapper.reviewResults();

		// ImageJ
		elastixWrapper.reviewResultsInImageJ();

		// Save as Tiff
		elastixWrapper.createTransformedImagesAndSaveAsTiff();

		settings.logService.info( "Done!" );
	}

	public static void main( String[] args )
	{
		new TestElastix().registerEulerSingleChannelImage();
	}
}
