package imapro.filters.project.filters;

import imapro.filters.utils.CalcUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.imapro.image.ImaproImage;
import sugarcube.insight.core.FxEnvironment;


public class OilPaintingFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new OilPaintingFilter(env);
    public @FXML CheckBox applyFilter;
    public @FXML Slider intensityLevelsSlider, radiusSlider, normSlider;
    private CalcUtils utils = new CalcUtils();

    public OilPaintingFilter(FxEnvironment env) {
        super(env, "Oil Painting Effect", false);
        intensityLevelsSlider = toolbar.addSlider("Intensity Levels", 1, 256, 20);
        radiusSlider = toolbar.newColumn().addSlider("Radius", 0, 100, 4);
        normSlider = toolbar.newColumn().addSlider("p-norm", 1, 10, 2);
        applyFilter = toolbar.newColumn().addCheckBox("Apply Filter", true);

        intensityLevelsSlider.setBlockIncrement(1.0);
        radiusSlider.setBlockIncrement(1.0);
        normSlider.setBlockIncrement(1.0);
    }

    @Override
    public void process(FilterImage image) {
        if (applyFilter.isSelected()) {
            int radius = (int) radiusSlider.getValue();
            int intensityLevels = (int) intensityLevelsSlider.getValue();
            int norm = (int) normSlider.getValue();

            paintWithOil(image, radius, intensityLevels, norm);
        } else {
            dontFilter(image);
        }
    }

    /**
     * Applies the oil painting filter to the image using the provided parameters
     */
    private void paintWithOil(FilterImage image, int radius, int intensityLevels, int norm) {
        for (int y = 0; y < image.source.getHeight(); y++) {
            for (int x = 0; x < image.source.getWidth(); x++) {
                calculatePixel(x, y, image, radius, intensityLevels, norm);
            }
        }
    }

    /**
     * Calculate the value of
     * @param x – x coord of the pixel
     * @param y - y coord of the pixel
     * @param norm – p-norm to measure distance
     */
    private void calculatePixel(int x, int y, FilterImage image, int radius, int intensityLevels, int norm) {
        // Create necessary buckets
        int[] intensityLevelsCount = new int[intensityLevels];
        int[] redSum = new int[intensityLevels];
        int[] greenSum = new int[intensityLevels];
        int[] blueSum = new int[intensityLevels];

        for (int scannerX = x - radius; scannerX <= x + radius; scannerX++) {
            for (int scannerY = y - radius; scannerY <= y + radius; scannerY++) {
                if (isInsideRadius(scannerX, scannerY, x, y, radius, norm)) {
                    int xCoord = getSafeCoord(scannerX, image.source, false);
                    int yCoord = getSafeCoord(scannerY, image.source, true);
                    fillBuckets(image, xCoord, yCoord, intensityLevels, intensityLevelsCount, redSum, greenSum, blueSum);
                }
            }
        }

        calculateFinalPixelValue(image, x, y, redSum, greenSum, blueSum, intensityLevelsCount, intensityLevels);
    }

    /**
     * Does some calculations with the pixel and fills the buckets with the corresponding values
     */
    private void fillBuckets(FilterImage image, int xCoord, int yCoord, int intensityLevels, int[] intensityLevelsCount, int[] redSum, int[] greenSum, int[] blueSum) {
        int intensityLevel = getIntensityLevelOfPixel(image, xCoord, yCoord, intensityLevels);

        intensityLevelsCount[intensityLevel]++;
        float[] rgb = image.source.getPixel(xCoord, yCoord);
        redSum[intensityLevel] += utils.intensityToPixelValue(rgb[0]);
        greenSum[intensityLevel] += utils.intensityToPixelValue(rgb[1]);
        blueSum[intensityLevel] += utils.intensityToPixelValue(rgb[2]);
    }

    private int getIntensityLevelOfPixel(FilterImage image, int x, int y, int intensityLevels) {
        return (int) (image.source.getValue(x, y) * (intensityLevels - 1));
    }

    /**
     * @param intensityLevelsCount Number of pixels for each intensity level bucket
     * @param intensityLevels Number of intensity levels
     * @param pixelIntensityLevel Intensity level of the source pixel considered. Used for "draws" between levels.
     */
    private int getMaxIntensityLevel(int[] intensityLevelsCount, int intensityLevels, int pixelIntensityLevel) {
        int maxIntensityLevel = 0;
        for (int i = 0; i < intensityLevels; i++) {
            if  (intensityLevelsCount[i] < intensityLevelsCount[maxIntensityLevel])
                continue;
            if (intensityLevelsCount[i] == intensityLevelsCount[maxIntensityLevel]) {
                if (Math.abs(i - pixelIntensityLevel) < Math.abs(maxIntensityLevel - pixelIntensityLevel))
                    maxIntensityLevel = i;
            } else {
                maxIntensityLevel = i;
            }
        }
        return maxIntensityLevel;
    }

    /**
     * Calculates the value of the pixel in the image giving the buckets
     */
    private void calculateFinalPixelValue(FilterImage image, int x, int y, int[] redSum, int[] greenSum, int[] blueSum, int[] intensityLevelsCount, int intensityLevels) {
        int pixelIntensityLevel = getIntensityLevelOfPixel(image, x, y, intensityLevels);
        int maxIntensityLevel = getMaxIntensityLevel(intensityLevelsCount, intensityLevels, pixelIntensityLevel);

        float red = (redSum[maxIntensityLevel] / ((float) intensityLevelsCount[maxIntensityLevel])) / 255f;
        float green = (greenSum[maxIntensityLevel] / ((float) intensityLevelsCount[maxIntensityLevel])) / 255f;
        float blue = (blueSum[maxIntensityLevel] / ((float) intensityLevelsCount[maxIntensityLevel])) / 255f;
        image.result.setPixel(x, y, red, green, blue);
    }

    /**
     * If virtual coordinate is outside the image, uses mirroring to get a valid image coordinate
     * @param virtualCoord coordinate component (either dimension) to be found in the image
     * @param image the image the coordinate corresponds to
     * @param height true iff the coordinate is the y coordinate (height)
     * @return Corresponding coordinate component inside the image respecting mirroring at the image borders
     */
    private int getSafeCoord(int virtualCoord, ImaproImage image, boolean height) {
        if (virtualCoord < 0)
            return -virtualCoord;
        int dimension = height ? image.getHeight() : image.getWidth();
        if (virtualCoord >= dimension)
            return dimension - (virtualCoord - dimension + 1);
        return virtualCoord;
    }

    public boolean isInsideRadius(int firstX, int firstY, int secondX, int secondY, int radius, int norm) {
        return getDistanceNoRoot(firstX, firstY, secondX, secondY, norm) <= power(radius, norm);
    }

    /**
     * Returns the distance of (firstX, firstY) to (secondX, secondY) according to the p-norm given by the parameter
     *  norm, but without taking the costly root
     */
    public int getDistanceNoRoot(int firstX, int firstY, int secondX, int secondY, int norm) {
        return power(firstX - secondX, norm) + power(firstY - secondY, norm);
    }

    public int power(int base, int exponent) {
        if (exponent < 0)
            return - 1;
        if (exponent == 0)
            return 1;
        if (exponent == 1)
            return base;
        return base * power(base, --exponent);
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