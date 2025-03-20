package mlcocktail.eda;

import java.util.*;
import smile.math.distance.EuclideanDistance;

public class Evaluator {

    private static final EuclideanDistance DISTANCE = new EuclideanDistance();

    /* ---------------------- Podstawowe wskaźniki ewaluacji ---------------------- */

    public static void Evaluate(double[][] data, int[] labels, int numClusters) {
        double silhouette = silhouetteScore(data, labels);
        double daviesBouldin = daviesBouldinIndex(data, labels, numClusters);
        double calinskiHarabasz = calinskiHarabaszIndex(data, labels, numClusters);
        double dunn = dunnIndex(data, labels, numClusters);
        double gap = gapStatistic(data, labels, numClusters);
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

    // Silhouette Score (przyjmuje double[][] zamiast List<double[]>)
    public static double silhouetteScore(double[][] data, int[] labels) {
        int n = data.length;
        double totalSilhouette = 0.0;
        int count = 0;
        EuclideanDistance distance = new EuclideanDistance();

        // 1. Grupujemy indeksy punktów według etykiety
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int label = labels[i];
            clusters.computeIfAbsent(label, k -> new ArrayList<>()).add(i);
        }

        // 2. Dla każdego punktu obliczamy silhouette
        for (int i = 0; i < n; i++) {
            int label = labels[i];
            List<Integer> sameCluster = clusters.get(label);

            // Jeśli klaster ma tylko 1 punkt (ten), silhouette jest nieokreślone
            if (sameCluster == null || sameCluster.size() <= 1) {
                continue;
            }

            // a(i): średnia odległość do innych punktów w tym samym klastrze
            double a = 0.0;
            int intraCount = 0;
            for (int j : sameCluster) {
                if (j == i) continue;  // nie liczymy odległości punktu do siebie
                a += distance.d(data[i], data[j]);
                intraCount++;
            }
            if (intraCount > 0) {
                a /= intraCount;
            } else {
                continue;
            }

            // b(i): najmniejsza średnia odległość do punktów z innego klastra
            double b = Double.MAX_VALUE;
            for (Map.Entry<Integer, List<Integer>> entry : clusters.entrySet()) {
                if (entry.getKey() == label) continue; // pomijamy ten sam klaster
                List<Integer> otherCluster = entry.getValue();
                double sum = 0.0;
                for (int j : otherCluster) {
                    sum += distance.d(data[i], data[j]);
                }
                double avg = sum / otherCluster.size();
                if (avg < b) {
                    b = avg;
                }
            }

            // silhouette dla punktu i
            double s = (b - a) / Math.max(a, b);
            totalSilhouette += s;
            count++;
        }

        double score = count > 0 ? totalSilhouette / count : 0.0;
        System.out.println("Średni silhouette score obliczony dla " + count + " punktów: " + score);
        return score;
    }
   
    // Davies-Bouldin Index
    public static double daviesBouldinIndex(double[][] data, int[] labels, int numClusters) {
        int dim = data[0].length;
        double[][] centroids = new double[numClusters][dim];
        int[] clusterSizes = new int[numClusters];

        // Sumowanie punktów do centroidów
        for (int i = 0; i < data.length; i++) {
            int cluster = labels[i];
            clusterSizes[cluster]++;
            for (int j = 0; j < dim; j++) {
                centroids[cluster][j] += data[i][j];
            }
        }
        // Obliczenie średnich (centroidów)
        for (int c = 0; c < numClusters; c++) {
            if (clusterSizes[c] > 0) {
                for (int j = 0; j < dim; j++) {
                    centroids[c][j] /= clusterSizes[c];
                }
            }
        }

        // Obliczenie średnich odległości punktów od centroidu dla każdego klastra
        double[] clusterAverages = new double[numClusters];
        for (int i = 0; i < data.length; i++) {
            int cluster = labels[i];
            clusterAverages[cluster] += DISTANCE.d(data[i], centroids[cluster]);
        }
        for (int c = 0; c < numClusters; c++) {
            if (clusterSizes[c] > 0) {
                clusterAverages[c] /= clusterSizes[c];
            }
        }
        
        double sum = 0.0;
        for (int i = 0; i < numClusters; i++) {
            double maxRatio = 0.0;
            for (int j = 0; j < numClusters; j++) {
                if (i != j) {
                    double centDist = DISTANCE.d(centroids[i], centroids[j]);
                    double ratio = (clusterAverages[i] + clusterAverages[j]) / centDist;
                    if (ratio > maxRatio) {
                        maxRatio = ratio;
                    }
                }
            }
            sum += maxRatio;
        }
        return sum / numClusters;
    }
    
