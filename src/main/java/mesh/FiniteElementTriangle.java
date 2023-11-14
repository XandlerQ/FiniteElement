package mesh;

import app.App;

import java.awt.*;

public class FiniteElementTriangle extends FiniteElement {

    private static double factorial2 = 2;
    private static double factorial3 = 6;
    private static double factorial4 = 24;

    public static double triangleArea(Node node1, Node node2, double x, double y) {
        double semiPerimeter = 0;
        double[] edgeLengths = new double[3];
        edgeLengths[0] = Node.distance(node1, node2);
        edgeLengths[1] = node1.distanceTo(x, y);
        edgeLengths[2] = node2.distanceTo(x, y);
        for (int i = 0; i < 3; i++) {
            semiPerimeter += edgeLengths[i] / 2;
        }
        double sqArea = semiPerimeter;
        for (int i = 0; i < 3; i++) {
            sqArea *= semiPerimeter - edgeLengths[i];
        }
        return Math.sqrt(sqArea);
    }

    public double area() {
        double semiPerimeter = 0;
        double[] edgeLengths = new double[3];
        int edgeNumber = 0;
        for (Edge edge : this.edges) {
            edgeLengths[edgeNumber] = edge.length();
            semiPerimeter += edgeLengths[edgeNumber] / 2;
            edgeNumber++;
        }
        double sqArea = semiPerimeter;
        for (int i = 0; i < 3; i++) {
            sqArea *= semiPerimeter - edgeLengths[i];
        }
        return Math.sqrt(sqArea);
    }

    public boolean contains(double x, double y) {
        double area = area();
        double s1 = triangleArea(this.nodes.get(0), this.nodes.get(1), x, y);
        double s2 = triangleArea(this.nodes.get(0), this.nodes.get(2), x, y);
        double s3 = triangleArea(this.nodes.get(1), this.nodes.get(2), x, y);
        double sumArea = s1 + s2 + s3;
        return sumArea <= area + area / 200 && sumArea >= area - area / 200;
    }

    public double evaluateAt(double x, double y) {
        double area = area();
        double[] A = calculateAVector();
        double[] B = calculateBVector();
        double[] C = calculateCVector();
        double phi0 = this.nodes.get(0).value;
        double phi1 = this.nodes.get(1).value;
        double phi2 = this.nodes.get(2).value;
        double alpha0 = (A[0] * phi0 + A[1] * phi1 + A[2] * phi2) / (2 * area);
        double alpha1 = (B[0] * phi0 + B[1] * phi1 + B[2] * phi2) / (2 * area);
        double alpha2 = (C[0] * phi0 + C[1] * phi1 + C[2] * phi2) / (2 * area);
        return  -(alpha0 + alpha1 * x + alpha2 * y);
    }

    public int[] getNodeIds() {
        int[] nodeIds = new int[3];
        nodeIds[0] = this.nodes.get(0).id;
        nodeIds[1] = this.nodes.get(1).id;
        nodeIds[2] = this.nodes.get(2).id;
        return  nodeIds;
    }

    public double getAverageValue() {
        return (this.nodes.get(0).value + this.nodes.get(1).value + this.nodes.get(2).value) / 3;
    }

    public double getValueAtLocalCoordinates(double[] coordinates) {
        return getValueAtLocalCoordinates(coordinates[0], coordinates[1], coordinates[2]);
    }

    public double getValueAtLocalCoordinates(double c1, double c2, double c3) {
        return this.nodes.get(0).value * c1 + this.nodes.get(1).value * c2 + this.nodes.get(2).value * c3;
    }

    public double[] calculateAVector() {
        double x1 = this.nodes.get(0).x;
        double x2 = this.nodes.get(1).x;
        double x3 = this.nodes.get(2).x;
        double y1 = this.nodes.get(0).y;
        double y2 = this.nodes.get(1).y;
        double y3 = this.nodes.get(2).y;
        double[] A = new double[3];
        A[0] = x2 * y3 - x3 * y2;
        A[1] = x3 * y1 - x1 * y3;
        A[2] = x1 * y2 - x2 * y1;
        return A;
    }

    public double[] calculateBVector() {
        double y1 = this.nodes.get(0).y;
        double y2 = this.nodes.get(1).y;
        double y3 = this.nodes.get(2).y;
        double[] B = new double[3];
        B[0] = y2 - y3;
        B[1] = y3 - y1;
        B[2] = y1 - y2;
        return B;
    }

    public double[] calculateCVector() {
        double x1 = this.nodes.get(0).x;
        double x2 = this.nodes.get(1).x;
        double x3 = this.nodes.get(2).x;
        double[] C = new double[3];
        C[0] = x3 - x2;
        C[1] = x1 - x3;
        C[2] = x2 - x1;
        return C;
    }

    public double[][] calculateLocalMatrix(double lambda) {
        double S = area();
        double[] bVector = calculateBVector();
        double[] cVector = calculateCVector();
        double[][] localMatrix = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                localMatrix[i][j] += lambda * (bVector[i] * bVector[j] + cVector[i] * cVector[j]) / (4 * S);
            }
        }
        for (int i = 0; i < 3; i++) {
            Edge edge = this.edges.get(i);
            if (edge.convectionAlpha == 0) continue;
            double edgeLength = edge.length();
            int localId1 = this.nodes.indexOf(edge.start);
            int localId2 = this.nodes.indexOf(edge.end);
            double convectionValue = edge.convectionAlpha * edgeLength / factorial3;
            localMatrix[localId1][localId1] += 2 * convectionValue;
            localMatrix[localId1][localId2] += convectionValue;
            localMatrix[localId2][localId1] += convectionValue;
            localMatrix[localId2][localId2] += 2 * convectionValue;
        }
        return localMatrix;
    }

    public double[] calculateLocalVector() {
        double[] localVector = new double[3];
        for (int i = 0; i < 3; i++) {
            Edge edge = this.edges.get(i);
            if (edge.flux != 0) {
                double edgeLength = edge.length();
                int localId1 = this.nodes.indexOf(edge.start);
                int localId2 = this.nodes.indexOf(edge.end);
                double fluxValue = edge.flux * edgeLength / factorial2;
                localVector[localId1] += fluxValue;
                localVector[localId2] += fluxValue;
            }
            if (edge.convectionAlpha != 0) {
                double edgeLength = edge.length();
                int localId1 = this.nodes.indexOf(edge.start);
                int localId2 = this.nodes.indexOf(edge.end);
                double convectionValue = edge.convectionAlpha * edge.convectionT * edgeLength / factorial2;
                localVector[localId1] += convectionValue;
                localVector[localId2] += convectionValue;
            }
        }
        return localVector;
    }

    public void render(Color color) {
        App.processingRef.stroke(color.getRGB());
        App.processingRef.fill(color.getRGB());
        App.processingRef.triangle(
                App.shiftX + (float)(App.scaleParameter * this.nodes.get(0).x),
                App.shiftY + (float)(App.scaleParameter * this.nodes.get(0).y),
                App.shiftX + (float)(App.scaleParameter * this.nodes.get(1).x),
                App.shiftY + (float)(App.scaleParameter * this.nodes.get(1).y),
                App.shiftX + (float)(App.scaleParameter * this.nodes.get(2).x),
                App.shiftY + (float)(App.scaleParameter * this.nodes.get(2).y)
        );
    }
}
