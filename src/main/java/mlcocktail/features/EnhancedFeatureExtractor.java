package mlcocktail.features;

import mlcocktail.data.Cocktail;
import mlcocktail.data.Ingredient;
import smile.nlp.dictionary.EnglishStopWords;
import smile.nlp.stemmer.LancasterStemmer;
import smile.nlp.stemmer.Stemmer;
import smile.nlp.tokenizer.SimpleTokenizer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Klasa do ekstrakcji i przetwarzania cech z listy koktajli.
 *
 * @author jakub
 * @version $Id: $Id
 */
public class EnhancedFeatureExtractor {
    private static final Stemmer STEMMER = new LancasterStemmer();
    private static final SimpleTokenizer TOKENIZER = new SimpleTokenizer(true);

    /**
     * Buduje słownik składników, filtrując te, które pojawiają się zbyt często.
     *
     * @param cocktails Lista koktajli.
     * @param threshold Próg, powyżej którego składnik jest odrzucany.
     * @return Posortowana lista przetworzonych nazw składników.
     */
    public static List<String> buildFilteredIngredientVocabulary(List<Cocktail> cocktails, double threshold) {
        Map<String, Integer> frequency = new HashMap<>();

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
            uniqueIngredients.forEach(name -> frequency.put(name, frequency.getOrDefault(name, 0) + 1));
        }

        return frequency.entrySet().stream()
                .filter(entry -> (double) entry.getValue() / cocktails.size() < threshold)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Przetwarza nazwę składnika – tokenizacja, zamiana na małe litery, usunięcie stop-słów, stemming.
     *
     * @param name Nazwa składnika.
     * @return Przetworzona nazwa.
     */
    private static String processIngredientName(String name) {
        return Stream.of(TOKENIZER.split(name))
                .map(String::toLowerCase)
                .filter(token -> !EnglishStopWords.DEFAULT.contains(token))
                .map(STEMMER::stem)
                .collect(Collectors.joining(" "));
    }

    /**
     * Tworzy wektory cech TF–IDF z wykorzystaniem binarnego TF (0 lub 1) oraz klasycznego wzoru IDF.
     *
     * @param cocktails  Lista koktajli.
     * @param vocabulary Lista wybranych składników (słownik).
     * @return Lista wektorów cech reprezentowanych jako double[].
     */
    public static List<double[]> createTFIDFFeatureVectors(List<Cocktail> cocktails, List<String> vocabulary) {
        int N = cocktails.size();
        int V = vocabulary.size();
        double[] df = new double[V];

        // Obliczenie liczby dokumentów zawierających dany termin
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
                double tf = present.contains(vocabulary.get(i)) ? 1.0 : 0.0;
                vector[i] = tf * idf[i];
            }
            // Normalizacja wektora
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
        
