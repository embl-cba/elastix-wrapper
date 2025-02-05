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
import de.embl.cba.elastix.wrapper.StagingManager;
import de.embl.cba.elastix.wrapper.transformix.TransformixWrapper;
import de.embl.cba.elastix.wrapper.transformix.TransformixWrapperSettings;
import de.embl.cba.metaimage_io.MetaImage_Reader;
import ij.IJ;
import ij.ImagePlus;
import net.imagej.ImageJ;

public class ExampleTransformixAPI
{
	public static void main( String[] args )
	{

		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final String inputImagePath =
				"/Users/tischer/Documents/rachel-mellwig-em-prospr-registration/data/FIB segmentation/muscle.tif";

		IJ.open( inputImagePath );

		TransformixWrapperSettings settings = new TransformixWrapperSettings();

		settings.logService = ij.log();
		settings.elastixDirectory = "/Applications/elastix_macosx64_v4.8" ;
		settings.tmpDir = "/Users/tischer/Desktop/elastix-tmp";
		settings.movingImageFilePath = inputImagePath;
		settings.transformationFilePath = "/Users/tischer/Desktop/transform.txt";

		final TransformixWrapper transformixWrapper = new TransformixWrapper( settings );
		transformixWrapper.runTransformix();

		MetaImage_Reader reader = new MetaImage_Reader();
		final ImagePlus transformed = reader.load(
				settings.tmpDir,
				StagingManager.TRANSFORMIX_OUTPUT_FILENAME
						+ "." + StagingManager.STAGING_FILE_TYPE,
				false );

		transformed.show();

		settings.logService.info( "Done!" );
	}

}
