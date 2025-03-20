package mlcocktail;

import smile.clustering.CentroidClustering;
//import smile.feature.extraction.PCA;
import smile.clustering.KMeans;
//import smile.clustering.DBSCAN;
import smile.feature.transform.Standardizer;
//import smile.manifold.UMAP;
import smile.data.DataFrame;
import smile.data.transform.InvertibleColumnTransform;

import mlcocktail.eda.*;
import mlcocktail.data.*;
import mlcocktail.evalV.Visualization;
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

        DataFrame df = DataFrame.of(tfidfData); 
        // Standaryzacja
       InvertibleColumnTransform transform = Standardizer.fit(df);
       DataFrame standardizedDf = transform.apply(df);

        List<Integer> outliers = OutlierRemoval.findOutliers(standardizedDf.toArray(),2.0);
        DataFrame cleanedData = DataFrame.of(OutlierRemoval.removeOutliers(standardizedDf.toArray(), outliers));
        // Grid Search na PCA i KMeans
        int minDim = 2;
        int maxDim = Math.min(10, cleanedData.ncol());
        int[] kCandidates = {2, 3, 4, 5, 6, 7, 8, 9, 10};
        OptParams bestParams = GridSearchClustering.gridSearch(cleanedData, minDim, maxDim, kCandidates);
        System.out.println("Best params: Dimensions = " + bestParams.bestDim + ", k = " + bestParams.bestK + ", Score = " + bestParams.bestScore);
        //PCA i KMeans z najlepszymi parametrami
//      PCA pca = PCA.fit(bestParams.bestReducedData);
//      PCA projection = pca.getProjection(bestParams.bestDim);
//      DataFrame reducedData = projection.apply(bestParams.bestReducedData);
//      double[][] reducedData = null;
//      double score = 0;
//      for (int i = 2; i < 120; i++){
//          double[][] tempData = UMAP.fit(cleanedData.toArray(),new UMAP.Options(i));
//          CentroidClustering<double[],double[]> kmeans = KMeans.fit(tempData,3,100);
//          int[] Labels = kmeans.group();
//          double tempscore = Evaluator.computeSilhouetteScore(tempData,Labels);
//          if (tempscore > score){
//              score = tempscore;
//              reducedData = tempData;
//          }
//      }
//
       // DBSCAN<double[]> dbscan = DBSCAN.fit(bestParams.bestReducedData.toArray(),2,0.3);


        CentroidClustering<double[],double[]> kmeans = KMeans.fit(bestParams.bestReducedData.toArray(), bestParams.bestK,100);
        //CentroidClustering<double[],double[]> kmeans = KMeans.fit(cleanedData.toArray(), 2,100);

        int[] finalLabels = kmeans.group();
//        int[] finalLabels = dbscan.group();
        //double finalSilhouette = Evaluator.silhouetteScore(bestParams.bestReducedData.toArray(), finalLabels);
        //double finalSilhouette = Evaluator.computeSilhouetteScore(cleanedData.toArray(), finalLabels);
        
        Evaluator.Evaluate(bestParams.bestReducedData.toArray(), finalLabels, bestParams.bestK);

        //System.out.println("Final silhouette score: " + finalSilhouette);

        // Wizualizacja
        Visualization.showClusters(bestParams.bestReducedData.toArray(), finalLabels);
        // Visualization.showClusters(cleanedData.toArray(), finalLabels);
    }
}

