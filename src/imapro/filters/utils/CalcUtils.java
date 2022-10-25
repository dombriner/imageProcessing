package imapro.filters.utils;

import sugarcube.imapro.image.FilterImage;

public class CalcUtils {

    public int intensityToPixelValue(float intensity) {
        return ((int) (255f * intensity));
    }

    public int cumulativeHistogram(int[] histogram, int value) {
        int sum = 0;
        for (int i = 0; i <= value; i++)
            sum += histogram[i];
        return sum;
    }

    public void applyKernel(FilterImage image, int width, int height, float[][] kernel) {
        int widthShift = (kernel[0].length - 1) / 2;
        int heightShift = (kernel.length - 1) / 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.result.setValue(x, y, applyKernel(image, x, y, height, width, kernel, widthShift, heightShift));
            }
        }
    }

    private Double applyKernel(FilterImage image, int x, int y, int height, int width, float[][] kernel, int widthShift, int heightShift) {
        double sum = 0d;
        for (int w = 0; w < kernel.length; w++) {
            for (int h = 0; h < kernel[0].length; h++) {
                sum += kernel[w][h] * getValue(image, x, y, - widthShift + w, - heightShift + h, width, height);
            }
        }
        return sum;
    }

    public float getValue(FilterImage image, int width, int height, int widthShift, int heightShift, int imageWidth, int imageHeight) {
        int virtualWidth = width + widthShift;
        int virtualHeight = height + heightShift;
        if (virtualWidth < 0)
            virtualWidth = - virtualWidth;
        if (virtualHeight < 0)
            virtualHeight = - virtualHeight;
        return image.source.getValue(virtualWidth % imageWidth, virtualHeight % imageHeight);
    }
}
