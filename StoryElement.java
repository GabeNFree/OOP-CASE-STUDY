import java.io.Serializable;

// Abstraction through abstract class
abstract class StoryElement implements ISearchable {

    private static final long serialVersionUID = 1L; 

// Encapsulation seen through private variables
    private String id;
    private String name;
    private String description;

    public StoryElement(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public String getId() {return id;}
    public void setId(String newId) {this.id = newId;}
    public String getName() {return name;}
    public void setName(String newName) {this.name = newName;}
    public String getDescription() {return description;}
    public void setDescription(String desc) {this.description = desc;}

    // Implementing the interface method with a really basic search logic
    @Override
    public boolean matchesKeyword(String keyword) {
        return name.toLowerCase().contains(keyword.toLowerCase())
                || description.toLowerCase().contains(keyword.toLowerCase());
    }
}