package mlcocktail.features;

import smile.data.DataFrame;

/**
 * <p>DimensionReducer interface.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public interface DimensionReducer {
    /**
     * Redukuje wymiary danych wejściowych.
     *
     * @param data       Dane wejściowe w formie DataFrame.
     * @param dimensions Docelowa liczba wymiarów.
     * @return Wynik redukcji jako macierz double[][].
     */
    double[][] reduceDimensions(DataFrame data, int dimensions);
    
    /**
     * Zwraca nazwę użytego algorytmu redukcji.
     *
     * @return Nazwa algorytmu.
     */
    String getAlgorithmName();
}

