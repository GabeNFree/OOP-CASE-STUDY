import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CharactersWindow extends JFrame {
    private static final Color HEADER_BG = new Color(210, 218, 226);
    private static final Color CONTENT_BG = new Color(240, 242, 245);
    private static final Color SECTION_HEADER_BG = new Color(230, 232, 245);
    private static final Color TEXT_COLOR = new Color(46, 49, 51);
    private static final Color BORDER_COLOR = new Color(200, 204, 208);

    public CharactersWindow() {
        setTitle("Character Profile");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 500));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CONTENT_BG);

        // Header
        JPanel header = createHeader();
        mainPanel.add(header, BorderLayout.NORTH);

        // Content
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = createFormPanel();
        contentWrapper.add(formPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(CONTENT_BG);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(getWidth(), 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setBorder(new EmptyBorder(0, 20, 0, 0));

        // Placeholder for icon
        JLabel iconLabel = new JLabel("\uD83D\uDC64"); // User icon
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        iconLabel.setForeground(new Color(100, 110, 200));
        
        JLabel titleLabel = new JLabel("Character Profile");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 15, 0, 0));

        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);

        header.add(leftPanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel outerPanel = new RoundedPanel(8, Color.WHITE);
        outerPanel.setLayout(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // Section Header
        JPanel sectionHeader = new RoundedPanel(8, SECTION_HEADER_BG, true, false);
        sectionHeader.setLayout(new BorderLayout());
        sectionHeader.setPreferredSize(new Dimension(0, 40));
        sectionHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        JLabel sectionTitle = new JLabel("Character Basics");
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        sectionTitle.setForeground(TEXT_COLOR);
        sectionTitle.setBorder(new EmptyBorder(0, 15, 0, 0));
        sectionHeader.add(sectionTitle, BorderLayout.WEST);

        outerPanel.add(sectionHeader, BorderLayout.NORTH);

        // Form Fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        fieldsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 5, 0);

        int row = 0;
        addFormField(fieldsPanel, "Name", new JTextField(), gbc, row++);
        addFormField(fieldsPanel, "Role", new JTextField(), gbc, row++);
        addFormField(fieldsPanel, "Main Goal", new JTextField(), gbc, row++);
        addFormField(fieldsPanel, "Current Goal", new JTextField(), gbc, row++);
        addFormField(fieldsPanel, "Major Flaw(s)", new JTextField(), gbc, row++);
        
        String[] arcTypes = {"Dropdown Character Arc Selection thing", "Hero's Journey", "Tragedy", "Growth", "Corruption"};
        JComboBox<String> arcTypeCombo = new JComboBox<>(arcTypes);
        arcTypeCombo.setBackground(Color.WHITE);
        addFormField(fieldsPanel, "Arc Type", arcTypeCombo, gbc, row++);

        outerPanel.add(fieldsPanel, BorderLayout.CENTER);

        return outerPanel;
    }

    private void addFormField(JPanel panel, String labelText, Component field, GridBagConstraints gbc, int row) {
        gbc.gridy = row * 2;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);

        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        if (field instanceof JTextField) {
            JTextField tf = (JTextField) field;
            tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
            tf.setPreferredSize(new Dimension(0, 35));
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else if (field instanceof JComboBox) {
            field.setPreferredSize(new Dimension(0, 35));
        }
        panel.add(field, gbc);
        gbc.insets = new Insets(0, 0, 5, 0); // reset insets
    }

    private static class RoundedPanel extends JPanel {
        private int arc;
        private Color fill;
        private boolean topRounded = true;
        private boolean bottomRounded = true;

        RoundedPanel(int arc, Color fill) {
            this.arc = arc;
            this.fill = fill;
            setOpaque(false);
        }

        RoundedPanel(int arc, Color fill, boolean top, boolean bottom) {
            this(arc, fill);
            this.topRounded = top;
            this.bottomRounded = bottom;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            
            if (topRounded && bottomRounded) {
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            } else if (topRounded) {
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.fillRect(0, arc, getWidth(), getHeight() - arc);
            } else {
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
