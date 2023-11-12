package mesh;

import app.App;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Edge {
    protected AtomicInteger idGenerator = new AtomicInteger(1);
    public int id;
    public Node start;
    public Node end;
    public double flux;
    public double convection;

    public Edge() {
        this.id = idGenerator.getAndIncrement();
        this.start = null;
        this.end = null;
        this.flux = 0;
        this.convection = 0;
    }

    public Edge(Node start, Node end) {
        this();
        this.start = start;
        this.end = end;
    }

    public Edge(Node start, Node end, double flux, double convection) {
        this(start, end);
        this.flux = flux;
        this.convection = convection;
    }

    public double length() {
        return Node.distance(this.start, this.end);
    }

    public void render() {
        App.processingRef.stroke(Color.BLACK.getRGB());
        App.processingRef.line(
                (float)(App.scaleParameter * this.start.x),
                (float)(App.scaleParameter * this.start.y),
                (float)(App.scaleParameter * this.end.x),
                (float)(App.scaleParameter * this.end.y)
        );
        this.start.render();
        this.end.render();
    }
}
