package mlcocktail;

import java.util.List;

public class Cocktail{
    private String name;
    private List<Ingredient> ingredients;
    public String getName(){
        return name;
    }
    public List<Ingredient> getIngredients(){
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


