import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class CharactersWindow extends JFrame {
    private final WorldDatabase worldDatabase;

    public CharactersWindow(WorldDatabase worldDatabase) {
        this.worldDatabase = worldDatabase;
        setTitle("Characters");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        setContentPane(panel);
    }
}
