package mlcocktail;

import smile.math.distance.EuclideanDistance;

public class Evaluator{
    public static double computeSilhouetteScore(double[][] data, int[] labels) {
        int n = data.length;
        double totalSilhouette = 0.0;
        int validCount = 0;
        EuclideanDistance distance = new EuclideanDistance();
        // Dla każdego punktu
        for (int i = 0; i < n; i++) {
            int clusterId = labels[i];
            double a = 0.0; // średnia odległość do punktów w tym samym klastrze
            int sameClusterCount = 0;
            double b = Double.MAX_VALUE; // minimalna średnia odległość do punktów z innego klastra

            // Oblicz średnią odległość do punktów w tym samym klastrze
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                if (labels[j] == clusterId) {
                    a += distance.d(data[i], data[j]);
                    sameClusterCount++;
                }
            }
            if (sameClusterCount > 0) {
                a /= sameClusterCount;
            } else {
                // Jeżeli punkt jest jedyny w klastrze, silhouette score dla niego jest zdefiniowany jako 0
                totalSilhouette += 0;
                validCount++;
                continue;
            }

            // Dla każdego innego klastra, oblicz średnią odległość
            // Najlepiej przejść po wszystkich punktach i grupować je według etykiety
            // Możemy użyć tablicy do zliczenia sum i liczby punktów dla każdego klastra
            int[] clusterCounts = new int[findMax(labels) + 1];
            double[] clusterSums = new double[clusterCounts.length];

            for (int j = 0; j < n; j++) {
                if (labels[j] != clusterId) {
                    double dist = distance.d(data[i], data[j]);
                    clusterSums[labels[j]] += dist;
                    clusterCounts[labels[j]]++;
                }
            }
            // Wybierz minimalną średnią odległość spośród innych klastrów
            for (int c = 0; c < clusterCounts.length; c++) {
                if (c == clusterId || clusterCounts[c] == 0) continue;
                double avg = clusterSums[c] / clusterCounts[c];
                if (avg < b) {
                    b = avg;
                }
            }
            double s = (b - a) / Math.max(a, b);
            totalSilhouette += s;
            validCount++;
        }
        return totalSilhouette / validCount;
    }
    
    /**
     * Znajduje maksymalną wartość w tablicy etykiet, przydatne do określenia rozmiaru tablic pomocniczych.
     */
    private static int findMax(int[] arr) {
        int max = arr[0];
        for (int num : arr) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }

}
