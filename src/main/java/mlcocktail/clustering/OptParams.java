package mlcocktail.clustering;

import smile.data.DataFrame;

/**
 * Klasa przechowująca optymalne parametry wyznaczone podczas grid search.
 *
 * @author jakub
 * @version $Id: $Id
 */
public class OptParams {
    private final int bestDim;
    private final int bestK;
    private final double bestScore;
    private final DataFrame bestReducedData;

    /**
     * <p>Constructor for OptParams.</p>
     *
     * @param bestDim a int
     * @param bestK a int
     * @param bestScore a double
     * @param bestReducedData a {@link smile.data.DataFrame} object
     */
    public OptParams(int bestDim, int bestK, double bestScore, DataFrame bestReducedData) {
        this.bestDim = bestDim;
        this.bestK = bestK;
        this.bestScore = bestScore;
        this.bestReducedData = bestReducedData;
    }

    /**
     * <p>Getter for the field <code>bestDim</code>.</p>
     *
     * @return a int
     */
    public int getBestDim() {
        return bestDim;
    }

    /**
     * <p>Getter for the field <code>bestK</code>.</p>
     *
     * @return a int
     */
    public int getBestK() {
        return bestK;
    }

    /**
     * <p>Getter for the field <code>bestScore</code>.</p>
     *
     * @return a double
     */
    public double getBestScore() {
        return bestScore;
    }

    /**
     * <p>Getter for the field <code>bestReducedData</code>.</p>
     *
     * @return a {@link smile.data.DataFrame} object
     */
    public DataFrame getBestReducedData() {
        return bestReducedData;
    }
}
