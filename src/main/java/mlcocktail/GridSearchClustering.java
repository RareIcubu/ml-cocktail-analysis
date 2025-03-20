package mlcocktail;

import smile.clustering.KMeans;
import smile.clustering.CentroidClustering;
import smile.feature.extraction.PCA;
import smile.data.DataFrame;
import mlcocktail.eda.Evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static OptParams gridSearch(DataFrame data, int minDim, int maxDim, int[] kCandidates) {
        int originalDim = data.ncol();
        int effectiveMaxDim = Math.min(maxDim, originalDim);
        List<CandidateResult> candidates = new ArrayList<>();

        for (int dim = minDim; dim <= effectiveMaxDim; dim++) {
            PCA pca = PCA.fit(data);
            PCA project = pca.getProjection(dim);
            DataFrame reducedData = project.apply(data);
            for (int k : kCandidates) {
                try {
                    CentroidClustering<double[], double[]> kmeans = KMeans.fit(reducedData.toArray(), k, 100);
                    int[] labels = kmeans.group();

                    double silhouette = Evaluator.silhouetteScore(reducedData.toArray(), labels);
                    double daviesBouldin = Evaluator.daviesBouldinIndex(reducedData.toArray(), labels, k);
                    double calinskiHarabasz = Evaluator.calinskiHarabaszIndex(reducedData.toArray(), labels, k);
                    double dunn = Evaluator.dunnIndex(reducedData.toArray(), labels, k);

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
            return 0.5; // Neutral value when all values are the same
        }
        if (higherIsBetter) {
            return (value - min) / (max - min);
        } else {
            return (max - value) / (max - min);
        }
    }
}

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
