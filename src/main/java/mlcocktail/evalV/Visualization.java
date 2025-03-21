package mlcocktail.evalV;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.knowm.xchart.style.BoxStyler.BoxplotCalCulationMethod;
import smile.math.distance.EuclideanDistance; 
import java.awt.Color;
import java.util.*;

public class Visualization {

    /**
     * Wyświetla podstawowy wykres 2D z punktami przypisanymi do klastrów oraz centroidami.
     *
     * @param data   macierz punktów (każdy punkt to wektor cech, wymagamy co najmniej 2 wymiary)
     * @param labels etykiety klastrów dla każdego punktu
     */
    public static void showClusters(double[][] data, int[] labels) {
        if (data == null || labels == null || data.length != labels.length) {
            System.err.println("Błąd: dane i etykiety muszą mieć taką samą liczbę elementów.");
            return;
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i].length < 2) {
                System.err.println("Punkt o indeksie " + i + " ma mniej niż 2 wymiary. Pomijam.");
                return;
            }
        }

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Klasteryzacja koktajli")
                .xAxisTitle("Wymiar 1")
                .yAxisTitle("Wymiar 2")
                .build();

        // Generowanie kolorów dla klastrów
        Map<Integer, Color> clusterColors = new HashMap<>();
        Random rand = new Random();
        for (int label : labels) {
            if (!clusterColors.containsKey(label)) {
                if (label == 2147483647 || label == -1) {
                    clusterColors.put(label, Color.GRAY);
                } else {
                    clusterColors.put(label, new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
                }
            }
        }

        // Grupowanie punktów według etykiety
        Map<Integer, List<Double>> clusterPointsX = new HashMap<>();
        Map<Integer, List<Double>> clusterPointsY = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            double x = data[i][0];
            double y = data[i][1];
            int cluster = labels[i];
            clusterPointsX.computeIfAbsent(cluster, k -> new ArrayList<>()).add(x);
            clusterPointsY.computeIfAbsent(cluster, k -> new ArrayList<>()).add(y);
        }

        // Dodanie serii punktów
        for (Map.Entry<Integer, List<Double>> entry : clusterPointsX.entrySet()) {
            int cluster = entry.getKey();
            List<Double> xPoints = entry.getValue();
            List<Double> yPoints = clusterPointsY.get(cluster);
            if (yPoints == null || xPoints.size() != yPoints.size()) {
                System.err.println("Mismatch in data lengths for cluster " + cluster);
                continue;
            }
            String seriesName = (cluster == 2147483647 || cluster == -1) ? "Szum" : "Klaster " + cluster;
            double[] xData = xPoints.stream().mapToDouble(Double::doubleValue).toArray();
            double[] yData = yPoints.stream().mapToDouble(Double::doubleValue).toArray();
            XYSeries series = chart.addSeries(seriesName, xData, yData);
            series.setMarker(SeriesMarkers.CIRCLE);
            series.setMarkerColor(clusterColors.get(cluster));
        }

        // Rysowanie centroidów
        Map<Integer, double[]> centroids = computeCentroids(data, labels);
        for (Map.Entry<Integer, double[]> entry : centroids.entrySet()) {
            int cluster = entry.getKey();
            double[] centroid = entry.getValue();
            String centroidName = "Centroid klastru " + cluster;
            chart.addSeries(centroidName,
                    new double[]{centroid[0]},
                    new double[]{centroid[1]})
                    .setMarker(SeriesMarkers.DIAMOND)
                    .setMarkerColor(clusterColors.get(cluster));
        }

        new SwingWrapper<>(chart).displayChart();
    }

    /**
     * Wyświetla silhouette plot.
     * Oblicza wartości silhouette dla każdego punktu, grupuje je według klastrów,
     * sortuje i tworzy wykres słupkowy.
     *
     * @param data   macierz danych (co najmniej 2 wymiary)
     * @param labels etykiety klastrów
     */
    public static void showSilhouettePlot(double[][] data, int[] labels) {
        // Oblicz silhouette dla każdego punktu
        Map<Integer, List<Double>> silhouetteValues = computeSilhouetteValues(data, labels);
        
        // Utwórz wykres słupkowy (XYChart) – każda seria to jeden klaster
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Silhouette Plot")
                .xAxisTitle("Indeks (po sortowaniu)")
                .yAxisTitle("Silhouette value")
                .build();

        // Losowe kolory dla serii
        Random rand = new Random();
        Map<Integer, Color> clusterColors = new HashMap<>();
        for (Integer cluster : silhouetteValues.keySet()) {
            if (cluster == 2147483647 || cluster == -1) {
                clusterColors.put(cluster, Color.GRAY);
            } else {
                clusterColors.put(cluster, new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
            }
        }
        
        // Dla każdego klastra dodajemy serię – sortujemy wartości silhouette w obrębie klastra
        for (Map.Entry<Integer, List<Double>> entry : silhouetteValues.entrySet()) {
            int cluster = entry.getKey();
            List<Double> values = entry.getValue();
            Collections.sort(values);
            double[] xData = new double[values.size()];
            double[] yData = new double[values.size()];
            for (int i = 0; i < values.size(); i++) {
                xData[i] = i; // indeks po sortowaniu
                yData[i] = values.get(i);
            }
            String seriesName = (cluster == 2147483647 || cluster == -1) ? "Szum" : "Klaster " + cluster;
            XYSeries series = chart.addSeries(seriesName, xData, yData);
            series.setMarker(SeriesMarkers.NONE);
            series.setLineColor(clusterColors.get(cluster));
        }
        
        new SwingWrapper<>(chart).displayChart();
    }

    /**
     * Wyświetla wykres profili centroidów.
     * Dla każdego klastra rysuje linię przedstawiającą wartości cech w centroidzie.
     *
     * @param data   macierz danych (wszystkie wymiary są brane pod uwagę)
     * @param labels etykiety klastrów
     */
    public static void showCentroidProfiles(double[][] data, int[] labels) {
        Map<Integer, double[]> centroids = computeCentroids(data, labels);
        int dim = data[0].length;
        // Przygotowanie danych: oś X to indeksy cech
        double[] xData = new double[dim];
        for (int i = 0; i < dim; i++) {
            xData[i] = i;
        }
        
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Profile centroidów")
                .xAxisTitle("Indeks cechy")
                .yAxisTitle("Wartość cechy")
                .build();
        
        // Losowe kolory
        Random rand = new Random();
        for (Integer cluster : centroids.keySet()) {
            Color color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            String seriesName = "Centroid klastru " + cluster;
            XYSeries series = chart.addSeries(seriesName, xData, centroids.get(cluster));
            series.setMarker(SeriesMarkers.NONE);
            series.setLineColor(color);
        }
        
        new SwingWrapper<>(chart).displayChart();
    }

    /**
     * Oblicza centroidy dla każdego klastra.
     *
     * @param data   macierz danych
     * @param labels etykiety klastrów
     * @return mapa: etykieta -> centroid (jako tablica double[])
     */
    private static Map<Integer, double[]> computeCentroids(double[][] data, int[] labels) {
        Map<Integer, double[]> centroids = new HashMap<>();
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            int cluster = labels[i];
            if (!centroids.containsKey(cluster)) {
                centroids.put(cluster, new double[data[i].length]);
                counts.put(cluster, 0);
            }
            double[] current = centroids.get(cluster);
            for (int j = 0; j < data[i].length; j++) {
                current[j] += data[i][j];
            }
            counts.put(cluster, counts.get(cluster) + 1);
        }
        for (Integer cluster : centroids.keySet()) {
            double[] centroid = centroids.get(cluster);
            int count = counts.get(cluster);
            for (int j = 0; j < centroid.length; j++) {
                centroid[j] /= count;
            }
        }
        return centroids;
    }

    /**
     * Oblicza wartości silhouette dla każdego punktu i grupuje je według klastrów.
     *
     * @param data   macierz danych
     * @param labels etykiety klastrów
     * @return mapa: etykieta -> lista wartości silhouette dla punktów w tym klastrze
     */
    private static Map<Integer, List<Double>> computeSilhouetteValues(double[][] data, int[] labels) {
        int n = data.length;
        Map<Integer, List<Double>> silhouetteMap = new HashMap<>();
        // Grupujemy indeksy punktów według etykiety
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int label = labels[i];
            clusters.computeIfAbsent(label, k -> new ArrayList<>()).add(i);
        }
        
        EuclideanDistance distance = new EuclideanDistance();
        // Dla każdego punktu obliczamy wartość silhouette
        for (int i = 0; i < n; i++) {
            int label = labels[i];
            List<Integer> sameCluster = clusters.get(label);
            if (sameCluster == null || sameCluster.size() <= 1) continue;
            
            double a = 0.0;
            int intraCount = 0;
            for (int j : sameCluster) {
                if (j == i) continue;
                a += distance.d(data[i], data[j]);
                intraCount++;
            }
            if (intraCount > 0) a /= intraCount;
            else continue;
            
            double b = Double.MAX_VALUE;
            for (Map.Entry<Integer, List<Integer>> entry : clusters.entrySet()) {
                if (entry.getKey() == label) continue;
                List<Integer> otherCluster = entry.getValue();
                double sum = 0.0;
                for (int j : otherCluster) {
                    sum += distance.d(data[i], data[j]);
                }
                double avg = sum / otherCluster.size();
                if (avg < b) b = avg;
            }
            
            double s = (b - a) / Math.max(a, b);
            silhouetteMap.computeIfAbsent(label, k -> new ArrayList<>()).add(s);
        }
        return silhouetteMap;
    }

		public static void showBoxplot(double[][] data, int[] labels, int featureIndex, String featureName) {
    // Grupujemy wartości danej cechy dla każdego klastra
    Map<Integer, List<Double>> clusterValues = new HashMap<>();
    for (int i = 0; i < data.length; i++) {
        int cluster = labels[i];
        clusterValues.computeIfAbsent(cluster, k -> new ArrayList<>()).add(data[i][featureIndex]);
    }
    
    // Tworzymy BoxChart
    BoxChart chart = new BoxChartBuilder()
            .title("Boxplot dla cechy: " + featureName)
            .width(800)
            .height(600)
            .build();
    
    // Ustawiamy metodę obliczeniową i tooltips
    chart.getStyler().setBoxplotCalCulationMethod(BoxplotCalCulationMethod.N_LESS_1_PLUS_1);
    chart.getStyler().setToolTipsEnabled(true);
    
    // Dodajemy serie – każda seria to wartości danej cechy dla jednego klastra
    for (Map.Entry<Integer, List<Double>> entry : clusterValues.entrySet()) {
        String seriesName = "Klaster " + entry.getKey();
        chart.addSeries(seriesName, entry.getValue());
    }
    
    new SwingWrapper<>(chart).displayChart();
    }
}

