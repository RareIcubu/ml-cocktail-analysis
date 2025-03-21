package mlcocktail.evaluation;

import java.util.*;
import smile.math.distance.EuclideanDistance;

public class Evaluator {

    private static final EuclideanDistance DISTANCE = new EuclideanDistance();

    public static void Evaluate(double[][] data, int[] labels) {
        double silhouette = silhouetteScore(data, labels);
        double daviesBouldin = daviesBouldinIndex(data, labels);
        double calinskiHarabasz = calinskiHarabaszIndex(data, labels);     
        double dunn = dunnIndex(data, labels);
        double gap = gapStatistic(data, labels);
        double dispersion = calculateDispersion(data, labels);
        
        double meanIntra = meanIntraClusterDistance(data, labels);
        double medianIntra = medianIntraClusterDistance(data, labels);
        double stdDevIntra = stdDevIntraClusterDistance(data, labels);
        
        System.out.println("----- Wskaźniki ewaluacji klasteryzacji -----");
        System.out.println("Silhouette Score: " + silhouette);
        System.out.println("Davies-Bouldin Index: " + daviesBouldin);
        System.out.println("Calinski-Harabasz Index: " + calinskiHarabasz);
        System.out.println("Dunn Index: " + dunn);
        System.out.println("Gap Statistic: " + gap);
        System.out.println("Dispersion: " + dispersion);
        System.out.println();
        System.out.println("----- Statystyki odległości wewnątrz klastrów -----");
        System.out.println("Mean Intra-cluster Distance: " + meanIntra);
        System.out.println("Median Intra-cluster Distance: " + medianIntra);
        System.out.println("Std Dev Intra-cluster Distance: " + stdDevIntra);
    }

    public static double silhouetteScore(double[][] data, int[] labels) {
        int n = data.length;
        double totalSilhouette = 0.0;
        int count = 0;

        Map<Integer, List<Integer>> clusters = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int label = labels[i];
            if (label == -1) continue;
            clusters.computeIfAbsent(label, k -> new ArrayList<>()).add(i);
        }

        if (clusters.size() <= 1) {
            return 0.0;
        }

        for (int i = 0; i < n; i++) {
            int label = labels[i];
            if (label == -1) continue;

            List<Integer> cluster = clusters.get(label);
            if (cluster.size() <= 1) continue;

            double a = 0.0;
            for (int j : cluster) {
                if (j != i) a += DISTANCE.d(data[i], data[j]);
            }
            a /= (cluster.size() - 1);

            double b = Double.MAX_VALUE;
            for (Map.Entry<Integer, List<Integer>> entry : clusters.entrySet()) {
                if (entry.getKey() == label) continue;
                double sum = 0.0;
                for (int j : entry.getValue()) {
                    sum += DISTANCE.d(data[i], data[j]);
                }
                double avg = sum / entry.getValue().size();
                b = Math.min(b, avg);
            }

            double s = (b - a) / Math.max(a, b);
            totalSilhouette += s;
            count++;
        }

