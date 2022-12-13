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
import java.util.*;
import java.util.stream.Collectors;

// Ideas: Optimize initialization (dbscan), varied-size border based on contrast, average color (?), glass effect, other metrics, border color
// DBSCAN: http://home.apache.org/~luc/commons-math-3.6-RC2-site/jacoco/org.apache.commons.math3.stat.clustering/DBSCANClusterer.java.html
public class MosaiqueFilter extends ImaproFilterFx {
    public static ImaproFilterFxLoader LOADER = env -> new MosaiqueFilter(env);
    public @FXML
    CheckBox applyFilter, showGrid, showBorder, smartInitialization, heatMapInitialization;
    public @FXML
    Slider averageTileRadius, maxIterationsSlider, minSiteDistanceSlider, epsilonSlider, minClusterSizeSlider;
    private int maxDistance = 1;
    private int terminalImprovement = 1; // Originally 0.01 – 1 makes calculations faster

    public MosaiqueFilter(FxEnvironment env) {
        super(env, "Mosaïque Effect", false);
        averageTileRadius = toolbar.addSlider("Average radius of tile", 1, 50, 5);
        minSiteDistanceSlider = toolbar.addSlider("Minimum site distance", 1, 50, 3);
        maxIterationsSlider = toolbar.newColumn().addSlider("Maximum Iterations", 0, 100, 10);
        epsilonSlider = toolbar.newColumn().addSlider("Epsilon", 1, 300, 5);
        minClusterSizeSlider = toolbar.addSlider("Min Cluster Size", 1, 100, 5);
        smartInitialization = toolbar.newColumn().addCheckBox("DBSCAN initialization", false);
        heatMapInitialization = toolbar.addCheckBox("Heat Map Initialization", false);

        showGrid = toolbar.newColumn().addCheckBox("Show Grid", false);
        showBorder = toolbar.addCheckBox("Show Border", true);
        applyFilter = toolbar.newColumn().addCheckBox("Apply Filter", false);

        averageTileRadius.setBlockIncrement(0.5);
        minSiteDistanceSlider.setBlockIncrement(1);
        maxIterationsSlider.setBlockIncrement(1);
        epsilonSlider.setBlockIncrement(0.5);
        minClusterSizeSlider.setBlockIncrement(1);
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
            System.out.println("Initializing sites...");
            List<Point> sites;
            if (smartInitialization.isSelected()) {
                sites = initializeSitesDBScan(image, (float) epsilonSlider.getValue() / 100000, (int) minClusterSizeSlider.getValue(), 1.5);
            } else if (heatMapInitialization.isSelected() ){
                sites = initializeSitesHeatMapStyle(image, tileRadius);
            } else {
                sites = initializeSitesNaïve(width, height, tileRadius);
            }
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

            start = Instant.now();
            System.out.println("Final paint job...");
            finalPainting(image, sites);
            System.out.println("Final tiles painted in " + Duration.between(start, Instant.now()));

            if (showBorder.isSelected()) {
                start = Instant.now();
                System.out.println("Calculating border...");
                createBorders(image, sites, maxDistance);
                System.out.println("Border calculated in " + Duration.between(start, Instant.now()));
            }

            if (showGrid.isSelected())
                showGrid(sites, image);
        } else {
            dontFilter(image);
        }
    }

    private List<Point> initializeSitesHeatMapStyle(FilterImage image, double averageTileRadius) {
        int width = image.source.getWidth();
        int height = image.source.getHeight();
        int size = width * height;
        double averageSize = averageTileRadius * averageTileRadius * Math.PI;
        double density = size / averageSize;

        int numberOfTiles = (int) density;
        double overallScore = getHeatMapScore(image, 0, 0, width, height, width, height);

        return calculateSites(image, overallScore, averageSize, numberOfTiles, 0, 0, width, height);
    }

    private List<Point> calculateSites(FilterImage image, double overallScore, double averageSize, int numberOfTiles, int xStart, int yStart, int xEnd, int yEnd) {
        double scorePerSite = overallScore / numberOfTiles;
        List<Point> sites = new ArrayList<>();

        double currentScore = getHeatMapScore(image, xStart, yStart, xEnd, yEnd, image.source.getWidth(), image.source.getHeight());
        if (((int) currentScore / scorePerSite) > 4 && (xEnd-xStart)*(yEnd-yStart) > 1.5 * averageSize) {
            int xHalf = (xEnd - xStart)/2 + xStart;
            int yHalf = (yEnd - yStart)/2 + yStart;
            sites = addAllWithoutDuplicates(calculateSites(image, overallScore, averageSize, numberOfTiles, xStart, yStart, xHalf, yHalf), sites);
            sites = addAllWithoutDuplicates(calculateSites(image, overallScore, averageSize, numberOfTiles, xHalf, yStart, xEnd, yHalf), sites);
            sites = addAllWithoutDuplicates(calculateSites(image, overallScore, averageSize, numberOfTiles, xStart, yHalf, xHalf, yEnd), sites);
            sites = addAllWithoutDuplicates(calculateSites(image, overallScore, averageSize, numberOfTiles, xHalf, yHalf, xEnd, yEnd), sites);
            return sites;
        } else {
            int sitesToCalculate = (int) Math.round(currentScore / scorePerSite);
            return optimizeSites(image, xStart, xEnd, yStart, yEnd, sitesToCalculate);
        }
    }

    private List<Point> optimizeSites(FilterImage image, int xStart, int xEnd, int yStart, int yEnd, int sitesToCalculate) {
        if (sitesToCalculate == 0)
            sitesToCalculate = 1;
        List<Point> bestSites = new ArrayList<>();
        Random rand = new Random();
        int lowestError = Integer.MAX_VALUE;
        int minDistance = (int) minSiteDistanceSlider.getValue();

        for (int i = 0; i < 50; i++) {
            List<Point> sites = new ArrayList<>();
            int countDown = 50;
            while (sites.size() < sitesToCalculate && countDown > 0) {
                Point candidate = new Point(rand.nextInt(xEnd - xStart) + xStart, rand.nextInt(yEnd - yStart) + yStart);
                if (!sites.contains(candidate)) {
                    boolean canAdd = true;
                    for (Point site: sites) {
                        if (getDistanceSquared(site, candidate) < minDistance * minDistance) {
                            canAdd = false;
                            countDown--;
                            break;
                        }
                    }
                    if (canAdd)
                        sites.add(candidate);
                }
            }

            int colorError = 0;
            for (int x = xStart; x < xEnd; x++) {
                for (int y = yStart; y < yEnd; y++) {
                    paintWithClosestSiteColor(image, x, y, sites, sites.get(0));
                    colorError += getColorDifference(image.source.getPixel(x, y), image.result.getPixel(x, y));
                }
            }
            if (colorError < lowestError) {
                lowestError = colorError;
                bestSites = new ArrayList<>();
                bestSites.addAll(sites);
            }
        }

        return bestSites;
    }

    private double getHeatMapScore(FilterImage image, int xStart, int yStart, int xEnd, int yEnd, int width, int height) {
        double score = 0.0d;
        for (int x = xStart; x < xEnd; x++) {
            for (int y = yStart; y < yEnd; y++) {
                float[] currentPixel = Arrays.copyOf(image.source.getPixel(x, y), 3);
                if (x + 1 < width) {
                    score += getColorDifference(currentPixel, Arrays.copyOf(image.source.getPixel(x+1, y), 3));
                    if (y + 1 < height) {
                        // Weigh by about 1/sqrt(2) since it's that much longer center to center (diagonal)
                        score += 0.707 * getColorDifference(currentPixel, Arrays.copyOf(image.source.getPixel(x+1, y+1), 3));
                    }
                }
                if (y + 1 < height) {
                    score += getColorDifference(currentPixel, Arrays.copyOf(image.source.getPixel(x, y+1), 3));
                }
            }
        }
        return score;
    }

    private void finalPainting(FilterImage image, List<Point> sites) {
        Point startingSite = sites.get(0);
        for (int y = 0; y < image.source.getHeight(); y++) {
            for (int x = 0; x < image.source.getWidth(); x++) {
                float[] nearestSiteColor = Arrays.copyOf(getNearestSiteColor(image, x, y, sites, startingSite), 3);
                image.result.setPixel(x, y, nearestSiteColor);
            }
        }
    }

    private void createBorders(FilterImage image, List<Point> sites, int maxDistance) {
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

    private boolean hasOtherColored4Neighbour(FilterImage image, int x, int y, List<Point> borderPixels) {
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

    private boolean hasOtherColor(int x, int y, FilterImage image, float[] siteColor, List<Point> borderPixels) {
        if (borderPixels.contains(new Point(x, y)))
            return false;
        float[] rightNeighbourColor = Arrays.copyOf(image.result.getPixel(x, y), 3);
        return !Arrays.equals(rightNeighbourColor, siteColor);
    }

    private Point createBorderIfOtherColored4NeighbourRight(FilterImage image, int x, int y, List<Point> sites, int maxDistance, List<Point> borderPixels) {
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

    private Point createBorderIfOtherColored4NeighbourDown(FilterImage image, int x, int y, List<Point> sites, int maxDistance, List<Point> borderPixels) {
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

    private int getNearestTwoSitesDistancesDifference(int x, int y, List<Point> sites, int maxDistance) {
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

    private void paintNewLocalImage(FilterImage image, List<Point> sites, int siteIndex, int maxDistance) {
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

    private double calculateColorErrorDifference(FilterImage image, List<Point> sites, int siteIndex, int maxDistance) {
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

    private ArrayList<Point> getAlternativeSites(Point site, FilterImage image, List<Point> sites, int minSiteDist) {
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

    private double calculateColorError(FilterImage image, List<Point> sites) {
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

    private float[] getNearestSiteColor(FilterImage image, int x, int y, List<Point> sites, Point startingSite) {
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

    private void paintWithClosestSiteColor(FilterImage image, int x, int y, List<Point> sites, Point startingSite) {
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

    private void showGrid(List<Point> sites, FilterImage image) {
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

    private ArrayList<Point> initializeSitesNaïve(int width, int height, double tileRadius) {
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

    private ArrayList<Point> initializeSitesDBScan(FilterImage image, float epsilon, int minClusterSize, double maxClusterNeighbourDistance) {
        final List<List<Point>> clusters = getClusters(image, epsilon, minClusterSize, maxClusterNeighbourDistance);
        ArrayList<Point> sites = new ArrayList<>();

        for (List<Point> cluster: clusters) {
            sites.add(getSiteEstimate(cluster));
        }

        return sites;
    }

    private Point getSiteEstimate(List<Point> cluster) {
        Point site = null;
        int lowestScore = Integer.MAX_VALUE;

        for (Point candidate: cluster) {
            int score = 0;
            for (Point point: cluster) {
                score += getDistanceSquared(candidate, point);
                if (score <= lowestScore) {
                    lowestScore = score;
                    site = candidate;
                }
            }
        }
        return site;
    }

    private List<List<Point>> getClusters(FilterImage image, float epsilon, int minClusterSize, double maxClusterNeighbourDistance) {
        final List<List<Point>> clusters = new ArrayList<>();
        final Map<Point, String> visited = new HashMap<>();

        final List<Point> points = new ArrayList<>();
        for (int y = 0; y < image.source.getHeight(); y++) {
            for (int x = 0; x < image.source.getWidth(); x++) {
                points.add(new Point(x, y));
            }
        }

        int done = 0;
        int fivePercentSteps = 0;

        for (Point point: points) {
            if (visited.get(point) != null)
                continue;
            final List<Point> region = regionQuery(point, points, epsilon, maxClusterNeighbourDistance, image);
            if (region.size() >= minClusterSize) {
                final List<Point> cluster = new ArrayList<>();
                List<Point> nextCluster = expandCluster(cluster, point, region, points, visited, epsilon, minClusterSize, maxClusterNeighbourDistance, image);
                done += nextCluster.size();
                if (((int) ((float) done / points.size() * 100)) / 5 > fivePercentSteps) {
                    fivePercentSteps++;
                    System.out.println("Progress: " + (float) done / points.size());
                }
                clusters.add(nextCluster);
            } else {
                visited.put(point, "NOISE");
            }
        }

        int clustered = 0;
        int noise = 0;
        for (Point point: visited.keySet()) {
            if (visited.get(point).equals("DONE"))
                clustered++;
            else
                noise++;
        }
        System.out.println("Cluster number: " + clusters.size());
        System.out.println("Percentage clustered: " + (float) clustered / points.size());
        System.out.println("Percentage noise: " + (float) noise / points.size());
        return clusters;
    }

    private List<Point> expandCluster(List<Point> cluster, Point point, List<Point> region, List<Point> points, Map<Point, String> visited, float epsilon, int minClusterSize, double maxClusterNeighbourDistance, FilterImage image) {
        cluster.add(point);
        visited.put(point, "DONE");

        List<Point> candidates = new ArrayList<>(region);
        int idx = 0;
        while (idx < candidates.size()) {
            Point current = candidates.get(idx);
            String status = visited.get(current);

            if (status == null) {
                List<Point> currentRegion = regionQuery(current, points, epsilon, maxClusterNeighbourDistance, image);
                if (currentRegion.size() >= minClusterSize)
                    candidates = addAllWithoutDuplicates(candidates, currentRegion);
            }

            if (!"DONE".equals(status)) {
                visited.put(current, "DONE");
                cluster.add(current);
            }
            idx++;
        }
//        System.out.println("Created cluster with size " + cluster.size());
        return cluster;
    }

    private List<Point> addAllWithoutDuplicates(List<Point> candidates, List<Point> currentRegion) {
        for (Point regional: currentRegion) {
            if (!candidates.contains(regional)) {
                candidates.add(regional);
            }
        }

        return candidates;
    }

    private List<Point> regionQuery(Point point, List<Point> points, float epsilon, double maxClusterNeighbourDistance, FilterImage image) {
        final List<Point> region = new ArrayList<>();
        for (Point candidate: points) {
            if (getDistanceSquared(point, candidate) <= maxClusterNeighbourDistance * maxClusterNeighbourDistance
                    && (getColorDifference(Arrays.copyOf(image.source.getPixel((int) candidate.x, (int) candidate.y), 3), Arrays.copyOf(image.source.getPixel((int) point.x, (int) point.y), 3))) / 3 <= epsilon
                    && !candidate.equals(point))
                region.add(candidate);
        }
        return region;
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