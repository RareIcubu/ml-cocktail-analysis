package mlcocktail.eda;

import smile.nlp.dictionary.EnglishStopWords;
import smile.nlp.stemmer.LancasterStemmer;
import smile.nlp.stemmer.Stemmer;
import smile.nlp.tokenizer.SimpleTokenizer;
import java.util.*;
import java.util.stream.Collectors;
import mlcocktail.Cocktail;
import mlcocktail.Ingredient;

/**
 * <p>EnhancedFeatureExtractor class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public class EnhancedFeatureExtractor {
    private static final Stemmer STEMMER = new LancasterStemmer();
    private static final SimpleTokenizer TOKENIZER = new SimpleTokenizer(true);

    /**
     * <p>buildFilteredIngredientVocabulary.</p>
     *
     * @param cocktails a {@link java.util.List} object
     * @param threshold a double
     * @return a {@link java.util.List} object
     */
    public static List<String> buildFilteredIngredientVocabulary(List<Cocktail> cocktails, double threshold) {
        Map<String, Integer> freq = new HashMap<>();
        
        for (Cocktail cocktail : cocktails) {
            Set<String> uniqueIngredients = new HashSet<>();
            for (Ingredient ing : cocktail.getIngredients()) {
                if (ing.getName() != null) {
                    String processed = processIngredientName(ing.getName());
                    if (!processed.isEmpty()) {
                        uniqueIngredients.add(processed);
                    }
                }
            }
            uniqueIngredients.forEach(name -> freq.put(name, freq.getOrDefault(name, 0) + 1));
        }

        return freq.entrySet().stream()
                .filter(entry -> (double) entry.getValue() / cocktails.size() < threshold)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    private static String processIngredientName(String name) {
        return Arrays.stream(TOKENIZER.split(name))
                .map(String::toLowerCase)
                .filter(token -> !EnglishStopWords.DEFAULT.contains(token))
                .map(STEMMER::stem)
                .collect(Collectors.joining(" "));
    }

    /**
     * Tworzy wektory cech TF–IDF:
     * - TF: dla uproszczenia binarne (1, jeśli termin występuje, 0 w przeciwnym wypadku).
     * - IDF: obliczane jako log(N / (1 + df)).
     *
     * @param cocktails a {@link java.util.List} object
     * @param vocabulary a {@link java.util.List} object
     * @return a {@link java.util.List} object
     */
    public static List<double[]> createTFIDFFeatureVectors(List<Cocktail> cocktails, List<String> vocabulary) {
        int N = cocktails.size();
        int V = vocabulary.size();
        double[] df = new double[V];

        // Obliczanie df: dla każdego terminu, w ilu dokumentach występuje
        for (Cocktail cocktail : cocktails) {
            Set<String> present = cocktail.getIngredients().stream()
                    .map(Ingredient::getName)
                    .filter(Objects::nonNull)
                    .map(EnhancedFeatureExtractor::processIngredientName)
                    .collect(Collectors.toSet());
            for (int i = 0; i < V; i++) {
                if (present.contains(vocabulary.get(i))) {
                    df[i]++;
                }
            }
        }

        double[] idf = new double[V];
        for (int i = 0; i < V; i++) {
            idf[i] = Math.log((double) N / (1 + df[i]));
        }

        // Tworzenie wektorów TF–IDF – przyjmujemy binarne TF
        List<double[]> featureVectors = new ArrayList<>();
        for (Cocktail cocktail : cocktails) {
            double[] vector = new double[V];
            Set<String> present = cocktail.getIngredients().stream()
                    .map(Ingredient::getName)
                    .filter(Objects::nonNull)
                    .map(EnhancedFeatureExtractor::processIngredientName)
                    .collect(Collectors.toSet());
            for (int i = 0; i < V; i++) {
                double tf =present.contains(vocabulary.get(i)) ? 1.0 : 0.0;
                vector[i] = tf * idf[i];
            }
            double norm = Math.sqrt(Arrays.stream(vector).map(x -> x * x).sum());
            if (norm > 0) {
                for (int i = 0; i < V; i++) {
                    vector[i] /= norm;
                }
            }
            featureVectors.add(vector);
        }
        return featureVectors;
    }
}
         
