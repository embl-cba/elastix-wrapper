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
package de.embl.cba.elastix.wrapper.elastix;

import de.embl.cba.elastix.wrapper.elastix.parameters.DefaultElastixParametersCreator.ParameterStyle;
import de.embl.cba.elastix.wrapper.elastix.parameters.ElastixParameters;
import de.embl.cba.elastix.wrapper.transformix.TransformixWrapperSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static de.embl.cba.elastix.wrapper.elastix.parameters.ElastixParameters.FINAL_RESAMPLER_LINEAR;

public class ElastixWrapperSettings extends TransformixWrapperSettings
{
    // before staging
    public String fixedImageFilePath = "";
    public String fixedMaskPath = "";
    public String movingMaskPath = "";

    // after staging - in order of channels
    public ArrayList<String> stagedFixedImageFilePaths;
    public ArrayList<String> stagedFixedMaskFilePaths;
    public ArrayList<String> stagedMovingMaskFilePaths;

    public String initialTransformationFilePath;
    public String parameterFilePath;

    // path to copy calculated transformation to
    public String transformationOutputFilePath;

    // minimal settings needed to generate defaults with DefaultElastixParametersCreator
    public ParameterStyle elastixParametersStyle = ParameterStyle.Default;
    public ElastixParameters.TransformationType transformationType;
    public int iterations = 1000;
    public String spatialSamples = "10000";
    public String downSamplingFactors = "10 10 10";
    public String bSplineGridSpacing = "50 50 50";
    public String finalResampler = FINAL_RESAMPLER_LINEAR;
    public int movingImageBitDepth = 8;
    public Map< Integer, Integer > fixedToMovingChannel = new HashMap<>(  );
    public double[] channelWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,};
}
