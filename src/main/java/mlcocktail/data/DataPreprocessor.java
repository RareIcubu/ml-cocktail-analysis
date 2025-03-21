package mlcocktail.data;

import java.util.List;
import java.util.stream.Collectors;
/**
 * <p>DataPreprocessor class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public class DataPreprocessor {

    // Normalizacja nazw koktajli (np. usunięcie nadmiarowych spacji)
    /**
     * <p>normalizeCocktailNames.</p>
     *
     * @param cocktails a {@link java.util.List} object
     */
    public static void normalizeCocktailNames(List<Cocktail> cocktails) {
        for (Cocktail cocktail : cocktails) {
            if (cocktail.getName() != null) {
                cocktail.setName(cocktail.getName().trim());
            }
        }
    }

    /**
     * <p>fillMissingIngredientFields.</p>
     *
     * @param cocktails a {@link java.util.List} object
     */
    public static void fillMissingIngredientFields(List<Cocktail> cocktails) {
        for (Cocktail cocktail : cocktails) {
            if (cocktail.getIngredients() != null) {
                for (Ingredient ingredient : cocktail.getIngredients()) {
                    // Measure
                    if (ingredient.getMeasure() == null || ingredient.getMeasure().trim().isEmpty()) {
                        ingredient.setMeasure("unknown");
                    } else {
                        ingredient.setMeasure(ingredient.getMeasure().trim());
                    }
                    
                    // Percentage – jeśli jest null, ustawiamy domyślnie 0.0
                    if (ingredient.getPercentage() == null) {
                        ingredient.setPercentage(0.0);
                    }
                    
                    // Description – jeśli brak, ustawiamy domyślny tekst
                    if (ingredient.getDescription() == null || ingredient.getDescription().trim().isEmpty()) {
                        ingredient.setDescription("No description");
                    } else {
                        ingredient.setDescription(ingredient.getDescription().trim());
                    }
                    
                    // Type – jeśli brak, ustawiamy "unknown"
                    if (ingredient.getType() == null || ingredient.getType().trim().isEmpty()) {
                        ingredient.setType("unknown");
                    } else {
                        ingredient.setType(ingredient.getType().trim());
                    }
                }
            }
        }
    }  
    // Usunięcie duplikatów składników w obrębie pojedynczego koktajlu
    /**
     * <p>removeDuplicateIngredients.</p>
     *
     * @param cocktails a {@link java.util.List} object
     */
    public static void removeDuplicateIngredients(List<Cocktail> cocktails) {
        for (Cocktail cocktail : cocktails) {
            if (cocktail.getIngredients() != null) {
                List<Ingredient> uniqueIngredients = cocktail.getIngredients().stream()
                        .distinct()  // Wymaga, aby klasa Ingredient miała przesłonięte equals() i hashCode()
                        .collect(Collectors.toList());
                cocktail.setIngredients(uniqueIngredients);
            }
        }
    }
}

