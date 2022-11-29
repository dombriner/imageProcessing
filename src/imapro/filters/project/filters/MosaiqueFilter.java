package imapro.filters.project.filters;

import imapro.filters.utils.CalcUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import sugarcube.imapro.filters.ImaproFilterFx;
import sugarcube.imapro.filters.ImaproFilterFxLoader;
import sugarcube.imapro.image.FilterImage;
import sugarcube.imapro.image.ImaproImage;
import sugarcube.insight.core.FxEnvironment;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

// Use zoom in
public class MosaiqueFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new MosaiqueFilter(env);
    public @FXML
    CheckBox applyFilter, showGrid;
    public @FXML
    Slider averageTileRadius, maxIterationsSlider;
    private int maxDistance = 1;

    public MosaiqueFilter(FxEnvironment env) {
        super(env, "Mosaïque Effect", false);
        averageTileRadius = toolbar.addSlider("Average radius of tile", 1, 50, 5);
        maxIterationsSlider = toolbar.newColumn().addSlider("Maximum Iterations", 0, 100, 10);
        showGrid = toolbar.newColumn().addCheckBox("Show Grid", false);
        applyFilter = toolbar.newColumn().addCheckBox("Apply Filter", false);

        averageTileRadius.setBlockIncrement(0.5);
        maxIterationsSlider.setBlockIncrement(1);
    }

    @Override
    public void process(FilterImage image) {
        if (applyFilter.isSelected()) {
            double tileRadius = averageTileRadius.getValue();
            int maxIterations = (int) maxIterationsSlider.getValue();
            int height = image.source.getHeight();
            int width = image.source.getWidth();
            maxDistance = (int) Math.ceil(tileRadius);

            Instant start = Instant.now();
            ArrayList<Point> sites = initializeSites(width, height, tileRadius);
            System.out.println("Sites initialization in " + Duration.between(start, Instant.now()));

            start = Instant.now();
            double currentError = calculateColorError(image, sites);
            double lastError = currentError + 1;
            System.out.println("Color error calculated in " + Duration.between(start, Instant.now()));

            for (int it = 0; it < maxIterations; it++) {
                if (Math.abs(lastError - currentError) < 0.01) {
                    System.out.println("Error improvement under threshold, stopping iterations.");
                    it = maxIterations;
                    break;
                }
                lastError = currentError;
                start = Instant.now();
                for (int idx = 0; idx < sites.size(); idx++) {
                    Point site = sites.get(idx);
                    Point minSite = site;
                    double minErrorDiff = 0;
                    boolean newSite = false;

                    ArrayList<Point> alternativeSites = getAlternativeSites(site, image);
                    for (Point alternativeSite : alternativeSites) {
                        sites.set(idx, alternativeSite);
                        double errorDifference = calculateColorErrorDifference(image, sites, idx, maxDistance);
                        if (errorDifference < minErrorDiff) {
                            minErrorDiff = errorDifference;
                            minSite = alternativeSite;
                            newSite = true;
                        }
                        sites.set(idx, site);
                    }

                    if (newSite) {
                        sites.set(idx, minSite);
                        paintNewLocalImage(image, sites, idx, maxDistance);
                    }
                    currentError += minErrorDiff;
                }
                System.out.println("Iteration " + (it + 1) + " done in " + Duration.between(start, Instant.now()));
                System.out.println("Error improvement from " + lastError + " to " + currentError);
            }

            if (showGrid.isSelected())
                showGrid(sites, image);
        } else {
            dontFilter(image);
        }
    }

    private void paintNewLocalImage(FilterImage image, ArrayList<Point> sites, int siteIndex, int maxDistance) {
        int radiusToConsider = maxDistance + 2;
        Point alternativeSite = sites.get(siteIndex);
        for (int x = (int) (alternativeSite.x - radiusToConsider); x <= alternativeSite.x + radiusToConsider; x++) {
            if (isWithinImage(image.source, x, false)) {
                for (int y = (int) (alternativeSite.y - radiusToConsider); y <= alternativeSite.y + radiusToConsider; y++) {
                    if (isWithinImage(image.source, y, true)) {
                        paintWithClosestSiteColor(image, x, y, sites, alternativeSite);
                    }
                }
            }
        }
    }

    private double calculateColorErrorDifference(FilterImage image, ArrayList<Point> sites, int siteIndex, int maxDistance) {
        double errorDifference = 0;
        int radiusToConsider = maxDistance + 2;
        Point alternativeSite = sites.get(siteIndex);
        for (int x = (int) (alternativeSite.x - radiusToConsider); x <= alternativeSite.x + radiusToConsider; x++) {
            if (isWithinImage(image.source, x, false)) {
                for (int y = (int) (alternativeSite.y - radiusToConsider); y <= alternativeSite.y + radiusToConsider; y++) {
                    if (isWithinImage(image.source, y, true)) {
                        float[] value = image.source.getPixel(x, y).clone();
                        float[] oldSite = image.result.getPixel(x, y).clone();
                        float[] newSite = getNearestSiteColor(image, x, y, sites, alternativeSite).clone();
                        double oldError = getColorDifference(value, oldSite);
                        double newError = getColorDifference(value, newSite);
                        errorDifference += (newError - oldError);
                    }
                }
            }
        }
        return errorDifference;
    }

    private ArrayList<Point> getAlternativeSites(Point site, FilterImage image) {
        ArrayList<Point> alternativeSites = new ArrayList<>();

        for (int xDiff = -1; xDiff <= 1; xDiff++) {
            for (int yDiff = -1; yDiff <= 1; yDiff += 1) {
                if (site.x + xDiff < 0 || site.x + xDiff >= image.source.getWidth())
                    break;
                if (site.y + yDiff < 0 || site.y + yDiff >= image.source.getHeight())
                    continue;
                if (yDiff == 0 && xDiff == yDiff)
                    continue;
                alternativeSites.add(new Point(site.x + xDiff, site.y + yDiff));
            }
        }
        return alternativeSites;
    }

    private double calculateColorError(FilterImage image, ArrayList<Point> sites) {
        double colorError = 0;
        Point currentNearestPoint = null;
        for (int y = 0; y < image.source.getHeight(); y++) {
            for (int x = 0; x < image.source.getWidth(); x++) {
                paintWithClosestSiteColor(image, x, y, sites, currentNearestPoint);
                colorError += getColorDifference(image.source.getPixel(x, y),
                        image.result.getPixel(x, y));
            }
        }
        return colorError;
    }

    private float[] getNearestSiteColor(FilterImage image, int x, int y, ArrayList<Point> sites, Point startingSite) {
        Point nearestSite = startingSite;
        long minDistance = nearestSite == null ? Integer.MAX_VALUE : getDistanceSquared(x, y, nearestSite);

        for (Point site : sites) {
            if (site.x - x > minDistance || site.x - x < -minDistance
                    || site.y - y > minDistance || site.y - y < -minDistance)
                continue;
            if (getDistanceSquared(x, y, site) < minDistance) {
                minDistance = getDistanceSquared(x, y, site);
                nearestSite = site;
            }
        }
        if (Math.ceil((Math.sqrt(minDistance) / 2)) > maxDistance) {
            maxDistance = (int) Math.ceil((Math.sqrt(minDistance) / 2));
        }
        float[] pixel = image.source.getPixel((int) nearestSite.x, (int) nearestSite.y);
        return new float[]{pixel[0], pixel[1], pixel[2]};
    }

    private void paintWithClosestSiteColor(FilterImage image, int x, int y, ArrayList<Point> sites, Point startingSite) {
        image.result.setPixel(x, y, getNearestSiteColor(image, x, y, sites, startingSite));
    }


    private double getColorDifference(float[] pixel, float[] secondPixel) {
        return (pixel[0] - secondPixel[0]) * (pixel[0] - secondPixel[0])
                + (pixel[1] - secondPixel[1]) * (pixel[1] - secondPixel[1])
                + (pixel[2] - secondPixel[2]) * (pixel[2] - secondPixel[2]);
    }

    // Note: Square of distance suffices, as we only care for the order, not the actual value.
    private long getDistanceSquared(int x, int y, Point point) {
        return (x - point.x) * (x - point.x)
                + (y - point.y) * (y - point.y);
    }

    private boolean isWithinImage(ImaproImage image, int coord, boolean height) {
        if (coord < 0)
            return false;
        if (height)
            return coord <= image.getHeight();
        return coord <= image.getWidth();
    }

    private void showGrid(ArrayList<Point> sites, FilterImage image) {
        for (int y = 0; y < image.source.getHeight(); y++) {
            for (int x = 0; x < image.source.getWidth(); x++) {
                //image.result.setPixel(x, y, 1, 1, 1);
                for (Point site : sites) {
                    if (site.equals(x, y))
                        image.result.setPixel(x, y, 0, 0, 0);
                }
            }
        }
    }

    private ArrayList<Point> initializeSites(int width, int height, double tileRadius) {
        double heightDifference = Math.sqrt(3.0) * tileRadius;

        ArrayList<Point> sites = new ArrayList<>();
        double x = 0;
        double y = 0;
        int row = 0;
        while (y <= height) {
            while (x <= width) {
                sites.add(new Point(Math.round(x), Math.round(y)));
                x += 2 * tileRadius;
            }
            row++;
            x = row % 2 == 1 ? tileRadius : 0;
            y += heightDifference;
        }

        return sites;
    }

    // Just do nothing
    private void dontFilter(FilterImage image) {
        for (int y = 0; y < image.source.getHeight(); y++) {
            for (int x = 0; x < image.source.getWidth(); x++) {
                image.result.setValue(x, y, image.source.getValue(x, y));
            }
        }
    }


    protected class Point {
        protected long x;
        protected long y;

        public Point(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return x + " / " + y;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Point && ((Point) obj).x == x && ((Point) obj).y == y;
        }

        public boolean equals(long x, long y) {
            return this.x == x && this.y == y;
        }
    }

    public static void main(String... args) {
        launch(LOADER);
    }
}