package mlcocktail;

import mlcocktail.config.ClustererConfig;
import mlcocktail.clustering.OptParams;
import mlcocktail.config.ReducerConfig;
import mlcocktail.data.DataLoader;
import mlcocktail.data.DataPreprocessor;
import mlcocktail.data.OutlierRemoval;
import mlcocktail.evaluation.Evaluator;
import mlcocktail.evaluation.Visualization;
import mlcocktail.features.DimensionReducer;
import mlcocktail.features.EnhancedFeatureExtractor;
import mlcocktail.features.ReducerFactory;
import mlcocktail.clustering.Clusterer;
import mlcocktail.data.Cocktail;
import mlcocktail.evaluation.ClusteringResult;
import mlcocktail.clustering.ClustererFactory;
import mlcocktail.clustering.GridSearchClustering;
import smile.data.DataFrame;
import smile.data.transform.InvertibleColumnTransform;
import smile.feature.transform.Standardizer;

import java.util.List;
import java.util.Map;

/**
 * <p>MainPipeline class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public class MainPipeline {
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects
     */
    public static void main(String[] args) {
        // 1. Ładowanie danych
        String filePath = "data/cocktail_dataset.json";
        List<Cocktail> cocktails = DataLoader.loadCocktails(filePath);
        if (cocktails == null || cocktails.isEmpty()) {
            System.err.println("Błąd podczas ładowania danych koktajli.");
            return;
        }
        System.out.println("Liczba załadowanych koktajli: " + cocktails.size());

        // 2. Preprocessing – normalizacja, uzupełnianie braków i usuwanie duplikatów składników
        DataPreprocessor.normalizeCocktailNames(cocktails);
        DataPreprocessor.fillMissingIngredientFields(cocktails);
        DataPreprocessor.removeDuplicateIngredients(cocktails);

        // 3. Ekstrakcja cech – budowa słownika i tworzenie wektorów TF–IDF
        double frequencyThreshold = 0.9;
        List<String> vocabulary = EnhancedFeatureExtractor.buildFilteredIngredientVocabulary(cocktails, frequencyThreshold);
        System.out.println("Rozmiar słownika składników: " + vocabulary.size());
        List<double[]> tfidfVectors = EnhancedFeatureExtractor.createTFIDFFeatureVectors(cocktails, vocabulary);
        double[][] tfidfData = tfidfVectors.toArray(new double[0][]);

        // 4. Konwersja do DataFrame i standaryzacja
        DataFrame df = DataFrame.of(tfidfData);
        InvertibleColumnTransform standardizer = Standardizer.fit(df);
        DataFrame standardizedDf = standardizer.apply(df);

        // 5. Usuwanie outlierów
        List<Integer> outliers = OutlierRemoval.findOutliers(standardizedDf.toArray(), 2.0);
        DataFrame cleanedData = DataFrame.of(OutlierRemoval.removeOutliers(standardizedDf.toArray(), outliers));
        System.out.println("Liczba punktów po usunięciu outlierów: " + cleanedData.nrow());

        // 6. Grid search – optymalizacja redukcji wymiarów (PCA) oraz liczby klastrów (KMeans)
        int minDim = 2;
        int maxDim = Math.min(10, cleanedData.ncol());
        int[] kCandidates = {2, 3, 4, 5, 6, 7, 8, 9, 10};
        OptParams bestParams = GridSearchClustering.gridSearch(cleanedData, minDim, maxDim, kCandidates);
        System.out.println("Najlepsze parametry:");
        System.out.println("  Wymiary = " + bestParams.getBestDim());
        System.out.println("  Liczba klastrów (k) = " + bestParams.getBestK());
        System.out.println("  Wynik composite = " + bestParams.getBestScore());

        // 7. Redukcja wymiarów przy użyciu fabryki reduktorów
        // Konfiguracja reduktora – można rozszerzyć, dodając odpowiednie parametry
        ReducerConfig reducerConfig = new ReducerConfig();
        // Wybieramy algorytm "pca" lub "umap" w zależności od potrzeb
        DimensionReducer reducer = ReducerFactory.createReducer("pca", reducerConfig, Map.of());
        double[][] reducedData = reducer.reduceDimensions(cleanedData, bestParams.getBestDim());
        System.out.println("Redukcja wymiarów za pomocą " + reducer.getAlgorithmName() + " zakończona.");

        // 8. Klasteryzacja przy użyciu fabryki klasteryzatorów
        // Konfiguracja klasteryzatora – na przykład ustawienia dla KMeans
        ClustererConfig clustererConfig = new ClustererConfig();
        // Zakładamy, że w mapie przekazujemy dodatkowe parametry, np. "maxIterations"
        Clusterer clusterer = ClustererFactory.createClusterer("kmeans", clustererConfig, Map.of("maxIterations", 100, "k", bestParams.getBestK()));
        // Klasteryzacja
        ClusteringResult result = clusterer.cluster(reducedData);
        System.out.println("Klasteryzacja (" + clusterer.getAlgorithmName() + ") zakończona.");

        // 9. Ewaluacja
        Evaluator.Evaluate(reducedData, result.getLabels());

        // 10. Wizualizacja
        Visualization.showClusters(reducedData, result.getLabels());
        Visualization.showSilhouettePlot(reducedData, result.getLabels());
        Visualization.showCentroidProfiles(reducedData, result.getLabels());
        Visualization.showBoxplot(reducedData, result.getLabels(), 0, "TF-IDF Feature 0");
    }
}

