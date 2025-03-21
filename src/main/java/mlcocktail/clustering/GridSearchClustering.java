package mlcocktail.clustering;

import smile.clustering.KMeans;
import smile.clustering.CentroidClustering;
import smile.feature.extraction.PCA;
import smile.data.DataFrame;
import mlcocktail.evaluation.Evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Klasa realizująca grid search dla klasteryzacji z wyborem najlepszych parametrów.
 *
 * @author jakub
 * @version $Id: $Id
 */
public class GridSearchClustering {

    private static class CandidateResult {
        int dim;
        int k;
        DataFrame reducedData;
        double silhouette;
        double daviesBouldin;
        double calinskiHarabasz;
        double dunnIndex;

        public CandidateResult(int dim, int k, DataFrame reducedData, double silhouette, double daviesBouldin, double calinskiHarabasz, double dunnIndex) {
            this.dim = dim;
            this.k = k;
            this.reducedData = reducedData;
            this.silhouette = silhouette;
            this.daviesBouldin = daviesBouldin;
            this.calinskiHarabasz = calinskiHarabasz;
            this.dunnIndex = dunnIndex;
        }
    }

    /**
     * Wykonuje grid search dla redukcji wymiarów i klasteryzacji.
     *
     * @param data a DataFrame z danymi
     * @param minDim minimalna liczba wymiarów
     * @param maxDim maksymalna liczba wymiarów
     * @param kCandidates tablica możliwych wartości k
     * @return OptParams zawierające optymalne parametry
     */
    public static OptParams gridSearch(DataFrame data, int minDim, int maxDim, int[] kCandidates) {
        int originalDim = data.ncol();
        int effectiveMaxDim = Math.min(maxDim, originalDim);
        List<CandidateResult> candidates = new ArrayList<>();

        for (int dim = minDim; dim <= effectiveMaxDim; dim++) {
            PCA pca = PCA.fit(data);
            PCA projection = pca.getProjection(dim);
            DataFrame reducedData = projection.apply(data);
            for (int k : kCandidates) {
                try {
                    CentroidClustering<double[], double[]> kmeans = KMeans.fit(reducedData.toArray(), k, 100);
                    int[] labels = kmeans.group();

                    double silhouette = Evaluator.silhouetteScore(reducedData.toArray(), labels);
                    double daviesBouldin = Evaluator.daviesBouldinIndex(reducedData.toArray(), labels);
                    double calinskiHarabasz = Evaluator.calinskiHarabaszIndex(reducedData.toArray(), labels);
                    double dunn = Evaluator.dunnIndex(reducedData.toArray(), labels);

                    candidates.add(new CandidateResult(dim, k, reducedData, silhouette, daviesBouldin, calinskiHarabasz, dunn));
                } catch (Exception ex) {
                    System.err.println("Error for dim = " + dim + ", k = " + k + ": " + ex.getMessage());
                }
            }
        }

        if (candidates.isEmpty()) {
            throw new RuntimeException("No valid candidates found.");
        }

        List<Double> silhouettes = new ArrayList<>();
        List<Double> daviesBouldins = new ArrayList<>();
        List<Double> calinskis = new ArrayList<>();
        List<Double> dunns = new ArrayList<>();

        for (CandidateResult cr : candidates) {
            silhouettes.add(cr.silhouette);
            daviesBouldins.add(cr.daviesBouldin);
            calinskis.add(cr.calinskiHarabasz);
            dunns.add(cr.dunnIndex);
        }

        double silhouetteMin = Collections.min(silhouettes);
        double silhouetteMax = Collections.max(silhouettes);
        double daviesMin = Collections.min(daviesBouldins);
        double daviesMax = Collections.max(daviesBouldins);
        double calinskiMin = Collections.min(calinskis);
        double calinskiMax = Collections.max(calinskis);
        double dunnMin = Collections.min(dunns);
        double dunnMax = Collections.max(dunns);

        CandidateResult bestCandidate = null;
        double bestComposite = -Double.MAX_VALUE;

        for (CandidateResult cr : candidates) {
            double normSilhouette = normalize(cr.silhouette, silhouetteMin, silhouetteMax, true);
            double normDavies = normalize(cr.daviesBouldin, daviesMin, daviesMax, false);
            double normCalinski = normalize(cr.calinskiHarabasz, calinskiMin, calinskiMax, true);
            double normDunn = normalize(cr.dunnIndex, dunnMin, dunnMax, true);

            double composite = normSilhouette + normDavies + normCalinski + normDunn;

            if (composite > bestComposite) {
                bestComposite = composite;
                bestCandidate = cr;
            }
        }

        return new OptParams(bestCandidate.dim, bestCandidate.k, bestComposite, bestCandidate.reducedData);
    }

    private static double normalize(double value, double min, double max, boolean higherIsBetter) {
        if (max == min) {
            return 0.5; // Wartość neutralna, gdy wszystkie wartości są równe
        }
        if (higherIsBetter) {
            return (value - min) / (max - min);
        } else {
            return (max - value) / (max - min);
        }
    }
}

