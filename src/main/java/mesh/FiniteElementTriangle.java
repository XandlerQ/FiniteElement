package mesh;

public class FiniteElementTriangle extends FiniteElement {
    public double area() {
        double semiPerimeter = 0;
        double[] edgeLengths = new double[3];
        int edgeNumber = 0;
        for (Edge edge : this.edges) {
            edgeLengths[edgeNumber] = edge.length();
            semiPerimeter += edgeLengths[edgeNumber];
            edgeNumber++;
        }
        edgeNumber = 0;
        double sqArea = semiPerimeter;
        for (Edge edge : this.edges) {
            sqArea *= semiPerimeter - edgeLengths[edgeNumber];
            edgeNumber++;
        }
        return Math.sqrt(sqArea);
    }
}
