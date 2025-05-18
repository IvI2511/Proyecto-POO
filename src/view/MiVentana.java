package view;

import control.Sistema;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.*;


public class MiVentana extends JFrame {
    private Sistema sistema;
    private JPanel lienzo;
    private Figura figuraSeleccionada = null;
    private int offsetX, offsetY;


    public MiVentana(Sistema sistema) {
        this.sistema = sistema;
        setTitle("GeoDraw");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menú
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFiguras = new JMenu("Añadir Figura");
        JMenuItem itemCirculo = new JMenuItem("Círculo");
        JMenuItem itemCuadrado = new JMenuItem("Cuadrado");
        JMenuItem itemPoligono = new JMenuItem("Polígono");
        JMenuItem itemCasa = new JMenuItem("Casa");
        JMenuItem itemOvalo = new JMenuItem("Óvalo");
        menuFiguras.add(itemCirculo);
        menuFiguras.add(itemCuadrado);
        menuFiguras.add(itemPoligono);
        menuFiguras.add(itemCasa);
        menuFiguras.add(itemOvalo);

        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        JMenuItem itemCargar = new JMenuItem("Cargar");
        JMenuItem itemLimpiar = new JMenuItem("Limpiar Lienzo");
        JMenuItem itemEstadisticas = new JMenuItem("Estadísticas");
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemCargar);
        menuArchivo.add(itemLimpiar);
        menuArchivo.add(itemEstadisticas);

        menuBar.add(menuArchivo);
        menuBar.add(menuFiguras);
        setJMenuBar(menuBar);

