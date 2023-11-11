package mesh;

import java.util.concurrent.atomic.AtomicInteger;

public class Edge {
    protected AtomicInteger idGenerator = new AtomicInteger(1);
    public int id;
    public Node start;
    public Node end;
    public double flux;

    public Edge() {
        this.id = idGenerator.getAndIncrement();
        this.start = null;
        this.end = null;
        this.flux = 0;
    }

    public Edge(Node start, Node end) {
        this();
        this.start = start;
        this.end = end;
    }

    public Edge(Node start, Node end, double flux) {
        this(start, end);
        this.flux = flux;
    }

    public double length() {
        return Node.distance(this.start, this.end);
    }
}
