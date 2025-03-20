package mlcocktail;
import java.util.Objects;

/**
 * <p>Ingredient class.</p>
 *
 * @author jakub
 * @version $Id: $Id
 */
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
    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a int
     */
    public int getId() { return id; }
    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a int
     */
    public void setId(int id) { this.id = id; }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() { return name; }
    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object
     */
    public void setName(String name) { this.name = name; }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getDescription() { return description; }
    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * <p>Getter for the field <code>alcohol</code>.</p>
     *
     * @return a int
     */
    public int getAlcohol() { return alcohol; }
    /**
     * <p>Setter for the field <code>alcohol</code>.</p>
     *
     * @param alcohol a int
     */
    public void setAlcohol(int alcohol) { this.alcohol = alcohol; }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getType() { return type; }
    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link java.lang.String} object
     */
    public void setType(String type) { this.type = type; }

    /**
     * <p>Getter for the field <code>percentage</code>.</p>
     *
     * @return a {@link java.lang.Double} object
     */
    public Double getPercentage() { return percentage; }
    /**
     * <p>Setter for the field <code>percentage</code>.</p>
     *
     * @param percentage a {@link java.lang.Double} object
     */
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    /**
     * <p>Getter for the field <code>imageUrl</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getImageUrl() { return imageUrl; }
    /**
     * <p>Setter for the field <code>imageUrl</code>.</p>
     *
     * @param imageUrl a {@link java.lang.String} object
     */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    /**
     * <p>Getter for the field <code>createdAt</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCreatedAt() { return createdAt; }
    /**
     * <p>Setter for the field <code>createdAt</code>.</p>
     *
     * @param createdAt a {@link java.lang.String} object
     */
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /**
     * <p>Getter for the field <code>updatedAt</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUpdatedAt() { return updatedAt; }
    /**
     * <p>Setter for the field <code>updatedAt</code>.</p>
     *
     * @param updatedAt a {@link java.lang.String} object
     */
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    /**
     * <p>Getter for the field <code>measure</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getMeasure() { return measure; }
    /**
     * <p>Setter for the field <code>measure</code>.</p>
     *
     * @param measure a {@link java.lang.String} object
     */
    public void setMeasure(String measure) { this.measure = measure; }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient)) return false;
        Ingredient that = (Ingredient) o;
        // Możesz porównać po id lub po name i measure – zakładamy, że id jest unikalne:
        return id == that.id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", measure='" + measure + '\'' +
                ", description='" + description + '\'' +
                ", alcohol=" + alcohol +
                ", type='" + type + '\'' +
                ", percentage=" + percentage +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}

