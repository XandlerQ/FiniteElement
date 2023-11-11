package mesh;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FiniteElement {
    protected AtomicInteger idGenerator = new AtomicInteger(1);
    public int id;
    public ArrayList<Node> nodes;
    public ArrayList<Edge> edges;

    public FiniteElement() {
        this.id = idGenerator.getAndIncrement();
        this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }
}
