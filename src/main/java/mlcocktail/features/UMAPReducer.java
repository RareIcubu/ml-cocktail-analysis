package mlcocktail.features;

import smile.manifold.UMAP;
import smile.data.DataFrame;

/**
 * Redukcja wymiarowości przy użyciu algorytmu UMAP.
 *
 * @author jakub
 * @version $Id: $Id
 */
public class UMAPReducer implements DimensionReducer {
    /** {@inheritDoc} */
    @Override
    public double[][] reduceDimensions(DataFrame data, int dimensions) {
        // Konwersja danych na tablicę double[][] i wywołanie algorytmu UMAP
        return UMAP.fit(data.toArray(), new UMAP.Options(dimensions));
    }
    
    /** {@inheritDoc} */
    @Override
    public String getAlgorithmName() {
        return "UMAP";
    }
}

