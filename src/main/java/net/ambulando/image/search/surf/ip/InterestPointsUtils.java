/**
 * 
 */
package net.ambulando.image.search.surf.ip;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import net.ambulando.image.search.Candidate;

import com.google.common.collect.Lists;

/**
 * @author massi
 *
 */
public class InterestPointsUtils {

    private static Opener opener = new Opener();

    private InterestPointsUtils() {
    }

    public static void displayCandidates(File image, List<Candidate> candidates) {
        ImagePlus image1 = opener.openImage(image.getAbsolutePath());
        ImageProcessor image1ProcessorCopy = image1.getProcessor().duplicate().convertToRGB();
        String image1NewTitle = String.format("Original %s", image1.getTitle().split(":")[0]);
        ImagePlus image1Copy = new ImagePlus(image1NewTitle, image1ProcessorCopy);
        int x = 100;
        int y = 100;
        image1Copy.show();
        image1Copy.getCanvas().setLocation(x, y);
        List<ImagePlus> images = Lists.newArrayList();
        for (Candidate candidate : candidates) {
            x += 25;
            y += 25;
            ImagePlus current = InterestPointsUtils.displayInterestingPoints(candidate, image1ProcessorCopy);
            images.add(current);
            current.show();
            current.getCanvas().setLocation(x, y);
        }
        image1Copy.repaintWindow();
        read("any key to continue");
        image1Copy.hide();
        images.forEach(new Consumer<ImagePlus>() {
            @Override
            public void accept(ImagePlus t) {
                t.hide();
            }
        });
    }

    public static ImagePlus displayInterestingPoints(Candidate candidate, ImageProcessor image1ProcessorCopy) {
        System.out.println("drawing...");
        // Draw matched interest points:
        ImagePlus image2 = opener.openImage(candidate.getImage().getAbsolutePath());
        Map<InterestPoint, InterestPoint> matchedPoints = candidate.getMatchedPoint();

        ImageProcessor image2ProcessorCopy = image2.getProcessor().duplicate()
                .convertToRGB();
        String image2NewTitle = String.format("Result %s: %d Matched Points",
                image2.getTitle().split(":")[0], matchedPoints.size());
        ImagePlus image2Copy = new ImagePlus(image2NewTitle,
                image2ProcessorCopy);

        for (Entry<InterestPoint, InterestPoint> pair : matchedPoints.entrySet()) {
            InterestPointDrawer.drawSingleInterestPoint(image1ProcessorCopy, pair.getKey());
            InterestPointDrawer.drawSingleInterestPoint(image2ProcessorCopy, pair.getValue());
        }
        return image2Copy;
    }

    private static String read(String string) {
        System.out.println(string);
        String command = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (command == null) {
            try {
                command = br.readLine();
            } catch (IOException ioe) {
                System.out.println("IO error trying to read from keyboard!");
                System.exit(1);
            }
        }
        return command;
    }

}
