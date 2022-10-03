package imapro.filters.point;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.insight.core.FxEnvironment;


public class ContrastFilterFx extends ImaproFilterFx
{
    public static ImaproFilterFxLoader LOADER = env -> new ContrastFilterFx(env);
    public @FXML Slider contrastSlider, luminositySlider;
    public @FXML CheckBox negativeCheckbox;

    public ContrastFilterFx(FxEnvironment env)
    {
        super(env, "Contrast & Luminosity", false);
        contrastSlider = toolbar.addSlider("Contrast", -100, 100, 0);
        luminositySlider = toolbar.newColumn().addSlider("Luminosity", -100, 100, 0);
        negativeCheckbox = toolbar.newColumn().addCheckBox("Negative Image", false);
    }

    @Override
    public void process(FilterImage image)
    {
        float contrast = (float) Math.tan((0.5 + contrastSlider.getValue() / 200) * Math.PI / 2.0);
        float luminosity = (float) luminositySlider.getValue() / 100f;

        if(negativeCheckbox.isSelected())
            contrast = -contrast;

        for (int y = 0; y < image.source.getHeight(); y++)
        {
            for (int x = 0; x < image.source.getWidth(); x++)
            {
                float value = (image.source.getValue(x, y) - 0.5f) * contrast + 0.5f;
                image.result.setValue(x, y, value < 0 ? 0 : (value > 1 ? 1 : value) + luminosity);
            }
        }
    }

    public static void main(String... args)
    {
        launch(LOADER);
    }
}