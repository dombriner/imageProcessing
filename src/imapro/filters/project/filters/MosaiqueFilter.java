package imapro.filters.project.filters;

import imapro.filters.utils.CalcUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.insight.core.FxEnvironment;

// Use zoom in
public class MosaiqueFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new MosaiqueFilter(env);
    public @FXML CheckBox applyFilter;public @FXML
    Slider intensityLevelsSlider, radiusSlider;
    private CalcUtils utils = new CalcUtils();

    public MosaiqueFilter(FxEnvironment env) {
        super(env, "Oil Painting Effect", false);
        intensityLevelsSlider = toolbar.addSlider("Intensity Levels", 1, 256, 20);
        radiusSlider = toolbar.newColumn().addSlider("Radius", 0, 100, 3);
        applyFilter = toolbar.newColumn().addCheckBox("Apply Filter", true);
    }

    @Override
    public void process(FilterImage image) {
        if (applyFilter.isSelected()) {

        } else {
            for (int y = 0; y < image.source.getHeight(); y++) {
                for (int x = 0; x < image.source.getWidth(); x++) {
                    image.result.setValue(x, y, image.source.getValue(x, y));
                }
            }
        }
    }

    public static void main(String... args) {
        launch(LOADER);
    }
}