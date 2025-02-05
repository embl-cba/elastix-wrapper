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
package de.embl.cba.elastix.wrapper.elastix.parameters;


import de.embl.cba.elastix.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ElastixParameters
{
    public enum TransformationType
    {
        Translation,
        Euler,
        Similarity,
        Affine,
        BSpline
    }

    public static final String TRANSLATION = "Translation";
    public static final String EULER = "Euler";
    public static final String SIMILARITY = "Similarity";
    public static final String AFFINE = "Affine";
    public static final String SPLINE = "BSpline";

    public static final String FINAL_RESAMPLER_LINEAR = "FinalLinearInterpolator";
    public static final String FINAL_RESAMPLER_NEAREST_NEIGHBOR = "FinalNearestNeighborInterpolator";

    List<String> parameters;
    private int nChannels;

    public ElastixParameters( TransformationType transformationType, int nChannels )
    {
        this.nChannels = nChannels;
        this.parameters = new ArrayList<>();
        addParameter("Transform", transformationType.toString() + "Transform", false, false );
    }

    public void addParameter(
            String key,
            String value,
            boolean isMultiChannelParameter,
            boolean isNumeric )
    {
        String keyValues = "(KEY VALUES)";

        keyValues = setKey( key, keyValues );
        keyValues = setValues( value, keyValues, isMultiChannelParameter, isNumeric );

        parameters.add( keyValues );
    }

    public void writeParameterFile( String parameterFilePath ) {
        System.out.println( "Number of parameters: " + parameters.size() );
        System.out.println( "Writing parameter file: " + parameterFilePath  );
        Utils.saveStringListToFile( parameters, parameterFilePath );
    }

    private String setValues( String value, String keyValues, boolean isMultiChannelParameter, boolean isNumeric )
    {
        String values = "";

        // int n = isMultiChannelParameter ? settings.fixedToMovingChannel.size() : 1;
        int n = isMultiChannelParameter ? nChannels : 1;

        for ( int c = 0; c < n; ++c )
            if ( isNumeric )
                values += value + " ";
            else
                values += "\"" + value + "\"" + " ";


        return keyValues.replace( "VALUES", values );

    }

    private String setKey( String key, String keyValues )
    {
        return keyValues.replace( "KEY", key );
    }

}
