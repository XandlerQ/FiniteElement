package scene;

import mesh.MeshGenerator;
import mesh.TriangleMesh2D;
import problem.Body;
import problem.Environment;

public class Scene {
    private Body body;
    private Environment environment;
    private TriangleMesh2D triangleMesh2D;

    public Scene() {
        this.body = null;
        this.environment = null;
        this.triangleMesh2D = null;
    }

    public Scene(Body body, Environment environment) {
        this();
        this.body = body;
        this.environment = environment;
    }

    public Body getBody() {
        return body;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public TriangleMesh2D getTriangleMesh2D() {
        return triangleMesh2D;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void generateMesh(double delta) {
        this.triangleMesh2D = MeshGenerator.generateTriangleMesh2D(this.body.shape, this.environment, delta);
    }

    public void render() {
        this.triangleMesh2D.render();
    }
}
