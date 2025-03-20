package mlcocktail.evalV;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.Color;
import java.util.*;

public class Visualization {

    /**
     * Wyświetla wykres 2D z punktami przypisanymi do klastrów.
     * Dodatkowo rysuje centroidy klastrów.
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

        // Utworzenie wykresu
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Klasteryzacja koktajli")
                .xAxisTitle("Wymiar 1")
                .yAxisTitle("Wymiar 2")
                .build();

        // Wygeneruj paletę kolorów dla klastrów
        Map<Integer, Color> clusterColors = new HashMap<>();
        Random rand = new Random();
        for (int label : labels) {
            if (!clusterColors.containsKey(label)) {
                // Jeśli etykieta wskazuje szum/outlier (przyjmujemy 2147483647 lub -1),
                // przypisujemy specjalny kolor (np. szary)
                if (label == 2147483647 || label == -1) {
                    clusterColors.put(label, Color.GRAY);
                } else {
                    // Losowy kolor
                    clusterColors.put(label, new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
                }
            }
        }

        // Grupowanie punktów według etykiety
        Map<Integer, List<Double>> clusterPointsX = new HashMap<>();
        Map<Integer, List<Double>> clusterPointsY = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            double x = data[i][0]; // pierwszy wymiar
            double y = data[i][1]; // drugi wymiar
            int cluster = labels[i];
            clusterPointsX.computeIfAbsent(cluster, k -> new ArrayList<>()).add(x);
            clusterPointsY.computeIfAbsent(cluster, k -> new ArrayList<>()).add(y);
        }

        // Dodajemy serie z punktami
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

        // Opcjonalnie: obliczamy i rysujemy centroidy klastrów
        Map<Integer, double[]> centroids = computeCentroids(data, labels);
        for (Map.Entry<Integer, double[]> entry : centroids.entrySet()) {
            int cluster = entry.getKey();
            double[] centroid = entry.getValue();
            // Nazwa serii centroidu
            String centroidName = "Centroid klastru " + cluster;
            chart.addSeries(centroidName,
                    new double[]{centroid[0]},
                    new double[]{centroid[1]})
                    .setMarker(SeriesMarkers.DIAMOND)
                    .setMarkerColor(clusterColors.get(cluster));
        }

        // Wyświetlanie wykresu
        new SwingWrapper<>(chart).displayChart();
    }

    /**
     * Oblicza centroidy dla każdego klastra.
     *
     * @param data   macierz danych
     * @param labels etykiety klastrów
     * @return mapa: etykieta -> centroid (jako tablica double[]).
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
        // Dzielenie przez liczbę punktów
        for (Integer cluster : centroids.keySet()) {
            double[] centroid = centroids.get(cluster);
            int count = counts.get(cluster);
            for (int j = 0; j < centroid.length; j++) {
                centroid[j] /= count;
            }
        }
        return centroids;
    }
}

