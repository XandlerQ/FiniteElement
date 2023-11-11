package problem;

public class Body {
    public Material material;
    public Shape shape;

    public Material getMaterial() {
        return material;
    }

    public Shape getShape() {
        return shape;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
}
