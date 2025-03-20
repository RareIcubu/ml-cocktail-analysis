package mlcocktail.Archive;

import java.util.List;
import java.util.stream.Collectors;
import mlcocktail.Cocktail;
import mlcocktail.Ingredient;

@Deprecated(since="1.5-ALPHA",forRemoval = false)
public class DataQuality{
    public static void missingIngredients(List<Cocktail> cocktails){
        List<Cocktail> missing = cocktails.stream().filter(coctail -> coctail.getIngredients().isEmpty()).collect(Collectors.toList());
        System.out.println("Number of cocktails with missing ingredients: " + missing.size());
    }
    public static void duplicateIngredients(List<Cocktail> cocktails){
        List<Cocktail> duplicates = cocktails.stream().filter(cocktail -> cocktail.getIngredients().size() != cocktail.getIngredients().stream().distinct().count()).collect(Collectors.toList());
        System.out.println("Number of cocktails with duplicate ingredients: " + duplicates.size());
    }
  public static void missingFieldsInIngredients(List<Cocktail> cocktails) {
        int missingCount = 0;
        for (Cocktail cocktail : cocktails) {
            if (cocktail.getIngredients() != null) {
                for (Ingredient ingredient : cocktail.getIngredients()) {
                    // Sprawdzamy, czy pole name lub measure jest puste lub null
                    if (ingredient.getName() == null || ingredient.getName().trim().isEmpty() ||
                        ingredient.getMeasure() == null || ingredient.getMeasure().trim().isEmpty()) {
                        missingCount++;
                        System.out.println("Missing field in ingredient: " + ingredient + " (Cocktail: " + cocktail.getName() + ")");
                    }
                }
            }
        }
        System.out.println("Total number of ingredients with missing fields: " + missingCount);
    }
}

