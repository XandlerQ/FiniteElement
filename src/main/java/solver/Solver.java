package solver;

import Jama.Matrix;
import mesh.FiniteElementTriangle;
import mesh.Node;
import mesh.TriangleMesh2D;
import scene.Scene;

public class Solver {
    private Scene scene;
    private Matrix K;
    private Matrix f;

    public Solver() {
        this.scene = null;
        this.K = null;
        this.f = null;
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
        this.K = new Matrix(dimensions, dimensions, 0);
        this.f = new Matrix(dimensions, 1, 0);
        for (FiniteElementTriangle finiteElementTriangle: triangleMesh2D.getFiniteElements()) {
            int[] globalNodeIds = finiteElementTriangle.getNodeIds();
            double[][] localMatrix = finiteElementTriangle.calculateLocalMatrix(this.scene.getBody().material.lambda);
            double[] localVector = finiteElementTriangle.calculateLocalVector();
            for (int i = 0; i < 3; i++) {
                this.f.set(
                        globalNodeIds[i],
                        0,
                        this.f.get(globalNodeIds[i], 0) + localVector[i]
                );
                for (int j = 0; j < 3; j++) {
                    this.K.set(
                            globalNodeIds[i],
                            globalNodeIds[j],
                            this.K.get(globalNodeIds[i], globalNodeIds[j]) + localMatrix[i][j]
                    );
                }
            }
        }
    }

    public void solve() {
        fillSystem();
        Matrix solution = this.K.solve(this.f);
        for (int i = 0; i < this.getScene().getTriangleMesh2D().getNodes().size(); i++) {
            Node node = this.scene.getTriangleMesh2D().getNodes().get(i);
            node.value = solution.get(i, 0);
        }
    }
}
