package mlcocktail.clustering;

import smile.clustering.XMeans;
import mlcocktail.evaluation.ClusteringResult;
import smile.clustering.CentroidClustering;
import java.util.Map;

/**
 * <p>XMeansClusterer class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public class XMeansClusterer implements Clusterer {
    private final int k;
    private final int maxIterations;

    /**
     * <p>Constructor for XMeansClusterer.</p>
     *
     * @param k a int
     * @param maxIterations a int
     */
    public XMeansClusterer(int k, int maxIterations) {
        this.k = k;
        this.maxIterations = maxIterations;
    }

    /** {@inheritDoc} */
    @Override
    public ClusteringResult cluster(double[][] data) {
        CentroidClustering<double[], double[]> xmeans = XMeans.fit(data, k, maxIterations);
        return new ClusteringResult(
            data,
            xmeans.group(),
            "X-Means",
            Map.of(
                "k", k,
                "maxIterations", maxIterations
            )
        );
    }

    /** {@inheritDoc} */
    @Override
    public String getAlgorithmName() {
        return "X-Means";
    }
}

