import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChapterOutlinerWindow extends JFrame {
    private static final String ADD_CHAPTER_OPTION = "Add Chapter...";
    private final WorldDatabase worldDatabase;
    private final Map<Integer, Chapter> chaptersByNumber = new LinkedHashMap<>();
    private JComboBox<String> chapterCombo;
    private JComboBox<String> povCombo;
    private JTextField plotBeatField;
    private JTextArea summaryArea;
    private int currentChapterNumber = -1;
    private boolean suppressEvents;

    public ChapterOutlinerWindow(WorldDatabase worldDatabase) {
        this.worldDatabase = worldDatabase;
        setTitle("Chapter-by-Chapter Outliner");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 640);
        setLocationRelativeTo(null);

        loadChaptersFromDatabase();

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(new Color(236, 239, 243));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel("\uD83D\uDCDD  Chapter-by-Chapter Outliner");
        title.setFont(new Font("SansSerif", Font.PLAIN, 36 / 2));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(makeLabel("Chapter Number:"), gbc);
        gbc.gridx = 1;
        form.add(makeLabel("Point of View (POV) Character:"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        chapterCombo = new JComboBox<>(new String[]{ADD_CHAPTER_OPTION});
        chapterCombo.setFont(new Font("SansSerif", Font.PLAIN, 18 / 2));
        form.add(chapterCombo, gbc);

        gbc.gridx = 1;
        povCombo = new JComboBox<>();
        povCombo.setFont(new Font("SansSerif", Font.PLAIN, 18 / 2));
        refreshPovCombo();
        form.add(povCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 2, 0);
        form.add(makeLabel("Plot Beat:"), gbc);

        gbc.gridy = 3;
        plotBeatField = new JTextField();
        plotBeatField.setFont(new Font("SansSerif", Font.PLAIN, 18 / 2));
        plotBeatField.setPreferredSize(new Dimension(0, 30));
        form.add(plotBeatField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(4, 0, 2, 0);
        form.add(makeLabel("Chapter Summary:"), gbc);

        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        summaryArea = new JTextArea();
        summaryArea.setFont(new Font("SansSerif", Font.PLAIN, 18 / 2));
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        JScrollPane summaryScroll = new JScrollPane(summaryArea);
        form.add(summaryScroll, gbc);

        root.add(form, BorderLayout.CENTER);
        setContentPane(root);

        syncChapterComboFromMap();
        bindEvents();
        if (!chaptersByNumber.isEmpty()) {
            loadChapterIntoForm(new TreeSet<>(chaptersByNumber.keySet()).first());
        } else {
            clearFormForNoChapter();
        }
    }

    private void bindEvents() {
        chapterCombo.addActionListener(event -> {
            if (suppressEvents) {
                return;
            }
            Object selected = chapterCombo.getSelectedItem();
            if (!(selected instanceof String)) {
                return;
            }
            String selectedLabel = (String) selected;
            if (selectedLabel.equals(ADD_CHAPTER_OPTION)) {
                promptAddChapter();
                return;
            }
            int selectedNumber = parseChapterLabel(selectedLabel);
            if (selectedNumber < 1) {
                return;
            }
            saveCurrentChapterFromForm();
            loadChapterIntoForm(selectedNumber);
        });

        povCombo.addActionListener(event -> {
            if (suppressEvents) {
                return;
            }
            saveCurrentChapterFromForm();
        });

        plotBeatField.addActionListener(event -> saveCurrentChapterFromForm());
        plotBeatField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent event) {
                saveCurrentChapterFromForm();
            }
        });

        summaryArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent event) {
                saveCurrentChapterFromForm();
            }
        });
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 30 / 2));
        return label;
    }

    private void refreshPovCombo() {
        suppressEvents = true;
        Object currentSelected = povCombo.getSelectedItem();
        povCombo.removeAllItems();
        
        List<String> characters = new ArrayList<>();
        for (StoryElement element : worldDatabase.filterByType("Character")) {
            characters.add(element.getName());
        }
        
        for (String name : characters) {
            povCombo.addItem(name);
        }
        
        if (currentSelected != null) {
            povCombo.setSelectedItem(currentSelected);
        }
        suppressEvents = false;
    }

    private String[] buildPovOptions() {
        java.util.List<String> names = new java.util.ArrayList<>();
        for (StoryElement element : worldDatabase.filterByType("Character")) {
            names.add(element.getName());
        }
        return names.toArray(new String[0]);
    }

    private void loadChaptersFromDatabase() {
        for (StoryElement element : worldDatabase.filterByType("Chapter")) {
            Chapter chapter = (Chapter) element;
            chaptersByNumber.put(chapter.getChapterNumber(), chapter);
        }
    }

    private void syncChapterComboFromMap() {
        suppressEvents = true;
        chapterCombo.removeAllItems();
        for (int number : new TreeSet<>(chaptersByNumber.keySet())) {
            chapterCombo.addItem("Chapter " + number);
        }
        chapterCombo.addItem(ADD_CHAPTER_OPTION);
        suppressEvents = false;
    }

    private void promptAddChapter() {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter chapter number:",
                "Add Chapter",
                JOptionPane.PLAIN_MESSAGE);
        if (input == null) {
            restoreCurrentSelection();
            return;
        }
        String cleaned = input.trim();
        if (cleaned.isEmpty()) {
            restoreCurrentSelection();
            return;
        }

        int chapterNumber;
        try {
            chapterNumber = Integer.parseInt(cleaned);
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Chapter number must be whole number.");
            restoreCurrentSelection();
            return;
        }
        if (chapterNumber < 1) {
            JOptionPane.showMessageDialog(this, "Chapter number must be 1 or greater.");
            restoreCurrentSelection();
            return;
        }
        if (chaptersByNumber.containsKey(chapterNumber)) {
            JOptionPane.showMessageDialog(this, "Chapter already exists.");
            loadChapterIntoForm(chapterNumber);
            return;
        }

        saveCurrentChapterFromForm();
        createOrGetChapter(chapterNumber);
        syncChapterComboFromMap();
        loadChapterIntoForm(chapterNumber);
    }

    private void restoreCurrentSelection() {
        suppressEvents = true;
        if (currentChapterNumber > 0) {
            chapterCombo.setSelectedItem("Chapter " + currentChapterNumber);
        } else {
            chapterCombo.setSelectedItem(ADD_CHAPTER_OPTION);
        }
        suppressEvents = false;
    }

    private int parseChapterLabel(String label) {
        if (label == null || !label.startsWith("Chapter ")) {
            return -1;
        }
        String numberText = label.substring("Chapter ".length()).trim();
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private Chapter createOrGetChapter(int chapterNumber) {
        Chapter existing = chaptersByNumber.get(chapterNumber);
        if (existing != null) {
            return existing;
        }
        Chapter created = new Chapter(
                "chapter-" + chapterNumber,
                "Chapter " + chapterNumber,
                "",
                chapterNumber);
        worldDatabase.addChapter(created);
        chaptersByNumber.put(chapterNumber, created);
        return created;
    }

    private void loadChapterIntoForm(int chapterNumber) {
        suppressEvents = true;
        currentChapterNumber = chapterNumber;
        Chapter chapter = createOrGetChapter(chapterNumber);

        chapterCombo.setSelectedItem("Chapter " + chapterNumber);
        plotBeatField.setText(chapter.getPlotBeat() == null ? "" : chapter.getPlotBeat());
        summaryArea.setText(chapter.getChapterSummary() == null ? "" : chapter.getChapterSummary());

        String pov = chapter.getPovCharacterId();
        if (pov == null || pov.trim().isEmpty()) {
            if (povCombo.getItemCount() > 0) {
                povCombo.setSelectedIndex(0);
            }
        } else {
            povCombo.setSelectedItem(pov);
        }
        suppressEvents = false;
    }

    private void clearFormForNoChapter() {
        suppressEvents = true;
        currentChapterNumber = -1;
        chapterCombo.setSelectedItem(ADD_CHAPTER_OPTION);
        plotBeatField.setText("");
        summaryArea.setText("");
        if (povCombo.getItemCount() > 0) {
            povCombo.setSelectedIndex(0);
        }
        suppressEvents = false;
    }

    private void saveCurrentChapterFromForm() {
        if (currentChapterNumber < 1) {
            return;
        }
        Chapter chapter = createOrGetChapter(currentChapterNumber);
        chapter.setPlotBeat(plotBeatField.getText().trim());
        chapter.setChapterSummary(summaryArea.getText().trim());
        chapter.setDescription(summaryArea.getText().trim());
        Object pov = povCombo.getSelectedItem();
        chapter.setPovCharacterId(pov == null ? "" : pov.toString());
    }
}
