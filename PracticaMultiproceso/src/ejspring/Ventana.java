
package ejspring;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Ventana extends JFrame {

    private JTextField campoExtension;
    private JTextArea areaResultados;

    public Ventana() {

        // Configuracion de la ventana
        setTitle("Mis Ficheros");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Zona donde se muestran los archivos
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);

        add(new JScrollPane(areaResultados), BorderLayout.CENTER);

        // Panel para introducir la extension
        JPanel panel = new JPanel();

        panel.add(new JLabel("Extension del archivo:"));

        campoExtension = new JTextField(12);
        panel.add(campoExtension);

        JButton botonBuscar = new JButton("Buscar");
        panel.add(botonBuscar);

        add(panel, BorderLayout.SOUTH);

        // Accion del boton
        botonBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarArchivos();
            }
        });

        // Doble clic para abrir un ejecutable
        areaResultados.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2) {
                    abrirArchivo(e);
                }
            }
        });
    }

    // Busca los archivos en todas las unidades
    private void buscarArchivos() {

        String extension = campoExtension.getText().trim().toLowerCase();

        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }

        if (!extension.matches("[a-zA-Z0-9]+")) {
            JOptionPane.showMessageDialog(this,
                    "Introduce una extension valida.");
            return;
        }

        areaResultados.setText("");

        File[] unidades = File.listRoots();

        if (unidades == null) {
            JOptionPane.showMessageDialog(this,
                    "No se han podido encontrar las unidades.");
            return;
        }

        for (int i = 0; i < unidades.length; i++) {
            buscarEnCarpeta(unidades[i], extension);
        }

        if (areaResultados.getText().isEmpty()) {
            areaResultados.setText("No se han encontrado archivos.");
        }
    }

    // Recorre las carpetas buscando la extension indicada
    private void buscarEnCarpeta(File carpeta, String extension) {

        File[] archivos = carpeta.listFiles();

        // Puede haber carpetas sin permisos de acceso
        if (archivos == null) {
            return;
        }

        for (int i = 0; i < archivos.length; i++) {

            if (archivos[i].isDirectory()) {

                buscarEnCarpeta(archivos[i], extension);

            } else {

                String nombre = archivos[i].getName().toLowerCase();

                if (nombre.endsWith("." + extension)) {
                    areaResultados.append(
                            archivos[i].getAbsolutePath() + "\n");
                }
            }
        }
    }

    // Abre el archivo cuando se hace doble clic sobre su ruta
    private void abrirArchivo(MouseEvent e) {

        try {

            int posicion = areaResultados.viewToModel2D(e.getPoint());

            int numeroLinea = areaResultados.getLineOfOffset(posicion);

            int inicio = areaResultados.getLineStartOffset(numeroLinea);
            int fin = areaResultados.getLineEndOffset(numeroLinea);

            String ruta = areaResultados.getText(
                    inicio, fin - inicio).trim();

            if (ruta.toLowerCase().endsWith(".exe")) {

                File archivo = new File(ruta);

                if (archivo.isFile()) {
                    ProcessBuilder pb = new ProcessBuilder(ruta);
                    Process proceso = pb.start();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "El archivo seleccionado no existe.");
                }

            } else {

                JOptionPane.showMessageDialog(this,
                        "Solo se pueden abrir archivos .exe");
            }

        } catch (BadLocationException e1) {

            JOptionPane.showMessageDialog(this,
                    "No se ha podido seleccionar la ruta.");

        } catch (IOException e1) {

            JOptionPane.showMessageDialog(this,
                    "No se ha podido abrir el archivo: "
                            + e1.getMessage());
        }
    }
}
