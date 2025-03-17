package mlcocktail;

import java.util.List;

public class Cocktail{
    private String name;
    private List<String> ingredients;
    public String getName(){
        return name;
    }
    public List<String> getIngredients(){
        return ingredients;
    }

    @Override
    public String toString() {
        return "Cocktail{" +
                "name='" + name + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }
}