        return count > 0 ? totalSilhouette / count : 0.0;
    }

    public static double daviesBouldinIndex(double[][] data, int[] labels) {
        Set<Integer> clusterIds = new HashSet<>();
        for (int label : labels) {
            if (label != -1) clusterIds.add(label);
        }
        int numClusters = clusterIds.size();
        if (numClusters <= 1) return 0.0;

        Map<Integer, double[]> centroids = computeCentroids(data, labels);
        Map<Integer, Double> clusterAverages = new HashMap<>();
        Map<Integer, Integer> clusterSizes = new HashMap<>();

        for (int i = 0; i < data.length; i++) {
            int label = labels[i];
            if (label == -1) continue;
            clusterSizes.put(label, clusterSizes.getOrDefault(label, 0) + 1);
            clusterAverages.put(label, clusterAverages.getOrDefault(label, 0.0) + DISTANCE.d(data[i], centroids.get(label)));
        }

        for (int label : clusterAverages.keySet()) {
            clusterAverages.put(label, clusterAverages.get(label) / clusterSizes.get(label));
        }

        double sum = 0.0;
        List<Integer> clusterList = new ArrayList<>(clusterIds);
        for (int i = 0; i < clusterList.size(); i++) {
            int labelI = clusterList.get(i);
            double maxRatio = 0.0;
            for (int j = 0; j < clusterList.size(); j++) {
                if (i == j) continue;
                int labelJ = clusterList.get(j);
                double dist = DISTANCE.d(centroids.get(labelI), centroids.get(labelJ));
                double ratio = (clusterAverages.get(labelI) + clusterAverages.get(labelJ)) / dist;
                maxRatio = Math.max(maxRatio, ratio);
            }
            sum += maxRatio;
        }
        return sum / numClusters;
    }

    public static double calinskiHarabaszIndex(double[][] data, int[] labels) {
        Set<Integer> clusterIds = new HashSet<>();
        for (int label : labels) if (label != -1) clusterIds.add(label);
        int numClusters = clusterIds.size();
        if (numClusters <= 1) return 0.0;

        int n = data.length;
        int dim = data[0].length;
        double[] globalMean = new double[dim];
        for (double[] point : data) {
            for (int j = 0; j < dim; j++) {
                globalMean[j] += point[j];
            }
        }
        for (int j = 0; j < dim; j++) globalMean[j] /= n;

        Map<Integer, double[]> centroids = computeCentroids(data, labels);
        Map<Integer, Integer> clusterSizes = new HashMap<>();
        for (int label : labels) {
            if (label != -1) clusterSizes.put(label, clusterSizes.getOrDefault(label, 0) + 1);
        }

        double between = 0.0;
        for (int label : clusterIds) {
            int size = clusterSizes.get(label);
            between += size * Math.pow(DISTANCE.d(centroids.get(label), globalMean), 2);
        }
        between /= (numClusters - 1);

        double within = 0.0;
        for (int i = 0; i < n; i++) {
            int label = labels[i];
            if (label == -1) continue;
            within += Math.pow(DISTANCE.d(data[i], centroids.get(label)), 2);
        }
        within /= (n - numClusters);

        return between / within;
    }

    public static double dunnIndex(double[][] data, int[] labels) {
        Map<Integer, List<double[]>> clusters = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            int label = labels[i];
            if (label == -1) continue;
            clusters.computeIfAbsent(label, k -> new ArrayList<>()).add(data[i]);
        }
        if (clusters.size() < 2) return 0.0;

        double maxIntra = 0.0;
        for (List<double[]> cluster : clusters.values()) {
            maxIntra = Math.max(maxIntra, computeMaxIntra(cluster));
        }

        double minInter = Double.MAX_VALUE;
        List<Integer> labelsList = new ArrayList<>(clusters.keySet());
        for (int i = 0; i < labelsList.size(); i++) {
            for (int j = i+1; j < labelsList.size(); j++) {
                minInter = Math.min(minInter, computeMinInter(clusters.get(labelsList.get(i)), clusters.get(labelsList.get(j))));
            }
        }

        return minInter / maxIntra;
    }

    private static double computeMaxIntra(List<double[]> cluster) {
        double max = 0.0;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = i+1; j < cluster.size(); j++) {
                max = Math.max(max, DISTANCE.d(cluster.get(i), cluster.get(j)));
            }
        }
        return max;
    }

    private static double computeMinInter(List<double[]> c1, List<double[]> c2) {
        double min = Double.MAX_VALUE;
        for (double[] p1 : c1) {
            for (double[] p2 : c2) {
                min = Math.min(min, DISTANCE.d(p1, p2));
            }
        }
        return min;
    }

    public static double gapStatistic(double[][] data, int[] labels) {
        // Uwaga: Wymaga poprawnej implementacji generowania danych referencyjnych i ponownej klasteryzacji
        return 0.0; // Tymczasowe rozwiązanie
    }

    private static double calculateDispersion(double[][] data, int[] labels) {
        Map<Integer, double[]> centroids = computeCentroids(data, labels);
        double sum = 0.0;
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            int label = labels[i];
            if (label == -1) continue;
            sum += DISTANCE.d(data[i], centroids.get(label));
            count++;
        }
        return count > 0 ? sum / count : 0.0;
    }

    private static Map<Integer, double[]> computeCentroids(double[][] data, int[] labels) {
        Map<Integer, double[]> centroids = new HashMap<>();
        Map<Integer, Integer> counts = new HashMap<>();
        int dim = data[0].length;

        for (int i = 0; i < data.length; i++) {
            int label = labels[i];
            if (label == -1) continue;
            if (!centroids.containsKey(label)) {
                centroids.put(label, new double[dim]);
                counts.put(label, 0);
            }
            counts.put(label, counts.get(label) + 1);
            for (int j = 0; j < dim; j++) {
                centroids.get(label)[j] += data[i][j];
            }
        }

        for (int label : centroids.keySet()) {
            int count = counts.get(label);
            for (int j = 0; j < dim; j++) {
                centroids.get(label)[j] /= count;
            }
        }
        return centroids;
    }

    public static double meanIntraClusterDistance(double[][] data, int[] labels) {
        Map<Integer, double[]> centroids = computeCentroids(data, labels);
        double sum = 0.0;
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            int label = labels[i];
            if (label == -1) continue;
            sum += DISTANCE.d(data[i], centroids.get(label));
            count++;
        }
        return count > 0 ? sum / count : 0.0;
    }

    public static double medianIntraClusterDistance(double[][] data, int[] labels) {
        List<Double> distances = new ArrayList<>();
        Map<Integer, double[]> centroids = computeCentroids(data, labels);
        for (int i = 0; i < data.length; i++) {
            int label = labels[i];
            if (label == -1) continue;
            distances.add(DISTANCE.d(data[i], centroids.get(label)));
        }
        Collections.sort(distances);
        int mid = distances.size() / 2;
        return distances.isEmpty() ? 0.0 :
            distances.size() % 2 == 0 ? (distances.get(mid-1) + distances.get(mid)) / 2.0 : distances.get(mid);
    }

    public static double stdDevIntraClusterDistance(double[][] data, int[] labels) {
        double mean = meanIntraClusterDistance(data, labels);
        Map<Integer, double[]> centroids = computeCentroids(data, labels);
        double sumSq = 0.0;
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            int label = labels[i];
            if (label == -1) continue;
            double d = DISTANCE.d(data[i], centroids.get(label));
            sumSq += (d - mean) * (d - mean);
            count++;
        }
        return count > 0 ? Math.sqrt(sumSq / count) : 0.0;
    }
}
