package mlcocktail;

import java.util.ArrayList;
import java.util.List;

public class OutlierRemoval {

    /**
     * Wyszukuje i zwraca listę indeksów outlierów na podstawie odległości od centroidu.
     *
     * @param data         macierz danych (wymiary: liczba_punktów x liczba_cech)
     * @param factor       współczynnik, np. 2.0 (dla 2 odchyleń standardowych), 3.0, itp.
     * @return lista indeksów punktów uznanych za outliery
     */
    public static List<Integer> findOutliers(double[][] data, double factor) {
        int n = data.length;
        if (n == 0) return new ArrayList<>();

        int dim = data[0].length;
        // 1. Obliczamy centroid (średni wektor) całego zbioru
        double[] centroid = new double[dim];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < dim; j++) {
                centroid[j] += data[i][j];
            }
        }
        for (int j = 0; j < dim; j++) {
            centroid[j] /= n;
        }

        // 2. Obliczamy odległości każdego punktu od centroidu
        double[] distances = new double[n];
        for (int i = 0; i < n; i++) {
            distances[i] = euclideanDistance(data[i], centroid);
        }

        // 3. Obliczamy średnią i odchylenie standardowe odległości
        double meanDist = 0.0;
        for (double d : distances) {
            meanDist += d;
        }
        meanDist /= n;

        double variance = 0.0;
        for (double d : distances) {
            double diff = d - meanDist;
            variance += diff * diff;
        }
        variance /= n;
        double stdevDist = Math.sqrt(variance);

        // 4. Wyznaczamy próg
        double threshold = meanDist + factor * stdevDist;

        // 5. Zbieramy indeksy punktów, które przekraczają próg
        List<Integer> outliers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (distances[i] > threshold) {
                outliers.add(i);
            }
        }

        return outliers;
    }

    /**
     * Usuwa z macierzy data wiersze o indeksach podanych w liście outliers.
     * Zwraca nową macierz danych z pominiętymi outlierami.
     */
    public static double[][] removeOutliers(double[][] data, List<Integer> outliers) {
        // Tworzymy zestaw indeksów outlierów, żeby łatwo sprawdzać zawieranie
        java.util.Set<Integer> outlierSet = new java.util.HashSet<>(outliers);

        // Liczymy ile punktów nie jest outlierami
        int total = data.length;
        int dim = data[0].length;
        int validCount = total - outliers.size();

        double[][] cleanedData = new double[validCount][dim];
        int idx = 0;
        for (int i = 0; i < total; i++) {
            if (!outlierSet.contains(i)) {
                System.arraycopy(data[i], 0, cleanedData[idx], 0, dim);
                idx++;
            }
        }
        return cleanedData;
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

