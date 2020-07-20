package org;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
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

public class App {

    static final Logger log = Logger.getLogger("org.app");
    static final int CONTOUR_THICKNESS = 1;
    static final String[] colorsList = {
            "0f0f0f",
            "0f0f30",
            "0f0f51",
            "0f300f",
            "0f3030",
            "0f3051",
            "0f510f",
            "0f5130",
            "0f5151",
            "300f0f",
            "300f30",
            "300f51",
            "30300f",
            "303030",
            "303051",
            "30510f",
            "305130",
            "305151",
            "510f0f",
            "510f30",
            "510f51",
            "51300f",
            "513030",
            "513051",
            "51510f",
            "515130",
            "515151"};

    public static void main(String[] args) throws IOException {
        File original = new File(args[0]);
        MBFImage image = ImageUtilities.readMBF(original);
        MBFImage clone = image.clone();

        Path copied = Paths.get("result.png");
        Path originalPath = original.toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

        MBFImage result = ImageUtilities.readMBF(new File("result.png"));
        // result.fill(RGBColour.WHITE);
        clone = ColourSpace.convert(clone, ColourSpace.CIE_Lab);
        //Split the image on segments
        FelzenszwalbHuttenlocherSegmenter segmenter = new FelzenszwalbHuttenlocherSegmenter(0.5f, 500f / 255f, 200);
        List<? extends PixelSet> coloredPS = segmenter.segment(clone);
        log.info("Segments found: " + coloredPS.size());
        for (PixelSet ps : coloredPS) {
            //Get contour
            ConnectedComponent connectedComponent = new ConnectedComponent(ps.getPixels());
            Polygon poly = connectedComponent.toPolygon();
            //Get the center of the contour
            Pixel centroidPixel = ps.calculateCentroidPixel();
            Float[] reducedColor = reduceColor(image.getPixelNative(centroidPixel));
            String color = getColorNumber(reducedColor);
            result.drawPolygon(poly, CONTOUR_THICKNESS, RGBColour.BLACK);
            log.info("Identified centroid: " + centroidPixel + color);
            result.drawText(color, centroidPixel.x - 10, centroidPixel.y - 10, HersheyFont.ROMAN_SIMPLEX, 8, RGBColour.BLACK);
        }

        clone = ColourSpace.convert(clone, ColourSpace.RGB);
        DisplayUtilities.display(result, "Paint by numbers");
        DisplayUtilities.display(clone, "Original image");
    }

    static Float[] reduceColor(float[] nativePixel) {
        Float[] clippedColour = new Float[3];

        for (int i = 0; i < nativePixel.length; i++) {
            if (nativePixel[i] > 1 / 3f && nativePixel[i] <= 2 / 3f) {
                clippedColour[i] = 0.48f;
            }
            if (nativePixel[i] > 2 / 3f) {
                clippedColour[i] = 0.81f;
            }
            if (nativePixel[i] <= 1 / 3f) {
                clippedColour[i] = 0.15f;
            }
        }
        return clippedColour;
    }

    static String getColorNumber(final Float[] clippedColour) {
        final String hex = String.format("%02x%02x%02x", Math.round(clippedColour[0] * 100f), Math.round(clippedColour[1] * 100), Math.round(clippedColour[2] * 100));
        int colorNumber = Arrays.asList(colorsList).indexOf(hex);

        if (colorNumber == -1) {
            log.warning("Color was not found for hex: " + hex);
            colorNumber = 0;
        }
        return String.valueOf(colorNumber);
    }
}
