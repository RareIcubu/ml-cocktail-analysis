package mlcocktail;

import smile.clustering.KMeans;
import smile.clustering.CentroidClustering;
import smile.feature.extraction.PCA;
import smile.data.DataFrame;
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
    public static OptParams gridSearch(DataFrame data, int minDim, int maxDim, int[] kCandidates) {
        int originalDim = data.ncol();
        int effectiveMaxDim = Math.min(maxDim, originalDim);
        double bestScore = -Double.MAX_VALUE;
        int bestDim = minDim;
        int bestK = kCandidates[0];
        DataFrame bestReducedData = null;

        for (int dim = minDim; dim <= effectiveMaxDim; dim++) {
            PCA pca = PCA.fit(data);
            PCA project = pca.getProjection(dim);
            DataFrame reducedData = project.apply(data);
            for (int k : kCandidates) {
                try {
                    System.out.println("PCA1: "+pca.varianceProportion()[0]*100+ " PCA2: "+ pca.varianceProportion()[1]*100 + " PCA3: " + pca.varianceProportion()[2]*100 + " PCA4: " + pca.varianceProportion()[3]*100 + " PCA5: " + pca.varianceProportion()[4]*100 + " PCA6: " + pca.varianceProportion()[5]*100);
                    CentroidClustering<double[],double[]> kmeans = KMeans.fit(reducedData.toArray(), k,100);
                    int[] labels = kmeans.group();
                    double score = Evaluator.computeSilhouetteScore(reducedData.toArray(), labels);
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
        return new OptParams(bestDim, bestK, bestScore, bestReducedData );
    }
}

/**
 * Klasa pomocnicza do przechowywania optymalnych parametrów.
 */
class OptParams {
    int bestDim;
    int bestK;
    double bestScore;
    DataFrame bestReducedData;

    public OptParams(int bestDim, int bestK, double bestScore, DataFrame bestReducedData) {
        this.bestDim = bestDim;
        this.bestK = bestK;
        this.bestScore = bestScore;
        this.bestReducedData = bestReducedData;
    }
}

