package mlcocktail;
import java.util.Objects;

public class Ingredient {
    private int id;
    private String name;
    private String description;
    private int alcohol;
    private String type;
    private Double percentage; // Może być null
    private String imageUrl;
    private String createdAt;
    private String updatedAt;
    private String measure;

    // Gettery i settery
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAlcohol() { return alcohol; }
    public void setAlcohol(int alcohol) { this.alcohol = alcohol; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getMeasure() { return measure; }
    public void setMeasure(String measure) { this.measure = measure; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient)) return false;
        Ingredient that = (Ingredient) o;
        // Możesz porównać po id lub po name i measure – zakładamy, że id jest unikalne:
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", measure='" + measure + '\'' +
                '}';
    }
}

