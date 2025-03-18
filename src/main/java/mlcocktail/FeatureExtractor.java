package mlcocktail;

import java.util.*;

public class FeatureExtractor {

    // Buduje słownik unikalnych nazw składników (przy ujednoliceniu do małych liter)
    public static List<String> buildVocabulary(List<Cocktail> cocktails) {
        Set<String> ingredientSet = new HashSet<>();
        for (Cocktail cocktail : cocktails) {
            for (Ingredient ing : cocktail.getIngredients()) {
                if (ing.getName() != null) {
                    ingredientSet.add(ing.getName().trim().toLowerCase());
                }
            }
        }
        List<String> vocabulary = new ArrayList<>(ingredientSet);
        Collections.sort(vocabulary);
        return vocabulary;
    }

    // Tworzy wektory cech dla każdego koktajlu na podstawie obecności składników
    public static List<double[]> createFeatureVectors(List<Cocktail> cocktails, List<String> vocabulary) {
        List<double[]> featureVectors = new ArrayList<>();
        for (Cocktail cocktail : cocktails) {
            double[] vector = new double[vocabulary.size()];
            for (Ingredient ing : cocktail.getIngredients()) {
                String ingredientName = ing.getName().trim().toLowerCase();
                int index = vocabulary.indexOf(ingredientName);
                if (index >= 0) {
                    vector[index] = 1;  // lub możesz zwiększać wartość, jeśli chcesz liczyć wystąpienia
                }
            }
            featureVectors.add(vector);
        }
        return featureVectors;
    }
}

