package imapro.filters.assignment3;

import sugarcube.imapro.image.ImaproImage;

public class GreyScaleHistogramCalculator extends HistogramCalculator {


    @Override
    public int getHistogramIndex(ImaproImage image, int x, int y) {
        return utils.intensityToPixelValue(image.getValue(x, y));
    }
}
