
import java.util.ArrayList;
import java.util.List;

public class WorldSystem extends StoryElement {

    private String systemCategory;
    private List<String> governingRules;

    public WorldSystem(String id, String name, String description) {
        super(id, name, description);
        this.governingRules = new ArrayList<>();
    }

    public String getSystemCategory() {
        return systemCategory;
    }

    public void setSystemCategory(String category) {
        this.systemCategory = category;
    }

    public List<String> getRules() {
        return governingRules;
    }

    public void addRule(String newRule) {
        this.governingRules.add(newRule);
    }
}