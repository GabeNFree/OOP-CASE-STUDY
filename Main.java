import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        WorldDatabase database = new WorldDatabase();
        ChapterTracker currentChapter = new ChapterTracker();
        boolean running = true;

        database.loadData();

        // Pre-loaded data (Proving Polymorphism)
        Character hero = new Character("C-001", "Marcus Lowenthal", "A rogue with a hidden past.", "Protagonist");
        hero.setPrimaryGoal("Find the lost artifact.");
        hero.setFatalFlaw("Trusts no one.");
        
        Location city = new Location("L-001", "Marthul", "The glowing city of mages.");
        city.setLocationType("Metropolis");
        city.setAssociatedTerritory("The Northern Border");
        
        WorldSystem magic = new WorldSystem("S-001", "Resonance", "Magic based on sound.");
        magic.setSystemCategory("Magic");
        magic.addRule("Spells require perfect vocal pitch.");

        database.addElement(hero);
        database.addElement(city);
        database.addElement(magic);

        // Pre-loaded Chapter Tracker (Proving Association)
        currentChapter.setChapterNumber(1);
        currentChapter.setPovCharacter(hero); 
        currentChapter.setPlotBeat("The hero arrives at the gates.");
        currentChapter.setChapterSummary("Marcus reaches Marthul but is denied entry.");

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
            System.out.println("6. Exit System");
            System.out.print("Select an option (1-6): ");

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
                    System.out.println("\nSaving narrative data...");
                    database.saveData();    
                    System.out.println("\nSaving narrative data... Shutting down World Nexus.");
                    running = false;
                    break;

                default:
                    System.out.println("\nInvalid option. Please type a number between 1 and 6.");
            }
        }
    }
}