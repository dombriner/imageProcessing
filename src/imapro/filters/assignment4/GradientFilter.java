package imapro.filters.assignment4;

import imapro.filters.utils.CalcUtils;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.insight.core.FxEnvironment;


public class GradientFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new GradientFilter(env);
    private CalcUtils utils = new CalcUtils();
    public @FXML RadioButton vertical;
    public @FXML RadioButton horizontal;
    public @FXML RadioButton robertsLeft;
    public @FXML RadioButton robertsRight;

    @FXML
    private void initialize() {
        ToggleGroup group = new ToggleGroup();
        vertical.setToggleGroup(group);
        horizontal.setToggleGroup(group);
        robertsLeft.setToggleGroup(group);
        robertsRight.setToggleGroup(group);
    }

    public GradientFilter(FxEnvironment env) {
        super(env, "Simple convolutional gradient filter", false);
        vertical = toolbar.newColumn().addRadioButton("Vertical", false, null);
        horizontal = toolbar.newColumn().addRadioButton("Horizontal", false, null);
        robertsLeft = toolbar.newColumn().addRadioButton("Roberts (diagonal bottom left)", false, null);
        robertsRight = toolbar.newColumn().addRadioButton("Roberts (diagonal top left)", false, null);
    }

    @Override
    public void process(FilterImage image) {
        int width = image.source.getWidth();
        int height = image.source.getHeight();
        if (vertical.isSelected()) {
            float[][] kernel = {{-1}, {1}};
            utils.applyKernel(image, width, height, kernel);
        }
        else if (horizontal.isSelected()) {
            float[][] kernel = {{-1, 1}};
            utils.applyKernel(image, width, height, kernel);
        } else if (robertsLeft.isSelected()) {
            float[][] kernel = {{-1, 0}, {0, 1}};
            utils.applyKernel(image, width, height, kernel);
        } else if (robertsRight.isSelected()) {
            float[][] kernel = {{0, 1}, {-1, 0}};
            utils.applyKernel(image, width, height, kernel);
        }
    }

    public static void main(String... args) {
        launch(LOADER);
    }
}