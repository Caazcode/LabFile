import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Interfaz extends JFrame {

    private JTextPane textPane;
    private JComboBox<String> fuentes;
    private JComboBox<Integer> tamano;
    private ManejadorArchivos manejadorArchivos;

    public Interfaz() {
        manejadorArchivos = new ManejadorArchivos(this);
        
        setTitle("Editor de Texto");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemAbrir = new JMenuItem("Abrir");
        itemAbrir.addActionListener(e -> manejadorArchivos.abrir());
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        itemGuardar.addActionListener(e -> manejadorArchivos.guardar());
        JMenuItem itemGuardarComo = new JMenuItem("Guardar Como...");
        itemGuardarComo.addActionListener(e -> manejadorArchivos.guardarComo());
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarComo);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);

        JPanel marcoAzul = new JPanel(new BorderLayout(10, 10));
        marcoAzul.setBorder(new MatteBorder(3, 3, 3, 3, new Color(25, 88, 170)));
        add(marcoAzul, BorderLayout.CENTER);
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(new Color(245, 241, 230));
        raiz.setBorder(new EmptyBorder(10, 10, 10, 10));
        marcoAzul.add(raiz, BorderLayout.CENTER);
        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        raiz.add(panelSuperior, BorderLayout.NORTH);
        panelSuperior.add(new JLabel("Fuente:"));
        String[] nombresFuentes = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fuentes = new JComboBox<>(nombresFuentes);
        fuentes.setSelectedItem("Lucida Calligraphy");
        fuentes.addActionListener(new ManejadorDeEstilos());
        panelSuperior.add(fuentes);
        panelSuperior.add(new JLabel("Tamano:"));
        Integer[] tamanosFuente = {8, 10, 12, 14, 18, 24, 36, 48, 64, 72};
        tamano = new JComboBox<>(tamanosFuente);
        tamano.setSelectedItem(48);
        tamano.addActionListener(new ManejadorDeEstilos());
        panelSuperior.add(tamano);
        
        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        JButton btnNegrita = new JButton(new StyledEditorKit.BoldAction());
        btnNegrita.setText("B");
        JButton btnItalica = new JButton(new StyledEditorKit.ItalicAction());
        btnItalica.setText("I");
        JButton btnSubrayado = new JButton(new StyledEditorKit.UnderlineAction());
        btnSubrayado.setText("U");
        toolBar.add(btnNegrita);
        toolBar.add(btnItalica);
        toolBar.add(btnSubrayado);
        toolBar.addSeparator();

        JButton btnAlignIzquierda = new JButton(new StyledEditorKit.AlignmentAction("Izquierda", StyleConstants.ALIGN_LEFT));
        btnAlignIzquierda.setText("Izda");
        toolBar.add(btnAlignIzquierda);
        JButton btnAlignCenter = new JButton(new StyledEditorKit.AlignmentAction("Centro", StyleConstants.ALIGN_CENTER));
        btnAlignCenter.setText("Centro");
        toolBar.add(btnAlignCenter);
        JButton btnAlignDerecha = new JButton(new StyledEditorKit.AlignmentAction("Derecha", StyleConstants.ALIGN_RIGHT));
        btnAlignDerecha.setText("Dcha");
        toolBar.add(btnAlignDerecha);
        
        panelSuperior.add(toolBar);
        panelSuperior.add(new JLabel("Color:"));
        JButton btnPaletaColores = new JButton("Elegir Color...");
        btnPaletaColores.addActionListener(e -> elegirColor());
        panelSuperior.add(btnPaletaColores);

        textPane = new JTextPane();
        textPane.setFont(new Font("Lucida Calligraphy", Font.PLAIN, 48));
        textPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textPane);
        raiz.add(scrollPane, BorderLayout.CENTER);
    }
    
    public JTextPane getTextPane() {
        return textPane;
    }
    
    private void elegirColor() {
        Color nuevoColor = JColorChooser.showDialog(this, "Selecciona un Color", textPane.getForeground());
        if (nuevoColor != null) {
            SimpleAttributeSet atributos = new SimpleAttributeSet();
            StyleConstants.setForeground(atributos, nuevoColor);
            textPane.setCharacterAttributes(atributos, false);
        }
    }

    private class ManejadorDeEstilos implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String familiaFuente = (String) fuentes.getSelectedItem();
            Integer tamanoFuente = (Integer) tamano.getSelectedItem();
            Action accionFuente = new StyledEditorKit.FontFamilyAction("fuente", familiaFuente);
            Action accionTamano = new StyledEditorKit.FontSizeAction("tamano", tamanoFuente);
            accionFuente.actionPerformed(new ActionEvent(textPane, ActionEvent.ACTION_PERFORMED, null));
            accionTamano.actionPerformed(new ActionEvent(textPane, ActionEvent.ACTION_PERFORMED, null));
        }
    }
}     