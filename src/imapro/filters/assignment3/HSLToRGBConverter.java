package imapro.filters.assignment3;

public class HSLToRGBConverter {

    public float[] convert(float[] hsl) {
        float c = calculateC(hsl);
        float x = calculateX(hsl, c);
        float m = hsl[2] - c / 2f;

        float normedHue = hsl[0] % 360f;
        float[] rgbPrime = new float[3];

        if (normedHue < 60)
            rgbPrime = new float[]{c, x, 0};
        else if (normedHue < 120)
            rgbPrime = new float[]{x, c, 0};
        else if (normedHue < 180)
            rgbPrime = new float[]{0, c, x};
        else if (normedHue < 240)
            rgbPrime = new float[]{0, x, c};
        else if (normedHue < 300)
            rgbPrime = new float[]{x, 0, c};
        else if (normedHue < 360)
            rgbPrime = new float[]{c, 0, x};
        return new float[]{rgbPrime[0] + m, rgbPrime[1] + m, rgbPrime[2] + m};
    }

    private float calculateC(float[] hsl) {
        float factor = 1 - Math.abs(2f*hsl[2]-1);
        return factor * hsl[1];
    }

    private float calculateX(float[] hsl, float c) {
        float factor = 1- Math.abs((hsl[0] / 60f) % 2f - 1);
        return c * factor;
    }
}
