package imapro.filters.assignment4;

import imapro.filters.utils.CalcUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.insight.core.FxEnvironment;


public class MaximumMinimumFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new MaximumMinimumFilter(env);
    public @FXML ListView<String> filterSize;
    private CalcUtils utils = new CalcUtils();
    public @FXML RadioButton maximum;
    public @FXML RadioButton minimum;
    public @FXML RadioButton difference;

    @FXML
    private void initialize() {
        ToggleGroup group = new ToggleGroup();
        minimum.setToggleGroup(group);
        maximum.setToggleGroup(group);
        difference.setToggleGroup(group);
    }

    public MaximumMinimumFilter(FxEnvironment env) {
        super(env, "Maximum / minimum filter", false);
        filterSize = toolbar.newColumn().addListView("Select filter size", "3x3", "5x5", "9x9");
        minimum = toolbar.newColumn().addRadioButton("Minimum", false, null);
        maximum = toolbar.newColumn().addRadioButton("Maximum", false, null);
        difference = toolbar.newColumn().addRadioButton("Difference (max - min)", false, null);
    }

    @Override
    public void process(FilterImage image) {
        if (!minimum.isSelected()
                && !maximum.isSelected()
                && !difference.isSelected())
            return;
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
        int shift = (kernelSize - 1) / 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.result.setValue(x, y, getValue(image, x, y, height, width, shift));
            }
        }
    }

    private double getValue(FilterImage image, int x, int y, int height, int width, int shift) {
        float max = 0f;
        float min = 256f;
        for (int widthShift = - shift; widthShift <= shift; widthShift++) {
            for (int heightShift = - shift; heightShift <= shift ; heightShift++) {
                float value = utils.getValue(image, x, y, widthShift, heightShift, width, height);
                if (value > max)
                    max = value;
                if (value < min)
                    min = value;
            }
        }
        if (difference.isSelected())
            return max - min;
        return maximum.isSelected() ? max : min;
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