package mlcocktail;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Scanner;
import java.lang.reflect.Type;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;

public class DataLoader{
    public static List<Cocktail> loadCocktails(String filepath){
        try(Scanner scan = new Scanner(new File(filepath))){
            scan.useDelimiter("\\A");
            String json = scan.hasNext() ? scan.next() : "";
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Cocktail>>(){}.getType();
            return gson.fromJson(json, listType);
        }catch (FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }
}

