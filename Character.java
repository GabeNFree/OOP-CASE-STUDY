// Inheritance through specialized classes using extends
public class Character extends StoryElement {
    private String role;
    private String primaryGoal;
    private String currentGoal;
    private String fatalFlaw;
    private String characterArcType;

    public Character(String id, String name, String description, String role) {
        super(id, name, description);
        this.role = role;
    }

    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}
    public String getPrimaryGoal() {return primaryGoal;}
    public void setPrimaryGoal(String goal) {this.primaryGoal = goal;}
    public String getCurrentGoal() {return currentGoal;}
    public void setCurrentGoal(String goal) {this.currentGoal = goal;}
    public String getFatalFlaw() {return fatalFlaw;}
    public void setFatalFlaw(String flaw) {this.fatalFlaw = flaw;}
    public String getCharacterArcType() {return characterArcType;}
    public void setCharacterArcType(String arcType) {this.characterArcType = arcType;}

    public void updateArc(String arcStatus) {
        System.out.println("Character arc updated to: " + arcStatus);
    }
}