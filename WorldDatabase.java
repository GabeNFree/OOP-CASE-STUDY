import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WorldDatabase {
    private List<StoryElement> allElements; 
    private final String SAVE_FILE = "world_nexus_save.dat";

    public WorldDatabase() {
        this.allElements = new ArrayList<>();
    }

    public void addElement(StoryElement element) {
        allElements.add(element);
    }

    // Standard Search
    public List<StoryElement> searchLore(String keyword) {
        List<StoryElement> results = new ArrayList<>();
        for (StoryElement element : allElements) {
            if (element.matchesKeyword(keyword)) {
                results.add(element);
            }
        }
        return results;
    }

    // Overloaded Search (Shows Polymorphism through overloading)
    public List<StoryElement> searchLore(String keyword, boolean exactMatch) {
        List<StoryElement> results = new ArrayList<>();
        for (StoryElement element : allElements) {
            if (exactMatch) {
                // Exact match logic: only returns if the name is an exact match (ignores case of course)
                if (element.getName().equalsIgnoreCase(keyword)) {
                    results.add(element);
                }
            } else {
                // Fallback to standard search if exactMatch is false
                if (element.matchesKeyword(keyword)) {
                    results.add(element);
                }
            }
        }
        return results;
    }

    public List<StoryElement> filterByType(String className) {
        List<StoryElement> results = new ArrayList<>();
        for (StoryElement element : allElements) {
            if (element.getClass().getSimpleName().equalsIgnoreCase(className)) {
                results.add(element);
            }
        }
        return results;
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(allElements);
            System.out.println("SYSTEM: Database successfully saved to " + SAVE_FILE);
        } catch (IOException e) {
            System.out.println("SYSTEM ERROR: Could not save data. " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadData() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                allElements = (List<StoryElement>) ois.readObject();
                System.out.println("SYSTEM: Previous database loaded successfully.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("SYSTEM ERROR: Could not load data. " + e.getMessage());
            }
        } else {
            System.out.println("SYSTEM: No previous save found. Booting fresh database.");
        }
    }
}