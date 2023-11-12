package mesh;

import app.App;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    private static final AtomicInteger idGenertor = new AtomicInteger(1);
    public int id;
    public double x;
    public double y;
    public boolean boundary;
    public double value;

    public Node() {
        this.id = idGenertor.getAndIncrement();
        this.x = 0;
        this.y = 0;
        this.boundary = false;
        this.value = 0;
    }

    public Node(double x, double y) {
        this();
        this.x = x;
        this.y = y;
        this.boundary = false;
    }

    public Node(double x, double y, boolean boundary) {
        this(x, y);
        this.boundary = boundary;
    }

    public static double distance(Node node1, Node node2) {
        return Math.sqrt((node1.x - node2.x) * (node1.x - node2.x) + (node1.y - node2.y) * (node1.y - node2.y));
    }

    public void render() {
        App.processingRef.stroke(Color.BLACK.getRGB());
        app.App.processingRef.circle((float)(App.scaleParameter * this.x), (float)(App.scaleParameter * this.y), 3);
    }
}