        // Lienzo
        lienzo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Figura f : sistema.getFiguras()) {
                    f.dibujar(g);
                }
            }
        };
        add(lienzo);
        
        lienzo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                for (int i = sistema.getFiguras().size() - 1; i >= 0; i--) {
                    Figura f = sistema.getFiguras().get(i);
                    if (f.contiene(x, y)) {
                        figuraSeleccionada = f;
                        offsetX = x - f.getPosicion().x;
                        offsetY = y - f.getPosicion().y;
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                figuraSeleccionada = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                // Doble click: elimina figura
                if (e.getClickCount() == 2) {
                    for (int i = sistema.getFiguras().size() - 1; i >= 0; i--) {
                        Figura f = sistema.getFiguras().get(i);
                        if (f.contiene(x, y)) {
                            sistema.getFiguras().remove(i);
                            lienzo.repaint();
                            return;
                        }
                    }
                } else if (e.getClickCount() == 1) { // Un click: cambia color
                    for (int i = sistema.getFiguras().size() - 1; i >= 0; i--) {
                        Figura f = sistema.getFiguras().get(i);
                        if (f.contiene(x, y)) {
                            Color nuevoColor = new Color(
                                (int)(Math.random()*255),
                                (int)(Math.random()*255),
                                (int)(Math.random()*255)
                            );
                            f.setColor(nuevoColor);
                            lienzo.repaint();
                            break;
                        }
                    }
                }
            }
        });
        
        lienzo.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (figuraSeleccionada != null) {
                    if (figuraSeleccionada instanceof Poligono) {
                        ((Poligono) figuraSeleccionada).moverA(e.getX(), e.getY(), offsetX, offsetY);
                    } else if (figuraSeleccionada instanceof Casa) {
                        ((Casa) figuraSeleccionada).moverA(e.getX(), e.getY(), offsetX, offsetY);
                    } else {
                        figuraSeleccionada.getPosicion().x = e.getX() - offsetX;
                        figuraSeleccionada.getPosicion().y = e.getY() - offsetY;
                    }
                    lienzo.repaint();
                }
            }
        });






        // Listeners
        itemCirculo.addActionListener(e -> agregarCirculo());
        itemCuadrado.addActionListener(e -> agregarCuadrado());
        itemPoligono.addActionListener(e -> agregarPoligono());
        itemCasa.addActionListener(e -> agregarCasa());
        itemOvalo.addActionListener(e -> agregarOvalo());
        itemEstadisticas.addActionListener(e -> mostrarEstadisticas());
        itemLimpiar.addActionListener(e -> {
            sistema.clearFiguras();
            lienzo.repaint();
        });
        itemGuardar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar figuras");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos GeoDraw (*.geo)", "geo");
            fileChooser.setFileFilter(filter);
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                // Añadir la extensión .geo si no está
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".geo")) {
                    filePath += ".geo";
                }
                try {
                    sistema.guardar(filePath);
                    JOptionPane.showMessageDialog(this, "Figuras guardadas en:\n" + filePath, "Guardar", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        itemCargar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Cargar figuras");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos GeoDraw (*.geo)", "geo");
            fileChooser.setFileFilter(filter);
            int userSelection = fileChooser.showOpenDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToOpen = fileChooser.getSelectedFile();
                try {
                    sistema.cargar(fileToOpen.getAbsolutePath());
                    lienzo.repaint();
                    JOptionPane.showMessageDialog(this, "Figuras cargadas de:\n" + fileToOpen.getName(), "Cargar", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });}
    

    private void agregarCirculo() {
        Random rand = new Random();
        int x = rand.nextInt(lienzo.getWidth() - 100) + 50;
        int y = rand.nextInt(lienzo.getHeight() - 100) + 50;
        int radio = rand.nextInt(30) + 20;
        Color color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        sistema.addFigura(new Circulo(new model.Point(x, y), color, radio));
        lienzo.repaint();
    }

    private void agregarCuadrado() {
        Random rand = new Random();
        int x = rand.nextInt(lienzo.getWidth() - 100) + 50;
        int y = rand.nextInt(lienzo.getHeight() - 100) + 50;
        int lado = rand.nextInt(40) + 20;
        Color color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        sistema.addFigura(new Cuadrado(new model.Point(x, y), color, lado));
        lienzo.repaint();
    }

    private void agregarPoligono() {
        Random rand = new Random();
        int numVertices = rand.nextInt(3) + 3; // 3 a 5 vértices
        ArrayList<model.Point> vertices = new ArrayList<>();
        int x0 = rand.nextInt(lienzo.getWidth() - 100) + 50;
        int y0 = rand.nextInt(lienzo.getHeight() - 100) + 50;
        int radio = rand.nextInt(30) + 30;
        double angulo = 2 * Math.PI / numVertices;
        for (int i = 0; i < numVertices; i++) {
            int x = (int) (x0 + radio * Math.cos(i * angulo));
            int y = (int) (y0 + radio * Math.sin(i * angulo));
            vertices.add(new model.Point(x, y));
        }
        Color color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        sistema.addFigura(new Poligono(color, vertices));
        lienzo.repaint();
    }
    
    private void agregarCasa() {
        Random rand = new Random();
        int x = rand.nextInt(lienzo.getWidth() - 100) + 50;
        int y = rand.nextInt(lienzo.getHeight() - 100) + 50;
        Color color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        sistema.addFigura(new Casa(new model.Point(x, y), color));
        lienzo.repaint();
    }
    
    private void agregarOvalo() {
        Random rand = new Random();
        int x = rand.nextInt(lienzo.getWidth() - 100) + 50;
        int y = rand.nextInt(lienzo.getHeight() - 100) + 50;
        int ancho = rand.nextInt(40) + 30;
        int alto = rand.nextInt(40) + 30;
        Color color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        sistema.addFigura(new Ovalo(new model.Point(x, y), color, ancho, alto));
        lienzo.repaint();
    }
    
    private void mostrarEstadisticas() {
        int total = sistema.getFiguras().size();
        java.util.Map<String, Integer> conteo = new java.util.HashMap<>();
        for (Figura f : sistema.getFiguras()) {
            String tipo = f.getClass().getSimpleName();
            conteo.put(tipo, conteo.getOrDefault(tipo, 0) + 1);
        }
        StringBuilder detalles = new StringBuilder();
        detalles.append("Total figuras: ").append(total).append("\n");
        if (conteo.isEmpty()) {
            detalles.append("No hay figuras.");
        } else {
            String masUsado = "";
            int max = 0;
            for (java.util.Map.Entry<String, Integer> entry : conteo.entrySet()) {
                detalles.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                if (entry.getValue() > max) {
                    masUsado = entry.getKey();
                    max = entry.getValue();
                }
            }
            detalles.append("Tipo más usado: ").append(masUsado);
        }
        JOptionPane.showMessageDialog(this, detalles.toString(), "Estadísticas de figuras", JOptionPane.INFORMATION_MESSAGE);
    }



}
