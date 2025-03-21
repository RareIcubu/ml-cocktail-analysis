package mlcocktail.features;

import mlcocktail.config.ReducerConfig;
import java.util.Map;

/**
 * Fabryka reduktorów wymiarów – tworzy instancje wybranego algorytmu na podstawie przekazanych parametrów.
 *
 * @author jakub
 * @version $Id: $Id
 */
public class ReducerFactory {
    /**
     * Tworzy obiekt implementujący interfejs DimensionReducer.
     *
     * @param algorithm       Nazwa algorytmu (np. "pca" lub "umap").
     * @param config          Konfiguracja reduktora.
     * @param algorithmParams Dodatkowe parametry algorytmu.
     * @return Instancja DimensionReducer.
     */
    public static DimensionReducer createReducer(String algorithm,
                                                   ReducerConfig config,
                                                   Map<String, Object> algorithmParams) {
        switch (algorithm.toLowerCase()) {
            case "pca":
                return new PCAReducer();
            case "umap":
                return new UMAPReducer();
            default:
                throw new IllegalArgumentException("Nieznany reduktor: " + algorithm);
        }
    }
}

