
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author adrianaguilar
 */
public class Interfaz extends JFrame {
   
       public final JComboBox<String> fuentes = new JComboBox<>();
       public final JComboBox<Integer> tamano = new JComboBox<>();
       public final JComboBox <String> Colores = new JComboBox<>(new String[]{"Negro", "Rojo", "Amarillo", "Azul", "Verde", "Gris"});
       public final JPanel panelColores = new JPanel();
       public final JButton btnAceptar = new JButton("Aceptar");
       public final JButton btnCancelar = new JButton("Cancelar");
       
       
      public Interfaz(){
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setSize(960, 620);
       setLocationRelativeTo(null);
       setLayout(new BorderLayout());
       
       JPanel marcoAzul = new JPanel(new BorderLayout(10, 10));
       marcoAzul.setBorder(new MatteBorder(3, 3, 3, 3, new Color(25, 88, 170))); 
       add(marcoAzul, BorderLayout.CENTER);
       
       JPanel raiz = new JPanel(new BorderLayout(10,10));
       raiz.setBackground(new Color(245, 241, 230));
       raiz.setBorder(new EmptyBorder(10,10,10,10));
       marcoAzul.add(raiz, BorderLayout.CENTER);
       
       raiz.add(Box.createVerticalStrut(4), BorderLayout.NORTH);
       
       
       JPanel topControls = new JPanel(new BorderLayout(12, 0));
       topControls.setOpaque(false);
       raiz.add(topControls, BorderLayout.CENTER);
       
       JPanel izquierda = new JPanel();
       izquierda.setOpaque(false);
       izquierda.setLayout(new GridBagLayout());
       GridBagConstraints gc = new GridBagConstraints();
       gc.insets = new Insets(4, 4, 4, 4);
       gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.WEST;
       
        JLabel lFuente = new JLabel("Fuente");
        JLabel lTamaño = new JLabel("Tamaño");
        JLabel lColor  = new JLabel("Color");

        Dimension comboW = new Dimension(260, 28);
        fuentes.setPreferredSize(comboW);
        tamano.setPreferredSize(comboW);
        Colores.setPreferredSize(comboW);



      }
    
    
            
        public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interfaz().setVisible(true));
    }
}
