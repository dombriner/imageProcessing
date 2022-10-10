package imapro.filters.assignment1;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.insight.core.FxEnvironment;


public class FlippingFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new FlippingFilter(env);
    public @FXML CheckBox verticalFlipper;
    public @FXML CheckBox horizontalFlipper;

    public FlippingFilter(FxEnvironment env) {
        super(env, "Image Flipper", false);
        horizontalFlipper = toolbar.newColumn().addCheckBox("Flip horizontally", false);
        verticalFlipper = toolbar.newColumn().addCheckBox("Flip vertically", false);
    }

    @Override
    public void process(FilterImage image) {
        int maxWidth = image.source.getWidth();
        int maxHeight = image.source.getHeight();
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < maxWidth; x++) {
                int newX = verticalFlipper.isSelected() ? maxWidth - x : x;
                int newY = horizontalFlipper.isSelected() ? maxHeight - y : y;
                float newValue = image.source.getValue(newX, newY);
                image.result.setValue(x, y, newValue);
            }
        }
    }

    public static void main(String... args) {
        launch(LOADER);
    }
}