package graph;

import org.junit.Test;
import static org.junit.Assert.*;

/** Tests for traversals.
 *  @author Brian Truong.
 */

public class TraversalTesting {

    public class Omega<VLabel, ELabel>
        extends Traversal<VLabel, ELabel> {

        /** Previsit Extension for graph E and vertex V0. */
        protected void preVisit(Graph<VLabel, ELabel>.Edge e,
                                Graph<VLabel, ELabel>.Vertex v0) {
            System.out.println("preVisit");
        }

        /** Visit Extension for vertex V. */
        protected void visit(Graph<VLabel, ELabel>.Vertex v) {
            System.out.println("visit");
        }

        /** PostVisit extension for vertex V. */
        protected void postVisit(Graph<VLabel, ELabel>.Vertex v) {
            System.out.println("postvisit");
        }
    }

    /** Runs a depthfirst traversal on a graph. */
    @Test
    public void depthFirstTraverse() {
        DirectedGraph<String, String> g
            = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("input1");
        Graph<String, String>.Vertex v2 = g.add("input2");
        Graph<String, String>.Vertex v3 = g.add("input3");
        Graph<String, String>.Edge e1 = g.add(v3, v2);
        Graph<String, String>.Edge e2 = g.add(v1, v3);
        Graph<String, String>.Edge e3 = g.add(v3, v1);
        Omega<String, String> client
            = new Omega<String, String>();
        System.out.println("Begin depthfirst traversal.");
        client.depthFirstTraverse(g, v3);
    }

    /** Runs a breadthfirst traversal on a graph. */
    @Test
    public void breadthFirstTraverse() {
        DirectedGraph<String, String> g
            = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("input1");
        Graph<String, String>.Vertex v2 = g.add("input2");
        Graph<String, String>.Vertex v3 = g.add("input3");
        Graph<String, String>.Edge e1 = g.add(v3, v2);
        Graph<String, String>.Edge e2 = g.add(v1, v3);
        Graph<String, String>.Edge e3 = g.add(v3, v1);
        Omega<String, String> client
            = new Omega<String, String>();
        System.out.println("Begin breadthfirst traversal.");
        client.breadthFirstTraverse(g, v3);
    }
}
