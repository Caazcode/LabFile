
import javax.swing.SwingUtilities;


public class LabFile {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Interfaz editor = new Interfaz();
            editor.setVisible(true);
        });
    }
}