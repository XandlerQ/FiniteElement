package solver;

import Jama.Matrix;
import mesh.FiniteElementTriangle;
import mesh.Node;
import mesh.TriangleMesh2D;
import scene.Scene;

public class Solver {
    private Scene scene;
    private Matrix Ke;
    private Matrix fe;

    public Solver() {
        this.scene = null;
        this.Ke = null;
        this.fe = null;
    }

    public Solver(Scene scene) {
        this();
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private void fillSystem() {
        if (this.scene == null) return;
        TriangleMesh2D triangleMesh2D = this.scene.getTriangleMesh2D();
        int dimensions = triangleMesh2D.getNodes().size();
        this.Ke = new Matrix(dimensions, dimensions, 0);
        this.fe = new Matrix(dimensions, 1, 0);
        for (FiniteElementTriangle finiteElementTriangle: triangleMesh2D.getFiniteElements()) {
            int[] globalNodeIds = finiteElementTriangle.getNodeIds();
            double[][] localMatrix = finiteElementTriangle.calculateLocalMatrix(this.scene.getBody().material.lambda);
            double[] localVector = finiteElementTriangle.calculateLocalVector();
            for (int i = 0; i < 3; i++) {
                this.fe.set(
                        globalNodeIds[i],
                        0,
                        this.fe.get(globalNodeIds[i], 0) + localVector[i]
                );
                for (int j = 0; j < 3; j++) {
                    this.Ke.set(
                            globalNodeIds[i],
                            globalNodeIds[j],
                            this.Ke.get(globalNodeIds[i], globalNodeIds[j]) + localMatrix[i][j]
                    );
                }
            }
        }
    }

    public void solve() {
        fillSystem();
        Matrix solution = this.Ke.solve(this.fe);
        for (int i = 0; i < this.getScene().getTriangleMesh2D().getNodes().size(); i++) {
            Node node = this.scene.getTriangleMesh2D().getNodes().get(i);
            node.value = solution.get(i, 0);
        }
    }
}
