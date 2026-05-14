import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class WorldSystemsWindow extends JFrame {
    private static final String CUSTOM_OPTION = "Custom";
    private static final String[] PRESET_CATEGORIES = {
        "Magic System", "Political System", "Religion", "Technology"
    };

    private final WorldDatabase worldDatabase;
    private JComboBox<String> systemSelector;
    private JComboBox<String> categoryCombo;
    private JTextArea exceptionsArea;
    private JTextArea rulesArea;
    private WorldSystem currentSystem;
    private boolean suppressEvents;

    public WorldSystemsWindow(WorldDatabase worldDatabase) {
        this.worldDatabase = worldDatabase;
        setTitle("World System Configuration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveCurrentSystem();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBackground(new Color(236, 239, 243));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel mainTitle = new JLabel("\u2699  World System Configuration");
        mainTitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        mainPanel.add(mainTitle, BorderLayout.NORTH);

        RoundedPanel configPanel = new RoundedPanel(new BorderLayout(), 10, new Color(208, 212, 229));
        configPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Selection Row (Added to manage multiple systems)
        JPanel selectorRow = new JPanel(new BorderLayout());
        selectorRow.setOpaque(false);
        systemSelector = new JComboBox<>();
        systemSelector.addActionListener(e -> {
            if (suppressEvents) return;
            handleSystemSelection();
        });
        selectorRow.add(new JLabel("Select System to Configure: "), BorderLayout.WEST);
        selectorRow.add(systemSelector, BorderLayout.CENTER);
        configPanel.add(selectorRow, BorderLayout.NORTH);

        JPanel innerContent = new JPanel(new BorderLayout(15, 0));
        innerContent.setOpaque(false);
        innerContent.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Left Panel: Category and Exceptions
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 5, 0);

        gbc.gridy = 0;
        leftPanel.add(makeLabel("System Category:"), gbc);
        
        gbc.gridy = 1;
        categoryCombo = new JComboBox<>();
        for (String cat : PRESET_CATEGORIES) categoryCombo.addItem(cat);
        categoryCombo.addItem(CUSTOM_OPTION);
        categoryCombo.addActionListener(e -> {
            if (suppressEvents) return;
            if (CUSTOM_OPTION.equals(categoryCombo.getSelectedItem())) {
                promptCustomCategory();
            } else {
                saveCurrentSystem();
            }
        });
        leftPanel.add(categoryCombo, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 5, 0);
        leftPanel.add(makeLabel("Exceptions:"), gbc);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        exceptionsArea = new JTextArea();
        exceptionsArea.setLineWrap(true);
        exceptionsArea.setWrapStyleWord(true);
        leftPanel.add(new JScrollPane(exceptionsArea), gbc);

        // Right Panel: Rules and Limitations
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(makeLabel("Rules and Limitations:"), BorderLayout.NORTH);
        rulesArea = new JTextArea();
        rulesArea.setLineWrap(true);
        rulesArea.setWrapStyleWord(true);
        rightPanel.add(new JScrollPane(rulesArea), BorderLayout.CENTER);

        innerContent.add(leftPanel, BorderLayout.WEST);
        leftPanel.setPreferredSize(new Dimension(400, 0));
        innerContent.add(rightPanel, BorderLayout.CENTER);

        configPanel.add(innerContent, BorderLayout.CENTER);
        mainPanel.add(configPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        refreshSystemSelector();
        bindRealTimeSync();
        
        if (systemSelector.getItemCount() > 1) {
            systemSelector.setSelectedIndex(0);
        } else {
            promptNewSystem();
        }
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return label;
    }

    private void bindRealTimeSync() {
        DocumentListener syncListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { saveCurrentSystem(); }
            public void removeUpdate(DocumentEvent e) { saveCurrentSystem(); }
            public void changedUpdate(DocumentEvent e) { saveCurrentSystem(); }
        };
        exceptionsArea.getDocument().addDocumentListener(syncListener);
        rulesArea.getDocument().addDocumentListener(syncListener);
    }

    private void refreshSystemSelector() {
        suppressEvents = true;
        systemSelector.removeAllItems();
        List<StoryElement> systems = worldDatabase.filterByType("WorldSystem");
        for (StoryElement s : systems) {
            systemSelector.addItem(s.getName());
        }
        systemSelector.addItem("[Add New System...]");
        suppressEvents = false;
    }

    private void handleSystemSelection() {
        String selected = (String) systemSelector.getSelectedItem();
        if ("[Add New System...]".equals(selected)) {
            promptNewSystem();
        } else if (selected != null) {
            loadSystemByName(selected);
        }
    }

    private void loadSystemByName(String name) {
        suppressEvents = true;
        List<StoryElement> results = worldDatabase.searchLore(name, true);
        if (!results.isEmpty() && results.get(0) instanceof WorldSystem) {
            currentSystem = (WorldSystem) results.get(0);
            
            // Set category
            String cat = currentSystem.getSystemCategory();
            boolean found = false;
            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                if (categoryCombo.getItemAt(i).equals(cat)) {
                    categoryCombo.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            if (!found && cat != null && !cat.isEmpty()) {
                categoryCombo.insertItemAt(cat, categoryCombo.getItemCount() - 1);
                categoryCombo.setSelectedItem(cat);
            } else if (cat == null || cat.isEmpty()) {
                categoryCombo.setSelectedIndex(0);
            }

            exceptionsArea.setText(currentSystem.getExceptions());
            rulesArea.setText(String.join("\n", currentSystem.getRules()));
        }
        suppressEvents = false;
    }

    private void promptNewSystem() {
        String name = JOptionPane.showInputDialog(this, "Enter name for new World System:");
        if (name == null || name.trim().isEmpty()) {
            if (systemSelector.getItemCount() > 1) {
                systemSelector.setSelectedIndex(0);
            }
            return;
        }
        
        WorldSystem newSys = new WorldSystem("ws-" + System.currentTimeMillis(), name, "World system configuration");
        worldDatabase.addWorldSystem(newSys);
        refreshSystemSelector();
        systemSelector.setSelectedItem(name);
    }

    private void promptCustomCategory() {
        String custom = JOptionPane.showInputDialog(this, "Enter custom category:");
        if (custom != null && !custom.trim().isEmpty()) {
            categoryCombo.insertItemAt(custom, categoryCombo.getItemCount() - 1);
            categoryCombo.setSelectedItem(custom);
            saveCurrentSystem();
        } else {
            categoryCombo.setSelectedIndex(0);
        }
    }

    private void saveCurrentSystem() {
        if (currentSystem == null || suppressEvents) return;
        
        currentSystem.setSystemCategory((String) categoryCombo.getSelectedItem());
        currentSystem.setExceptions(exceptionsArea.getText());
        
        List<String> rulesList = Arrays.stream(rulesArea.getText().split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        currentSystem.getRules().clear();
        for (String r : rulesList) currentSystem.addRule(r);

        // Update StoryElement description for search consistency
        StringBuilder summary = new StringBuilder();
        summary.append("Category: ").append(currentSystem.getSystemCategory()).append("\n");
        summary.append("Rules: ").append(String.join(", ", rulesList)).append("\n");
        if (!currentSystem.getExceptions().isEmpty()) {
            summary.append("Exceptions: ").append(currentSystem.getExceptions());
        }
        currentSystem.setDescription(summary.toString());
    }

    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;

        RoundedPanel(LayoutManager layout, int arc, Color fill) {
            super(layout);
            this.arc = arc;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

