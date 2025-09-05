import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit; // Importante: Se usa la libreria especifica para RTF
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Interfaz extends JFrame {

    private JTextPane textPane;
    private JComboBox<String> fuentes;
    private JComboBox<Integer> tamano;
    private File archivoActual = null;

    public Interfaz() {
        setTitle("Editor de Texto");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemAbrir = new JMenuItem("Abrir");
        itemAbrir.addActionListener(e -> abrirArchivo());

        JMenuItem itemGuardar = new JMenuItem("Guardar");
        itemGuardar.addActionListener(e -> guardarArchivo());

        JMenuItem itemGuardarComo = new JMenuItem("Guardar Como...");
        itemGuardarComo.addActionListener(e -> guardarComoArchivo());

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
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.X_AXIS));
        raiz.add(panelSuperior, BorderLayout.NORTH);

        JPanel panelEstilos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEstilos.setOpaque(false);

        panelEstilos.add(new JLabel("Fuente:"));
        String[] nombresFuentes = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fuentes = new JComboBox<>(nombresFuentes);
        fuentes.setSelectedItem("Lucida Calligraphy");
        fuentes.addActionListener(new ManejadorDeEstilos());
        panelEstilos.add(fuentes);

        panelEstilos.add(Box.createHorizontalStrut(10));

        panelEstilos.add(new JLabel("Tamano:"));
        Integer[] tamanosFuente = {8, 10, 12, 14, 18, 24, 36, 48, 64, 72};
        tamano = new JComboBox<>(tamanosFuente);
        tamano.setSelectedItem(48);
        tamano.addActionListener(new ManejadorDeEstilos());
        panelEstilos.add(tamano);
        
        panelSuperior.add(panelEstilos);
        panelSuperior.add(Box.createHorizontalStrut(20));

        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);

        JButton btnNegrita = new JButton(new StyledEditorKit.BoldAction());
        btnNegrita.setText("B");
        btnNegrita.setToolTipText("Negrita");

        JButton btnItalica = new JButton(new StyledEditorKit.ItalicAction());
        btnItalica.setText("I");
        btnItalica.setToolTipText("Italica");

        JButton btnSubrayado = new JButton(new StyledEditorKit.UnderlineAction());
        btnSubrayado.setText("U");
        btnSubrayado.setToolTipText("Subrayado");

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
        toolBar.addSeparator();
        
        panelSuperior.add(toolBar);
        panelSuperior.add(Box.createHorizontalStrut(20));

        JPanel panelColores = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelColores.setOpaque(false);
        panelColores.add(new JLabel("Color:"));
        
        panelColores.add(crearBotonColor(Color.BLACK));
        panelColores.add(crearBotonColor(Color.RED));
        panelColores.add(crearBotonColor(new Color(255, 165, 0)));
        panelColores.add(crearBotonColor(Color.ORANGE));
        panelColores.add(crearBotonColor(Color.YELLOW));
        panelColores.add(crearBotonColor(Color.BLUE));
        panelColores.add(crearBotonColor(Color.GREEN));
        
        panelSuperior.add(panelColores);

        textPane = new JTextPane();
        textPane.setFont(new Font("Lucida Calligraphy", Font.PLAIN, 48));
        textPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textPane);
        raiz.add(scrollPane, BorderLayout.CENTER);
    }

    private JButton crearBotonColor(Color color) {
        JButton botonColor = new JButton();
        botonColor.setPreferredSize(new Dimension(25, 25));
        botonColor.setBackground(color);
        botonColor.addActionListener(e -> {
            SimpleAttributeSet atributos = new SimpleAttributeSet();
            StyleConstants.setForeground(atributos, color);
            textPane.setCharacterAttributes(atributos, false);
        });
        return botonColor;
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

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir Archivo");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivo de Texto con Formato (*.rtf)", "rtf"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            RTFEditorKit kit = new RTFEditorKit();
            try (FileInputStream fis = new FileInputStream(archivo)) {
                textPane.setDocument(kit.createDefaultDocument());
                kit.read(fis, textPane.getDocument(), 0);
                archivoActual = archivo;
                setTitle("Editor de Texto - " + archivo.getName());
            } catch (IOException | BadLocationException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarArchivo() {
        if (archivoActual == null) {
            guardarComoArchivo();
        } else {
            guardarEnArchivo(archivoActual);
        }
    }

    private void guardarComoArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Como...");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivo de Texto con Formato (*.rtf)", "rtf"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            String ruta = archivo.getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".rtf")) {
                archivo = new File(ruta + ".rtf");
            }
            guardarEnArchivo(archivo);
        }
    }

    private void guardarEnArchivo(File archivo) {
        RTFEditorKit kit = new RTFEditorKit();
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            kit.write(fos, textPane.getDocument(), 0, textPane.getDocument().getLength());
            archivoActual = archivo;
            setTitle("Editor de Texto - " + archivo.getName());
        } catch (IOException | BadLocationException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interfaz().setVisible(true));
    }
}