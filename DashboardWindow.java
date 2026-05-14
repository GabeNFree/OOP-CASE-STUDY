import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class DashboardWindow extends JFrame {
    private WorldDatabase database;

    public DashboardWindow() {
        this.database = new WorldDatabase();
        this.database.loadData();

        setTitle("World Database Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel frameBg = new JPanel(new BorderLayout());
        frameBg.setBackground(new Color(216, 221, 224));
        frameBg.setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 16));

        RoundedPanel card = new RoundedPanel(new BorderLayout(), 8, new Color(237, 238, 239));
        card.setBorder(BorderFactory.createEmptyBorder(10, 16, 14, 16));

        JLabel title = new JLabel("WORLD DATABASE DASHBOARD", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.PLAIN, 20));
        title.setForeground(new Color(46, 49, 51));
        card.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(Box.createRigidArea(new Dimension(0, 18)));
        JPanel searchRow = buildSearchRow();
        searchRow.setAlignmentX(CENTER_ALIGNMENT);
        center.add(searchRow);
        center.add(Box.createRigidArea(new Dimension(0, 18)));
        JPanel tilesGrid = buildTilesGrid();
        tilesGrid.setAlignmentX(CENTER_ALIGNMENT);
        center.add(tilesGrid);

        card.add(center, BorderLayout.CENTER);
        frameBg.add(card, BorderLayout.CENTER);
        setContentPane(frameBg);
    }

    private JPanel buildSearchRow() {
        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setOpaque(false);

        HintTextField queryField = new HintTextField("Query Lore Database...");
        queryField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        queryField.setBackground(new Color(148, 154, 159));
        queryField.setForeground(new Color(246, 248, 250));
        queryField.setCaretColor(Color.WHITE);
        queryField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 8));

        JButton searchButton = new JButton("\uD83D\uDD0D");
        searchButton.setFocusPainted(false);
        searchButton.setMargin(new Insets(0, 0, 0, 0));
        searchButton.setBackground(new Color(172, 177, 181));
        searchButton.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(123, 128, 132)));
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 22));
        searchButton.setPreferredSize(new Dimension(56, 52));

        row.add(queryField, BorderLayout.CENTER);
        row.add(searchButton, BorderLayout.EAST);
        row.setBorder(BorderFactory.createLineBorder(new Color(123, 128, 132), 1, true));
        Dimension compactSize = new Dimension(520, 52);
        row.setPreferredSize(compactSize);
        row.setMaximumSize(compactSize);
        row.setMinimumSize(compactSize);
        return row;
    }

    private JPanel buildTilesGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setOpaque(false);

        DashboardTileButton charactersButton = new DashboardTileButton(
                "\uD83D\uDC65  CHARACTERS",
                new Color(75, 122, 166),
                new Color(56, 95, 132));
        charactersButton.addActionListener(e -> {
            CharactersWindow win = new CharactersWindow(database, null);
            win.setVisible(true);
        });

        DashboardTileButton locationsButton = new DashboardTileButton(
                "\u25CC  LOCATIONS",
                new Color(103, 138, 128),
                new Color(84, 118, 109));
        DashboardTileButton systemsButton = new DashboardTileButton(
                "\u2699  WORLD SYSTEMS",
                new Color(105, 141, 157),
                new Color(84, 115, 130));
        DashboardTileButton chapterButton = new DashboardTileButton(
                "\u2261  CHAPTER OUTLINER",
                new Color(107, 139, 102),
                new Color(85, 118, 81));

        wireNavigation(locationsButton, LocationsWindow::new);
        wireNavigation(systemsButton, WorldSystemsWindow::new);
        wireNavigation(chapterButton, ChapterOutlinerWindow::new);

        grid.add(charactersButton);
        grid.add(locationsButton);
        grid.add(systemsButton);
        grid.add(chapterButton);
        Dimension gridSize = new Dimension(620, 240);
        grid.setPreferredSize(gridSize);
        grid.setMaximumSize(gridSize);
        grid.setMinimumSize(gridSize);

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.add(Box.createHorizontalGlue());
        wrapper.add(grid);
        wrapper.add(Box.createHorizontalGlue());
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, gridSize.height));
        return wrapper;
    }

    private void wireNavigation(JButton button, Supplier<JFrame> destinationFactory) {
        button.addActionListener(event -> {
            JFrame destination = destinationFactory.get();
            destination.setVisible(true);
        });
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

    private static class HintTextField extends JTextField {
        private final String hint;

        HintTextField(String hint) {
            this.hint = hint;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!getText().isEmpty() || isFocusOwner()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(224, 228, 232));
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            Insets insets = getInsets();
            int x = insets.left + 2;
            int availableHeight = getHeight() - insets.top - insets.bottom;
            int y = insets.top + (availableHeight - fm.getHeight()) / 2 + fm.getAscent();
            int minBaseline = insets.top + fm.getAscent();
            int maxBaseline = getHeight() - insets.bottom - fm.getDescent();
            y = Math.max(minBaseline, Math.min(y, maxBaseline));
            g2.drawString(hint, x, y);
            g2.dispose();
        }
    }

    private static class DashboardTileButton extends JButton {
        private final Color start;
        private final Color end;

        DashboardTileButton(String text, Color start, Color end) {
            super(text);
            this.start = start;
            this.end = end;
            setFont(new Font("SansSerif", Font.BOLD, 18));
            setForeground(new Color(245, 247, 248));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 10));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gradient = new GradientPaint(0, 0, start, getWidth(), getHeight(), end);
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
