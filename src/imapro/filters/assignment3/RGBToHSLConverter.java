package imapro.filters.assignment3;

public class RGBToHSLConverter {

    public float[] convert(float[] rgb) {
        int cMaxIndex = 0;
        int cMinIndex = 0;
        for (int i = 1; i < 3; i++) {
            if (rgb[i] > rgb[cMaxIndex])
                cMaxIndex = i;
            if (rgb[i] < rgb[cMinIndex])
                cMinIndex = i;
        }
        float delta = rgb[cMaxIndex] - rgb[cMinIndex];

        float lightness = (rgb[cMaxIndex] + rgb[cMinIndex]) / 2f;
        float hue = calculateHue(rgb, cMaxIndex, delta);
        float saturation = calculateSaturation(delta, lightness);

        return new float[]{hue, saturation, lightness};
    }

    private float calculateHue(float[] rgb, int cMaxIndex, float delta) {
        if (delta == 0)
            return 0;
        float factor = 0;
        switch (cMaxIndex) {
            case 0: {
                factor = ((rgb[1] - rgb[2])/delta) % 6f;
                break;
            }
            case 1: {
                factor = ((rgb[2] - rgb[0])/delta) + 2;
                break;
            }
            case 2: {
                factor = ((rgb[0] - rgb[1])/delta) + 4;
                break;
            }
        }

        return 60 * factor;
    }

    private float calculateSaturation(float delta, float lightness) {
        if (delta == 0)
            return 0;
        float denominator = 1 - Math.abs(2f*lightness-1);
        return delta / denominator;
    }
}
