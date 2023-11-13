package app;

import mesh.MeshGenerator;
import mesh.TriangleMesh2D;
import problem.Problem;
import problem.ProblemLoader;
import processing.core.PApplet;
import scene.Scene;
import solver.Solver;

public class App extends PApplet {
    public static PApplet processingRef;
    public static double scaleParameter = 1000;
    public static float shiftX = 20;
    public static float shiftY = 20;

    private Scene scene;
    private Solver solver;
    private double delta = 0.05;

    public void settings() {
        App.processingRef = this;
        size(1500, 1500);
    }

    public void setup() {
        background(255);
        Problem problem = ProblemLoader.loadProblem();
        this.scene = new Scene(problem.body, problem.environment);
        this.scene.generateMesh(this.delta);
        System.out.println("Nodes: " + this.scene.getTriangleMesh2D().getNodes().size());
        System.out.println("Edges: " + this.scene.getTriangleMesh2D().getEdges().size());
        System.out.println("FE: " + this.scene.getTriangleMesh2D().getFiniteElements().size());
        this.solver = new Solver(this.scene);
        this.solver.solve();
        this.scene.render();
    }

    public void draw() {

    }

    public static void main(String[] args) {
        PApplet.main("app.App");
    }
}
