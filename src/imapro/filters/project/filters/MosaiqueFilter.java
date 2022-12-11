package imapro.filters.project.filters;

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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Ripple: Don't zoom in, use the values from the other side
// Oil Painting: Other metrics, what to do if two buckets have the same count of pixels in it
// Ideas: Optimize initialization (dbscan), varied-size border based on contrast, average color (?), glass effect, other metrics, border color, heat map (for initialization)
// DBSCAN: http://home.apache.org/~luc/commons-math-3.6-RC2-site/jacoco/org.apache.commons.math3.stat.clustering/DBSCANClusterer.java.html
public class MosaiqueFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new MosaiqueFilter(env);
    public @FXML
    CheckBox applyFilter, showGrid;
    public @FXML
    Slider averageTileRadius, maxIterationsSlider, minSiteDistanceSlider;
    private int maxDistance = 1;
    private int terminalImprovement = 1; // Originally 0.01 – 1 makes calculations faster

    public MosaiqueFilter(FxEnvironment env) {
        super(env, "Mosaïque Effect", false);
        averageTileRadius = toolbar.addSlider("Average radius of tile", 1, 50, 5);
        minSiteDistanceSlider = toolbar.addSlider("Minimum site distance", 1, 50, 3);
        maxIterationsSlider = toolbar.newColumn().addSlider("Maximum Iterations", 0, 100, 10);
        showGrid = toolbar.newColumn().addCheckBox("Show Grid", false);
        applyFilter = toolbar.newColumn().addCheckBox("Apply Filter", false);

        averageTileRadius.setBlockIncrement(0.5);
        minSiteDistanceSlider.setBlockIncrement(1);
        maxIterationsSlider.setBlockIncrement(1);
    }

    @Override
    public void process(FilterImage image) {
        if (applyFilter.isSelected()) {
            double tileRadius = averageTileRadius.getValue();
            int minSiteDist = (int) minSiteDistanceSlider.getValue();
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
                if (Math.abs(lastError - currentError) < terminalImprovement) {
                    System.out.println("Error improvement under threshold, stopping iterations.");
                    break;
                }
                lastError = currentError;
                start = Instant.now();
                for (int idx = 0; idx < sites.size(); idx++) {
                    Point site = sites.get(idx);
                    Point minSite = site;
                    double minErrorDiff = 0;
                    boolean newSite = false;

                    ArrayList<Point> alternativeSites = getAlternativeSites(site, image, sites, minSiteDist);
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

            finalPainting(image, sites);

            start = Instant.now();
            System.out.println("Calculating border...");
            createBorders(image, sites, maxDistance);
            System.out.println("Border calculated in " + Duration.between(start, Instant.now()));

            if (showGrid.isSelected())
                showGrid(sites, image);
        } else {
            dontFilter(image);
        }
    }

    private void finalPainting(FilterImage image, ArrayList<Point> sites) {
        Point startingSite = sites.get(0);
        for (int y = 0; y < image.source.getHeight(); y++) {
            for (int x = 0; x < image.source.getWidth(); x++) {
                float[] nearestSiteColor = Arrays.copyOf(getNearestSiteColor(image, x, y, sites, startingSite), 3);
                image.result.setPixel(x, y, nearestSiteColor);
            }
        }
    }

    private void createBorders(FilterImage image, ArrayList<Point> sites, int maxDistance) {
        boolean unfinishedBorders = true;
        ArrayList<Point> borderPixels = new ArrayList<>();

        int lastSize = -1;
        while (unfinishedBorders && lastSize < borderPixels.size()) {
            lastSize = borderPixels.size();
            unfinishedBorders = false;
            for (int y = 0; y < image.source.getHeight(); y++) {
                for (int x = 0; x < image.source.getWidth(); x++) {
                    if (hasOtherColored4Neighbour(image, x, y, borderPixels)) {
                        unfinishedBorders = true;
                        Point next = createBorderIfOtherColored4NeighbourRight(image, x, y, sites, maxDistance, borderPixels);
                        if (next != null)
                            borderPixels.add(next);
                        next = createBorderIfOtherColored4NeighbourDown(image, x, y, sites, maxDistance, borderPixels);
                        if (next != null)
                            borderPixels.add(next);
                    }
                }
            }
        }
        for (Point borderPixel : borderPixels) {
            image.result.setPixel((int) borderPixel.x, (int) borderPixel.y, 0, 0, 0);
        }
    }

    private boolean hasOtherColored4Neighbour(FilterImage image, int x, int y, ArrayList<Point> borderPixels) {
        if (borderPixels.contains(new Point(x, y)))
            return false;
        float[] siteColor = Arrays.copyOf(image.result.getPixel(x, y), 3);
        if (x + 1 < image.result.getWidth()) {
            if (hasOtherColor(x + 1, y, image, siteColor, borderPixels))
                return true;
        }
        if (y + 1 < image.result.getHeight()) {
            if (hasOtherColor(x, y + 1, image, siteColor, borderPixels))
                return true;
        }
        if (x - 1 >= 0) {
            if (hasOtherColor(x - 1, y, image, siteColor, borderPixels))
                return true;
        }
        if (y - 1 >= 0) {
            if (hasOtherColor(x, y - 1, image, siteColor, borderPixels))
                return true;
        }
        return false;
    }

    private boolean hasOtherColor(int x, int y, FilterImage image, float[] siteColor, ArrayList<Point> borderPixels) {
        if (borderPixels.contains(new Point(x, y)))
            return false;
        float[] rightNeighbourColor = Arrays.copyOf(image.result.getPixel(x, y), 3);
        return !Arrays.equals(rightNeighbourColor, siteColor);
    }

    private Point createBorderIfOtherColored4NeighbourRight(FilterImage image, int x, int y, ArrayList<Point> sites, int maxDistance, ArrayList<Point> borderPixels) {
        float[] siteColor = Arrays.copyOf(image.result.getPixel(x, y), 3);
        if (x + 1 < image.result.getWidth() && !borderPixels.contains(new Point(x + 1, y))) {
            float[] rightNeighbourColor = Arrays.copyOf(image.result.getPixel(x + 1, y), 3);
            if (!Arrays.equals(rightNeighbourColor, siteColor)) {
                if (getNearestTwoSitesDistancesDifference(x, y, sites, maxDistance) <= getNearestTwoSitesDistancesDifference(x + 1, y, sites, maxDistance)) {
                    return new Point(x, y);
                } else {
                    return new Point(x + 1, y);
                }
            }
        }
        return null;
    }

    private Point createBorderIfOtherColored4NeighbourDown(FilterImage image, int x, int y, ArrayList<Point> sites, int maxDistance, ArrayList<Point> borderPixels) {
        float[] siteColor = Arrays.copyOf(image.result.getPixel(x, y), 3);
        if (y + 1 < image.result.getHeight() && !borderPixels.contains(new Point(x, y + 1))) {
            float[] downNeighbourColor = Arrays.copyOf(image.result.getPixel(x, y + 1), 3);
            if (!Arrays.equals(downNeighbourColor, siteColor)) {
                if (getNearestTwoSitesDistancesDifference(x, y, sites, maxDistance) <= getNearestTwoSitesDistancesDifference(x, y + 1, sites, maxDistance)) {
                    return new Point(x, y);
                } else {
                    return new Point(x, y + 1);
                }
            }
        }
        return null;
    }

    private int getNearestTwoSitesDistancesDifference(int x, int y, ArrayList<Point> sites, int maxDistance) {
        int nearestDistance = Integer.MAX_VALUE;
        int secondNearestDistance = Integer.MAX_VALUE;
        for (Point site : sites) {
            if (site.x - x > 2 * maxDistance || site.y - y > 2 * maxDistance)
                continue;
            if (getDistanceSquared(x, y, site) < nearestDistance) {
                secondNearestDistance = nearestDistance;
                nearestDistance = (int) getDistanceSquared(x, y, site);
            } else if (getDistanceSquared(x, y, site) < secondNearestDistance) {
                secondNearestDistance = (int) getDistanceSquared(x, y, site);
            }
        }
        return secondNearestDistance - nearestDistance;
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

    private ArrayList<Point> getAlternativeSites(Point site, FilterImage image, ArrayList<Point> sites, int minSiteDist) {
        ArrayList<Point> alternativeSites = new ArrayList<>();

        List<Point> minDistanceSites = sites.stream().filter(point -> getDistanceSquared(site, point) <= (minSiteDist + 1) * (minSiteDist + 1) && getDistanceSquared(site, point) > 0).collect(Collectors.toList());
        for (int xDiff = -1; xDiff <= 1; xDiff++) {
            for (int yDiff = -1; yDiff <= 1; yDiff += 1) {
                if (site.x + xDiff < 0 || site.x + xDiff >= image.source.getWidth())
                    continue;
                if (site.y + yDiff < 0 || site.y + yDiff >= image.source.getHeight())
                    continue;
                if (yDiff == 0 && xDiff == yDiff)
                    continue;

                Point candidateSite = new Point(site.x + xDiff, site.y + yDiff);
                boolean isCandidate = true;
                for (Point minDistSite : minDistanceSites)
                    if (getDistanceSquared(candidateSite, minDistSite) < minSiteDist * minSiteDist) {
                        isCandidate = false;
                    }
                if (isCandidate)
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

    private long getDistanceSquared(Point a, Point b) {
        return getDistanceSquared((int) a.x, (int) a.y, b);
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