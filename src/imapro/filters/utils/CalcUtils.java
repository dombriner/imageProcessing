package imapro.filters.utils;

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
}
