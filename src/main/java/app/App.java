package app;

import mesh.MeshGenerator;
import mesh.TriangleMesh2D;
import problem.Problem;
import problem.ProblemLoader;
import processing.core.PApplet;

public class App extends PApplet {
    public static PApplet processingRef;
    TriangleMesh2D triangleMesh2D;
    public static double scaleParameter = 500;

    public void settings() {
        App.processingRef = this;
        size(1000, 1000);
    }

    public void setup() {
        background(255);
        Problem problem = ProblemLoader.loadProblem();
        this.triangleMesh2D = MeshGenerator.generateTriangleMesh2D(problem.body.shape, problem.environment, 0.05);
    }

    public void draw() {
        this.triangleMesh2D.render();
    }

    public static void main(String[] args) {
        PApplet.main("app.App");
    }
}
