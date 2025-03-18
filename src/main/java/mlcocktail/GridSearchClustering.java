package mlcocktail;

import smile.clustering.KMeans;
import smile.projection.PCA;

public class GridSearchClustering {

    /**
     * Przeszukuje siatkę parametrów (docelowa liczba wymiarów oraz liczba klastrów)
     * i zwraca najlepszą kombinację parametrów (dla której silhouette score jest maksymalne).
     *
     * @param data           standardyzowane dane (przed redukcją wymiarowości)
     * @param minDim         minimalna liczba wymiarów po redukcji (np. 2)
     * @param maxDim         maksymalna liczba wymiarów (np. 10)
     * @param kCandidates    tablica możliwych wartości k (np. {2, 3, 4, 5, 6, 7, 8})
     * @return Optymalna konfiguracja parametrów (w formie obiektu OptParams)
     */

  /**
     * Przeszukuje przestrzeń parametrów (docelowy wymiar oraz liczba klastrów) i zwraca konfigurację z najwyższym silhouette score.
     */
    public static OptParams gridSearch(double[][] data, int minDim, int maxDim, int[] kCandidates) {
        int originalDim = data[0].length;
        int effectiveMaxDim = Math.min(maxDim, originalDim);
        double bestScore = -Double.MAX_VALUE;
        int bestDim = minDim;
        int bestK = kCandidates[0];
        double[][] bestReducedData = null;

        for (int dim = minDim; dim <= effectiveMaxDim; dim++) {
            PCA pca = PCA.fit(data);
            pca.setProjection(dim);
            double[][] reducedData = pca.project(data);

            for (int k : kCandidates) {
                try {
                    KMeans kmeans = KMeans.fit(reducedData, k);
                    int[] labels = kmeans.y;
                    double score = Evaluator.computeSilhouetteScore(reducedData, labels);
                    System.out.println("Dim = " + dim + ", k = " + k + " => Silhouette score: " + score);
                    if (score > bestScore) {
                        bestScore = score;
                        bestDim = dim;
                        bestK = k;
                        bestReducedData = reducedData;
                    }
                } catch (Exception ex) {
                    System.err.println("Error for dim = " + dim + ", k = " + k + ": " + ex.getMessage());
                }
            }
        }
        return new OptParams(bestDim, bestK, bestScore, bestReducedData);
    }
}

/**
 * Klasa pomocnicza do przechowywania optymalnych parametrów.
 */
class OptParams {
    int bestDim;
    int bestK;
    double bestScore;
    double[][] bestReducedData;

    public OptParams(int bestDim, int bestK, double bestScore, double[][] bestReducedData) {
        this.bestDim = bestDim;
        this.bestK = bestK;
        this.bestScore = bestScore;
        this.bestReducedData = bestReducedData;
    }
}

