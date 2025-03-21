package mlcocktail.clustering;

import smile.clustering.KMeans;
import mlcocktail.evaluation.ClusteringResult;
import smile.clustering.CentroidClustering;
import java.util.Map;

/**
 * <p>KMeansClusterer class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public class KMeansClusterer implements Clusterer {
    private final int k;
    private final int maxIterations;

    /**
     * <p>Constructor for KMeansClusterer.</p>
     *
     * @param k a int
     * @param maxIterations a int
     */
    public KMeansClusterer(int k, int maxIterations) {
        this.k = k;
        this.maxIterations = maxIterations;
    }

    /** {@inheritDoc} */
    @Override
    public ClusteringResult cluster(double[][] data) {
        CentroidClustering<double[], double[]> kmeans = KMeans.fit(data, k, maxIterations);
        return new ClusteringResult(
            data,
            kmeans.group(),
            "K-Means",
            Map.of(
                "k", k,
                "maxIterations", maxIterations
            )
        );
    }

    /** {@inheritDoc} */
    @Override
    public String getAlgorithmName() {
        return "K-Means";
    }
}

