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
    public @FXML Slider intensityLevelsSlider, radiusSlider;
    private CalcUtils utils = new CalcUtils();

    public OilPaintingFilter(FxEnvironment env) {
        super(env, "Oil Painting Effect", false);
        intensityLevelsSlider = toolbar.addSlider("Intensity Levels", 1, 256, 20);
        radiusSlider = toolbar.newColumn().addSlider("Radius", 0, 100, 4);
        applyFilter = toolbar.newColumn().addCheckBox("Apply Filter", true);

        intensityLevelsSlider.setBlockIncrement(1.0);
        radiusSlider.setBlockIncrement(1.0);
    }

    @Override
    public void process(FilterImage image) {
        if (applyFilter.isSelected()) {
            int radius = (int) radiusSlider.getValue();
            int intensityLevels = (int) intensityLevelsSlider.getValue();

            paintWithOil(image, radius, intensityLevels);
        } else {
            dontFilter(image);
        }
    }

    /**
     * Applies the oil painting filter to the image using the provided parameters
     */
    private void paintWithOil(FilterImage image, int radius, int intensityLevels) {
        for (int y = 0; y < image.source.getHeight(); y++) {
            for (int x = 0; x < image.source.getWidth(); x++) {
                calculatePixel(x, y, image, radius, intensityLevels);
            }
        }
    }

    /**
     * Calculate the value of
     * @param x – x coord of the pixel
     * @param y - y coord of the pixel
     */
    private void calculatePixel(int x, int y, FilterImage image, int radius, int intensityLevels) {
        // Create necessary buckets
        int[] intensityLevelsCount = new int[intensityLevels];
        int[] redSum = new int[intensityLevels];
        int[] greenSum = new int[intensityLevels];
        int[] blueSum = new int[intensityLevels];

        for (int scannerX = x - radius; scannerX <= x + radius; scannerX++) {
            for (int scannerY = y - radius; scannerY <= y + radius; scannerY++) {
                if (isInsideRadius(scannerX, scannerY, x, y, radius)) {
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
        int intensityLevel = (int) (image.source.getValue(xCoord, yCoord) * (intensityLevels - 1));

        intensityLevelsCount[intensityLevel]++;
        float[] rgb = image.source.getPixel(xCoord, yCoord);
        redSum[intensityLevel] += utils.intensityToPixelValue(rgb[0]);
        greenSum[intensityLevel] += utils.intensityToPixelValue(rgb[1]);
        blueSum[intensityLevel] += utils.intensityToPixelValue(rgb[2]);
    }

    private int getMaxIntensityLevel(int[] intensityLevelsCount, int intensityLevels) {
        int maxIntensityLevel = 0;
        for (int i = 0; i < intensityLevels; i++) {
            if (intensityLevelsCount[i] > intensityLevelsCount[maxIntensityLevel]) {
                maxIntensityLevel = i;
            }
        }
        return maxIntensityLevel;
    }

    /**
     * Calculates the value of the pixel in the image giving the buckets
     */
    private void calculateFinalPixelValue(FilterImage image, int x, int y, int[] redSum, int[] greenSum, int[] blueSum, int[] intensityLevelsCount, int intensityLevel) {
        int maxIntensityLevel = getMaxIntensityLevel(intensityLevelsCount, intensityLevel);

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

    public boolean isInsideRadius(int firstX, int firstY, int secondX, int secondY, int radius) {
        return getDistanceSquared(firstX, firstY, secondX, secondY) <= radius * radius;
    }

    public float getDistanceSquared(int firstX, int firstY, int secondX, int secondY) {
        return (firstX - secondX) * (firstX - secondX) + (firstY - secondY) * (firstY - secondY);
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