package mlcocktail;

import java.util.List;
import java.util.stream.Collectors;

public class DataPreprocessor {

    // Normalizacja nazw koktajli (np. usunięcie nadmiarowych spacji)
    public static void normalizeCocktailNames(List<Cocktail> cocktails) {
        for (Cocktail cocktail : cocktails) {
            if (cocktail.getName() != null) {
                cocktail.setName(cocktail.getName().trim());
            }
        }
    }

    // Uzupełnienie brakujących wartości w polu measure składników
    public static void fillMissingIngredientMeasure(List<Cocktail> cocktails) {
        for (Cocktail cocktail : cocktails) {
            if (cocktail.getIngredients() != null) {
                for (Ingredient ingredient : cocktail.getIngredients()) {
                    if (ingredient.getMeasure() == null || ingredient.getMeasure().trim().isEmpty()) {
                        ingredient.setMeasure("unknown");
                    } else {
                        // Opcjonalnie: normalizacja pola measure (usunięcie zbędnych spacji)
                        ingredient.setMeasure(ingredient.getMeasure().trim());
                    }
                }
            }
        }
    }

    // Usunięcie duplikatów składników w obrębie pojedynczego koktajlu
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

    // Opcjonalnie: metoda usuwająca lub ignorująca niepotrzebne pola z obiektów Cocktail lub Ingredient.
    // Możesz np. stworzyć nową reprezentację, która zawiera tylko te pola, które będą użyte w modelu.
}

