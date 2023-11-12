package mesh;

import java.util.ArrayList;

public class TriangleMesh2D {
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    ArrayList<FiniteElementTriangle> finiteElements;

    public TriangleMesh2D() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.finiteElements = new ArrayList<>();
    }

    public void addNode(Node node) {
        if (!this.nodes.contains(node)) this.nodes.add(node);
    }

    public void addEdge(Edge edge) {
        if (!this.edges.contains(edge)) this.edges.add(edge);
    }

    public void addFiniteElement(FiniteElementTriangle finiteElement) {
        this.finiteElements.add(finiteElement);
    }

    public void render() {
        for (Edge edge: this.edges) {
            edge.render();
        }
        for (Node node: this.nodes) {
            node.render();
        }
    }
}
