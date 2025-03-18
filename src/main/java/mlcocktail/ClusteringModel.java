package mlcocktail;

import smile.clustering.KMeans;
import java.util.Arrays;

public class ClusteringModel {

    /**
     * Metoda wykonuje klasteryzację metodą K-means i wypisuje wyniki.
     *
     * @param data - tablica wektorów cech koktajli
     * @param k    - liczba klastrów
     */
    public static void clusterCocktails(double[][] data, int k) {
        // Wykonanie klasteryzacji metodą K-means
        KMeans kmeans = KMeans.fit(data, k);
        int[] labels = kmeans.y;  // etykiety klastrów dla każdego punktu

        // Wypisanie liczby koktajli przypisanych do każdego klastra
        for (int i = 0; i < k; i++) {
            final int clusterIndex = i;
            int count = (int) Arrays.stream(labels).filter(label -> label == clusterIndex).count();
            System.out.println("Cluster " + i + ": " + count + " cocktails");
        }

        // Opcjonalnie: wypisz centroidy klastrów
        System.out.println("Centroids:");
        for (int i = 0; i < k; i++) {
            System.out.println("Cluster " + i + " centroid: " + Arrays.toString(kmeans.centroids[i]));
        }
    }
}

