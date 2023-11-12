package mesh;

public class FiniteElementTriangle extends FiniteElement {

    private static double factorial2 = 2;
    private static double factorial3 = 6;
    private static double factorial4 = 24;

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

    int[] getNodeIds() {
        int[] nodeIds = new int[3];
        nodeIds[0] = this.nodes.get(0).id;
        nodeIds[1] = this.nodes.get(1).id;
        nodeIds[2] = this.nodes.get(2).id;
        return  nodeIds;
    }

    double[] calculateAVector() {
        double x1 = this.nodes.get(1).x;
        double x2 = this.nodes.get(2).x;
        double x3 = this.nodes.get(3).x;
        double y1 = this.nodes.get(1).y;
        double y2 = this.nodes.get(2).y;
        double y3 = this.nodes.get(3).y;
        double[] A = new double[3];
        A[0] = x2 * y3 - x3 * y2;
        A[1] = x3 * y1 - x1 - y3;
        A[2] = x1 * y2 - x2 * y1;
        return A;
    }

    double[] calculateBVector() {
        double y1 = this.nodes.get(1).y;
        double y2 = this.nodes.get(2).y;
        double y3 = this.nodes.get(3).y;
        double[] B = new double[3];
        B[0] = y2 - y3;
        B[1] = y3 - y1;
        B[2] = y1 - y2;
        return B;
    }

    double[] calculateCVector() {
        double x1 = this.nodes.get(1).x;
        double x2 = this.nodes.get(2).x;
        double x3 = this.nodes.get(3).x;
        double[] C = new double[3];
        C[0] = x3 - x2;
        C[1] = x1 - x3;
        C[2] = x2 - x1;
        return C;
    }

    double[][] calculateLocalMatrix(double lambda) {
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

    double[] calculateLocalVector() {
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
}
