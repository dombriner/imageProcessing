package imapro.filters.assignment3;

import sugarcube.imapro.image.ImaproImage;

public class HSLHistogramCalculator extends HistogramCalculator {

    @Override
    public int getHistogramIndex(ImaproImage image, int x, int y) {
        float[] rgb = image.getPixel(x, y);
        float lightness = new RGBToHSLConverter().convert(rgb)[2];
        return utils.intensityToPixelValue(lightness);
    }
}
