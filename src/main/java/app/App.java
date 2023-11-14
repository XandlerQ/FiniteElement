package app;

import mesh.MeshGenerator;
import mesh.TriangleMesh2D;
import problem.Problem;
import problem.ProblemLoader;
import processing.core.PApplet;
import processing.core.PFont;
import scene.Scene;
import solver.Solver;

import java.awt.*;

public class App extends PApplet {
    public static PApplet processingRef;
    public static double scaleParameter = 1000;
    public static float shiftX = 20;
    public static float shiftY = 20;

    private Scene scene;
    private Solver solver;
    private double delta = 0.1;

    private int savedMouseX;
    private int savedMouseY;
    private boolean savedMouseCoordinates = false;

    public void settings() {
        App.processingRef = this;
        size(1500, 1500);
    }

    public void setup() {
        PFont font = createFont("PressStart2P-Regular.ttf", 8);
        textFont(font);
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
        if (this.savedMouseCoordinates) {
            this.savedMouseCoordinates = false;
            background(255);
            this.scene.render();
            float value = (float)this.scene.getTriangleMesh2D().evaluateAt((mouseX - shiftX) / scaleParameter, (mouseY - shiftY) / scaleParameter);
            if (Float.isNaN(value)) return;
            Color pickColor = new Color(0, 176, 70);
            App.processingRef.stroke(pickColor.getRGB());
            App.processingRef.fill(pickColor.getRGB());
            App.processingRef.circle(this.savedMouseX, this.savedMouseY, 5);
            App.processingRef.textSize(12);
            App.processingRef.fill(pickColor.getRGB());
            App.processingRef.text(
                    value,
                    this.savedMouseX + 5,
                    this.savedMouseY
            );
        }
    }

    public void mouseClicked() {
//        System.out.println(this.scene.getTriangleMesh2D().evaluateAt((mouseX - shiftX) / scaleParameter, (mouseY - shiftY) / scaleParameter));
        this.savedMouseX = mouseX;
        this.savedMouseY = mouseY;
        this.savedMouseCoordinates = true;
    }

    public static void main(String[] args) {
        PApplet.main("app.App");
    }
}
