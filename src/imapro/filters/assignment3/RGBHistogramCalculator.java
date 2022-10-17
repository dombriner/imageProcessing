package imapro.filters.assignment3;

import sugarcube.imapro.image.ImaproImage;

public class RGBHistogramCalculator extends HistogramCalculator {
    protected int colorIndex;

    public RGBHistogramCalculator(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    @Override
    public int getHistogramIndex(ImaproImage image, int x, int y) {
        return utils.intensityToPixelValue(image.getPixel(x, y)[colorIndex]);
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }
}
