import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ManejadorArchivos {

    private Interfaz interfaz;
    private File archivoActual = null;

    public ManejadorArchivos(Interfaz interfaz) {
        this.interfaz = interfaz;
    }

    public void abrir() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir Archivo");
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Documento Word (*.docx)", "docx"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Texto con Formato (*.rtf)", "rtf"));
        if (fileChooser.showOpenDialog(interfaz) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            String nombreArchivo = archivo.getName().toLowerCase();
            if (nombreArchivo.endsWith(".docx")) {
                abrirDocx(archivo);
            } else if (nombreArchivo.endsWith(".rtf")) {
                abrirRtf(archivo);
            }
        }
    }

    public void guardar() {
        if (archivoActual == null) {
            guardarComo();
        } else {
            guardarRtf(archivoActual);
        }
    }

    public void guardarComo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Como...");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivo de Texto con Formato (*.rtf)", "rtf"));
        if (fileChooser.showSaveDialog(interfaz) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            String ruta = archivo.getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".rtf")) {
                archivo = new File(ruta + ".rtf");
            }
            guardarRtf(archivo);
        }
    }

    private void abrirRtf(File archivo) {
        RTFEditorKit kit = new RTFEditorKit();
        try (FileInputStream fis = new FileInputStream(archivo)) {
            JTextPane textPane = interfaz.getTextPane();
            textPane.setDocument(kit.createDefaultDocument());
            kit.read(fis, textPane.getDocument(), 0);
            archivoActual = archivo;
            interfaz.setTitle("Editor de Texto - " + archivo.getName());
        } catch (IOException | BadLocationException ex) {
            JOptionPane.showMessageDialog(interfaz, "Error al abrir el archivo RTF.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarRtf(File archivo) {
        RTFEditorKit kit = new RTFEditorKit();
        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            JTextPane textPane = interfaz.getTextPane();
            kit.write(fos, textPane.getDocument(), 0, textPane.getDocument().getLength());
            archivoActual = archivo;
            interfaz.setTitle("Editor de Texto - " + archivo.getName());
        } catch (IOException | BadLocationException ex) {
            JOptionPane.showMessageDialog(interfaz, "Error al guardar el archivo RTF.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDocx(File archivo) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archivo))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().equals("word/document.xml")) {
                    interfaz.getTextPane().setText("");
                    
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    
                    LectorXml handler = new LectorXml(interfaz.getTextPane().getStyledDocument());
                    saxParser.parse(zis, handler);
                    
                    this.archivoActual = null;
                    interfaz.setTitle("Editor de Texto - " + archivo.getName() + " (importado)");
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(interfaz, "Error al procesar el archivo DOCX.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class LectorXml extends DefaultHandler {
    private StyledDocument styledDoc;
    private SimpleAttributeSet atributosActuales;
    private StringBuilder textoActual;
    private boolean dentroDeEtiquetaDeTexto;

    public LectorXml(StyledDocument doc) {
        this.styledDoc = doc;
        this.textoActual = new StringBuilder();
        this.atributosActuales = new SimpleAttributeSet();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if ("w:r".equals(qName)) {
            atributosActuales = new SimpleAttributeSet();
        }
        if ("w:t".equals(qName)) {
            textoActual.setLength(0);
            dentroDeEtiquetaDeTexto = true;
        }
        if ("w:b".equals(qName)) StyleConstants.setBold(atributosActuales, true);
        if ("w:i".equals(qName)) StyleConstants.setItalic(atributosActuales, true);
        if ("w:u".equals(qName)) StyleConstants.setUnderline(atributosActuales, true);
        if ("w:color".equals(qName)) {
            String colorHex = attributes.getValue("w:val");
            if (colorHex != null && !colorHex.isEmpty()) {
                StyleConstants.setForeground(atributosActuales, new Color(Integer.parseInt(colorHex, 16)));
            }
        }
        if ("w:sz".equals(qName)) {
            String szVal = attributes.getValue("w:val");
            if (szVal != null && !szVal.isEmpty()) {
                StyleConstants.setFontSize(atributosActuales, Integer.parseInt(szVal) / 2);
            }
        }
        if ("w:rFonts".equals(qName)) {
            String fontName = attributes.getValue("w:ascii");
            if (fontName != null && !fontName.isEmpty()) {
                StyleConstants.setFontFamily(atributosActuales, fontName);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (dentroDeEtiquetaDeTexto) {
            textoActual.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if ("w:t".equals(qName)) {
            try {
                styledDoc.insertString(styledDoc.getLength(), textoActual.toString(), atributosActuales);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            dentroDeEtiquetaDeTexto = false;
        }
        if ("w:p".equals(qName)) {
            try {
                styledDoc.insertString(styledDoc.getLength(), "\n", null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
}