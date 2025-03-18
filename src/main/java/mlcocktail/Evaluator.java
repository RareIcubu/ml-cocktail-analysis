package mlcocktail;

import java.util.*;

public class Evaluator {

    /**
     * Oblicza średni silhouette score dla zbioru danych i etykiet klastrów.
     * Zakłada, że etykiety pochodzą z algorytmu klasteryzacji (np. KMeans) i są w zakresie 0..(k-1).
     *
     * @param data   macierz punktów (każdy punkt to wektor cech)
     * @param labels etykiety klastrów dla każdego punktu
     * @return średni silhouette score
     */
    public static double computeSilhouetteScore(double[][] data, int[] labels) {
        int n = data.length;
        double totalSilhouette = 0.0;
        int count = 0;

        // Grupujemy indeksy punktów według etykiety
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int label = labels[i];
            clusters.computeIfAbsent(label, k -> new ArrayList<>()).add(i);
        }

        // Dla każdego punktu obliczamy silhouette score
        for (int i = 0; i < n; i++) {
            int label = labels[i];
            List<Integer> sameCluster = clusters.get(label);
            if (sameCluster == null || sameCluster.size() <= 1) continue;

            // a(i): średnia odległość do innych punktów w tym samym klastrze
            double a = 0.0;
            int intraCount = 0;
            for (int j : sameCluster) {
                // Dodajemy kontrolę, żeby upewnić się, że j mieści się w zakresie
                if (j < 0 || j >= n) {
                    System.err.println("Warning: skipping invalid index " + j);
                    continue;
                }
                if (i == j) continue;
                a += euclideanDistance(data[i], data[j]);
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
                if (entry.getKey() == label) continue;
                List<Integer> otherCluster = entry.getValue();
                double sum = 0.0;
                int interCount = 0;
                for (int j : otherCluster) {
                    if (j < 0 || j >= n) {
                        System.err.println("Warning: skipping invalid index " + j);
                        continue;
                    }
                    sum += euclideanDistance(data[i], data[j]);
                    interCount++;
                }
                if (interCount > 0) {
                    double avg = sum / interCount;
                    if (avg < b) {
                        b = avg;
                    }
                }
            }

            double s = (b - a) / Math.max(a, b);
            totalSilhouette += s;
            count++;
        }

        double score = count > 0 ? totalSilhouette / count : 0.0;
        System.out.println("Średni silhouette score obliczony dla " + count + " punktów: " + score);
        return score;
    }

    private static double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}

