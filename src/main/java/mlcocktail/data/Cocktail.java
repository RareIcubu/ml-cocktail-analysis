package mlcocktail.data;

import java.util.List;

/**
 * <p>Cocktail class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
public class Cocktail{
    private String name;
    private List<Ingredient> ingredients;
    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName(){
        return name;
    }
    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object
     */
    public void setName(String name){
        this.name = name;
    }
    /**
     * <p>Getter for the field <code>ingredients</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<Ingredient> getIngredients(){
        return ingredients;
    }
    /**
     * <p>Setter for the field <code>ingredients</code>.</p>
     *
     * @param ingredients a {@link java.util.List} object
     */
    public void setIngredients(List<Ingredient> ingredients){
        this.ingredients = ingredients;
    }
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Cocktail{" +
                "name='" + name + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }
}


