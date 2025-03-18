package mlcocktail;

import mlcocktail.eda.DataExplorer;
import mlcocktail.eda.DataQuality;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "data/cocktail_dataset.json";
        List<Cocktail> cocktails = DataLoader.loadCocktails(filePath);
        if (cocktails == null) {
            System.err.println("Error loading cocktails data.");
            return;
        }
        
        // EDA i preprocessing (wywołanie metod z DataExplorer, DataQuality oraz DataPreprocessor)
        DataExplorer.printSummary(cocktails);
        DataExplorer.averageIngredients(cocktails);
        DataExplorer.distributionIngredients(cocktails);
        DataQuality.missingIngredients(cocktails);
        DataQuality.duplicateIngredients(cocktails);
        DataQuality.missingFieldsInIngredients(cocktails);
        DataPreprocessor.normalizeCocktailNames(cocktails);
        DataPreprocessor.fillMissingIngredientMeasure(cocktails);
        DataPreprocessor.removeDuplicateIngredients(cocktails);

        // Augmentacja – ekstrakcja cech
        List<String> vocabulary = FeatureExtractor.buildVocabulary(cocktails);
        List<double[]> featureVectors = FeatureExtractor.createFeatureVectors(cocktails, vocabulary);
        double[][] data = featureVectors.toArray(new double[featureVectors.size()][]);

        // Klasteryzacja – ustawiamy liczbę klastrów, np. 4
        int k = 4;
        int[] labels = ClusteringModel.clusterCocktails(data, k);
        double silhouetteScore = Evaluator.computeSilhouetteScore(data, labels);
        System.out.println("Silhouette score: " + silhouetteScore);
    }
}

