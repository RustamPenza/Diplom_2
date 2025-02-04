import java.util.List;

public class Order {

    private List<String> ingredientsIds;
    private List<Ingredient> ingredients;

//    public Order(List<String> ingredientsIds) {
//        this.ingredientsIds = ingredientsIds;
//    }

    public Order(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredientsIds() {
        return ingredientsIds;
    }

    public void setIngredientsIds(List<String> ingredientsIds) {
        this.ingredientsIds = ingredientsIds;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
