package mlcocktail;

public class Standardizer {
    private final double[] means;
    private final double[] stds;

    public Standardizer(double[][] data) {
        int n = data.length;
        int dim = data[0].length;
        means = new double[dim];
        stds = new double[dim];

        // Obliczenie średnich dla każdej cechy
        for (int j = 0; j < dim; j++) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += data[i][j];
            }
            means[j] = sum / n;
        }
        // Obliczenie odchyleń standardowych
        for (int j = 0; j < dim; j++) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                double diff = data[i][j] - means[j];
                sum += diff * diff;
            }
            stds[j] = Math.sqrt(sum / n);
            if (stds[j] == 0) {
                stds[j] = 1; // unikamy dzielenia przez zero
            }
        }
    }

    public double[][] transform(double[][] data) {
        int n = data.length;
        int dim = data[0].length;
        double[][] result = new double[n][dim];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < dim; j++) {
                result[i][j] = (data[i][j] - means[j]) / stds[j];
            }
        }
        return result;
    }
}

