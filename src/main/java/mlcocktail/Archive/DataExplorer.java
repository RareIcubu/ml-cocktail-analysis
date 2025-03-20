package mlcocktail.Archive;

import mlcocktail.Cocktail;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

@Deprecated(since="1.5-ALPHA", forRemoval = false)
public class DataExplorer{
    public static void printSummary(List<Cocktail> cocktails){
        System.out.println("Number of cocktails: " + cocktails.size());

        cocktails.stream().limit(5).forEach(System.out::println);

    }
    public static void averageIngredients(List<Cocktail> cocktails){
        double avg = cocktails.stream().mapToInt(cocktail -> cocktail.getIngredients().size()).average().getAsDouble();
        System.out.println("Average number of ingredients: " + avg);
    }
    public static void distributionIngredients(List<Cocktail> cocktails){
        Map<Integer, Long> distribution = cocktails.stream().collect(Collectors.groupingBy(cocktail -> cocktail.getIngredients().size(), Collectors.counting()));
        System.out.println("Distribution of ingredients: " + distribution);
    }

}


