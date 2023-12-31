package mesh;

import app.App;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    private static final AtomicInteger idGenertor = new AtomicInteger(0);
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

    public double distanceTo(Node node) {
        return distance(this, node);
    }

    public double distanceTo(double x, double y) {
        return Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
    }

    public void render() {
        App.processingRef.stroke(Color.BLACK.getRGB());
        App.processingRef.circle(App.shiftX + (float)(App.scaleParameter * this.x), App.shiftY + (float)(App.scaleParameter * this.y), 3);
        App.processingRef.textSize(8);
        App.processingRef.fill(Color.BLACK.getRGB());
        App.processingRef.text((float)this.value, App.shiftX + (float)(App.scaleParameter * this.x), App.shiftY + (float)(App.scaleParameter * this.y));
    }
}
