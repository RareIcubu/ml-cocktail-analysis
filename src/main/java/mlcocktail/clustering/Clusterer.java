package mlcocktail.clustering;

import mlcocktail.evaluation.ClusteringResult;

/**
 * <p>Clusterer interface.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public interface Clusterer {
    /**
     * <p>cluster.</p>
     *
     * @param data an array of {@link double} objects
     * @return a {@link mlcocktail.evaluation.ClusteringResult} object
     */
    ClusteringResult cluster(double[][] data);
    /**
     * <p>getAlgorithmName.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getAlgorithmName();
}

