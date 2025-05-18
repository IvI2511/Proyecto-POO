package model;
import java.awt.*;
import java.util.*;

public class Casa extends Figura {
    private ArrayList<Figura> partes;

    public Casa(model.Point posicion, Color color) {
        super(posicion, color);
        partes = new ArrayList<>();
        int lado = 40;
        int alturaTecho = 30; // Altura del triángulo

        // Cuerpo (cuadrado)
        // El cuadrado está centrado en (posicion.x, posicion.y + lado/2)
        model.Point centroCuadrado = new model.Point(posicion.x, posicion.y + lado / 2);
        partes.add(new Cuadrado(centroCuadrado, color, lado));

        // Techo (triángulo)
        ArrayList<model.Point> techo = new ArrayList<>();
        // Base alineada con la parte superior del cuadrado: y = posicion.y
        techo.add(new model.Point(posicion.x - lado / 2, posicion.y));
        techo.add(new model.Point(posicion.x + lado / 2, posicion.y));
        techo.add(new model.Point(posicion.x, posicion.y - alturaTecho));
        partes.add(new Poligono(color, techo));
    }


    @Override
    public void dibujar(Graphics g) {
        for (Figura f : partes)
            f.dibujar(g);
    }

    @Override
    public boolean contiene(int x, int y) {
        for (Figura f : partes)
            if (f.contiene(x, y)) return true;
        return false;
    }
    
    public void moverA(int nuevoX, int nuevoY, int offsetX, int offsetY) {
        int dx = (nuevoX - offsetX) - posicion.x;
        int dy = (nuevoY - offsetY) - posicion.y;

        posicion.x = nuevoX - offsetX;
        posicion.y = nuevoY - offsetY;

        for (Figura f : partes) {
            if (f instanceof Cuadrado) {
                // Solo mueve la posición
                f.getPosicion().x += dx;
                f.getPosicion().y += dy;
            } else if (f instanceof Poligono) {
                // Solo mueve los vértices, no la posición, porque la posición ya es el primer vértice
                ArrayList<model.Point> verts = ((Poligono) f).getVertices();
                for (model.Point v : verts) {
                    v.x += dx;
                    v.y += dy;
                }
                // No tocar f.getPosicion() (evita el doble desplazamiento)
            }
        }
    }
    
    @Override
    public void setColor(Color nuevoColor) {
        this.color = nuevoColor; // Por si usas el color padre
        for (Figura f : partes) {
            f.setColor(nuevoColor);
        }
    }



}
