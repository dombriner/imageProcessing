package imapro.filters.assignment3;

import imapro.filters.utils.CalcUtils;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.imapro.image.ImaproImage;
import sugarcube.insight.core.FxEnvironment;


public class HistogramEqualizer extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new HistogramEqualizer(env);
    public @FXML RadioButton greyScale;
    public @FXML RadioButton useRGB;
    public @FXML RadioButton useHSL;
    private CalcUtils utils = new CalcUtils();

    @FXML
    private void initialize() {
        ToggleGroup group = new ToggleGroup();
        greyScale.setToggleGroup(group);
        useHSL.setToggleGroup(group);
    }

    public HistogramEqualizer(FxEnvironment env) {
        super(env, "Histogram equalizer", false);
        greyScale = toolbar.newColumn().addRadioButton("Operate on grey scale", false, null);
        useRGB = toolbar.newColumn().addRadioButton("Operate on RGB", false, null);
        useHSL = toolbar.newColumn().addRadioButton("Operate on lightness from HSL", false, null);
    }

    @Override
    public void process(FilterImage image) {
        int width = image.source.getWidth();
        int height = image.source.getHeight();
        float size = width * (float) height;
        if (greyScale.isSelected())
            equalizeGreyScale(image, height, width, size);
        else if (useRGB.isSelected()) {
            equalizeRBG(image, height, width, size);
        } else if (useHSL.isSelected()) {
            equalizeHSL(image, height, width, size);
        }
    }

    private void equalizeGreyScale(FilterImage image, int height, int width, float size) {
        int[] histogram = new GreyScaleHistogramCalculator().calcHistogram(image.source);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int cumulativeHistogram = utils.cumulativeHistogram(histogram, utils.intensityToPixelValue(image.source.getValue(x, y)));
                image.result.setValue(x, y, cumulativeHistogram / size);
            }
        }
    }

    private void equalizeRBG(FilterImage image, int height, int width, float size) {
        RGBHistogramCalculator histogramCalculator = new RGBHistogramCalculator(0);
        int[] redHistogram = histogramCalculator.calcHistogram(image.source);
        histogramCalculator.setColorIndex(1);
        int[] blueHistogram = histogramCalculator.calcHistogram(image.source);
        histogramCalculator.setColorIndex(2);
        int[] greenHistogram = histogramCalculator.calcHistogram(image.source);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int redCumulativeHistogram = utils.cumulativeHistogram(redHistogram, utils.intensityToPixelValue(image.source.getPixel(x, y)[0]));
                int blueCumulativeHistogram = utils.cumulativeHistogram(blueHistogram, utils.intensityToPixelValue(image.source.getPixel(x, y)[1]));
                int greenCumulativeHistogram = utils.cumulativeHistogram(greenHistogram, utils.intensityToPixelValue(image.source.getPixel(x, y)[2]));
                image.result.setPixel(x, y, redCumulativeHistogram / size, blueCumulativeHistogram / size, greenCumulativeHistogram / size);
            }
        }
    }

    private void equalizeHSL(FilterImage image, int height, int width, float size) {
        RGBToHSLConverter toHSLConverter = new RGBToHSLConverter();
        HSLToRGBConverter toRGBConverter = new HSLToRGBConverter();
        HSLHistogramCalculator histogramCalculator = new HSLHistogramCalculator();
        int[] histogram = histogramCalculator.calcHistogram(image.source);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int cumulativeHistogram = utils.cumulativeHistogram(histogram, histogramCalculator.getHistogramIndex(image.source, x, y));
                float[] hsl = toHSLConverter.convert(image.source.getPixel(x, y));
                hsl[2] = cumulativeHistogram / size;
                image.result.setPixel(x, y, toRGBConverter.convert(hsl));
            }
        }
    }

    public static void main(String... args) {
        launch(LOADER);
    }
}