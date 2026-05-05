
import java.util.ArrayList;
import java.util.List;

public class Location extends StoryElement {

    private String locationType;
    private String associatedTerritory;
    private List<String> importantDetails;

    public Location(String id, String name, String description) {
        super(id, name, description);
        this.importantDetails = new ArrayList<>();
    }

    public String getLocationType() { return locationType; }
    public void setLocationType(String type) { this.locationType = type; }
    public String getAssociatedTerritory() { return associatedTerritory; }
    public void setAssociatedTerritory(String territory) { this.associatedTerritory = territory; }
    public List<String> getImportantDetails() { return importantDetails; }
    
    public void addImportantDetail(String detail) {
        this.importantDetails.add(detail);
    }
}