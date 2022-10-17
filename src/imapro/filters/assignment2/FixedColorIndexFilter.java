package imapro.filters.assignment2;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.insight.core.FxEnvironment;

import java.util.ArrayList;
import java.util.List;


public class FixedColorIndexFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new FixedColorIndexFilter(env);
    public @FXML CheckBox showColorTable;
    private List<Float[]> colorTable = new ArrayList<>();

    public FixedColorIndexFilter(FxEnvironment env) {
        super(env, "Use fixed 256 color table", false);
        showColorTable = toolbar.newColumn().addCheckBox("Show color table", false);
        colorTable.add(new Float[]{0f / 255f, 0f / 255f, 0f / 255f});
        colorTable.add(new Float[]{128f / 255f, 0f / 255f, 0f / 255f});
        colorTable.add(new Float[]{0f / 255f, 128f / 255f, 0f / 255f});
        colorTable.add(new Float[]{128f / 255f, 128f / 255f, 0f / 255f});
        colorTable.add(new Float[]{0f / 255f, 0f / 255f, 128f / 255f});
        colorTable.add(new Float[]{128f / 255f, 0f / 255f, 128f / 255f});
        colorTable.add(new Float[]{0f / 255f, 128f / 255f, 128f / 255f});
        colorTable.add(new Float[]{192f / 255f, 192f / 255f, 192f / 255f});
        colorTable.add(new Float[]{128f / 255f, 128f / 255f, 128f / 255f});
        colorTable.add(new Float[]{255f / 255f, 0f / 255f, 0f / 255f});
        colorTable.add(new Float[]{0f / 255f, 255f / 255f, 0f / 255f});
        colorTable.add(new Float[]{255f / 255f, 255f / 255f, 0f / 255f});
        colorTable.add(new Float[]{0f / 255f, 0f / 255f, 255f / 255f});
        colorTable.add(new Float[]{255f / 255f, 0f / 255f, 255f / 255f});
        colorTable.add(new Float[]{0f / 255f, 255f / 255f, 255f / 255f});
        colorTable.add(new Float[]{255f / 255f, 255f / 255f, 255f / 255f});
        colorTable.add(new Float[]{0f / 255f, 0f / 255f, 0f / 255f});
        colorTable.add(new Float[]{0f / 255f, 0f / 255f, 95f / 255f});
        colorTable.add(new Float[]{0f / 255f, 0f / 255f, 135f / 255f});
        colorTable.add(new Float[]{0f / 255f, 0f / 255f, 175f / 255f});
        colorTable.add(new Float[]{0f / 255f, 0f / 255f, 215f / 255f});
        colorTable.add(new Float[]{0f / 255f, 0f / 255f, 255f / 255f});
        colorTable.add(new Float[]{0f / 255f, 95f / 255f, 0f / 255f});
        colorTable.add(new Float[]{0f / 255f, 95f / 255f, 95f / 255f});
        colorTable.add(new Float[]{0f / 255f, 95f / 255f, 135f / 255f});
        colorTable.add(new Float[]{0f / 255f, 95f / 255f, 175f / 255f});
        colorTable.add(new Float[]{0f / 255f, 95f / 255f, 215f / 255f});
        colorTable.add(new Float[]{0f / 255f, 95f / 255f, 255f / 255f});
        colorTable.add(new Float[]{0f / 255f, 135f / 255f, 0f / 255f});
        colorTable.add(new Float[]{0f / 255f, 135f / 255f, 95f / 255f});
        colorTable.add(new Float[]{0f / 255f, 135f / 255f, 135f / 255f});
        colorTable.add(new Float[]{0f / 255f, 135f / 255f, 175f / 255f});
        colorTable.add(new Float[]{0f / 255f, 135f / 255f, 215f / 255f});
        colorTable.add(new Float[]{0f / 255f, 135f / 255f, 255f / 255f});
        colorTable.add(new Float[]{0f / 255f, 175f / 255f, 0f / 255f});
        colorTable.add(new Float[]{0f / 255f, 175f / 255f, 95f / 255f});
        colorTable.add(new Float[]{0f / 255f, 175f / 255f, 135f / 255f});
        colorTable.add(new Float[]{0f / 255f, 175f / 255f, 175f / 255f});
        colorTable.add(new Float[]{0f / 255f, 175f / 255f, 215f / 255f});
        colorTable.add(new Float[]{0f / 255f, 175f / 255f, 255f / 255f});
        colorTable.add(new Float[]{0f / 255f, 215f / 255f, 0f / 255f});
        colorTable.add(new Float[]{0f / 255f, 215f / 255f, 95f / 255f});
        colorTable.add(new Float[]{0f / 255f, 215f / 255f, 135f / 255f});
        colorTable.add(new Float[]{0f / 255f, 215f / 255f, 175f / 255f});
        colorTable.add(new Float[]{0f / 255f, 215f / 255f, 215f / 255f});
        colorTable.add(new Float[]{0f / 255f, 215f / 255f, 255f / 255f});
        colorTable.add(new Float[]{0f / 255f, 255f / 255f, 0f / 255f});
        colorTable.add(new Float[]{0f / 255f, 255f / 255f, 95f / 255f});
        colorTable.add(new Float[]{0f / 255f, 255f / 255f, 135f / 255f});
        colorTable.add(new Float[]{0f / 255f, 255f / 255f, 175f / 255f});
        colorTable.add(new Float[]{0f / 255f, 255f / 255f, 215f / 255f});
        colorTable.add(new Float[]{0f / 255f, 255f / 255f, 255f / 255f});
        colorTable.add(new Float[]{95f / 255f, 0f / 255f, 0f / 255f});
        colorTable.add(new Float[]{95f / 255f, 0f / 255f, 95f / 255f});
        colorTable.add(new Float[]{95f / 255f, 0f / 255f, 135f / 255f});
        colorTable.add(new Float[]{95f / 255f, 0f / 255f, 175f / 255f});
        colorTable.add(new Float[]{95f / 255f, 0f / 255f, 215f / 255f});
        colorTable.add(new Float[]{95f / 255f, 0f / 255f, 255f / 255f});
        colorTable.add(new Float[]{95f / 255f, 95f / 255f, 0f / 255f});
        colorTable.add(new Float[]{95f / 255f, 95f / 255f, 95f / 255f});
        colorTable.add(new Float[]{95f / 255f, 95f / 255f, 135f / 255f});
        colorTable.add(new Float[]{95f / 255f, 95f / 255f, 175f / 255f});
        colorTable.add(new Float[]{95f / 255f, 95f / 255f, 215f / 255f});
        colorTable.add(new Float[]{95f / 255f, 95f / 255f, 255f / 255f});
        colorTable.add(new Float[]{95f / 255f, 135f / 255f, 0f / 255f});
        colorTable.add(new Float[]{95f / 255f, 135f / 255f, 95f / 255f});
        colorTable.add(new Float[]{95f / 255f, 135f / 255f, 135f / 255f});
        colorTable.add(new Float[]{95f / 255f, 135f / 255f, 175f / 255f});
        colorTable.add(new Float[]{95f / 255f, 135f / 255f, 215f / 255f});
        colorTable.add(new Float[]{95f / 255f, 135f / 255f, 255f / 255f});
        colorTable.add(new Float[]{95f / 255f, 175f / 255f, 0f / 255f});
        colorTable.add(new Float[]{95f / 255f, 175f / 255f, 95f / 255f});
        colorTable.add(new Float[]{95f / 255f, 175f / 255f, 135f / 255f});
        colorTable.add(new Float[]{95f / 255f, 175f / 255f, 175f / 255f});
        colorTable.add(new Float[]{95f / 255f, 175f / 255f, 215f / 255f});
        colorTable.add(new Float[]{95f / 255f, 175f / 255f, 255f / 255f});
        colorTable.add(new Float[]{95f / 255f, 215f / 255f, 0f / 255f});
        colorTable.add(new Float[]{95f / 255f, 215f / 255f, 95f / 255f});
        colorTable.add(new Float[]{95f / 255f, 215f / 255f, 135f / 255f});
        colorTable.add(new Float[]{95f / 255f, 215f / 255f, 175f / 255f});
        colorTable.add(new Float[]{95f / 255f, 215f / 255f, 215f / 255f});
        colorTable.add(new Float[]{95f / 255f, 215f / 255f, 255f / 255f});
        colorTable.add(new Float[]{95f / 255f, 255f / 255f, 0f / 255f});
        colorTable.add(new Float[]{95f / 255f, 255f / 255f, 95f / 255f});
        colorTable.add(new Float[]{95f / 255f, 255f / 255f, 135f / 255f});
        colorTable.add(new Float[]{95f / 255f, 255f / 255f, 175f / 255f});
        colorTable.add(new Float[]{95f / 255f, 255f / 255f, 215f / 255f});
        colorTable.add(new Float[]{95f / 255f, 255f / 255f, 255f / 255f});
        colorTable.add(new Float[]{135f / 255f, 0f / 255f, 0f / 255f});
        colorTable.add(new Float[]{135f / 255f, 0f / 255f, 95f / 255f});
        colorTable.add(new Float[]{135f / 255f, 0f / 255f, 135f / 255f});
        colorTable.add(new Float[]{135f / 255f, 0f / 255f, 175f / 255f});
        colorTable.add(new Float[]{135f / 255f, 0f / 255f, 215f / 255f});
        colorTable.add(new Float[]{135f / 255f, 0f / 255f, 255f / 255f});
        colorTable.add(new Float[]{135f / 255f, 95f / 255f, 0f / 255f});
        colorTable.add(new Float[]{135f / 255f, 95f / 255f, 95f / 255f});
        colorTable.add(new Float[]{135f / 255f, 95f / 255f, 135f / 255f});
        colorTable.add(new Float[]{135f / 255f, 95f / 255f, 175f / 255f});
        colorTable.add(new Float[]{135f / 255f, 95f / 255f, 215f / 255f});
        colorTable.add(new Float[]{135f / 255f, 95f / 255f, 255f / 255f});
        colorTable.add(new Float[]{135f / 255f, 135f / 255f, 0f / 255f});
        colorTable.add(new Float[]{135f / 255f, 135f / 255f, 95f / 255f});
        colorTable.add(new Float[]{135f / 255f, 135f / 255f, 135f / 255f});
        colorTable.add(new Float[]{135f / 255f, 135f / 255f, 175f / 255f});
        colorTable.add(new Float[]{135f / 255f, 135f / 255f, 215f / 255f});
        colorTable.add(new Float[]{135f / 255f, 135f / 255f, 255f / 255f});
        colorTable.add(new Float[]{135f / 255f, 175f / 255f, 0f / 255f});
        colorTable.add(new Float[]{135f / 255f, 175f / 255f, 95f / 255f});
        colorTable.add(new Float[]{135f / 255f, 175f / 255f, 135f / 255f});
        colorTable.add(new Float[]{135f / 255f, 175f / 255f, 175f / 255f});
        colorTable.add(new Float[]{135f / 255f, 175f / 255f, 215f / 255f});
        colorTable.add(new Float[]{135f / 255f, 175f / 255f, 255f / 255f});
        colorTable.add(new Float[]{135f / 255f, 215f / 255f, 0f / 255f});
        colorTable.add(new Float[]{135f / 255f, 215f / 255f, 95f / 255f});
        colorTable.add(new Float[]{135f / 255f, 215f / 255f, 135f / 255f});
        colorTable.add(new Float[]{135f / 255f, 215f / 255f, 175f / 255f});
        colorTable.add(new Float[]{135f / 255f, 215f / 255f, 215f / 255f});
        colorTable.add(new Float[]{135f / 255f, 215f / 255f, 255f / 255f});
        colorTable.add(new Float[]{135f / 255f, 255f / 255f, 0f / 255f});
        colorTable.add(new Float[]{135f / 255f, 255f / 255f, 95f / 255f});
        colorTable.add(new Float[]{135f / 255f, 255f / 255f, 135f / 255f});
        colorTable.add(new Float[]{135f / 255f, 255f / 255f, 175f / 255f});
        colorTable.add(new Float[]{135f / 255f, 255f / 255f, 215f / 255f});
        colorTable.add(new Float[]{135f / 255f, 255f / 255f, 255f / 255f});
        colorTable.add(new Float[]{175f / 255f, 0f / 255f, 0f / 255f});
        colorTable.add(new Float[]{175f / 255f, 0f / 255f, 95f / 255f});
        colorTable.add(new Float[]{175f / 255f, 0f / 255f, 135f / 255f});
        colorTable.add(new Float[]{175f / 255f, 0f / 255f, 175f / 255f});
        colorTable.add(new Float[]{175f / 255f, 0f / 255f, 215f / 255f});
        colorTable.add(new Float[]{175f / 255f, 0f / 255f, 255f / 255f});
        colorTable.add(new Float[]{175f / 255f, 95f / 255f, 0f / 255f});
        colorTable.add(new Float[]{175f / 255f, 95f / 255f, 95f / 255f});
        colorTable.add(new Float[]{175f / 255f, 95f / 255f, 135f / 255f});
        colorTable.add(new Float[]{175f / 255f, 95f / 255f, 175f / 255f});
        colorTable.add(new Float[]{175f / 255f, 95f / 255f, 215f / 255f});
        colorTable.add(new Float[]{175f / 255f, 95f / 255f, 255f / 255f});
        colorTable.add(new Float[]{175f / 255f, 135f / 255f, 0f / 255f});
        colorTable.add(new Float[]{175f / 255f, 135f / 255f, 95f / 255f});
        colorTable.add(new Float[]{175f / 255f, 135f / 255f, 135f / 255f});
        colorTable.add(new Float[]{175f / 255f, 135f / 255f, 175f / 255f});
        colorTable.add(new Float[]{175f / 255f, 135f / 255f, 215f / 255f});
        colorTable.add(new Float[]{175f / 255f, 135f / 255f, 255f / 255f});
        colorTable.add(new Float[]{175f / 255f, 175f / 255f, 0f / 255f});
        colorTable.add(new Float[]{175f / 255f, 175f / 255f, 95f / 255f});
        colorTable.add(new Float[]{175f / 255f, 175f / 255f, 135f / 255f});
        colorTable.add(new Float[]{175f / 255f, 175f / 255f, 175f / 255f});
        colorTable.add(new Float[]{175f / 255f, 175f / 255f, 215f / 255f});
        colorTable.add(new Float[]{175f / 255f, 175f / 255f, 255f / 255f});
        colorTable.add(new Float[]{175f / 255f, 215f / 255f, 0f / 255f});
        colorTable.add(new Float[]{175f / 255f, 215f / 255f, 95f / 255f});
        colorTable.add(new Float[]{175f / 255f, 215f / 255f, 135f / 255f});
        colorTable.add(new Float[]{175f / 255f, 215f / 255f, 175f / 255f});
        colorTable.add(new Float[]{175f / 255f, 215f / 255f, 215f / 255f});
        colorTable.add(new Float[]{175f / 255f, 215f / 255f, 255f / 255f});
        colorTable.add(new Float[]{175f / 255f, 255f / 255f, 0f / 255f});
        colorTable.add(new Float[]{175f / 255f, 255f / 255f, 95f / 255f});
        colorTable.add(new Float[]{175f / 255f, 255f / 255f, 135f / 255f});
        colorTable.add(new Float[]{175f / 255f, 255f / 255f, 175f / 255f});
        colorTable.add(new Float[]{175f / 255f, 255f / 255f, 215f / 255f});
        colorTable.add(new Float[]{175f / 255f, 255f / 255f, 255f / 255f});
        colorTable.add(new Float[]{215f / 255f, 0f / 255f, 0f / 255f});
        colorTable.add(new Float[]{215f / 255f, 0f / 255f, 95f / 255f});
        colorTable.add(new Float[]{215f / 255f, 0f / 255f, 135f / 255f});
        colorTable.add(new Float[]{215f / 255f, 0f / 255f, 175f / 255f});
        colorTable.add(new Float[]{215f / 255f, 0f / 255f, 215f / 255f});
        colorTable.add(new Float[]{215f / 255f, 0f / 255f, 255f / 255f});
        colorTable.add(new Float[]{215f / 255f, 95f / 255f, 0f / 255f});
        colorTable.add(new Float[]{215f / 255f, 95f / 255f, 95f / 255f});
        colorTable.add(new Float[]{215f / 255f, 95f / 255f, 135f / 255f});
        colorTable.add(new Float[]{215f / 255f, 95f / 255f, 175f / 255f});
        colorTable.add(new Float[]{215f / 255f, 95f / 255f, 215f / 255f});
        colorTable.add(new Float[]{215f / 255f, 95f / 255f, 255f / 255f});
        colorTable.add(new Float[]{215f / 255f, 135f / 255f, 0f / 255f});
        colorTable.add(new Float[]{215f / 255f, 135f / 255f, 95f / 255f});
        colorTable.add(new Float[]{215f / 255f, 135f / 255f, 135f / 255f});
        colorTable.add(new Float[]{215f / 255f, 135f / 255f, 175f / 255f});
        colorTable.add(new Float[]{215f / 255f, 135f / 255f, 215f / 255f});
        colorTable.add(new Float[]{215f / 255f, 135f / 255f, 255f / 255f});
        colorTable.add(new Float[]{215f / 255f, 175f / 255f, 0f / 255f});
        colorTable.add(new Float[]{215f / 255f, 175f / 255f, 95f / 255f});
        colorTable.add(new Float[]{215f / 255f, 175f / 255f, 135f / 255f});
        colorTable.add(new Float[]{215f / 255f, 175f / 255f, 175f / 255f});
        colorTable.add(new Float[]{215f / 255f, 175f / 255f, 215f / 255f});
        colorTable.add(new Float[]{215f / 255f, 175f / 255f, 255f / 255f});
        colorTable.add(new Float[]{215f / 255f, 215f / 255f, 0f / 255f});
        colorTable.add(new Float[]{215f / 255f, 215f / 255f, 95f / 255f});
        colorTable.add(new Float[]{215f / 255f, 215f / 255f, 135f / 255f});
        colorTable.add(new Float[]{215f / 255f, 215f / 255f, 175f / 255f});
        colorTable.add(new Float[]{215f / 255f, 215f / 255f, 215f / 255f});
        colorTable.add(new Float[]{215f / 255f, 215f / 255f, 255f / 255f});
        colorTable.add(new Float[]{215f / 255f, 255f / 255f, 0f / 255f});
        colorTable.add(new Float[]{215f / 255f, 255f / 255f, 95f / 255f});
        colorTable.add(new Float[]{215f / 255f, 255f / 255f, 135f / 255f});
        colorTable.add(new Float[]{215f / 255f, 255f / 255f, 175f / 255f});
        colorTable.add(new Float[]{215f / 255f, 255f / 255f, 215f / 255f});
        colorTable.add(new Float[]{215f / 255f, 255f / 255f, 255f / 255f});
        colorTable.add(new Float[]{255f / 255f, 0f / 255f, 0f / 255f});
        colorTable.add(new Float[]{255f / 255f, 0f / 255f, 95f / 255f});
        colorTable.add(new Float[]{255f / 255f, 0f / 255f, 135f / 255f});
        colorTable.add(new Float[]{255f / 255f, 0f / 255f, 175f / 255f});
        colorTable.add(new Float[]{255f / 255f, 0f / 255f, 215f / 255f});
        colorTable.add(new Float[]{255f / 255f, 0f / 255f, 255f / 255f});
        colorTable.add(new Float[]{255f / 255f, 95f / 255f, 0f / 255f});
        colorTable.add(new Float[]{255f / 255f, 95f / 255f, 95f / 255f});
        colorTable.add(new Float[]{255f / 255f, 95f / 255f, 135f / 255f});
        colorTable.add(new Float[]{255f / 255f, 95f / 255f, 175f / 255f});
        colorTable.add(new Float[]{255f / 255f, 95f / 255f, 215f / 255f});
        colorTable.add(new Float[]{255f / 255f, 95f / 255f, 255f / 255f});
        colorTable.add(new Float[]{255f / 255f, 135f / 255f, 0f / 255f});
        colorTable.add(new Float[]{255f / 255f, 135f / 255f, 95f / 255f});
        colorTable.add(new Float[]{255f / 255f, 135f / 255f, 135f / 255f});
        colorTable.add(new Float[]{255f / 255f, 135f / 255f, 175f / 255f});
        colorTable.add(new Float[]{255f / 255f, 135f / 255f, 215f / 255f});
        colorTable.add(new Float[]{255f / 255f, 135f / 255f, 255f / 255f});
        colorTable.add(new Float[]{255f / 255f, 175f / 255f, 0f / 255f});
        colorTable.add(new Float[]{255f / 255f, 175f / 255f, 95f / 255f});
        colorTable.add(new Float[]{255f / 255f, 175f / 255f, 135f / 255f});
        colorTable.add(new Float[]{255f / 255f, 175f / 255f, 175f / 255f});
        colorTable.add(new Float[]{255f / 255f, 175f / 255f, 215f / 255f});
        colorTable.add(new Float[]{255f / 255f, 175f / 255f, 255f / 255f});
        colorTable.add(new Float[]{255f / 255f, 215f / 255f, 0f / 255f});
        colorTable.add(new Float[]{255f / 255f, 215f / 255f, 95f / 255f});
        colorTable.add(new Float[]{255f / 255f, 215f / 255f, 135f / 255f});
        colorTable.add(new Float[]{255f / 255f, 215f / 255f, 175f / 255f});
        colorTable.add(new Float[]{255f / 255f, 215f / 255f, 215f / 255f});
        colorTable.add(new Float[]{255f / 255f, 215f / 255f, 255f / 255f});
        colorTable.add(new Float[]{255f / 255f, 255f / 255f, 0f / 255f});
        colorTable.add(new Float[]{255f / 255f, 255f / 255f, 95f / 255f});
        colorTable.add(new Float[]{255f / 255f, 255f / 255f, 135f / 255f});
        colorTable.add(new Float[]{255f / 255f, 255f / 255f, 175f / 255f});
        colorTable.add(new Float[]{255f / 255f, 255f / 255f, 215f / 255f});
        colorTable.add(new Float[]{255f / 255f, 255f / 255f, 255f / 255f});
        colorTable.add(new Float[]{8f / 255f, 8f / 255f, 8f / 255f});
        colorTable.add(new Float[]{18f / 255f, 18f / 255f, 18f / 255f});
        colorTable.add(new Float[]{28f / 255f, 28f / 255f, 28f / 255f});
        colorTable.add(new Float[]{38f / 255f, 38f / 255f, 38f / 255f});
        colorTable.add(new Float[]{48f / 255f, 48f / 255f, 48f / 255f});
        colorTable.add(new Float[]{58f / 255f, 58f / 255f, 58f / 255f});
        colorTable.add(new Float[]{68f / 255f, 68f / 255f, 68f / 255f});
        colorTable.add(new Float[]{78f / 255f, 78f / 255f, 78f / 255f});
        colorTable.add(new Float[]{88f / 255f, 88f / 255f, 88f / 255f});
        colorTable.add(new Float[]{98f / 255f, 98f / 255f, 98f / 255f});
        colorTable.add(new Float[]{108f / 255f, 108f / 255f, 108f / 255f});
        colorTable.add(new Float[]{118f / 255f, 118f / 255f, 118f / 255f});
        colorTable.add(new Float[]{128f / 255f, 128f / 255f, 128f / 255f});
        colorTable.add(new Float[]{138f / 255f, 138f / 255f, 138f / 255f});
        colorTable.add(new Float[]{148f / 255f, 148f / 255f, 148f / 255f});
        colorTable.add(new Float[]{158f / 255f, 158f / 255f, 158f / 255f});
        colorTable.add(new Float[]{168f / 255f, 168f / 255f, 168f / 255f});
        colorTable.add(new Float[]{178f / 255f, 178f / 255f, 178f / 255f});
        colorTable.add(new Float[]{188f / 255f, 188f / 255f, 188f / 255f});
        colorTable.add(new Float[]{198f / 255f, 198f / 255f, 198f / 255f});
        colorTable.add(new Float[]{208f / 255f, 208f / 255f, 208f / 255f});
        colorTable.add(new Float[]{218f / 255f, 218f / 255f, 218f / 255f});
        colorTable.add(new Float[]{228f / 255f, 228f / 255f, 228f / 255f});
        colorTable.add(new Float[]{238f / 255f, 238f / 255f, 238f / 255f});
    }

    @Override
    public void process(FilterImage image) {
        if (showColorTable.isSelected()) {
            int idx = 0;
            int dim = 16;
            image.result.setDimensions(dim, dim);
            for (int y = 0; y < dim; y++) {
                for (int x = 0; x < dim; x++) {
                    Float[] nextColor = colorTable.get(idx);
                    image.result.setPixel(x, y, new float[]{nextColor[0], nextColor[1], nextColor[2]});
                    idx++;
                }
            }
        } else {
            int maxWidth = image.source.getWidth();
            int maxHeight = image.source.getHeight();
            for (int y = 0; y < maxHeight; y++) {
                for (int x = 0; x < maxWidth; x++) {
                    float[] oldValue = image.source.getPixel(x, y);
                    Float[] currentColor = colorTable.get(0);
                    float currentDistance = getDistanceSqaured(oldValue, currentColor);
                    for (Float[] color : colorTable) {
                        if (currentDistance > getDistanceSqaured(oldValue, color)) {
                            currentColor = color;
                            currentDistance = getDistanceSqaured(oldValue, color);
                        }
                    }
                    image.result.setPixel(x, y, new float[]{currentColor[0], currentColor[1], currentColor[2]});
                }
            }
        }
    }

    // Note: Square of distance suffices, as we only care for the order, not the actual value.
    public float getDistanceSqaured(float[] first, Float[] second) {
        return (first[0] - second[0]) * (first[0] - second[0])
                + (first[1] - second[1]) * (first[1] - second[1])
                + (first[2] - second[2]) * (first[2] - second[2]);
    }

    public static void main(String... args) {
        launch(LOADER);
    }
}