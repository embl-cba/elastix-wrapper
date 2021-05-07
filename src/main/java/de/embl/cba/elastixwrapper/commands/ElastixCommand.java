package de.embl.cba.elastixwrapper.commands;

import de.embl.cba.elastixwrapper.wrapper.elastix.parameters.DefaultElastixParametersCreator.ParameterStyle;
import de.embl.cba.elastixwrapper.wrapper.elastix.ElastixWrapper;
import de.embl.cba.elastixwrapper.wrapper.elastix.ElastixWrapperSettings;
import de.embl.cba.elastixwrapper.utils.Utils;
import de.embl.cba.elastixwrapper.wrapper.elastix.parameters.ElastixParameters;
import de.embl.cba.elastixwrapper.wrapper.elastix.parameters.ElastixParameters.TransformationType;
import ij.Prefs;
import ij.gui.GenericDialog;
import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import java.io.File;

@Plugin(type = Command.class, menuPath = "Plugins>Registration>Elastix>Elastix" )
public class ElastixCommand implements Command
{
    public static final String NONE = "None";
    public static final String SHOW_OUTPUT_IN_IMAGEJ1 = "Show output in ImageJ1";
    public static final String SHOW_OUTPUT_IN_BDV = "Show output in Bdv";
    public static final String SAVE_TRANSFORMED_AS_TIFF = "Save transformed images in working directory as Tiff";

    public static final String PARAMETERS_DEFAULT = "Default";
    public static final String PARAMETERS_CLEM = "CLEM";

    @Parameter( label = "Elastix installation directory", style = "directory" )
    public File elastixDirectory;

    @Parameter( label = "Temporary directory for intermediate files", style = "directory" )
    public File tmpDir = new File( System.getProperty("java.io.tmpdir") );

    @Parameter( label = "Fixed image" )
    public File fixedImageFile;

    @Parameter( label = "Moving image" )
    public File movingImageFile;

    @Parameter( label = "Transformation type", choices = {
            ElastixParameters.TRANSLATION,
            ElastixParameters.EULER,
            ElastixParameters.SIMILARITY,
            ElastixParameters.AFFINE,
            ElastixParameters.SPLINE } )
    public String transformationType;

    @Parameter( label = "Grid spacing for BSpline transformation [voxels]", required = false )
    public String bSplineGridSpacing = "50,50,50";

    @Parameter( label = "Number of iterations" )
    public int numIterations = 1000;

    @Parameter( label = "Number of spatial samples" )
    public String numSpatialSamples = "10000";

    @Parameter( label = "Gaussian smoothing sigma [voxels]" )
    public String gaussianSmoothingSigmas = "10,10,10";

    @Parameter( label = "Transformation output file", style = "save" )
    public File transformationOutputFile;

    @Parameter( label = "Image output modality",
            choices = {
                    NONE,
                    SHOW_OUTPUT_IN_IMAGEJ1,
                    SAVE_TRANSFORMED_AS_TIFF
            } )
    public String outputModality;

    @Parameter( label = "Use fixed image mask" )
    public boolean useFixedMask;

    @Parameter( label = "Fixed image mask file", required = false )
    public File fixedMaskFile;

    @Parameter( label = "Use moving image mask" )
    public boolean useMovingMask;

    @Parameter( label = "Moving image mask file", required = false )
    public File movingMaskFile;

    @Parameter( label = "Use initial transformation" )
    public boolean useInitialTransformation;

    @Parameter( label = "Initial transformation file", required = false )
    public File initialTransformationFile;

    @Parameter( label = "Elastix parameters", choices =
            {
                    PARAMETERS_DEFAULT,
                    PARAMETERS_CLEM
            })
    public String elastixParameters = PARAMETERS_DEFAULT;

    @Parameter( label = "Final resampler",
            choices = {
                    ElastixParameters.FINAL_RESAMPLER_LINEAR,
                    ElastixParameters.FINAL_RESAMPLER_NEAREST_NEIGHBOR
            } )
    public String finalResampler = ElastixParameters.FINAL_RESAMPLER_LINEAR;

