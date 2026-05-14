import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

public class LocationsWindow extends JFrame {
    private static final String CUSTOM_OPTION = "Custom...";
    private static final String TERRITORY_PLACEHOLDER = "[Enter Territory Name]";

    private final Map<String, Location> locationTypeData = new LinkedHashMap<>();
    private final Map<String, List<String>> territoryHistoryByType = new LinkedHashMap<>();
    private JComboBox<String> locationTypeCombo;
    private JTextField territoryField;
    private DefaultListModel<String> territoryListModel;
    private JList<String> territoryList;
    private JTextArea detailsArea;
    private String currentType;
    private boolean suppressTypeEvents;

    public LocationsWindow() {
        setTitle("Location & Territory Manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 650);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(new Color(236, 239, 243));
        root.setBorder(BorderFactory.createEmptyBorder(10, 8, 8, 8));

        JLabel title = new JLabel("\uD83D\uDDFA  Location & Territory Manager");
        title.setFont(new Font("SansSerif", Font.PLAIN, 20));
        title.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        root.add(title, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setOpaque(false);
        body.add(buildLeftPane(), BorderLayout.WEST);
        body.add(buildRightPane(), BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);
        setContentPane(root);

        refreshTerritoryField();
        refreshTerritoryList();
        refreshDetailsArea();
    }

    private JPanel buildLeftPane() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(610, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel locationTypeLabel = new JLabel("Location Type");
        locationTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.insets = new Insets(4, 0, 4, 0);
        left.add(locationTypeLabel, gbc);

        gbc.gridy++;
        String[] types = {CUSTOM_OPTION};
        locationTypeCombo = new JComboBox<>(types);
        locationTypeCombo.setSelectedItem(CUSTOM_OPTION);
        locationTypeCombo.setFont(new Font("SansSerif", Font.PLAIN, 9));
        locationTypeCombo.setPreferredSize(new Dimension(0, 38));
        locationTypeCombo.addActionListener(event -> {
            if (suppressTypeEvents) {
                return;
            }
            handleLocationTypeChange();
        });
        gbc.insets = new Insets(0, 0, 10, 0);
        left.add(locationTypeCombo, gbc);

        gbc.gridy++;
        JLabel territoryLabel = new JLabel("Associated Territory");
        territoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.insets = new Insets(0, 0, 4, 0);
        left.add(territoryLabel, gbc);

        gbc.gridy++;
        territoryField = new JTextField();
        territoryField.setFont(new Font("SansSerif", Font.PLAIN, 9));
        territoryField.setPreferredSize(new Dimension(0, 34));
        territoryField.addActionListener(event -> addTerritoryFromField());
        territoryField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                if (territoryField.getText().equals(TERRITORY_PLACEHOLDER)) {
                    territoryField.setText("");
                    territoryField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent event) {
                saveCurrentTerritory();
                refreshTerritoryField();
            }
        });
        gbc.insets = new Insets(0, 0, 0, 0);
        left.add(territoryField, gbc);

        gbc.gridy++;
        territoryListModel = new DefaultListModel<>();
        territoryList = new JList<>(territoryListModel);
        territoryList.setFont(new Font("SansSerif", Font.PLAIN, 9));
        territoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        territoryList.addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) {
                return;
            }
            String selected = territoryList.getSelectedValue();
            if (selected == null) {
                return;
            }
            territoryField.setText(selected);
            territoryField.setForeground(Color.BLACK);
            saveCurrentTerritory();
        });
        JScrollPane territoryListScroll = new JScrollPane(territoryList);
        territoryListScroll.setPreferredSize(new Dimension(0, 84));
        gbc.insets = new Insets(8, 0, 0, 0);
        left.add(territoryListScroll, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        left.add(Box.createVerticalGlue(), gbc);

        return left;
    }

    private JPanel buildRightPane() {
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);

        detailsArea = new JTextArea();
        detailsArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(145, 151, 175), 1),
                "Important Sensory & Historical Details");
        titledBorder.setTitleFont(new Font("SansSerif", Font.PLAIN, 20));
        scrollPane.setBorder(titledBorder);
        right.add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        bottom.setOpaque(false);

        JButton addNoteButton = new JButton("+ Add Note");
        addNoteButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        addNoteButton.setPreferredSize(new Dimension(140, 42));
        addNoteButton.addActionListener(event -> addCategorizedNote());

        bottom.add(addNoteButton);
        right.add(bottom, BorderLayout.SOUTH);
        return right;
    }

    private void handleLocationTypeChange() {
        Object selected = locationTypeCombo.getSelectedItem();
        if (!(selected instanceof String)) {
            return;
        }
        String selectedType = (String) selected;
        if (selectedType.equals(CUSTOM_OPTION)) {
            promptCustomType();
            return;
        }

        saveCurrentTerritory();
        currentType = selectedType;
        createOrGetLocationType(currentType);
        refreshTerritoryField();
        refreshTerritoryList();
        refreshDetailsArea();
    }

    private void promptCustomType() {
        String customType = JOptionPane.showInputDialog(
                this,
                "Enter custom location type:",
                "Custom Location Type",
                JOptionPane.PLAIN_MESSAGE);
        if (customType == null || customType.trim().isEmpty()) {
            restoreCurrentTypeSelection();
            return;
        }

        String cleaned = customType.trim();
        String existingType = findExistingTypeName(cleaned);
        if (existingType != null) {
            JOptionPane.showMessageDialog(this, "Location type already exists.");
            saveCurrentTerritory();
            currentType = existingType;
            suppressTypeEvents = true;
            locationTypeCombo.setSelectedItem(existingType);
            suppressTypeEvents = false;
            refreshTerritoryField();
            refreshTerritoryList();
            refreshDetailsArea();
            return;
        }

        createOrGetLocationType(cleaned);

        boolean existsInDropdown = false;
        for (int i = 0; i < locationTypeCombo.getItemCount(); i++) {
            if (cleaned.equals(locationTypeCombo.getItemAt(i))) {
                existsInDropdown = true;
                break;
            }
        }
        if (!existsInDropdown) {
            int customIndex = locationTypeCombo.getItemCount() - 1;
            locationTypeCombo.insertItemAt(cleaned, customIndex);
        }

        saveCurrentTerritory();
        currentType = cleaned;
        suppressTypeEvents = true;
        locationTypeCombo.setSelectedItem(cleaned);
        suppressTypeEvents = false;
        refreshTerritoryField();
        refreshTerritoryList();
        refreshDetailsArea();
    }

    private void restoreCurrentTypeSelection() {
        suppressTypeEvents = true;
        if (currentType == null) {
            locationTypeCombo.setSelectedItem(CUSTOM_OPTION);
        } else {
            locationTypeCombo.setSelectedItem(currentType);
        }
        suppressTypeEvents = false;
    }

    private void addCategorizedNote() {
        if (currentType == null) {
            JOptionPane.showMessageDialog(this, "Add location type first.");
            return;
        }

        String[] categories = {"Sensory", "Historical", "Environmental"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        JTextArea noteInput = new JTextArea(4, 24);
        noteInput.setLineWrap(true);
        noteInput.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteInput);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(new JLabel("Category:"), gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(categoryCombo, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(10, 0, 6, 0);
        form.add(new JLabel("Note:"), gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(noteScroll, gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Add Note",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String category = (String) categoryCombo.getSelectedItem();
        String noteText = noteInput.getText();
        if (noteText == null || noteText.trim().isEmpty()) {
            return;
        }

        Location currentLocation = createOrGetLocationType(currentType);
        currentLocation.addImportantDetail(getCategoryPrefix(category) + noteText.trim());
        refreshDetailsArea();
    }

    private String getCategoryPrefix(String category) {
        if (category.equals("Historical")) {
            return "H|";
        }
        if (category.equals("Environmental")) {
            return "E|";
        }
        return "S|";
    }

    private void saveCurrentTerritory() {
        if (territoryField == null || currentType == null) {
            return;
        }
        Location currentLocation = createOrGetLocationType(currentType);
        String value = territoryField.getText().trim();
        if (value.isEmpty() || value.equals(TERRITORY_PLACEHOLDER)) {
            currentLocation.setAssociatedTerritory("");
            return;
        }
        currentLocation.setAssociatedTerritory(value);
    }

    private void addTerritoryFromField() {
        if (currentType == null) {
            JOptionPane.showMessageDialog(this, "Add location type first.");
            return;
        }
        String entered = territoryField.getText().trim();
        if (entered.isEmpty() || entered.equals(TERRITORY_PLACEHOLDER)) {
            return;
        }
        Location currentLocation = createOrGetLocationType(currentType);
        List<String> history = getTerritoryHistory(currentType);
        for (String territory : history) {
            if (territory.equalsIgnoreCase(entered)) {
                JOptionPane.showMessageDialog(this, "Territory already exists for this location type.");
                return;
            }
        }
        history.add(entered);
        currentLocation.setAssociatedTerritory(entered);

        refreshTerritoryList();
        territoryList.setSelectedValue(entered, true);
        refreshTerritoryField();
    }

    private void refreshTerritoryField() {
        if (currentType == null) {
            territoryField.setText(TERRITORY_PLACEHOLDER);
            territoryField.setForeground(new Color(120, 120, 120));
            return;
        }
        Location currentLocation = createOrGetLocationType(currentType);
        String territory = currentLocation.getAssociatedTerritory();
        if (territory == null || territory.isEmpty()) {
            territoryField.setText(TERRITORY_PLACEHOLDER);
            territoryField.setForeground(new Color(120, 120, 120));
            return;
        }
        territoryField.setText(territory);
        territoryField.setForeground(Color.BLACK);
    }

    private void refreshTerritoryList() {
        if (territoryListModel == null) {
            return;
        }
        territoryListModel.clear();
        if (currentType == null) {
            return;
        }
        List<String> history = getTerritoryHistory(currentType);
        for (String territory : history) {
            territoryListModel.addElement(territory);
        }
    }

    private void refreshDetailsArea() {
        if (detailsArea == null) {
            return;
        }

        List<String> sensory = new ArrayList<>();
        List<String> historical = new ArrayList<>();
        List<String> environmental = new ArrayList<>();
        if (currentType != null) {
            Location currentLocation = createOrGetLocationType(currentType);
            sensory = extractNotes(currentLocation, "S|");
            historical = extractNotes(currentLocation, "H|");
            environmental = extractNotes(currentLocation, "E|");
        }

        StringBuilder sb = new StringBuilder();
        appendSection(sb, "Sensory Details", sensory, "[Add sensory detail...]");
        sb.append('\n');
        appendSection(sb, "Historical Notes", historical, "[Add historical note...]");
        sb.append('\n');
        appendSection(sb, "Environmental Details", environmental, "[Add environmental detail...]");
        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }

    private List<String> extractNotes(Location location, String prefix) {
        List<String> notes = new ArrayList<>();
        for (String detail : location.getImportantDetails()) {
            if (detail.startsWith(prefix)) {
                notes.add(detail.substring(prefix.length()));
            }
        }
        return notes;
    }

    private String findExistingTypeName(String candidate) {
        for (String existing : locationTypeData.keySet()) {
            if (existing.equalsIgnoreCase(candidate)) {
                return existing;
            }
        }
        return null;
    }

    private void appendSection(StringBuilder sb, String title, List<String> notes, String placeholder) {
        sb.append("• ").append(title).append('\n');
        if (notes.isEmpty()) {
            sb.append("    ◦ ").append(placeholder).append('\n');
            return;
        }
        for (String note : notes) {
            sb.append("    ◦ ").append(note).append('\n');
        }
    }

    private Location createOrGetLocationType(String typeName) {
        if (typeName == null || typeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Location type cannot be empty.");
        }
        Location existing = locationTypeData.get(typeName);
        if (existing != null) {
            return existing;
        }
        String id = "location-type-" + typeName.toLowerCase().replaceAll("\\s+", "-");
        Location created = new Location(id, typeName, "Location type bucket");
        created.setLocationType(typeName);
        created.setAssociatedTerritory("");
        locationTypeData.put(typeName, created);
        territoryHistoryByType.putIfAbsent(typeName, new ArrayList<>());
        return created;
    }

    private List<String> getTerritoryHistory(String typeName) {
        return territoryHistoryByType.computeIfAbsent(typeName, ignored -> new ArrayList<>());
    }
}
