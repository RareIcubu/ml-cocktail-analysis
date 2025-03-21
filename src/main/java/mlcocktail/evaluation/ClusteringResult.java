package mlcocktail.evaluation;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>ClusteringResult class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public class ClusteringResult {
    private final double[][] data;
    private final int[] labels;
    private final String algorithmName;
    private final Map<String, Object> algorithmParameters;
    private final Instant timestamp;
    private Double silhouetteScore;

    /**
     * <p>Constructor for ClusteringResult.</p>
     *
     * @param data an array of {@link double} objects
     * @param labels an array of {@link int} objects
     * @param algorithmName a {@link java.lang.String} object
     * @param algorithmParameters a {@link java.util.Map} object
     */
    public ClusteringResult(double[][] data, 
                           int[] labels, 
                           String algorithmName, 
                           Map<String, Object> algorithmParameters) {
        this.data = deepCopy(data);
        this.labels = Arrays.copyOf(labels, labels.length);
        this.algorithmName = Objects.requireNonNull(algorithmName);
        this.algorithmParameters = Collections.unmodifiableMap(new HashMap<>(algorithmParameters));
        this.timestamp = Instant.now();
    }

    // Głęboka kopia dla danych 2D
    private double[][] deepCopy(double[][] matrix) {
        return Arrays.stream(matrix)
                     .map(row -> Arrays.copyOf(row, row.length))
                     .toArray(double[][]::new);
    }

    // Gettery
    /**
     * <p>Getter for the field <code>data</code>.</p>
     *
     * @return an array of {@link double} objects
     */
    public double[][] getData() { return deepCopy(data); }
    /**
     * <p>Getter for the field <code>labels</code>.</p>
     *
     * @return an array of {@link int} objects
     */
    public int[] getLabels() { return Arrays.copyOf(labels, labels.length); }
    /**
     * <p>Getter for the field <code>algorithmName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getAlgorithmName() { return algorithmName; }
    /**
     * <p>Getter for the field <code>algorithmParameters</code>.</p>
     *
     * @return a {@link java.util.Map} object
     */
    public Map<String, Object> getAlgorithmParameters() { return new HashMap<>(algorithmParameters); }
    /**
     * <p>Getter for the field <code>timestamp</code>.</p>
     *
     * @return a {@link java.time.Instant} object
     */
    public Instant getTimestamp() { return timestamp; }
    
    // Metody pomocnicze
    /**
     * <p>getNumberOfClusters.</p>
     *
     * @return a int
     */
    public int getNumberOfClusters() {
        return (int) Arrays.stream(labels)
                          .distinct()
                          .filter(label -> label >= 0) // Ignoruj szum dla DBSCAN
                          .count();
    }

    /**
     * <p>calculateSilhouetteScore.</p>
     */
    public void calculateSilhouetteScore() {
        this.silhouetteScore = Evaluator.silhouetteScore(data, labels);
    }

    /**
     * <p>Getter for the field <code>silhouetteScore</code>.</p>
     *
     * @return a {@link java.lang.Double} object
     */
    public Double getSilhouetteScore() {
        if(silhouetteScore == null) {
            calculateSilhouetteScore();
        }
        return silhouetteScore;
    }

    // Reprezentacja tekstowa
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format(
            "Clustering Result [%s]\n" +
            "- Clusters: %d\n" +
            "- Parameters: %s\n" +
            "- Silhouette Score: %.3f\n" +
            "- Timestamp: %s",
            algorithmName,
            getNumberOfClusters(),
            algorithmParameters,
            getSilhouetteScore(),
            timestamp
        );
    }

    // Serializacja do formatu CSV
    /**
     * <p>toCsv.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String toCsv() {
        StringBuilder sb = new StringBuilder();
        // Nagłówek
        sb.append("x,y,cluster\n");
        
        // Dane
        for(int i = 0; i < data.length; i++) {
            sb.append(data[i][0])
              .append(",")
              .append(data[i][1])
              .append(",")
              .append(labels[i])
              .append("\n");
        }
        return sb.toString();
    }
}
