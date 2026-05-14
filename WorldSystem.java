import java.util.ArrayList;
import java.util.List;

public class WorldSystem extends StoryElement {

    private String systemCategory;
    private List<String> governingRules;
    private String exceptions;

    public WorldSystem(String id, String name, String description) {
        super(id, name, description);
        this.governingRules = new ArrayList<>();
        this.exceptions = "";
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

    public String getExceptions() {
        return exceptions;
    }

    public void setExceptions(String exceptions) {
        this.exceptions = exceptions;
    }
}