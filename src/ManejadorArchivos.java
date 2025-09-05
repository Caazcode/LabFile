
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document xmlDoc = dBuilder.parse(zis);
                    xmlDoc.getDocumentElement().normalize();
                    interfaz.getTextPane().setText("");
                    leerXml(xmlDoc);
                    this.archivoActual = archivo;
                    interfaz.setTitle("Editor de Texto - " + archivo.getName());
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(interfaz, "Error al procesar el archivo DOCX.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void leerXml(Document xmlDoc) {
        StyledDocument styledDoc = interfaz.getTextPane().getStyledDocument();
        NodeList parrafos = xmlDoc.getElementsByTagName("w:p");
        try {
            for (int i = 0; i < parrafos.getLength(); i++) {
                NodeList runs = ((Element) parrafos.item(i)).getElementsByTagName("w:r");
                for (int j = 0; j < runs.getLength(); j++) {
                    Element runElement = (Element) runs.item(j);
                    String texto = "";
                    NodeList textNodes = runElement.getElementsByTagName("w:t");
                    if (textNodes.getLength() > 0 && textNodes.item(0) != null) {
                        texto = textNodes.item(0).getTextContent();
                    }
                    SimpleAttributeSet atributos = new SimpleAttributeSet();
                    NodeList props = runElement.getElementsByTagName("w:rPr");
                    if (props.getLength() > 0) {
                        Element propsElement = (Element) props.item(0);
                        if (propsElement.getElementsByTagName("w:b").getLength() > 0) StyleConstants.setBold(atributos, true);
                        if (propsElement.getElementsByTagName("w:i").getLength() > 0) StyleConstants.setItalic(atributos, true);
                        if (propsElement.getElementsByTagName("w:u").getLength() > 0) StyleConstants.setUnderline(atributos, true);
                        
                        NodeList colorNodes = propsElement.getElementsByTagName("w:color");
                        if (colorNodes.getLength() > 0) {
                            String colorHex = ((Element) colorNodes.item(0)).getAttribute("w:val");
                            if (colorHex != null && !colorHex.isEmpty()) {
                                StyleConstants.setForeground(atributos, new Color(Integer.parseInt(colorHex, 16)));
                            }
                        }
                        NodeList szNodes = propsElement.getElementsByTagName("w:sz");
                        if (szNodes.getLength() > 0) {
                            String szVal = ((Element) szNodes.item(0)).getAttribute("w:val");
                            if (szVal != null && !szVal.isEmpty()) {
                                StyleConstants.setFontSize(atributos, Integer.parseInt(szVal) / 2);
                            }
                        }
                        NodeList fontNodes = propsElement.getElementsByTagName("w:rFonts");
                        if (fontNodes.getLength() > 0) {
                            String fontName = ((Element) fontNodes.item(0)).getAttribute("w:ascii");
                            if (fontName != null && !fontName.isEmpty()) {
                                StyleConstants.setFontFamily(atributos, fontName);
                            }
                        }
                    }
                    styledDoc.insertString(styledDoc.getLength(), texto, atributos);
                }
                styledDoc.insertString(styledDoc.getLength(), "\n", null);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}