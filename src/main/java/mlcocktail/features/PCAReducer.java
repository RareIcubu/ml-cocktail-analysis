package mlcocktail.features;

import smile.data.DataFrame;
import smile.feature.extraction.PCA;

/**
 * Redukcja wymiarowości przy użyciu analizy PCA.
 *
 * @author jakub
 * @version $Id: $Id
 */
public class PCAReducer implements DimensionReducer {
    /** {@inheritDoc} */
    @Override
    public double[][] reduceDimensions(DataFrame data, int dimensions) {
        PCA pca = PCA.fit(data);
        PCA projection = pca.getProjection(dimensions);
        return projection.apply(data).toArray();
    }
    
    /** {@inheritDoc} */
    @Override
    public String getAlgorithmName() {
        return "PCA";
    }
}

