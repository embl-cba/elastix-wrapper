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
import de.embl.cba.elastix.wrapper.transformix.TransformixWrapper;
import de.embl.cba.elastix.wrapper.transformix.TransformixWrapperSettings;
import net.imagej.ImageJ;

import java.io.File;


public class TestElastixAndTransformixCLEM
{
	//@Test
	public void registerFluoToEM()
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		ElastixWrapperSettings settings = new ElastixWrapperSettings();

		settings.logService = ij.log();
		settings.elastixDirectory = "/Applications/elastix-5.2.0-mac" ;
		settings.tmpDir = "/Users/tischer/Documents/elastixWrapper/src/test/resources/test-data/clem/tmp";
		settings.transformationType = ElastixParameters.TransformationType.BSpline;
		settings.fixedImageFilePath = "/Users/tischer/Documents/elastixWrapper/src/test/resources/test-data/clem/em.tif";
		settings.movingImageFilePath = "/Users/tischer/Documents/elastixWrapper/src/test/resources/test-data/clem/fluo_green.tif";
		settings.downSamplingFactors = "2 2";
		settings.bSplineGridSpacing = "100 100";
		settings.fixedMaskPath = "";
		settings.movingMaskPath = "";

		final ElastixWrapper elastixWrapper = new ElastixWrapper( settings );
		elastixWrapper.runElastix();

		// Bdv
		elastixWrapper.reviewResults();

		settings.logService.info( "Done!" );
	}

	//@Test
	public void transformTwoChannelFluo()
	{
		TransformixWrapperSettings settings = new TransformixWrapperSettings();

		final ImageJ ij = new ImageJ();

		settings.logService = ij.log();
		settings.elastixDirectory = "/Applications/elastix_macosx64_v4.8" ;
		settings.tmpDir = "/Users/tischer/Desktop/elastix-tmp";
		settings.movingImageFilePath = "/Users/tischer/Documents/fiji-plugin-elastixWrapper/src/test/resources/test-data/clem/fluo_red_green.tif";
		settings.transformationFilePath = "/Users/tischer/Documents/fiji-plugin-elastixWrapper/src/test/resources/test-data/clem/tmp/TransformParameters.0.txt";

		settings.outputModality = TransformixWrapperSettings.OutputModality.Save_as_tiff;
		settings.outputFile = new File( "/Users/tischer/Documents/fiji-plugin-elastixWrapper/src/test/resources/test-data/clem/aligned" );

		final TransformixWrapper transformixWrapper = new TransformixWrapper( settings );

		transformixWrapper.runTransformix();

		settings.logService.info( "Done!" );
	}


	public static void main( String[] args )
	{
		new TestElastixAndTransformixCLEM().registerFluoToEM();
		new TestElastixAndTransformixCLEM().transformTwoChannelFluo();
	}



}