    // Calinski-Harabasz Index
    public static double calinskiHarabaszIndex(double[][] data, int[] labels, int numClusters) {
        int n = data.length;
        int dim = data[0].length;
        double[] globalMean = new double[dim];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < dim; j++) {
                globalMean[j] += data[i][j];
            }
        }
        for (int j = 0; j < dim; j++) {
            globalMean[j] /= n;
        }
        
        // Obliczenie centroidów dla poszczególnych klastrów
        double[][] centroids = new double[numClusters][dim];
        int[] clusterSizes = new int[numClusters];
        for (int i = 0; i < n; i++) {
            int cluster = labels[i];
            clusterSizes[cluster]++;
            for (int j = 0; j < dim; j++) {
                centroids[cluster][j] += data[i][j];
            }
        }
        for (int c = 0; c < numClusters; c++) {
            if (clusterSizes[c] > 0) {
                for (int j = 0; j < dim; j++) {
                    centroids[c][j] /= clusterSizes[c];
                }
            }
        }
        
        double betweenClusterVariance = 0.0;
        for (int c = 0; c < numClusters; c++) {
            double dist = DISTANCE.d(centroids[c], globalMean);
            betweenClusterVariance += clusterSizes[c] * dist * dist;
        }
        
        double withinClusterVariance = 0.0;
        for (int i = 0; i < n; i++) {
            int cluster = labels[i];
            double dist = DISTANCE.d(data[i], centroids[cluster]);
            withinClusterVariance += dist * dist;
        }
        
        return (betweenClusterVariance / (numClusters - 1)) / (withinClusterVariance / (n - numClusters));
    }
    
    // Dunn Index
    public static double dunnIndex(double[][] data, int[] labels, int numClusters) {
        double minInterClusterDist = Double.MAX_VALUE;
        double maxIntraClusterDist = 0.0;
        
        for (int i = 0; i < data.length; i++) {
            for (int j = i + 1; j < data.length; j++) {
                double dist = DISTANCE.d(data[i], data[j]);
                if (labels[i] == labels[j]) {
                    if (dist > maxIntraClusterDist) {
                        maxIntraClusterDist = dist;
                    }
                } else {
                    if (dist < minInterClusterDist) {
                        minInterClusterDist = dist;
                    }
                }
            }
        }
        
        return minInterClusterDist / maxIntraClusterDist;
    }
    
    // Gap Statistic (częściowo)
    public static double gapStatistic(double[][] data, int[] labels, int numClusters) {
        double realDispersion = calculateDispersion(data, labels);
        double referenceDispersion = 0.0;
        Random random = new Random();
        int iterations = 10;
        
        for (int i = 0; i < iterations; i++) {
            double[][] referenceData = new double[data.length][data[0].length];
            // Generujemy dane referencyjne – dla każdej cechy losujemy wartość z [min, max]
            for (int j = 0; j < data[0].length; j++) {
                double min = Double.MAX_VALUE;
                double max = -Double.MAX_VALUE;
                for (int i1 = 0; i1 < data.length; i1++) {
                    min = Math.min(min, data[i1][j]);
                    max = Math.max(max, data[i1][j]);
                }
                for (int i1 = 0; i1 < data.length; i1++) {
                    referenceData[i1][j] = min + (max - min) * random.nextDouble();
                }
            }
            // Używamy tych samych etykiet – dla uproszczenia
            referenceDispersion += calculateDispersion(referenceData, labels);
        }
        
        referenceDispersion /= iterations;
        return Math.log(referenceDispersion) - Math.log(realDispersion);
    }
    
    // Pomocnicza metoda do obliczania dyspersji (średniej odległości punktu do "centroidu" przypisanego etykiety)
    private static double calculateDispersion(double[][] data, int[] labels) {
        int n = data.length;
        int dim = data[0].length;
        // Obliczamy centroidy na podstawie etykiet
        Map<Integer, double[]> centroidMap = new HashMap<>();
        Map<Integer, Integer> counts = new HashMap<>();
        
        for (int i = 0; i < n; i++) {
            int cluster = labels[i];
            centroidMap.putIfAbsent(cluster, new double[dim]);
            counts.put(cluster, counts.getOrDefault(cluster, 0) + 1);
            for (int j = 0; j < dim; j++) {
                centroidMap.get(cluster)[j] += data[i][j];
            }
        }
        for (Integer cluster : centroidMap.keySet()) {
            double[] centroid = centroidMap.get(cluster);
            int count = counts.get(cluster);
            for (int j = 0; j < dim; j++) {
                centroid[j] /= count;
            }
        }
        
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            int cluster = labels[i];
            sum += DISTANCE.d(data[i], centroidMap.get(cluster));
        }
        return sum / n;
    }
    
    /* ---------------------- Statystyki odległości wewnątrz klastrów ---------------------- */
    
    // Oblicza średnią odległość od punktów do ich centroidów (wewnątrz klastrów)
    public static double meanIntraClusterDistance(double[][] data, int[] labels) {
        double[][] centroids = computeCentroids(data, labels);
        double sum = 0.0;
        for (int i = 0; i < data.length; i++) {
            sum += DISTANCE.d(data[i], centroids[labels[i]]);
        }
        return sum / data.length;
    }
    
    // Oblicza medianę odległości od punktów do ich centroidów
    public static double medianIntraClusterDistance(double[][] data, int[] labels) {
        double[][] centroids = computeCentroids(data, labels);
        double[] distances = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            distances[i] = DISTANCE.d(data[i], centroids[labels[i]]);
        }
        Arrays.sort(distances);
        int mid = distances.length / 2;
        return distances.length % 2 == 0 ? (distances[mid - 1] + distances[mid]) / 2.0 : distances[mid];
    }
    
    // Odchylenie standardowe odległości punktów do centroidów
    public static double stdDevIntraClusterDistance(double[][] data, int[] labels) {
        double mean = meanIntraClusterDistance(data, labels);
        double[][] centroids = computeCentroids(data, labels);
        double sumSq = 0.0;
        for (int i = 0; i < data.length; i++) {
            double d = DISTANCE.d(data[i], centroids[labels[i]]);
            sumSq += (d - mean) * (d - mean);
        }
        return Math.sqrt(sumSq / data.length);
    }
    
    // Pomocnicza metoda do obliczania centroidów dla danych
    private static double[][] computeCentroids(double[][] data, int[] labels) {
        int n = data.length;
        int dim = data[0].length;
        int numClusters = Arrays.stream(labels).max().orElse(-1) + 1;
        double[][] centroids = new double[numClusters][dim];
        int[] counts = new int[numClusters];
        
        for (int i = 0; i < n; i++) {
            int cluster = labels[i];
            counts[cluster]++;
            for (int j = 0; j < dim; j++) {
                centroids[cluster][j] += data[i][j];
            }
        }
        for (int c = 0; c < numClusters; c++) {
            if (counts[c] > 0) {
                for (int j = 0; j < dim; j++) {
                    centroids[c][j] /= counts[c];
                }
            }
        }
        return centroids;
    }
}

