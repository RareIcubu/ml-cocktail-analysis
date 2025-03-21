package mlcocktail.config;

/**
 * <p>ReducerConfig class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public class ReducerConfig {
    private int minDimensions;
    private int maxDimensions;

    /**
     * <p>Constructor for ReducerConfig.</p>
     *
     * @param minDimensions a int
     * @param maxDimensions a int
     */
    public ReducerConfig(int minDimensions, int maxDimensions) {
        this.minDimensions = minDimensions;
        this.maxDimensions = maxDimensions;
    }

    /**
     * <p>Constructor for ReducerConfig.</p>
     */
    public ReducerConfig() {
        this.minDimensions = 2;
        this.maxDimensions = 10;
    }

    /**
     * <p>Getter for the field <code>minDimensions</code>.</p>
     *
     * @return a int
     */
    public int getMinDimensions() {
        return minDimensions;
    }
    /**
     * <p>Setter for the field <code>minDimensions</code>.</p>
     *
     * @param minDimensions a int
     */
    public void setMinDimensions(int minDimensions) {
        this.minDimensions = minDimensions;
    }
    /**
     * <p>Getter for the field <code>maxDimensions</code>.</p>
     *
     * @return a int
     */
    public int getMaxDimensions() {
        return maxDimensions;
    }
    /**
     * <p>Setter for the field <code>maxDimensions</code>.</p>
     *
     * @param maxDimensions a int
     */
    public void setMaxDimensions(int maxDimensions) {
        this.maxDimensions = maxDimensions;
    }
}

