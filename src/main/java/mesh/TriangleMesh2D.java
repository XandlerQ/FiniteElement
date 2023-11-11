package mesh;

import java.util.ArrayList;

public class TriangleMesh2D {
    ArrayList<FiniteElementTriangle> finiteElements;

    public TriangleMesh2D() {
        this.finiteElements = new ArrayList<FiniteElementTriangle>();
    }

    public void addFiniteElement(FiniteElementTriangle finiteElement) {
        this.finiteElements.add(finiteElement);
    }
}
