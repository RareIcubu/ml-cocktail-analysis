package mlcocktail.evalV;

import org.knowm.xchart.*;
import java.util.*;

public class Visualization {

    public static void showClusters(double[][] data, int[] labels) {
        // Sprawdzenie, czy dane i etykiety nie są puste i mają tę samą długość
        if (data == null || labels == null || data.length != labels.length) {
            System.err.println("Błąd: dane i etykiety muszą mieć taką samą liczbę elementów.");
            return;
        }
        
        // Upewnij się, że każdy punkt ma co najmniej 2 wymiary (bo rysujemy tylko 2D)
        for (int i = 0; i < data.length; i++) {
            if (data[i].length < 2) {
                System.err.println("Punkt o indeksie " + i + " ma mniej niż 2 wymiary. Pomijam.");
                return;
            }
        }

        // Tworzenie wykresu
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Klasteryzacja koktajli")
                .xAxisTitle("Wymiar 1")
                .yAxisTitle("Wymiar 2")
                .build();

        // Mapowanie etykiet klastrów do list punktów
        Map<Integer, List<Double>> clusterPointsX = new HashMap<>();
        Map<Integer, List<Double>> clusterPointsY = new HashMap<>();

        // Grupowanie punktów według etykiet
        for (int i = 0; i < data.length; i++) {
            double x = data[i][0]; // Pierwszy wymiar
            double y = data[i][1]; // Drugi wymiar
            int cluster = labels[i]; // Etykieta klastra

            // Inicjalizacja list, jeśli klaster nie istnieje w mapie
            clusterPointsX.computeIfAbsent(cluster, k -> new ArrayList<>()).add(x);
            clusterPointsY.computeIfAbsent(cluster, k -> new ArrayList<>()).add(y);
        }

        // Dodawanie punktów do wykresu
        for (Map.Entry<Integer, List<Double>> entry : clusterPointsX.entrySet()) {
            int cluster = entry.getKey();
            List<Double> xPoints = entry.getValue();
            List<Double> yPoints = clusterPointsY.get(cluster);
            
            // Sprawdzamy, czy listy punktów mają tę samą długość
            if (yPoints == null || xPoints.size() != yPoints.size()) {
                System.err.println("Mismatch in data lengths for cluster " + cluster);
                continue;
            }

            // Nazwa serii – traktujemy etykietę 2147483647 jako szum/outlier
            String seriesName = (cluster == 2147483647) ? "Szum" : "Klaster " + cluster;

            // Konwersja list na tablice
            double[] xData = xPoints.stream().mapToDouble(Double::doubleValue).toArray();
            double[] yData = yPoints.stream().mapToDouble(Double::doubleValue).toArray();

            // Dodanie serii do wykresu
            chart.addSeries(seriesName, xData, yData);
        }

        // Wyświetlanie wykresu
        new SwingWrapper<>(chart).displayChart();
    }
}

