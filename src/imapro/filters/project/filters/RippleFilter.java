package imapro.filters.project.filters;

import imapro.filters.utils.CalcUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.insight.core.FxEnvironment;

import java.util.Arrays;

/**
 * Naïve implementation of a ripple filter
 * What could be done: Smooth borders (of images), allow distortions with other functions, add phase shift
 */
public class RippleFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new RippleFilter(env);
    public @FXML CheckBox applyFilter, interPolateValues;
    public @FXML Slider amplitudeSliderX, frequencySliderX, amplitudeSliderY, frequencySliderY;
    private CalcUtils utils = new CalcUtils();

    public RippleFilter(FxEnvironment env) {
        super(env, "Ripple Effect", false);
        amplitudeSliderX = toolbar.addSlider("Amplitude X", 1, 256, 20);
        frequencySliderX = toolbar.addSlider("Frequency X", 0, 1000, 2);
        amplitudeSliderY = toolbar.newColumn().addSlider("Amplitude Y", 1, 256, 20);
        frequencySliderY = toolbar.addSlider("Frequency Y", 0, 1000, 0);
        interPolateValues = toolbar.newColumn().addCheckBox("Interpolate", true);
        applyFilter = toolbar.newColumn().addCheckBox("Apply Filter", true);

        amplitudeSliderX.setBlockIncrement(1.0);
        amplitudeSliderY.setBlockIncrement(1.0);
        frequencySliderX.setBlockIncrement(0.5);
        frequencySliderY.setBlockIncrement(0.5);
    }

    @Override
    public void process(FilterImage image) {
        if (applyFilter.isSelected()) {
            double frequencyX = frequencySliderX.getValue();
            double amplitudeX = amplitudeSliderX.getValue();
            double frequencyY = frequencySliderY.getValue();
            double amplitudeY = amplitudeSliderY.getValue();

            paintWithOil(image, frequencyX, amplitudeX, frequencyY, amplitudeY);
        } else {
            dontFilter(image);
        }
    }

    private void paintWithOil(FilterImage image, double frequencyX, double amplitudeX, double frequencyY, double amplitudeY) {
        for (int y = 0; y < image.source.getHeight(); y++) {
            for (int x = 0; x < image.source.getWidth(); x++) {
                double displacementFactorX = amplitudeX * Math.sin(frequencyX*2*Math.PI/image.source.getHeight() * y);
                double sourceX = x - displacementFactorX;
                double displacementFactorY = amplitudeY * Math.sin(frequencyY*2*Math.PI/image.source.getWidth() * x);
                double sourceY = y - displacementFactorY;

                if (interPolateValues.isSelected()) {
                    image.result.setPixel(x, y, getInterpolatedSafeCoord(image, sourceX, sourceY));
                } else {
                    int xToLookup = (int) Math.round(sourceX);
                    int yToLookup = (int) Math.round(sourceY);
                    image.result.setPixel(x, y, getSafeCoord(image, xToLookup, yToLookup));
                }
            }
        }
    }

    private float[] getInterpolatedSafeCoord(FilterImage image, double xToLookup, double yToLookup) {
        while (xToLookup >= image.source.getWidth()) {
            xToLookup = xToLookup - image.source.getWidth();
        } while (xToLookup < 0) {
            xToLookup += image.source.getWidth();
        }
        while (yToLookup >= image.source.getHeight()) {
            yToLookup = yToLookup - image.source.getHeight();
        } while (yToLookup < 0) {
            yToLookup += image.source.getHeight();
        }
        int x1 = (int) xToLookup;
        int x2 = (int) Math.ceil(xToLookup);
        if (x2 == x1)
            x2++;
        int y1 = (int) yToLookup;
        int y2 = (int) Math.ceil(yToLookup);
        if (y2 == y1)
            y2++;
        float[] q11 = Arrays.copyOf(image.source.getPixel(x1, y1), 3);
        float[] q12 = Arrays.copyOf(image.source.getPixel(x1, y2), 3);
        float[] q21 = Arrays.copyOf(image.source.getPixel(x2, y1), 3);
        float[] q22 = Arrays.copyOf(image.source.getPixel(x2, y2), 3);

        float factorOne = (float) (x2 - xToLookup);
        float factorTwo = (float) (xToLookup - x1);
        float[] fxy1 = new float[]{factorOne * q11[0] + factorTwo * q21[0], factorOne * q11[1] + factorTwo * q21[1], factorOne * q11[2] + factorTwo * q21[2]};
        float[] fxy2 = new float[]{factorOne * q12[0] + factorTwo * q22[0], factorOne * q12[1] + factorTwo * q22[1], factorOne * q12[2] + factorTwo * q22[2]};

        factorOne = (float) (y2 - yToLookup);
        factorTwo = (float) (yToLookup - y1);

        return new float[]{factorOne * fxy1[0] + factorTwo * fxy2[0], factorOne * fxy1[1] + factorTwo * fxy2[1], factorOne * fxy1[2] + factorTwo * fxy2[2]};
    }

    private float[] getSafeCoord(FilterImage image, int xToLookup, int yToLookup) {
        if (xToLookup >= image.source.getWidth()) {
            xToLookup = xToLookup % image.source.getWidth();
        } else while (xToLookup < 0) {
            xToLookup += image.source.getWidth();
        }
        if (yToLookup >= image.source.getHeight()) {
            yToLookup = yToLookup % image.source.getHeight();
        } else while (yToLookup < 0) {
            yToLookup += image.source.getHeight();
        }
        return Arrays.copyOf(image.source.getPixel(xToLookup, yToLookup), 3);
    }


    // Just do nothing
    private void dontFilter(FilterImage image) {
        for (int y = 0; y < image.source.getHeight(); y++) {
            for (int x = 0; x < image.source.getWidth(); x++) {
                image.result.setValue(x, y, image.source.getValue(x, y));
            }
        }
    }

    public static void main(String... args) {
        launch(LOADER);
    }
}