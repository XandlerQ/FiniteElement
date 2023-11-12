package mesh;

import problem.Environment;
import problem.Shape;

import java.util.ArrayList;

public class MeshGenerator {
    public static TriangleMesh2D generateTriangleMesh2D(Shape shape, Environment environment, double delta) {
        //       __________
        //      |          |
        //      |          |
        //      |          |
        //      |     1    |
        //      |          |
        //      |          |
        //  ____|__________|____
        // |    |          |    |
        // |    |     2    |    |
        // |  3 |          |  4 |
        // |____|__________|____|
        TriangleMesh2D triangleMesh2D = new TriangleMesh2D();
        double d = shape.d;
        double h = shape.h;
        double t = shape.t;

        //       __________
        //      |          |
        //      |          |
        //      |          |
        //      |     1    |
        //      |          |
        //      |          |
        //      |__________|

        int hCount1 = (int)Math.ceil(d / delta);
        int vCount1 = (int)Math.ceil(h / delta);
        double width1 = d / hCount1;
        double height1 = h / vCount1;

        // Nodes for area 1
        ArrayList<Node> nodes1 = new ArrayList<>();

        //0                 1                 3                 4              ...
        //hCount1 + 1       hCount1 + 2      ...
        //2 * (hCount1 + 1) ...

        double nodes1vShift = 0;
        double nodes1hShift = (1 - d) / 2;
        for (int row = 0; row <= vCount1; row++) {
            for (int col = 0; col <= hCount1; col++) {
                double x = nodes1hShift + col * width1;
                double y = nodes1vShift + row * height1;
                nodes1.add(new Node(x, y));
            }
        }

        // Edges for area 1
        ArrayList<Edge> horizontalEdges1 = new ArrayList<>();
        ArrayList<Edge> verticalEdges1 = new ArrayList<>();
        ArrayList<Edge> diagonalEdges1 = new ArrayList<>();
        // FE for area 1
        ArrayList<FiniteElementTriangle> finiteElementTriangles1 = new ArrayList<>();

        // Top horizontal
        //      FOR
        // Left vertical
        // Inner vertical
        // Right vertical
        // Bottom horizontal
        // Diagonal
        //    END FOR

        // Add top horizontal edges
        for (int col = 0; col < hCount1; col++) {
            horizontalEdges1.add(
                    new Edge(
                            nodes1.get(col),
                            nodes1.get(col + 1),
                            0,
                            environment.T,
                            environment.alpha
                    )
            );
        }

        // For every row
        for (int row = 0; row < vCount1; row++) {
            // Add left vertical edge
            verticalEdges1.add(
                    new Edge(
                            nodes1.get(row * (hCount1 + 1)),
                            nodes1.get((row + 1) * (hCount1 + 1)),
                            0,
                            environment.T,
                            environment.alpha
                    )
            );
            // Add vertical edges (inner for area 1)
            for (int col = 1; col < hCount1; col++) {
                verticalEdges1.add(
                        new Edge(
                                nodes1.get(row * (hCount1 + 1) + col),
                                nodes1.get((row + 1) * (hCount1 + 1) + col)
                        )
                );
            }
            // Add right vertical edge
            verticalEdges1.add(
                    new Edge(
                            nodes1.get(row * (hCount1 + 1) + hCount1),
                            nodes1.get((row + 1) * (hCount1 + 1) + hCount1),
                            0,
                            environment.T,
                            environment.alpha
                    )
            );

            // Add bottom edges (inner for area 1)
            for (int col = 0; col < hCount1; col++) {
                horizontalEdges1.add(
                        new Edge(
                                nodes1.get((row + 1) * (hCount1 + 1) + col),
                                nodes1.get((row + 1) * (hCount1 + 1) + col + 1)
                        )
                );
            }

            // Add diagonal edges (inner for every area)
            for (int col = 0; col < hCount1; col++) {
                diagonalEdges1.add(
                        new Edge(
                                nodes1.get(row * (hCount1 + 1) + col),
                                nodes1.get((row + 1) * (hCount1 + 1) + col + 1)
                        )
                );
            }

            // Add all FE
            for (int col = 0; col < hCount1; col++) {
                // Top FE for column
                FiniteElementTriangle top = new FiniteElementTriangle();
                // Right angle node
                top.addNode(nodes1.get(row * (hCount1 + 1) + col + 1));
                // Top left node
                top.addNode(nodes1.get(row * (hCount1 + 1) + col));
                // Bottom right node
                top.addNode(nodes1.get((row + 1) * (hCount1 + 1) + col + 1));

                // Top horizontal leg
                top.addEdge(horizontalEdges1.get(row * hCount1 + col));
                // Hypotenuse
                top.addEdge(diagonalEdges1.get(row * hCount1 + col));
                // Right vertical leg
                top.addEdge(verticalEdges1.get(row * (hCount1 + 1) + col + 1));

                // Bottom FE for column
                FiniteElementTriangle bottom = new FiniteElementTriangle();
                // Right angle node
                bottom.addNode(nodes1.get((row + 1) * (hCount1 + 1) + col));
                // Bottom right node
                bottom.addNode(nodes1.get((row + 1) * (hCount1 + 1) + col + 1));
                // Top left node
                bottom.addNode(nodes1.get(row * (hCount1 + 1) + col));

                // Bottom horizontal leg
                bottom.addEdge(horizontalEdges1.get((row + 1) * hCount1 + col));
                // Hypotenuse
                bottom.addEdge(diagonalEdges1.get(row * hCount1 + col));
                // Left vertical leg
                bottom.addEdge(verticalEdges1.get(row * (hCount1 + 1) + col));

                finiteElementTriangles1.add(top);
                finiteElementTriangles1.add(bottom);
            }
        }

        //       __________
        //      |          |
        //      |     2    |
        //      |          |
        //      |__________|

        int hCount2 = hCount1;
        int vCount2 = (int)Math.ceil(t / delta);
        double width2 = width1;
        double height2 = t / vCount2;

        // Nodes for area 2
        ArrayList<Node> nodes2 = new ArrayList<>();

        //nodes1            nodes1           ...
        //0                 1                ...
        //hCount2 + 1       ...

        double nodes2vShift = h;
        double nodes2hShift = (1 - d) / 2;
        // Copy nodes from nodes1
        for (int col = 0; col <= hCount2; col++) {
            nodes2.add(nodes1.get(vCount1 * (hCount1 + 1) + col));
        }
        for (int row = 1; row <= vCount2; row++) {
            for (int col = 0; col <= hCount2; col++) {
                double x = nodes2hShift + col * width2;
                double y = nodes2vShift + row * height2;
                nodes2.add(new Node(x, y));
            }
        }

        // Edges for area 2
        ArrayList<Edge> horizontalEdges2 = new ArrayList<>();
        ArrayList<Edge> verticalEdges2 = new ArrayList<>();
        ArrayList<Edge> diagonalEdges2 = new ArrayList<>();
        // FE for area 2
        ArrayList<FiniteElementTriangle> finiteElementTriangles2 = new ArrayList<>();

        // Copy top horizontal from edges1
        //      FOR
        // Top horizontal (except for initial row)
        // All vertical
        // Diagonal
        //    END FOR
        // Bottom horizontal

        // Copy top horizontal edges from edges1
        for (int col = 0; col < hCount2; col++) {
            horizontalEdges2.add(
                    horizontalEdges1.get(vCount1 * hCount1 + col)
            );
        }

        for (int row = 0; row < vCount2; row++) {
            // Add top edges (inner for area 1)
            if (row != 0) {
                for (int col = 0; col < hCount2; col++) {
                    horizontalEdges2.add(
                            new Edge(
                                    nodes2.get(row * (hCount2 + 1) + col),
                                    nodes2.get(row * (hCount2 + 1) + col + 1)
                            )
                    );
                }
            }

            // Add all vertical edges
            for (int col = 0; col <= hCount2; col++) {
                verticalEdges2.add(
                        new Edge(
                                nodes2.get(row * (hCount2 + 1) + col),
                                nodes2.get((row + 1) * (hCount2 + 1) + col)
                        )
                );
            }

            // Add all diagonal edges
            for (int col = 0; col < hCount2; col++) {
                diagonalEdges2.add(
                        new Edge(
                                nodes2.get(row * (hCount2 + 1) + col),
                                nodes2.get((row + 1) * (hCount2 + 1) + col + 1)
                        )
                );
            }
        }

        // Add bottom horizontal edges (heated, flux q)
        for (int col = 0; col < hCount2; col++) {
            horizontalEdges2.add(
                    new Edge(
                            nodes2.get(vCount2 * (hCount2 + 1) + col),
                            nodes2.get(vCount2 * (hCount2 + 1) + col + 1),
                            environment.q,
                            0,
                            0
                    )
            );
        }

        // Add all FE
        for (int row = 0; row < vCount2; row++) {
            for (int col = 0; col < hCount2; col++) {
                // Top FE for column
                FiniteElementTriangle top = new FiniteElementTriangle();
                // Right angle node
                top.addNode(nodes2.get(row * (hCount2 + 1) + col + 1));
                // Top left node
                top.addNode(nodes2.get(row * (hCount2 + 1) + col));
                // Bottom right node
                top.addNode(nodes2.get((row + 1) * (hCount2 + 1) + col + 1));

                // Top horizontal leg
                top.addEdge(horizontalEdges2.get(row * hCount2 + col));
                // Hypotenuse
                top.addEdge(diagonalEdges2.get(row * hCount2 + col));
                // Right vertical leg
                top.addEdge(verticalEdges2.get(row * (hCount2 + 1) + col + 1));

                // Bottom FE for column
                FiniteElementTriangle bottom = new FiniteElementTriangle();
                // Right angle node
                bottom.addNode(nodes2.get((row + 1) * (hCount2 + 1) + col));
                // Bottom right node
                bottom.addNode(nodes2.get((row + 1) * (hCount2 + 1) + col + 1));
                // Top left node
                bottom.addNode(nodes2.get(row * (hCount2 + 1) + col));

                // Bottom horizontal leg
                bottom.addEdge(horizontalEdges2.get((row + 1) * hCount2 + col));
                // Hypotenuse
                bottom.addEdge(diagonalEdges2.get(row * hCount2 + col));
                // Left vertical leg
                bottom.addEdge(verticalEdges2.get(row * (hCount2 + 1) + col));

                finiteElementTriangles2.add(top);
                finiteElementTriangles2.add(bottom);
            }
        }

        //  ____
        // |    |
        // |    |
        // |  3 |
        // |____|

        int hCount3 = (int)Math.ceil((1 - d) / (2 * delta));
        int vCount3 = vCount2;
        double width3 = (1 - d) / (2 * hCount3);
        double height3 = height2;

        // Nodes for area 3
        ArrayList<Node> nodes3 = new ArrayList<>();

        //0                 1                ...            nodes2
        //hCount1 + 1       hCount1 + 2      ...            nodes2
        //2 * (hCount1 + 1) ...

        double nodes3vShift = h;
        double nodes3hShift = 0;

        for (int row = 0; row <= vCount3; row++) {
            for (int col = 0; col <= hCount3; col++) {
                // If right side node then copy from nodes2
                if (col == hCount3) {
                    nodes3.add(
                            nodes2.get(row * (hCount2 + 1))
                    );
                }
                else {
                    double x = nodes3hShift + col * width3;
                    double y = nodes3vShift + row * height3;
                    nodes3.add(new Node(x, y));
                }
            }
        }

        // Edges for area 3
        ArrayList<Edge> horizontalEdges3 = new ArrayList<>();
        ArrayList<Edge> verticalEdges3 = new ArrayList<>();
        ArrayList<Edge> diagonalEdges3 = new ArrayList<>();
        // FE for area 3
        ArrayList<FiniteElementTriangle> finiteElementTriangles3 = new ArrayList<>();

        // Top horizontal
        //      FOR
        // Top horizontal except for initial row
        // All vertical except for far right
        // Copy far right from edges2
        // Diagonal
        //    END FOR
        // Bottom horizontal (heated, flux q)

        // Add top horizontal edges
        for (int col = 0; col < hCount3; col++) {
            horizontalEdges3.add(
                    new Edge(
                            nodes3.get(col),
                            nodes3.get(col + 1),
                            0,
                            environment.T,
                            environment.alpha
                    )
            );
        }

        // For every row
        for (int row = 0; row < vCount3; row++) {
            // Add top horizontal edges
            if (row != 0) {
                for (int col = 0; col < hCount3; col++) {
                    horizontalEdges3.add(
                            new Edge(
                                    nodes3.get(row * (hCount3 + 1) + col),
                                    nodes3.get(row * (hCount3 + 1) + col + 1)
                            )
                    );
                }
            }

            // Add all vertical edges except for far right
            for (int col = 0; col < hCount3; col++) {
                verticalEdges3.add(
                        new Edge(
                                nodes3.get(row * (hCount3 + 1) + col),
                                nodes3.get((row + 1) * (hCount3 + 1) + col)
                        )
                );
            }

            // Copy far right vertical edge from edges2
            verticalEdges3.add(
                    verticalEdges2.get(row * (hCount2 + 1))
            );

            // Add all diagonal edges
            for (int col = 0; col < hCount3; col++) {
                diagonalEdges3.add(
                        new Edge(
                                nodes3.get(row * (hCount3 + 1) + col),
                                nodes3.get((row + 1) * (hCount3 + 1) + col + 1)
                        )
                );
            }
        }

        // Add bottom horizontal edges (heated, flux q)
        for (int col = 0; col < hCount3; col++) {
            horizontalEdges3.add(
                    new Edge(
                            nodes3.get(vCount3 * (hCount3 + 1) + col),
                            nodes3.get(vCount3 * (hCount3 + 1) + col + 1),
                            environment.q,
                            0,
                            0
                    )
            );
        }

        // Add all FE
        for (int row = 0; row < vCount3; row++) {
            for (int col = 0; col < hCount3; col++) {
                // Top FE for column
                FiniteElementTriangle top = new FiniteElementTriangle();
                // Right angle node
                top.addNode(nodes3.get(row * (hCount3 + 1) + col + 1));
                // Top left node
                top.addNode(nodes3.get(row * (hCount3 + 1) + col));
                // Bottom right node
                top.addNode(nodes3.get((row + 1) * (hCount3 + 1) + col + 1));

                // Top horizontal leg
                top.addEdge(horizontalEdges3.get(row * hCount3 + col));
                // Hypotenuse
                top.addEdge(diagonalEdges3.get(row * hCount3 + col));
                // Right vertical leg
                top.addEdge(verticalEdges3.get(row * (hCount3 + 1) + col + 1));

                // Bottom FE for column
                FiniteElementTriangle bottom = new FiniteElementTriangle();
                // Right angle node
                bottom.addNode(nodes3.get((row + 1) * (hCount3 + 1) + col));
                // Bottom right node
                bottom.addNode(nodes3.get((row + 1) * (hCount3 + 1) + col + 1));
                // Top left node
                bottom.addNode(nodes3.get(row * (hCount3 + 1) + col));

                // Bottom horizontal leg
                bottom.addEdge(horizontalEdges3.get((row + 1) * hCount3 + col));
                // Hypotenuse
                bottom.addEdge(diagonalEdges3.get(row * hCount3 + col));
                // Left vertical leg
                bottom.addEdge(verticalEdges3.get(row * (hCount3 + 1) + col));

                finiteElementTriangles3.add(top);
                finiteElementTriangles3.add(bottom);
            }
        }

        //                  ____
        //                 |    |
        //                 |    |
        //                 |  4 |
        //                 |____|

        int hCount4 = hCount3;
        int vCount4 = vCount3;
        double width4 = width3;
        double height4 = height3;

        // Nodes for area 3
        ArrayList<Node> nodes4 = new ArrayList<>();

        //nodes2                 1                ...
        //nodes2            hCount1 + 2           ...
        //nodes2                ...

        double nodes4vShift = h;
        double nodes4hShift = (1 - d) / 2 + d;

        for (int row = 0; row <= vCount4; row++) {
            for (int col = 0; col <= hCount4; col++) {
                // If left side node then copy from nodes2
                if (col == 0) {
                    nodes4.add(
                            nodes2.get(row * (hCount2 + 1) + hCount2)
                    );
                }
                else {
                    double x = nodes4hShift + col * width4;
                    double y = nodes4vShift + row * height4;
                    nodes4.add(new Node(x, y));
                }
            }
        }

        // Edges for area 4
        ArrayList<Edge> horizontalEdges4 = new ArrayList<>();
        ArrayList<Edge> verticalEdges4 = new ArrayList<>();
        ArrayList<Edge> diagonalEdges4 = new ArrayList<>();
        // FE for area 4
        ArrayList<FiniteElementTriangle> finiteElementTriangles4 = new ArrayList<>();

        // Top horizontal
        //      FOR
        // Top horizontal except for initial row
        // Copy far left from edges2
        // All vertical except for far left
        // Diagonal
        //    END FOR
        // Bottom horizontal (heated, flux q)

        // Add top horizontal edges
        for (int col = 0; col < hCount4; col++) {
            horizontalEdges4.add(
                    new Edge(
                            nodes4.get(col),
                            nodes4.get(col + 1),
                            0,
                            environment.T,
                            environment.alpha
                    )
            );
        }

        // For every row
        for (int row = 0; row < vCount4; row++) {
            // Add top horizontal edges
            if (row != 0) {
                for (int col = 0; col < hCount4; col++) {
                    horizontalEdges4.add(
                            new Edge(
                                    nodes4.get(row * (hCount4 + 1) + col),
                                    nodes4.get(row * (hCount4 + 1) + col + 1)
                            )
                    );
                }
            }

            // Copy far left vertical edge from edges2
            verticalEdges4.add(
                    verticalEdges2.get(row * (hCount2 + 1) + hCount2)
            );

            // Add all vertical edges except for far left
            for (int col = 1; col <= hCount4; col++) {
                verticalEdges4.add(
                        new Edge(
                                nodes4.get(row * (hCount4 + 1) + col),
                                nodes4.get((row + 1) * (hCount4 + 1) + col)
                        )
                );
            }

            // Add all diagonal edges
            for (int col = 0; col < hCount4; col++) {
                diagonalEdges4.add(
                        new Edge(
                                nodes4.get(row * (hCount4 + 1) + col),
                                nodes4.get((row + 1) * (hCount4 + 1) + col + 1)
                        )
                );
            }
        }

        // Add bottom horizontal edges (heated, flux q)
        for (int col = 0; col < hCount4; col++) {
            horizontalEdges4.add(
                    new Edge(
                            nodes4.get(vCount4 * (hCount4 + 1) + col),
                            nodes4.get(vCount4 * (hCount4 + 1) + col + 1),
                            environment.q,
                            0,
                            0
                    )
            );
        }

        // Add all FE
        for (int row = 0; row < vCount4; row++) {
            for (int col = 0; col < hCount4; col++) {
                // Top FE for column
                FiniteElementTriangle top = new FiniteElementTriangle();
                // Right angle node
                top.addNode(nodes4.get(row * (hCount4 + 1) + col + 1));
                // Top left node
                top.addNode(nodes4.get(row * (hCount4 + 1) + col));
                // Bottom right node
                top.addNode(nodes4.get((row + 1) * (hCount4 + 1) + col + 1));

                // Top horizontal leg
                top.addEdge(horizontalEdges4.get(row * hCount4 + col));
                // Hypotenuse
                top.addEdge(diagonalEdges4.get(row * hCount4 + col));
                // Right vertical leg
                top.addEdge(verticalEdges4.get(row * (hCount4 + 1) + col + 1));

                // Bottom FE for column
                FiniteElementTriangle bottom = new FiniteElementTriangle();
                // Right angle node
                bottom.addNode(nodes4.get((row + 1) * (hCount4 + 1) + col));
                // Bottom right node
                bottom.addNode(nodes4.get((row + 1) * (hCount4 + 1) + col + 1));
                // Top left node
                bottom.addNode(nodes4.get(row * (hCount4 + 1) + col));

                // Bottom horizontal leg
                bottom.addEdge(horizontalEdges4.get((row + 1) * hCount4 + col));
                // Hypotenuse
                bottom.addEdge(diagonalEdges4.get(row * hCount4 + col));
                // Left vertical leg
                bottom.addEdge(verticalEdges4.get(row * (hCount4 + 1) + col));

                finiteElementTriangles4.add(top);
                finiteElementTriangles4.add(bottom);
            }
        }

        for (Node node: nodes1) {
            triangleMesh2D.addNode(node);
        }
        for (Node node: nodes2) {
            triangleMesh2D.addNode(node);
        }
        for (Node node: nodes3) {
            triangleMesh2D.addNode(node);
        }
        for (Node node: nodes4) {
            triangleMesh2D.addNode(node);
        }
        for (Edge edge: horizontalEdges1) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: horizontalEdges2) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: horizontalEdges3) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: horizontalEdges4) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: verticalEdges1) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: verticalEdges2) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: verticalEdges3) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: verticalEdges4) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: diagonalEdges1) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: diagonalEdges2) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: diagonalEdges3) {
            triangleMesh2D.addEdge(edge);
        }
        for (Edge edge: diagonalEdges4) {
            triangleMesh2D.addEdge(edge);
        }
        for (FiniteElementTriangle finiteElementTriangle: finiteElementTriangles1) {
            triangleMesh2D.addFiniteElement(finiteElementTriangle);
        }
        for (FiniteElementTriangle finiteElementTriangle: finiteElementTriangles2) {
            triangleMesh2D.addFiniteElement(finiteElementTriangle);
        }
        for (FiniteElementTriangle finiteElementTriangle: finiteElementTriangles3) {
            triangleMesh2D.addFiniteElement(finiteElementTriangle);
        }
        for (FiniteElementTriangle finiteElementTriangle: finiteElementTriangles4) {
            triangleMesh2D.addFiniteElement(finiteElementTriangle);
        }

        return triangleMesh2D;
    }
}
