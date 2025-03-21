package mlcocktail.clustering;

import mlcocktail.config.ClustererConfig;
import java.util.Map;

/**
 * Fabryka klasteryzatorów – tworzy instancje obiektów implementujących interfejs Clusterer
 * na podstawie nazwy algorytmu oraz przekazanej konfiguracji.
 *
 * @author jakub
 * @version $Id: $Id
 */
public class ClustererFactory {

    /**
     * Tworzy instancję klasteryzatora na podstawie podanego algorytmu, konfiguracji oraz dodatkowych parametrów.
     *
     * @param algorithm nazwa algorytmu (np. "kmeans", "dbscan", "xmeans")
     * @param config    obiekt konfiguracji klasteryzatora
     * @param params    dodatkowe parametry (np. liczba klastrów, maxIterations, eps, minPts)
     * @return instancja Clusterer
     */
    public static Clusterer createClusterer(String algorithm, ClustererConfig config, Map<String, Object> params) {
        switch (algorithm.toLowerCase()) {
            case "kmeans": {
                // Pobieramy liczbę klastrów i maksymalną liczbę iteracji – najpierw z parametrów, potem z konfiguracji
                int k = params.containsKey("k") ? (int) params.get("k") : config.getMaxK();
                int maxIterations = params.containsKey("maxIterations") ? (int) params.get("maxIterations") : 100;
                return new KMeansClusterer(k, maxIterations);
            }
            case "dbscan": {
                // Pobieramy parametry eps i minPts – najpierw z parametrów, potem z konfiguracji
                double eps = params.containsKey("eps") ? (double) params.get("eps") : config.getMinEps();
                int minPts = params.containsKey("minPts") ? (int) params.get("minPts") : config.getMinK();
                return new DBSCANClusterer(eps, minPts);
            }
            case "xmeans": {
                int k = params.containsKey("k") ? (int) params.get("k") : config.getMaxK();
                int maxIterations = params.containsKey("maxIterations") ? (int) params.get("maxIterations") : 100;
                return new XMeansClusterer(k, maxIterations);
            }
            default:
                throw new IllegalArgumentException("Nieznany algorytm klasteryzacji: " + algorithm);
        }
    }
}
