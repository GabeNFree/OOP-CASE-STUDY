import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class CharactersWindow extends JFrame {
    public CharactersWindow() {
        setTitle("Characters");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        setContentPane(panel);
    }
}
