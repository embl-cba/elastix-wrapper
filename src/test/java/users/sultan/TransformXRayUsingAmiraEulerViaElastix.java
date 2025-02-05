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
package users.sultan;

import de.embl.cba.elastix.wrapper.transformix.TransformixWrapper;
import de.embl.cba.elastix.wrapper.transformix.TransformixWrapperSettings;
import ij.IJ;
import ij.ImagePlus;
import itc.converters.AffineTransform3DToElastixAffine3D;
import itc.converters.AmiraEulerToAffineTransform3D;
import itc.transforms.elastix.ElastixAffineTransform3D;
import itc.transforms.elastix.ElastixTransform;
import net.imagej.ImageJ;
import net.imglib2.realtransform.AffineTransform3D;

public class TransformXRayUsingAmiraEulerViaElastix
{
	public static void main( String[] args )
	{
		final ImageJ imageJ = new ImageJ();
		imageJ.ui().showUI();

		final String xRayPath = "/Users/tischer/Documents/sultan-schwab/platy_90_02_neuropile_1um.tif";
		final ImagePlus xRayImp = IJ.openImage( xRayPath );

		double[] translationInMicrometer = new double[]{ -54.6, 2.59, 53.39 };
		double[] rotationAxis = new double[]{ -0.27, 0.81, 0.51 };
		double rotationAngleDegrees = 71.7;

		final double[] imageVoxelSizeInMicrometer = { 1.0, 1.0, 1.0 };

		final double[] rotationCentreInMicrometer = new double[ 3 ];
		rotationCentreInMicrometer[ 0 ] = xRayImp.getWidth() / 2.0 * imageVoxelSizeInMicrometer[ 0 ];
		rotationCentreInMicrometer[ 1 ] = xRayImp.getHeight() / 2.0 * imageVoxelSizeInMicrometer[ 1 ];
		rotationCentreInMicrometer[ 2 ] = xRayImp.getNSlices() / 2.0 * imageVoxelSizeInMicrometer[ 1 ];

		final AffineTransform3D affineTransform3DInMillimeter =
				AmiraEulerToAffineTransform3D.convert(
						rotationAxis,
						rotationAngleDegrees,
						translationInMicrometer,
						rotationCentreInMicrometer );


		/**
		 * This must be the dimensions of the image that we would like to produce
		 * with transformix, e.g. the dimensions of the image that we would like to match,
		 * using this transform as an initial transform, here this is this one:
		 * /Volumes/cba/exchange/Sultan/prospr_neuropile_0.4um.tif
		 *
		 */

		final AffineTransform3DToElastixAffine3D affineTransform3DToElastixAffine3D
				= new AffineTransform3DToElastixAffine3D(
				ElastixTransform.FINAL_LINEAR_INTERPOLATOR,
				ElastixTransform.RESULT_IMAGE_PIXEL_TYPE_UNSIGNED_CHAR,
				new Double[]{ 0.0004, 0.0004, 0.0004 },
				new Integer[]{ 687, 648, 713 }
		);

		// invert, because elastix transform goes from output to input
		final AffineTransform3D inverse = affineTransform3DInMillimeter.inverse();

		final ElastixAffineTransform3D elastixAffineTransform3D
				= affineTransform3DToElastixAffine3D.convert( inverse );

		final String elastixTransformPath =
				"/Users/tischer/Desktop/elastix-affine-transform.txt";
		elastixAffineTransform3D.save( elastixTransformPath );


		TransformixWrapperSettings settings = new TransformixWrapperSettings();

		settings.logService = imageJ.log();
		settings.elastixDirectory = "/Applications/elastix_macosx64_v4.8";
		settings.tmpDir = "/Users/tischer/Desktop/elastix-tmp/";
		settings.movingImageFilePath = xRayPath;
		settings.transformationFilePath = elastixTransformPath;
		settings.numWorkers = 4;
		settings.outputModality = TransformixWrapperSettings.OutputModality.Show_images;
		// settings.outputFile = outputFile;

		TransformixWrapper transformixWrapper = new TransformixWrapper( settings );
		transformixWrapper.runTransformix();



	}

}
