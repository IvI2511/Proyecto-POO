package model;
import java.awt.*;
import java.util.ArrayList;

public class Poligono extends Figura {
    private ArrayList<Point> vertices;

    public Poligono(Color color, ArrayList<Point> vertices) {
        super(vertices.get(0), color);
        this.vertices = vertices;
    }

    @Override
    public void dibujar(Graphics g) {
        g.setColor(color);
        int n = vertices.size();
        int[] xs = new int[n];
        int[] ys = new int[n];
        for (int i = 0; i < n; i++) {
            xs[i] = vertices.get(i).x;
            ys[i] = vertices.get(i).y;
        }
        g.fillPolygon(xs, ys, n);
    }
    
    @Override
    public boolean contiene(int x, int y) {
        int crossings = 0;
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            Point v1 = vertices.get(i);
            Point v2 = vertices.get((i + 1) % n);
            if (((v1.y > y) != (v2.y > y))) {
                double atX = (double)(v2.x - v1.x) * (y - v1.y) / (double)(v2.y - v1.y) + v1.x;
                if (x < atX)
                    crossings++;
            }
        }
        return (crossings % 2 == 1);
    }
    
    public void moverA(int nuevoX, int nuevoY, int offsetX, int offsetY) {
        // Calcula el desplazamiento respecto al primer vértice (la "posición")
        int dx = (nuevoX - offsetX) - posicion.x;
        int dy = (nuevoY - offsetY) - posicion.y;

        for (Point v : vertices) {
            v.x += dx;
            v.y += dy;
        }
        posicion.x = nuevoX - offsetX;
        posicion.y = nuevoY - offsetY;
    }

    public ArrayList<model.Point> getVertices() {
        return vertices;
    }


}
