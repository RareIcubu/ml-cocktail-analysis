package mlcocktail.eda;

import java.util.List;
import java.util.stream.Collectors;
import mlcocktail.Cocktail;

public class DataQuality{
    public static void missingIngredients(List<Cocktail> cocktails){
        List<Cocktail> missing = cocktails.stream().filter(coctail -> coctail.getIngredients().isEmpty()).collect(Collectors.toList());
        System.out.println("Number of cocktails with missing ingredients: " + missing.size());
    }
    public static void duplicateIngredients(List<Cocktail> cocktails){
        List<Cocktail> duplicates = cocktails.stream().filter(cocktail -> cocktail.getIngredients().size() != cocktail.getIngredients().stream().distinct().count()).collect(Collectors.toList());
        System.out.println("Number of cocktails with duplicate ingredients: " + duplicates.size());
    }

}

