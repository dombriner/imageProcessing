package imapro.filters.assignment6;

import imapro.filters.utils.CalcUtils;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.insight.core.FxEnvironment;

import java.util.ArrayList;
import java.util.Collections;


/**
 * NOTE: While I managed to encode and decode the image, I cannot use the compression on the image provided as it is a
 *  gif and this program cannot load gifs…
 */
public class CompressionDecompressionFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new CompressionDecompressionFilter(env);
    private CalcUtils utils = new CalcUtils();

    public CompressionDecompressionFilter(FxEnvironment env) {
        super(env, "2D RLE compression decompression filter (does nothing)", false);
    }

    /**
     * NOTES: This filter does nothing of consequence as it compresses the image immediately followed by decompressing it.
     */
    @Override
    public void process(FilterImage image) {
        ArrayList<Integer> compressedImage = getCompressedImage(image);

        decompressImage(compressedImage, image);
    }

    /**
     * NOTE: Performs 2D RLE compression, but stores as an arraylist of integers; only thing left to do is to convert to
     * bytes which is trivial.
     */
    private ArrayList<Integer> getCompressedImage(FilterImage image) {
        int width = image.source.getWidth();
        int height = image.source.getHeight();

        ArrayList<Integer> compressedImage = new ArrayList<>();

        ArrayList<Integer> sortedColors = getColors(image, width, height);

        ArrayList<Integer> rowHeights = getRowHeights(image, width, height);

        compressedImage.add(width);
        compressedImage.add(height);
        compressedImage.add(sortedColors.size());
        compressedImage.addAll(sortedColors);
        int row = 0;
        for (int rowHeight: rowHeights) {
            compressedImage.add(rowHeight);
            int tailColumn = 0;
            int headColumn = 1;
            while (headColumn < width) {
                if (getIndex(utils.intensityToPixelValue(image.source.getValue(tailColumn, row)), sortedColors) != getIndex(utils.intensityToPixelValue(image.source.getValue(headColumn, row)), sortedColors)) {
                    compressedImage.add(getIndex(utils.intensityToPixelValue(image.source.getValue(tailColumn, row)), sortedColors));
                    compressedImage.add(headColumn - tailColumn);
                    tailColumn = headColumn;
                }
                headColumn++;
                if (headColumn == width) {
                    compressedImage.add(getIndex(utils.intensityToPixelValue(image.source.getValue(tailColumn, row)), sortedColors));
                    compressedImage.add(headColumn - tailColumn);
                }
            }
        }
        return compressedImage;
    }

    private void decompressImage(ArrayList<Integer> compressedImage, FilterImage image) {
        int width = compressedImage.get(0);
        int height = compressedImage.get(1);
        int numberOfColors = compressedImage.get(2);
        ArrayList<Integer> sortedColors = new ArrayList();
        for (int i = 3; i < 3 + numberOfColors; i++) {
            sortedColors.add(compressedImage.get(i));
        }
        int index = 3 + numberOfColors;

        int currentHeight = 0;
        int currentWidth = 0;
        int rowHeight = 0;
        while (currentHeight + rowHeight < height) {
            rowHeight = compressedImage.get(index);
            index++;
            while (currentWidth < width) {
                int colorIndex = compressedImage.get(index);
                float color = ((sortedColors.get(colorIndex).floatValue())) / 255f;
                index++;
                int bandWidth = compressedImage.get(index);
                index++;
                for (int h = currentHeight; h < currentHeight + rowHeight; h++) {
                    for (int w = currentWidth; w < currentWidth + bandWidth; w++) {
                        image.result.setValue(w, h, color);
                    }
                }
                currentWidth += bandWidth;
            }
        }
    }

    private int getIndex(int intensityToPixelValue, ArrayList<Integer> sortedColors) {
        return sortedColors.indexOf(intensityToPixelValue);
    }

    private ArrayList<Integer> getColors(FilterImage image, int width, int height) {
        ArrayList<Integer> colors = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = utils.intensityToPixelValue(image.source.getValue(x, y));
                if (!colors.contains(value))
                    colors.add(value);
            }
        }
        Collections.sort(colors);
        return colors;
    }

    private ArrayList<Integer> getRowHeights(FilterImage image, int width, int height) {
        ArrayList<Integer> rowHeights = new ArrayList<>();
        int tailRow = 0;
        int headRow = 1;
        while (headRow < height) {
            for (int x = 0; x < width; x++) {
                if (utils.intensityToPixelValue(image.source.getValue(x, tailRow)) != utils.intensityToPixelValue(image.source.getValue(x, headRow))) {
                    rowHeights.add(headRow-tailRow);
                    tailRow = headRow;
                    headRow++;
                    break;
                }
            }
        }
        int sum = 0;
        for (int rowHeight: rowHeights) {
            sum += rowHeight;
        }
        if (sum != height)
            System.out.println("Oh no!");
        return rowHeights;
    }

    public static void main(String... args) {
        launch(LOADER);
    }
}