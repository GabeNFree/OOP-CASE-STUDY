import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        WorldDatabase database = new WorldDatabase();
        ChapterTracker currentChapter = new ChapterTracker();
        boolean running = true;

        database.loadData();

        System.out.println("=========================================");
        System.out.println("              WORLD NEXUS                ");
        System.out.println("=========================================");

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. View All Database Entities");
            System.out.println("2. Add New Lore (Character/Location/System)");
            System.out.println("3. Search Database (Proves Overloading & Interfaces)");
            System.out.println("4. Filter by Category");
            System.out.println("5. View Current Chapter Tracker (Proves Association)");
            System.out.println("6. Edit Entity Description (Update)"); 
            System.out.println("7. Delete Entity (Delete)");           
            System.out.println("8. Save and Exit System");           
            System.out.print("Select an option (1-8): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n--- ALL DATABASE ENTITIES ---");
                    // Searching with an empty string just returns everything
                    for (StoryElement element : database.searchLore("")) {
                         System.out.println("[" + element.getClass().getSimpleName() + "] " + element.getName() + " - " + element.getDescription());
                    }
                    break;

                case "2":
                    System.out.println("\n--- ADD NEW LORE ---");
                    System.out.println("A. Character | B. Location | C. World System");
                    System.out.print("Select type (A/B/C): ");
                    String typeChoice = scanner.nextLine().toUpperCase();
                    
                    System.out.print("Enter ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Description: ");
                    String desc = scanner.nextLine();

                    if (typeChoice.equals("A")) {
                        System.out.print("Enter Narrative Role: ");
                        String role = scanner.nextLine();
                        database.addElement(new Character(id, name, desc, role));
                        System.out.println("SUCCESS: Character added.");
                    } else if (typeChoice.equals("B")) {
                        Location newLoc = new Location(id, name, desc);
                        System.out.print("Enter Location Type (e.g., Forest, City): ");
                        newLoc.setLocationType(scanner.nextLine());
                        database.addElement(newLoc);
                        System.out.println("SUCCESS: Location added.");
                    } else if (typeChoice.equals("C")) {
                        WorldSystem newSys = new WorldSystem(id, name, desc);
                        System.out.print("Enter System Category (e.g., Magic, Politics): ");
                        newSys.setSystemCategory(scanner.nextLine());
                        database.addElement(newSys);
                        System.out.println("SUCCESS: World System added.");
                    } else {
                        System.out.println("Invalid selection.");
                    }
                    break;

                case "3":
                    System.out.println("\n--- SEARCH DATABASE ---");
                    System.out.print("Enter keyword: ");
                    String keyword = scanner.nextLine();
                    System.out.print("Require EXACT name match? (Y/N): ");
                    boolean exact = scanner.nextLine().equalsIgnoreCase("Y");

                    // Method Overloading demonstrated here
                    List<StoryElement> searchResults;
                    if (exact) {
                        searchResults = database.searchLore(keyword, true);
                    } else {
                        searchResults = database.searchLore(keyword); // Standard search
                    }
                    
                    System.out.println("\n--- RESULTS ---");
                    if (searchResults.isEmpty()) {
                        System.out.println("No entries found.");
                    } else {
                        for (StoryElement result : searchResults) {
                            System.out.println("FOUND: [" + result.getClass().getSimpleName() + "] " + result.getName());
                        }
                    }
                    break;

                case "4":
                    System.out.print("\nEnter category to filter (Character/Location/WorldSystem): ");
                    String filterType = scanner.nextLine();
                    List<StoryElement> filterResults = database.filterByType(filterType);
                    
                    System.out.println("\n--- FILTER RESULTS ---");
                    if (filterResults.isEmpty()) {
                        System.out.println("No entries found.");
                    } else {
                        for (StoryElement result : filterResults) {
                            System.out.println(result.getName() + " - " + result.getDescription());
                        }
                    }
                    break;

                case "5":
                    System.out.println("\n--- CURRENT CHAPTER TRACKER ---");
                    System.out.println("Chapter: " + currentChapter.getChapterNumber());
                    System.out.println("Plot Beat: " + currentChapter.getPlotBeat());
                    System.out.println("Summary: " + currentChapter.getChapterSummary());
                    
                    // Association
                    if (currentChapter.getPovCharacter() != null) {
                        System.out.println("POV Character: " + currentChapter.getPovCharacter().getName() 
                                           + " (Role: " + currentChapter.getPovCharacter().getRole() + ")");
                    } else {
                        System.out.println("POV Character: None assigned.");
                    }
                    break;

                case "6":
                    System.out.println("\n--- UPDATE ENTITY ---");
                    System.out.print("Enter the ID of the entity you want to edit: ");
                    String updateId = scanner.nextLine();
                    StoryElement elementToUpdate = database.getElementById(updateId);
                    
                    if (elementToUpdate != null) {
                        System.out.println("Editing: " + elementToUpdate.getName());
                        System.out.println("Current Description: " + elementToUpdate.getDescription());
                        System.out.print("Enter NEW Description: ");
                        String newDesc = scanner.nextLine();
                        elementToUpdate.setDescription(newDesc);
                        System.out.println("SUCCESS: Entity updated.");
                    } else {
                        System.out.println("ERROR: No entity found with ID '" + updateId + "'.");
                    }
                    break;

                case "7":
                    System.out.println("\n--- DELETE ENTITY ---");
                    System.out.print("Enter the ID of the entity you want to permanently delete: ");
                    String deleteId = scanner.nextLine();
                    
                    boolean isDeleted = database.deleteElement(deleteId);
                    if (isDeleted) {
                        System.out.println("SUCCESS: Entity '" + deleteId + "' has been deleted.");
                    } else {
                        System.out.println("ERROR: No entity found with ID '" + deleteId + "'.");
                    }
                    break;

                case "8":
                    System.out.println("\nSaving narrative data...");
                    database.saveData(); 
                    System.out.println("Shutting down World Nexus.");
                    running = false;
                    break;

                default:
                    System.out.println("\nInvalid option. Please type a number between 1 and 6.");
            }
        }
    }
}
