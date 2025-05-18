package model;
import java.awt.*;

public class Ovalo extends Figura {
    private int ancho, alto;

    public Ovalo(model.Point posicion, Color color, int ancho, int alto) {
        super(posicion, color);
        this.ancho = ancho;
        this.alto = alto;
    }

    @Override
    public void dibujar(Graphics g) {
        g.setColor(color);
        g.fillOval(posicion.x - ancho/2, posicion.y - alto/2, ancho, alto);
    }

    @Override
    public boolean contiene(int x, int y) {
        double dx = (double)(x - posicion.x) / (ancho / 2);
        double dy = (double)(y - posicion.y) / (alto / 2);
        return dx*dx + dy*dy <= 1;
    }
}
