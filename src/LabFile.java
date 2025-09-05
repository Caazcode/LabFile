/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author saidn
 */
import javax.swing.SwingUtilities;

public class LabFile {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Interfaz editor = new Interfaz();
            editor.setVisible(true);
        });
    }
}
