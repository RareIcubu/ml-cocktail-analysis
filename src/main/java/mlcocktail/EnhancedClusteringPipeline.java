package mlcocktail;

import smile.clustering.KMeans;
import smile.projection.PCA;
import java.util.*;

public class EnhancedClusteringPipeline {

    public static void main(String[] args) {
        // Ładowanie i przetwarzanie danych
        String filePath = "data/cocktail_dataset.json";
        List<Cocktail> cocktails = DataLoader.loadCocktails(filePath);
        if (cocktails == null) {
            System.err.println("Error loading cocktail data.");
            return;
        }

        // Preprocessing: normalizacja, uzupełnianie braków i usuwanie duplikatów
        DataPreprocessor.normalizeCocktailNames(cocktails);
        DataPreprocessor.fillMissingIngredientFields(cocktails);
        DataPreprocessor.removeDuplicateIngredients(cocktails);

        // Budujemy słownik składników, filtrując bardzo popularne składniki (>90% występowania)
        double frequencyThreshold = 0.9;
        List<String> vocabulary = EnhancedFeatureExtractor.buildFilteredIngredientVocabulary(cocktails, frequencyThreshold);
        System.out.println("Filtered vocabulary size: " + vocabulary.size());

        // Tworzymy TF-IDF wektory cech dla koktajli
        List<double[]> tfidfVectors = EnhancedFeatureExtractor.createTFIDFFeatureVectors(cocktails, vocabulary);
        double[][] tfidfData = tfidfVectors.toArray(new double[tfidfVectors.size()][]);

        // Dodatkowe cechy: liczba składników oraz proporcja składników alkoholowych
        double[][] additionalFeatures = new double[cocktails.size()][2];
        for (int i = 0; i < cocktails.size(); i++) {
            Cocktail c = cocktails.get(i);
            int total = c.getIngredients().size();
            int alcoholCount = 0;
            for (Ingredient ing : c.getIngredients()) {
                if (ing.getAlcohol() == 1) {
                    alcoholCount++;
                }
            }
            additionalFeatures[i][0] = total;
            additionalFeatures[i][1] = total > 0 ? (double) alcoholCount / total : 0;
        }

        // Łączymy cechy (konkatenacja wektorów)
        int tfidfDim = tfidfData[0].length;
        int addDim = additionalFeatures[0].length;
        double[][] combinedFeatures = new double[cocktails.size()][tfidfDim + addDim];
        for (int i = 0; i < cocktails.size(); i++) {
            System.arraycopy(tfidfData[i], 0, combinedFeatures[i], 0, tfidfDim);
            System.arraycopy(additionalFeatures[i], 0, combinedFeatures[i], tfidfDim, addDim);
        }

        // Standaryzacja – używamy własnej klasy Standardizer
        Standardizer standardizer = new Standardizer(combinedFeatures);
        double[][] standardizedData = standardizer.transform(combinedFeatures);

        // Przeprowadzenie grid searchu po docelowej liczbie wymiarów oraz liczbie klastrów
        int minDim = 2;
        // Ustalamy maxDim np. na 10 lub nie więcej niż oryginalna liczba cech
        int maxDim = Math.min(10, standardizedData[0].length);
        int[] kCandidates = {2, 3, 4, 5, 6, 7, 8, 9, 10};

        OptParams bestParams = GridSearchClustering.gridSearch(standardizedData, minDim, maxDim, kCandidates);
        System.out.println("Najlepsze parametry: docelowa liczba wymiarów = " + bestParams.bestDim +
                ", k = " + bestParams.bestK +
                ", silhouette score = " + bestParams.bestScore);

        // Dla najlepszej konfiguracji wykonujemy PCA i klasteryzację
        PCA pca = PCA.fit(standardizedData);
        pca.setProjection(bestParams.bestDim);
        double[][] reducedData = pca.project(standardizedData);

        // Opcjonalnie usuwamy outliery
        List<Integer> outliers = OutlierRemoval.findOutliers(reducedData, 2.0);
        System.out.println("Found outliers: " + outliers.size());
        double[][] cleanedData = OutlierRemoval.removeOutliers(reducedData, outliers);
        System.out.println("Data size after removing outliers: " + cleanedData.length);

        KMeans kmeans = KMeans.fit(cleanedData, bestParams.bestK);
        int[] finalLabels = kmeans.y;
        double finalSilhouette = Evaluator.computeSilhouetteScore(cleanedData, finalLabels);
        System.out.println("Final silhouette score: " + finalSilhouette);

        // Wizualizacja wyników (zakładając, że dane mają co najmniej 2 wymiary)
        Visualization.showClusters(cleanedData, finalLabels);
    
    }
}
