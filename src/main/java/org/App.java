package org;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Logger;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.pixel.Pixel;
import org.openimaj.image.pixel.PixelSet;
import org.openimaj.image.segmentation.FelzenszwalbHuttenlocherSegmenter;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.shape.Polygon;

/**
 * OpenIMAJ Hello world!
 */
public class App {

    static final Logger log = Logger.getLogger("org.app");
    static final int CONTOUR_THICKNESS = 3;

    public static void main(String[] args) throws IOException {
        File original = new File("test.png");
        MBFImage image = ImageUtilities.readMBF(original);
        MBFImage clone = image.clone();

        Path copied = Paths.get("result.png");
        Path originalPath = original.toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        MBFImage result = ImageUtilities.readMBF(new File("result.png"));

        clone = ColourSpace.convert(clone, ColourSpace.CIE_Lab);
        //Split the image on segments
        FelzenszwalbHuttenlocherSegmenter segmenter = new FelzenszwalbHuttenlocherSegmenter(0.5f,500f / 255f, 200 );
        List<? extends PixelSet> coloredPS = segmenter.segment(clone);
        log.info("Segments found: " + coloredPS.size());
        for (PixelSet ps : coloredPS) {
            //Get contour
            ConnectedComponent connectedComponent = new ConnectedComponent(ps.getPixels());
            Polygon poly = connectedComponent.toPolygon();
            result.drawPolygon(poly, CONTOUR_THICKNESS, RGBColour.BLACK);
            //Get the center of the contour
            Pixel centroidPixel = ps.calculateCentroidPixel();
            float[] nativeColour = image.getPixelNative(centroidPixel);
            Color color = new Color(nativeColour[0], nativeColour[1], nativeColour[2]);
            log.info(String.format("Identified centroid: "
                                           + centroidPixel + printColour(color)));
            result.drawText(printColour(color), centroidPixel.x, centroidPixel.y, HersheyFont.ROMAN_SIMPLEX, 14, RGBColour.BLACK);
        }

        clone = ColourSpace.convert(clone, ColourSpace.RGB);
        DisplayUtilities.display(clone, "Original image");
        DisplayUtilities.display(result, "Paint by numbers");
    }

    public static String printColour(Color nativeColour) {
        return String.format("#%02x%02x%02x", nativeColour.getRed(), nativeColour.getGreen(), nativeColour.getBlue());
    }
}
