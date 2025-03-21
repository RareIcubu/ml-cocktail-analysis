package mlcocktail.clustering;

import smile.clustering.DBSCAN;
import mlcocktail.evaluation.ClusteringResult;
import java.util.Map;

/**
 * <p>DBSCANClusterer class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public class DBSCANClusterer implements Clusterer {
    private final double eps;
    private final int minPts;

    /**
     * <p>Constructor for DBSCANClusterer.</p>
     *
     * @param eps a double
     * @param minPts a int
     */
    public DBSCANClusterer(double eps, int minPts) {
        this.eps = eps;
        this.minPts = minPts;
    }

    /** {@inheritDoc} */
    @Override
    public ClusteringResult cluster(double[][] data) {
        // Użycie biblioteki Smile do klasteryzacji DBSCAN
        DBSCAN<double[]> dbscan = DBSCAN.fit(data, minPts, eps);
        return new ClusteringResult(
            data,
            dbscan.group(),
            "DBSCAN",
            Map.of(
                "eps", eps,
                "minPts", minPts
            )
        );
    }

    /** {@inheritDoc} */
    @Override
    public String getAlgorithmName() {
        return "DBSCAN";
    }
}

