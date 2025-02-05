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
package de.embl.cba.elastix.wrapper;

import bdv.util.*;
import ij.ImagePlus;
import ij.measure.Calibration;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.ARGBType;

import java.util.ArrayList;

import static de.embl.cba.elastix.utils.Utils.loadMetaImage;

public class BdvManager {

    private ArrayList<ARGBType> colors;
    private Bdv bdv;
    private int colorIndex;

    public BdvManager() {
        initColors();
    }

    private void initColors()
    {
        colors = new ArrayList<>();
        colors.add( new ARGBType( ARGBType.rgba( 000, 255, 000, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 255, 000, 255, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 255, 000, 000, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 000, 000, 255, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 000, 255, 255, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 255, 255, 000, 255 ) ) );

        colors.add( new ARGBType( ARGBType.rgba( 255, 255, 255, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 255, 255, 255, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 255, 255, 255, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 255, 255, 255, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 255, 255, 255, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 255, 255, 255, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 255, 255, 255, 255 ) ) );
        colors.add( new ARGBType( ARGBType.rgba( 255, 255, 255, 255 ) ) );
        colorIndex = 0;
    }

    public Bdv showMetaImageInBdv( String imageDir, String imageName )  {
        ImagePlus imagePlus = loadMetaImage( imageDir, imageName );
        final BdvStackSource bdvStackSource = showImagePlusInBdv( imagePlus );
        bdvStackSource.setColor( colors.get( colorIndex++ )  );
        bdv = bdvStackSource.getBdvHandle();
        return bdv;
    }

    public BdvStackSource showImagePlusInBdv(
            ImagePlus imp )
    {
        final Calibration calibration = imp.getCalibration();

        if ( imp.getNSlices() > 1 )
        {
            final double[] calib = {
                    calibration.pixelWidth,
                    calibration.pixelHeight,
                    calibration.pixelDepth
            };
            return BdvFunctions.show(
                    (RandomAccessibleInterval) ImageJFunctions.wrapReal( imp ),
                    imp.getTitle(),
                    BdvOptions.options().addTo( bdv ).axisOrder( AxisOrder.XYZ ).sourceTransform( calib ) );
        }
        else
        {

            final double[] calib = {
                    calibration.pixelWidth,
                    calibration.pixelHeight
            };

            return BdvFunctions.show(
                    ( RandomAccessibleInterval ) ImageJFunctions.wrapReal( imp ),
                    imp.getTitle(),
                    BdvOptions.options().addTo( bdv ).is2D().sourceTransform( calib ) );
        }
    }
}
