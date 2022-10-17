package imapro.filters.assignment3;

import imapro.filters.utils.CalcUtils;
import sugarcube.imapro.image.ImaproImage;

public abstract class HistogramCalculator {
    protected CalcUtils utils = new CalcUtils();

    public int[] calcHistogram(ImaproImage image) {
        int[] histogram = new int[256];
        int maxWidth = image.getWidth();
        int maxHeight = image.getHeight();
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < maxWidth; x++) {
                int index = getHistogramIndex(image, x, y);
                histogram[index]++;
            }
        }
        return histogram;
    }

    public abstract int getHistogramIndex(ImaproImage image, int x, int y);
}
