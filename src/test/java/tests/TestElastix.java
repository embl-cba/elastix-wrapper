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
