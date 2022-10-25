package imapro.filters.assignment4;

import imapro.filters.utils.CalcUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.insight.core.FxEnvironment;

import java.util.Arrays;


public class ConvolutionalMeanFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new ConvolutionalMeanFilter(env);
    public @FXML ListView<String> filterSize;
    private CalcUtils utils = new CalcUtils();

    @FXML
    private void initialize() {

    }

    public ConvolutionalMeanFilter(FxEnvironment env) {
        super(env, "Convolutional mean filter", false);
        filterSize = toolbar.newColumn().addListView("Select filter size", "3x3", "5x5", "9x9");
    }

    @Override
    public void process(FilterImage image) {
        int width = image.source.getWidth();
        int height = image.source.getHeight();
        switch (filterSize.getSelectionModel().getSelectedItem()) {
            case "3x3": {
                filter(image, width, height, 3);
                break;
            }
            case "5x5": {
                filter(image, width, height, 5);
                break;
            }
            case "9x9": {
                filter(image, width, height, 9);
                break;
            }
            default: {
                // Probably not necessary, but oh well
                copyOriginalImage(image, height, width);
            }
        }
    }

    private void filter(FilterImage image, int width, int height, int kernelSize) {
        float normalizationFactor = kernelSize * kernelSize;
        float[][] kernel = new float[kernelSize][kernelSize];
        for (float[] floats : kernel) {
            Arrays.fill(floats, 1f / normalizationFactor);
        }

        utils.applyKernel(image, width, height, kernel);
    }

    private void copyOriginalImage(FilterImage image, int height, int width) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.result.setValue(x, y, image.source.getValue(x, y));
            }
        }
    }


    public static void main(String... args) {
        launch(LOADER);
    }
}