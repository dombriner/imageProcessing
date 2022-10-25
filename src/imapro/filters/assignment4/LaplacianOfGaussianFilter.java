package imapro.filters.assignment4;

import imapro.filters.utils.CalcUtils;
import javafx.fxml.FXML;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.insight.core.FxEnvironment;


public class LaplacianOfGaussianFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new LaplacianOfGaussianFilter(env);
    private CalcUtils utils = new CalcUtils();

    @FXML
    private void initialize() {

    }

    public LaplacianOfGaussianFilter(FxEnvironment env) {
        super(env, "Laplacian of Gaussian filter (5x5)", false);
    }

    @Override
    public void process(FilterImage image) {
        float[][] kernel = {
                {0, 1, 1, 1, 0},
                {1, 3, 0, 3, 1},
                {1, 0, -24, 0, 1},
                {1, 3, 0, 3, 1},
                {0, 1, 1, 1, 0}
        };

        utils.applyKernel(image, image.source.getWidth(), image.source.getHeight(), kernel);
    }

    public static void main(String... args) {
        launch(LOADER);
    }
}