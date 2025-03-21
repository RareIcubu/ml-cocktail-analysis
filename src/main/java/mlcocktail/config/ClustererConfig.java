package mlcocktail.config;

/**
 * <p>ClustererConfig class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public class ClustererConfig {
    private Integer minK;
    private Integer maxK;
    private Double minEps;
    private Double maxEps;

    /**
     * <p>Constructor for ClustererConfig.</p>
     */
    public ClustererConfig() {
        this.minK = 2;
        this.maxK = 10;
        this.minEps = 0.1;
        this.maxEps = 1.0;
    }

    /**
     * <p>Getter for the field <code>minK</code>.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getMinK() {
        return minK;
    }
    /**
     * <p>Setter for the field <code>minK</code>.</p>
     *
     * @param minK a {@link java.lang.Integer} object
     */
    public void setMinK(Integer minK) {
        this.minK = minK;
    }
    /**
     * <p>Getter for the field <code>maxK</code>.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getMaxK() {
        return maxK;
    }
    /**
     * <p>Setter for the field <code>maxK</code>.</p>
     *
     * @param maxK a {@link java.lang.Integer} object
     */
    public void setMaxK(Integer maxK) {
        this.maxK = maxK;
    }
    /**
     * <p>Getter for the field <code>minEps</code>.</p>
     *
     * @return a {@link java.lang.Double} object
     */
    public Double getMinEps() {
        return minEps;
    }
    /**
     * <p>Setter for the field <code>minEps</code>.</p>
     *
     * @param minEps a {@link java.lang.Double} object
     */
    public void setMinEps(Double minEps) {
        this.minEps = minEps;
    }
    /**
     * <p>Getter for the field <code>maxEps</code>.</p>
     *
     * @return a {@link java.lang.Double} object
     */
    public Double getMaxEps() {
        return maxEps;
    }
    /**
     * <p>Setter for the field <code>maxEps</code>.</p>
     *
     * @param maxEps a {@link java.lang.Double} object
     */
    public void setMaxEps(Double maxEps) {
        this.maxEps = maxEps;
    }
}

