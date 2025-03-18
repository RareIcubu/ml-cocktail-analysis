package mlcocktail;

import java.util.*;

public class EnhancedFeatureExtractor {

    /**
     * Buduje słownik składników, wykluczając te, które występują w więcej niż
     * threshold (np. 90%) koktajli.
     */
    public static List<String> buildFilteredIngredientVocabulary(List<Cocktail> cocktails, double threshold) {
        Map<String, Integer> freq = new HashMap<>();
        for (Cocktail cocktail : cocktails) {
            Set<String> ingrSet = new HashSet<>();
            for (Ingredient ing : cocktail.getIngredients()) {
                if (ing.getName() != null) {
                    ingrSet.add(ing.getName().trim().toLowerCase());
                }
            }
            for (String name : ingrSet) {
                freq.put(name, freq.getOrDefault(name, 0) + 1);
            }
        }
        int total = cocktails.size();
        List<String> vocabulary = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : freq.entrySet()) {
            if ((double) entry.getValue() / total < threshold) {
                vocabulary.add(entry.getKey());
            }
        }
        Collections.sort(vocabulary);
        return vocabulary;
    }

    /**
     * Tworzy wektory cech TF-IDF na podstawie słownika składników.
     * Dla każdego koktajlu:
     * - TF: przyjmujemy 1 jeśli składnik występuje, 0 jeśli nie.
     * - IDF: obliczane jako log(N / (1 + df)).
     */
    public static List<double[]> createTFIDFFeatureVectors(List<Cocktail> cocktails, List<String> vocabulary) {
        int N = cocktails.size();
        int V = vocabulary.size();
        double[] df = new double[V];
        
        // Obliczanie df: dla każdego składnika, w ilu koktajlach występuje
        for (Cocktail cocktail : cocktails) {
            Set<String> present = new HashSet<>();
            for (Ingredient ing : cocktail.getIngredients()) {
                if (ing.getName() != null) {
                    String name = ing.getName().trim().toLowerCase();
                    present.add(name);
                }
            }
            for (int i = 0; i < V; i++) {
                if (present.contains(vocabulary.get(i))) {
                    df[i]++;
                }
            }
        }
        
        // Obliczanie idf
        double[] idf = new double[V];
        for (int i = 0; i < V; i++) {
            idf[i] = Math.log((double) N / (1 + df[i]));
        }
        
        // Tworzenie wektorów TF-IDF
        List<double[]> featureVectors = new ArrayList<>();
        for (Cocktail cocktail : cocktails) {
            double[] vector = new double[V];
            Set<String> present = new HashSet<>();
            for (Ingredient ing : cocktail.getIngredients()) {
                if (ing.getName() != null) {
                    present.add(ing.getName().trim().toLowerCase());
                }
            }
            for (int i = 0; i < V; i++) {
                double tf = present.contains(vocabulary.get(i)) ? 1.0 : 0.0;
                vector[i] = tf * idf[i];
            }
            featureVectors.add(vector);
        }
        return featureVectors;
    }
}

