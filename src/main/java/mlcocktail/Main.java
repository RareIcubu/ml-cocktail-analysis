package mlcocktail;

import java.util.List;
import mlcocktail.eda.DataExplorer;
import mlcocktail.eda.DataQuality;
public class Main{
    public static void main(String args[]){
        //Declarating the path of the dataset
        final String filepath = "data/cocktail_dataset.json";
        
        //Loading the dataset
        List<Cocktail> cocktails = DataLoader.loadCocktails(filepath);
        if (cocktails == null){
            System.out.println("Failed to load data");
            return;
        }

        //EDA analysis
        DataExplorer.printSummary(cocktails);
        DataExplorer.averageIngredients(cocktails);
        DataExplorer.distributionIngredients(cocktails);
        DataQuality.missingIngredients(cocktails);
        DataQuality.duplicateIngredients(cocktails);

    }
}