    @Parameter( label = "Weights for multi channel images" )
    public String multiChannelWeights = "1.0,3.0,1.0,1.0,1.0,1.0";

    @Parameter
    public LogService logService;

    @Parameter
    public UIService uiService;

    @Parameter
    public Context context;

    private ElastixWrapper elastixWrapper;

    public void run()
    {
        runElastix();
    }

    private void runElastix( )
    {
        ElastixWrapperSettings settings = getElastixSettings();

        if ( settings == null ) return;

        elastixWrapper = new ElastixWrapper( settings );

        elastixWrapper.runElastix();

        settings.logService.info( "Handling elastix output...." );

        elastixWrapper.saveTransformationFile();

        if ( outputModality.equals( SHOW_OUTPUT_IN_BDV ))
        {
            elastixWrapper.reviewResults();
        }
        else if ( outputModality.equals( SHOW_OUTPUT_IN_IMAGEJ1 ))
        {
            elastixWrapper.reviewResultsInImageJ();
        }
        else if ( outputModality.equals( SAVE_TRANSFORMED_AS_TIFF ) )
        {
            elastixWrapper.createTransformedImagesAndSaveAsTiff();
        }

        settings.logService.info( "...done!" );
    }

    private ElastixWrapperSettings getElastixSettings()
    {
        ElastixWrapperSettings settings = new ElastixWrapperSettings();

        settings.headless = uiService.isHeadless();
        settings.logService = logService;
        settings.elastixDirectory = elastixDirectory.toString();

        if ( ! new File( settings.elastixDirectory ).exists() )
            Utils.logErrorAndExit( settings, "The elastix directory does not exist: " + settings.elastixDirectory );

        settings.tmpDir = tmpDir.toString();

        if ( useInitialTransformation )
            settings.initialTransformationFilePath = initialTransformationFile.toString();
        else
            settings.initialTransformationFilePath = "";

        if ( useFixedMask )
            settings.fixedMaskPath = fixedMaskFile.toString();
        else
            settings.fixedMaskPath = "";

        if ( useMovingMask )
            settings.movingMaskPath = movingMaskFile.toString();
        else
            settings.movingMaskPath = "";

        settings.fixedImageFilePath = fixedImageFile.toString();
        settings.movingImageFilePath = movingImageFile.toString();

        settings.numWorkers = Prefs.getThreads();

        switch (transformationType) {
            case ElastixParameters.TRANSLATION:
                settings.transformationType = TransformationType.Translation;
                break;
            case ElastixParameters.EULER:
                settings.transformationType = TransformationType.Euler;
                break;
            case ElastixParameters.SIMILARITY:
                settings.transformationType = TransformationType.Similarity;
                break;
            case ElastixParameters.AFFINE:
                settings.transformationType = TransformationType.Affine;
                break;
            case ElastixParameters.SPLINE:
                settings.transformationType = TransformationType.BSpline;
                break;
        }

        if ( elastixParameters.equals(PARAMETERS_DEFAULT) ) {
            settings.elastixParametersStyle = ParameterStyle.Default;
        } else if ( elastixParameters.equals(PARAMETERS_CLEM) ) {
            settings.elastixParametersStyle = ParameterStyle.CLEM;
        }

        settings.iterations = numIterations;
        settings.spatialSamples = numSpatialSamples;
        settings.downSamplingFactors = gaussianSmoothingSigmas;
        settings.bSplineGridSpacing = bSplineGridSpacing;
        settings.finalResampler = finalResampler;
        settings.channelWeights = Utils.delimitedStringToDoubleArray( multiChannelWeights, "," );

        if ( transformationOutputFile.exists() )
        {
            GenericDialog gd = new GenericDialog("File exists");
            gd.addMessage("The transformation output file exists: " + transformationOutputFile.getAbsolutePath());
            gd.enableYesNoCancel("Overwrite", "Exit");
            gd.showDialog();

            if (gd.wasCanceled())
            {
                return null;
            }
            else if (gd.wasOKed())
            {
                //
            }
            else
                return null;
        }

        settings.transformationOutputFilePath = transformationOutputFile.getAbsolutePath();

        return settings;
    }

}

