package mesh;

import java.awt.*;
import java.util.ArrayList;

public class TriangleMesh2D {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private ArrayList<FiniteElementTriangle> finiteElements;

    public TriangleMesh2D() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.finiteElements = new ArrayList<>();
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<FiniteElementTriangle> getFiniteElements() {
        return finiteElements;
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

    public double evaluateAt(double x, double y) {
        for (FiniteElementTriangle finiteElementTriangle: this.finiteElements) {
            if (finiteElementTriangle.contains(x, y)) {
                return finiteElementTriangle.evaluateAt(x, y);
            }
        }
        return Double.NaN;
    }

    public void render() {
        double maxValue = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;
        for (Node node: this.nodes) {
            if (node.value > maxValue) {
                maxValue = node.value;
            }
            if (node.value < minValue) {
                minValue = node.value;
            }
        }
        double difference = maxValue - minValue;
        Color colorHot = new Color(255, 249, 130);
        Color colorWarm = new Color(252, 7, 3);
        Color colorCold = new Color(3, 53, 252);
        for (FiniteElementTriangle finiteElementTriangle: this.finiteElements) {
            double value = finiteElementTriangle.getAverageValue();
            Color color;
            if (value < minValue + difference / 2) {
                double fraction = (value - minValue) / (difference / 2);
                color = new Color(
                    (int)(fraction * colorWarm.getRed() + (1 - fraction) * colorCold.getRed()),
                    (int)(fraction * colorWarm.getGreen() + (1 - fraction) * colorCold.getGreen()),
                    (int)(fraction * colorWarm.getBlue() + (1 - fraction) * colorCold.getBlue())
                );
            }
            else {
                double fraction = (value - minValue - difference / 2) / (difference / 2);
                color = new Color(
                        (int)(fraction * colorHot.getRed() + (1 - fraction) * colorWarm.getRed()),
                        (int)(fraction * colorHot.getGreen() + (1 - fraction) * colorWarm.getGreen()),
                        (int)(fraction * colorHot.getBlue() + (1 - fraction) * colorWarm.getBlue())
                );
            }
            finiteElementTriangle.render(color);
        }
        for (Edge edge: this.edges) {
            edge.render();
        }
        for (Node node: this.nodes) {
            node.render();
        }
    }
}
