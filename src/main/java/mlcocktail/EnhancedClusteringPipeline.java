package mlcocktail;

import smile.clustering.KMeans;
//import smile.feature.extraction.PCA;
import smile.feature.transform.Standardizer;
import smile.data.DataFrame;
import smile.data.vector.DoubleVector;
import smile.data.transform.InvertibleColumnTransform;
import java.util.stream.IntStream;
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

        // Budowa słownika składników
        double frequencyThreshold = 0.9;
        List<String> vocabulary = EnhancedFeatureExtractor.buildFilteredIngredientVocabulary(cocktails, frequencyThreshold);
        System.out.println("Filtered vocabulary size: " + vocabulary.size());

        // Tworzenie TF-IDF wektorów cech
        List<double[]> tfidfVectors = EnhancedFeatureExtractor.createTFIDFFeatureVectors(cocktails, vocabulary);
        double[][] tfidfData = tfidfVectors.toArray(new double[0][]);

        // Dodatkowe cechy
        double[][] additionalFeatures = new double[cocktails.size()][2];
        for (int i = 0; i < cocktails.size(); i++) {
            Cocktail c = cocktails.get(i);
            int total = c.getIngredients().size();
            int alcoholCount = (int) c.getIngredients().stream().filter(ing -> ing.getAlcohol() == 1).count();
            additionalFeatures[i][0] = total;
            additionalFeatures[i][1] = total > 0 ? (double) alcoholCount / total : 0;
        }

        // Łączenie cech
        int d = tfidfData[0].length + additionalFeatures[0].length;
        String[] colNames = IntStream.range(0, d).mapToObj(i -> "x" + (i + 1)).toArray(String[]::new);
        DoubleVector[] vectors = new DoubleVector[d];
        for (int j = 0; j < d; j++) {
            double[] colData = new double[cocktails.size()];
            for (int i = 0; i < cocktails.size(); i++) {
                colData[i] = (j < tfidfData[0].length) ? tfidfData[i][j] : additionalFeatures[i][j - tfidfData[0].length];
            }
            vectors[j] = DoubleVector.of(colNames[j], colData);
        }
        DataFrame df = DataFrame.of(vectors);

        // Standaryzacja
       // InvertibleColumnTransform transform = Standardizer.fit(df);
       // DataFrame standardizedDf = transform.apply(df);

        List<Integer> outliers = OutlierRemoval.findOutliers(df.toArray(),2.0);
        DataFrame cleanedData = DataFrame.of(OutlierRemoval.removeOutliers(df.toArray(), outliers));
        // Grid Search na PCA i KMeans
        int minDim = 2;
        int maxDim = Math.min(10, cleanedData.ncol());
        int[] kCandidates = {2, 3, 4, 5, 6, 7, 8, 9, 10};
        OptParams bestParams = GridSearchClustering.gridSearch(cleanedData, minDim, maxDim, kCandidates);
        System.out.println("Best params: Dimensions = " + bestParams.bestDim + ", k = " + bestParams.bestK + ", Score = " + bestParams.bestScore);
        //PCA i KMeans z najlepszymi parametrami
        //PCA pca = PCA.fit(bestParams.bestReducedData);
        //PCA projection = pca.getProjection(bestParams.bestDim);
        //DataFrame reducedData = projection.apply(bestParams.bestReducedData);

        int k = 3;
        KMeans kmeans = KMeans.fit(bestParams.bestReducedData.toArray(), k,100,1E-4);
        int[] finalLabels = kmeans.y;
        double finalSilhouette = Evaluator.computeSilhouetteScore(bestParams.bestReducedData.toArray(), finalLabels);
        System.out.println("Final silhouette score: " + finalSilhouette);

        // Wizualizacja
        Visualization.showClusters(bestParams.bestReducedData.toArray(), finalLabels);
    }
}

