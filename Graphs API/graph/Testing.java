package graph;

import org.junit.Test;
import ucb.junit.textui;
import static org.junit.Assert.*;

import java.util.ArrayList;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your graph package per se (that is, it must be
 * possible to remove them and still have your package work). */

/** Unit tests for the graph package.
 *  @author Brian Truong.
 */
public class Testing {

    /** Run all JUnit tests in the graph package. */
    public static void main(String[] ignored) {
        textui.runClasses(graph.Testing.class);
        System.exit(textui.runClasses(graph.TraversalTesting.class));
    }

    @Test
    public void emptyGraph() {
        DirectedGraph g = new DirectedGraph();
        assertEquals("Initial graph has vertices", 0, g.vertexSize());
        assertEquals("Initial graph has edges", 0, g.edgeSize());
    }

    @Test
    public void vertexSizeTest1() {
        DirectedGraph<String, String> g1 = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g1.add("foo");
        Graph<String, String>.Vertex v2 = g1.add("hello");
        Graph<String, String>.Vertex v3 = g1.add("world");
        Graph<String, String>.Vertex v4 = g1.add("test");
        g1.remove(v4);
        assertEquals(3, g1.vertexSize());
        Graph<String, String>.Vertex v5 = g1.add("bar");
        Graph<String, String>.Vertex v6 = g1.add("test");
        assertEquals(5, g1.vertexSize());
        Graph<String, String>.Edge e1 = g1.add(v1, v3);
        Graph<String, String>.Edge e2 = g1.add(v6, v1);
        Graph<String, String>.Edge e3 = g1.add(v3, v4);
        Graph<String, String>.Edge e4 = g1.add(v1, v4);
        Graph<String, String>.Edge e5 = g1.add(v5, v1);
        assertEquals("Wrong vertex size.", g1.vertexSize(), 5);
        g1.remove(v5);
        assertEquals("Wrong vertex size after removal.", g1.vertexSize(), 4);
    }

    @Test
    public void vertexSizeTest2() {
        DirectedGraph<String, String> g1 = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g1.add("foo1");
        Graph<String, String>.Vertex v2 = g1.add("foo2");
        Graph<String, String>.Vertex v3 = g1.add("foo3");
        Graph<String, String>.Vertex v4 = g1.add("foo4");
        Graph<String, String>.Vertex v5 = g1.add("foo5");
        g1.remove(v5);
        g1.remove(v1);
        assertEquals("Wrong vertex size after 2 removals.", g1.vertexSize(), 3);
        g1.remove(v3);
        assertEquals("Wrong vertex size after removals.", g1.vertexSize(), 2);
    }

    @Test
    public void vertexRemovalTest() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("L1");
        Graph<String, String>.Vertex v2 = g.add("L2");
        Graph<String, String>.Vertex v3 = g.add("L3");
        Graph<String, String>.Edge e1 = g.add(v1, v2);
        assertEquals(true, g.contains(v1, v2));
        Graph<String, String>.Edge e2 = g.add(v2, v3);
        g.remove(v1);
        assertEquals("Wrong vertex count.", g.vertexSize(), 2);
    }

    @Test
    public void edgeRemovalTest1() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("box");
        Graph<String, String>.Vertex v2 = g.add("triangle");
        Graph<String, String>.Vertex v3 = g.add("square");
        Graph<String, String>.Vertex v4 = g.add("rectangle");
        Graph<String, String>.Vertex v5 = g.add("trapezoid");
        Graph<String, String>.Edge e1 = g.add(v1, v2);
        Graph<String, String>.Edge e2 = g.add(v2, v3);
        Graph<String, String>.Edge e3 = g.add(v3, v4);
        Graph<String, String>.Edge e4 = g.add(v4, v5);
        assertEquals("Wrong initial edge count.", g.edgeSize(), 4);
        g.remove(e2);
        assertEquals(false, g.contains(v2, v3));
        assertEquals("Wrong edge count.", g.edgeSize(), 3);
    }

    @Test
    public void edgeRemovalVertex1() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("play");
        Graph<String, String>.Vertex v2 = g.add("played");
        Graph<String, String>.Vertex v3 = g.add("payed");
        Graph<String, String>.Vertex v4 = g.add("pay");
        Graph<String, String>.Vertex v5 = g.add("pen");
        Graph<String, String>.Edge e1 = g.add(v1, v2);
        Graph<String, String>.Edge e2 = g.add(v2, v3);
        Graph<String, String>.Edge e3 = g.add(v3, v4);
        Graph<String, String>.Edge e4 = g.add(v4, v5);
        g.remove(v1, v2);
        assertEquals("Wrong edge count.", g.edgeSize(), 3);
        assertEquals("Edge was removed.", g.contains(v1, v2), false);
    }

    @Test
    public void edgeRemovalVertex2() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("X1");
        Graph<String, String>.Vertex v2 = g.add("X2");
        Graph<String, String>.Vertex v3 = g.add("X3");
        Graph<String, String>.Vertex v4 = g.add("X4");
        Graph<String, String>.Vertex v5 = g.add("X5");
        Graph<String, String>.Edge e1 = g.add(v1, v2);
        Graph<String, String>.Edge e2 = g.add(v2, v3);
        Graph<String, String>.Edge e3 = g.add(v3, v4);
        Graph<String, String>.Edge e4 = g.add(v4, v5);
        g.remove(v3);
        assertEquals("Wrong edge count.", g.edgeSize(), 2);
        assertEquals("Edge was removed.", g.contains(v2, v3), false);
        assertEquals("Edge was removed.", g.contains(v3, v4), false);
        assertEquals("Edge was never removed.", g.contains(v1, v2), true);
    }

    @Test

    public void invalidAddEdge() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        String l1 = "x1";
        String l2 = "x2";
        Graph<String, String>.Vertex v1 = g.add(l1);
        Graph<String, String>.Vertex v2 = g.add(l2);
        Graph<String, String>.Edge e = g.add(v1, v2);
        assertEquals(true, g.contains(v1, v2));
        g.remove(e);
        assertEquals(false, g.contains(v1, v2));
        Graph<String, String>.Edge e2 = g.add(v1, v2, "fresh");
        DirectedGraph<String, String> g2 = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex newvertex = g2.add("new");
        Graph<String, String>.Edge e3 = g.add(v1, newvertex);
        assertEquals(null, e3);
    }

    @Test
    public void outDegreeTestDirected() {
        DirectedGraph<String, String> g1 = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g1.add("pants");
        Graph<String, String>.Vertex v2 = g1.add("hoodie");
        Graph<String, String>.Vertex v3 = g1.add("sweater");
        Graph<String, String>.Vertex v4 = g1.add("hat");
        Graph<String, String>.Vertex v5 = g1.add("sock");
        Graph<String, String>.Edge e1 = g1.add(v1, v3);
        Graph<String, String>.Edge e2 = g1.add(v2, v1);
        Graph<String, String>.Edge e3 = g1.add(v3, v4);
        Graph<String, String>.Edge e4 = g1.add(v1, v4);
        Graph<String, String>.Edge e5 = g1.add(v5, v1);
        assertEquals("Wrong out degree.", g1.outDegree(v1), 2);
        g1.remove(v1);
        assertEquals("Out edge was removed", g1.contains(v2, v1), false);
    }

    @Test
    public void outDegreeTestUndirected() {
        UndirectedGraph<String, String> g1
            = new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g1.add("shoe");
        Graph<String, String>.Vertex v2 = g1.add("sandal");
        Graph<String, String>.Vertex v3 = g1.add("flip");
        Graph<String, String>.Vertex v4 = g1.add("flop");
        Graph<String, String>.Vertex v5 = g1.add("beach");
        Graph<String, String>.Edge e1 = g1.add(v1, v3);
        Graph<String, String>.Edge e2 = g1.add(v2, v1);
        Graph<String, String>.Edge e3 = g1.add(v3, v4);
        Graph<String, String>.Edge e4 = g1.add(v1, v4);
        Graph<String, String>.Edge e5 = g1.add(v5, v1);
        assertEquals("Wrong out degree.", g1.outDegree(v1), 4);
        g1.remove(v1);
        assertEquals("Out edge was removed", g1.contains(v2, v1), false);
    }

    @Test
    public void inDegreeTestDirected() {
        DirectedGraph<String, String> g1 = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g1.add("dog");
        Graph<String, String>.Vertex v2 = g1.add("cat");
        Graph<String, String>.Vertex v3 = g1.add("guinea");
        Graph<String, String>.Vertex v4 = g1.add("pig");
        Graph<String, String>.Vertex v5 = g1.add("cow");
        Graph<String, String>.Edge e1 = g1.add(v1, v3);
        Graph<String, String>.Edge e2 = g1.add(v2, v1);
        Graph<String, String>.Edge e3 = g1.add(v3, v4);
        Graph<String, String>.Edge e4 = g1.add(v1, v4);
        Graph<String, String>.Edge e5 = g1.add(v5, v1);
        assertEquals("Wrong in degree v1.", g1.inDegree(v1), 2);
        assertEquals("Wrong in degree v4.", g1.inDegree(v4), 2);
        g1.remove(v1);
        assertEquals("In edge to v1 was removed", g1.contains(v5, v1), false);
    }

    @Test
    public void inDegreeTestUndirected() {
        UndirectedGraph<String, String> g1
            = new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g1.add("pepsi");
        Graph<String, String>.Vertex v2 = g1.add("coke");
        Graph<String, String>.Vertex v3 = g1.add("7up");
        Graph<String, String>.Vertex v4 = g1.add("sprite");
        Graph<String, String>.Vertex v5 = g1.add("pepper");
        Graph<String, String>.Edge e1 = g1.add(v1, v3);
        Graph<String, String>.Edge e2 = g1.add(v2, v1);
        Graph<String, String>.Edge e3 = g1.add(v3, v4);
        Graph<String, String>.Edge e4 = g1.add(v1, v4);
        Graph<String, String>.Edge e5 = g1.add(v5, v1);
        assertEquals("Wrong in degree v1.", g1.inDegree(v1), 4);
        assertEquals("Wrong in degree v3.", g1.inDegree(v3), 2);
        g1.remove(v1);
        assertEquals("In edge v2 was removed", g1.contains(v2, v1), false);
    }

    public void invalidRemoveVertex() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        String l1 = "x1";
        String l2 = "x2";
        Graph<String, String>.Vertex v1 = g.add(l1);
        Graph<String, String>.Vertex v2 = g.add(l2);
        Graph<String, String>.Edge e = g.add(v1, v2);
        g.remove(e);
        assertEquals(false, g.contains(v1, v2));
        DirectedGraph<String, String> g2 = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex newvertex = g2.add("inside");
        g.remove(newvertex);
        assertEquals("No vertices were removed from graph g",
            g.vertexSize(), 2);
        g.remove(v1);
        assertEquals("single vertex removed", g.vertexSize(), 1);
    }

    /** EWeighter to test A*. */
    public static final Weighting<Integer> EWEIGHTER =
        new Weighting<Integer>() {
            @Override
            public double weight(Integer x) {
                double result = (double) x;
                return result;
            }
        };

    /** VWeighter to test A*. */
    public static final Weighter<Integer> VWEIGHTER =
        new Weighter<Integer>() {

            @Override
            public double weight(Integer x) {
                double result = (double) x;
                return result;
            }

            @Override
            public void setWeight(Integer x, double v) {
            }
        };

    @Test
    @SuppressWarnings("unchecked")
    public void testAStarRun() {
        DirectedGraph<Integer, Integer> g
            = new DirectedGraph<Integer, Integer>();
        Graph<Integer, Integer>.Vertex v1 = g.add(10);
        Graph<Integer, Integer>.Vertex v2 = g.add(5);
        Graph<Integer, Integer>.Vertex v3 = g.add(4);
        Graph<Integer, Integer>.Vertex v4 = g.add(88);
        Graph<Integer, Integer>.Vertex v5 = g.add(8);
        Graph<Integer, Integer>.Edge e0 = g.add(v4, v1, 1);
        Graph<Integer, Integer>.Edge e1 = g.add(v2, v5, 4);
        Graph<Integer, Integer>.Edge e2 = g.add(v1, v4, 2);
        Graph<Integer, Integer>.Edge e3 = g.add(v2, v3, 2);
        Graph<Integer, Integer>.Edge e4 = g.add(v2, v1, 5);
        Graph<Integer, Integer>.Edge e5 = g.add(v3, v5, 12);
        Graph<Integer, Integer>.Edge e6 = g.add(v1, v5, 3);
        Graphs.shortestPath(g, v1, v5, Graphs.ZERO_DISTANCER,
            VWEIGHTER, EWEIGHTER);
    }

    @Test
    public void containsTest1() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("weight");
        Graph<String, String>.Vertex v2 = g.add("wait");
        Graph<String, String>.Vertex v3 = g.add("weigh");
        Graph<String, String>.Edge e1 = g.add(v1, v2);
        Graph<String, String>.Edge e2 = g.add(v2, v3);
        g.remove(v3);
        assertEquals("Edge is contained.", g.contains(v1, v2), true);
    }

    @Test
    public void containsTest2() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("cloud");
        Graph<String, String>.Vertex v2 = g.add("sunshine");
        Graph<String, String>.Vertex v3 = g.add("rain");
        Graph<String, String>.Edge e1 = g.add(v1, v2);
        Graph<String, String>.Edge e2 = g.add(v2, v3);
        g.remove(v2);
        assertEquals("Edge is not contained.", g.contains(v1, v2), false);
    }

    @Test
    public void containsTestLabel() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("W1");
        Graph<String, String>.Vertex v2 = g.add("W2");
        Graph<String, String>.Vertex v3 = g.add("W3");
        Graph<String, String>.Edge e1 = g.add(v1, v2, "foo");
        Graph<String, String>.Edge e2 = g.add(v2, v3, "bar");
        g.remove(v1);
        assertEquals("Edge with null label is here.",
            g.contains(v2, v3, "bar"), true);
        assertEquals("Does not contain edge with 'bar'.",
            g.contains(v2, v3, "foo"), false);
    }

    @Test
    public void removeEdgeTest() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("N1");
        Graph<String, String>.Vertex v2 = g.add("N2");
        Graph<String, String>.Vertex v3 = g.add("N3");
        Graph<String, String>.Edge e1 = g.add(v1, v2, "foo");
        Graph<String, String>.Edge e2 = g.add(v2, v3, "bar");
        assertEquals("Edge was not removed.", g.contains(v1, v2, "foo"), true);
        g.remove(e1);
        assertEquals("Edge e2 was removed.", g.contains(v1, v2, "foo"), false);
    }

    @Test
    public void removeEdgeVerticesTest() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("MIDDLE1");
        Graph<String, String>.Vertex v2 = g.add("MIDDLE2");
        Graph<String, String>.Vertex v3 = g.add("MIDDLE3");
        Graph<String, String>.Edge e1 = g.add(v1, v2, "foo");
        Graph<String, String>.Edge e2 = g.add(v2, v3, "bar");
        assertEquals("Edge between v1 & v2 is present.",
            g.contains(v1, v2, "foo"), true);
        g.remove(v1, v2);
        assertEquals("Edge between v1 & v2 was removed.",
            g.contains(v1, v2, "foo"), false);
    }

    public void invalidRemoveEdge() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("box1");
        Graph<String, String>.Vertex v2 = g.add("box2");
        Graph<String, String>.Edge e = g.add(v1, v2);
        assertEquals(true, g.contains(v1, v2));
        DirectedGraph<String, String> g2 = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex newvertex = g2.add("inside");
        Graph<String, String>.Vertex newvertex2 = g2.add("inside2");
        Graph<String, String>.Edge e2 = g2.add(newvertex, newvertex2);
        g.remove(e2);
        assertEquals("No edge e2 in graph g", true, g.contains(v1, v2));
        assertEquals(true, g2.contains(newvertex, newvertex2));
    }

    @Test
    public void verticesIterationTest() {
        ArrayList<Graph<String, String>.Vertex> storage
            = new ArrayList<Graph<String, String>.Vertex>();
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("Q1");
        Graph<String, String>.Vertex v2 = g.add("Q2");
        Graph<String, String>.Vertex v3 = g.add("Q3");
        Graph<String, String>.Edge e1 = g.add(v1, v2, "foo");
        Graph<String, String>.Edge e2 = g.add(v2, v3, "bar");
        Iteration<Graph<String, String>.Vertex> iter
            = g.vertices();
        while (iter.hasNext()) {
            storage.add(iter.next());
        }
        assertEquals("Iteration of vertices incorrect.", storage.size(), 3);
    }

    @Test
    public void verticesIterationTest2() {
        ArrayList<Graph<String, String>.Vertex> storage
            = new ArrayList<Graph<String, String>.Vertex>();
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("Q1");
        Graph<String, String>.Vertex v2 = g.add("Q2");
        Graph<String, String>.Vertex v3 = g.add("Q3");
        Graph<String, String>.Edge e1 = g.add(v1, v2, "foo");
        Graph<String, String>.Edge e2 = g.add(v2, v3, "bar");
        Iteration<Graph<String, String>.Vertex> iter
            = g.vertices();
        Graph<String, String>.Vertex next = iter.next();
        assertEquals("First vertex should be v1.", v1, next);
    }

    @Test
    public void successorsIterationTest() {
        ArrayList<Graph<String, String>.Vertex> storage
            = new ArrayList<Graph<String, String>.Vertex>();
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("Q1");
        Graph<String, String>.Vertex v2 = g.add("Q2");
        Graph<String, String>.Vertex v3 = g.add("Q3");
        Graph<String, String>.Edge e1 = g.add(v2, v1, "foo");
        Graph<String, String>.Edge e2 = g.add(v2, v3, "bar");
        Iteration<Graph<String, String>.Vertex> iter
            = g.successors(v2);
        while (iter.hasNext()) {
            storage.add(iter.next());
        }
        assertEquals("Iteration of successors incorrect.", storage.size(), 2);
    }

    @Test
    public void successorsIterationTest2() {
        ArrayList<Graph<String, String>.Vertex> storage
            = new ArrayList<Graph<String, String>.Vertex>();
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("Q1");
        Graph<String, String>.Vertex v2 = g.add("Q2");
        Graph<String, String>.Vertex v3 = g.add("Q3");
        Graph<String, String>.Edge e1 = g.add(v2, v1, "foo");
        Graph<String, String>.Edge e2 = g.add(v2, v3, "bar");
        Iteration<Graph<String, String>.Vertex> iter
            = g.successors(v2);
        Graph<String, String>.Vertex next = iter.next();
        assertEquals("First successor of v2 is v1.", v1, next);
    }

    @Test
    public void predecessorsIterationTest() {
        ArrayList<Graph<String, String>.Vertex> storage
            = new ArrayList<Graph<String, String>.Vertex>();
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("Q1");
        Graph<String, String>.Vertex v2 = g.add("Q2");
        Graph<String, String>.Vertex v3 = g.add("Q3");
        Graph<String, String>.Edge e1 = g.add(v2, v1, "foo");
        Graph<String, String>.Edge e2 = g.add(v3, v1, "bar");
        Iteration<Graph<String, String>.Vertex> iter
            = g.predecessors(v1);
        while (iter.hasNext()) {
            storage.add(iter.next());
        }
        assertEquals("Iteration of predecessors incorrect.", storage.size(), 2);
    }

    @Test
    public void predecessorsIterationTest2() {
        ArrayList<Graph<String, String>.Vertex> storage
            = new ArrayList<Graph<String, String>.Vertex>();
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("Q1");
        Graph<String, String>.Vertex v2 = g.add("Q2");
        Graph<String, String>.Vertex v3 = g.add("Q3");
        Graph<String, String>.Edge e1 = g.add(v2, v1, "foo");
        Graph<String, String>.Edge e2 = g.add(v3, v1, "bar");
        Iteration<Graph<String, String>.Vertex> iter
            = g.predecessors(v1);
        Graph<String, String>.Vertex next = iter.next();
        assertEquals("First predecessor of v1 is v2.", v2, next);
    }

    @Test
    public void edgesIterationTest() {
        ArrayList<Graph<String, String>.Edge> storage
            = new ArrayList<Graph<String, String>.Edge>();
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("Q1");
        Graph<String, String>.Vertex v2 = g.add("Q2");
        Graph<String, String>.Vertex v3 = g.add("Q3");
        Graph<String, String>.Edge e1 = g.add(v2, v1, "foo");
        Graph<String, String>.Edge e2 = g.add(v3, v1, "bar");
        Iteration<Graph<String, String>.Edge> iter
            = g.edges();
        while (iter.hasNext()) {
            storage.add(iter.next());
        }
        storage.remove(0);
        assertEquals("Second edge stored is e2.", storage.get(0), e2);
    }

    @Test
    public void outEdgesIterationTest() {
        ArrayList<Graph<String, String>.Edge> storage
            = new ArrayList<Graph<String, String>.Edge>();
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("K1");
        Graph<String, String>.Vertex v2 = g.add("K2");
        Graph<String, String>.Vertex v3 = g.add("K3");
        Graph<String, String>.Edge e1 = g.add(v2, v1);
        Graph<String, String>.Edge e2 = g.add(v3, v1);
        Iteration<Graph<String, String>.Edge> iter
            = g.outEdges(v3);
        while (iter.hasNext()) {
            storage.add(iter.next());
        }
        assertEquals("There is only one edge stored.", storage.size(), 1);
        assertEquals("Stored edge is e2", storage.get(0), e2);
    }

    @Test
    public void inEdgesIterationTest2() {
        ArrayList<Graph<String, String>.Edge> storage
            = new ArrayList<Graph<String, String>.Edge>();
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("W1");
        Graph<String, String>.Vertex v2 = g.add("W2");
        Graph<String, String>.Vertex v3 = g.add("W3");
        Graph<String, String>.Edge e1 = g.add(v2, v1);
        Graph<String, String>.Edge e2 = g.add(v3, v1);
        Iteration<Graph<String, String>.Edge> iter
            = g.inEdges(v1);
        while (iter.hasNext()) {
            storage.add(iter.next());
        }
        assertEquals("There are two edges stored.", storage.size(), 2);
        assertEquals("First stored edge is e1", storage.remove(0), e1);
        assertEquals("Second stored edge is e2", storage.get(0), e2);
    }

    @Test
    public void edgesCountTestUndirected() {
        UndirectedGraph<String, String> g1
            = new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g1.add("bar1");
        Graph<String, String>.Vertex v2 = g1.add("bar2");
        Graph<String, String>.Vertex v3 = g1.add("bar3");
        Graph<String, String>.Vertex v4 = g1.add("bar4");
        Graph<String, String>.Vertex v5 = g1.add("bar5");
        Graph<String, String>.Edge e1 = g1.add(v1, v3);
        Graph<String, String>.Edge e2 = g1.add(v2, v1);
        Graph<String, String>.Edge e3 = g1.add(v3, v4);
        Graph<String, String>.Edge e4 = g1.add(v1, v4);
        Graph<String, String>.Edge e5 = g1.add(v5, v1);
        assertEquals("Incorrect number of edges.", g1.edgeSize(), 5);
        g1.remove(e4);
        assertEquals("Incorrect number edges after removal", g1.edgeSize(), 4);
    }

    @Test
    public void multiGraph() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("MG1");
        Graph<String, String>.Vertex v2 = g.add("MG2");
        Graph<String, String>.Edge edge1 = g.add(v2, v1, "different1");
        Graph<String, String>.Edge edge2 = g.add(v2, v1, "different2");
        assertEquals("Two edges with same vertices", g.edgeSize(), 2);
        assertEquals(false, edge1 == edge2);
    }

    @Test
    public void multiGraph2() {
        DirectedGraph<String, String> g = new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("XG1");
        Graph<String, String>.Vertex v2 = g.add("XG2");
        Graph<String, String>.Edge edge1 = g.add(v2, v1, "box1");
        Graph<String, String>.Edge edge2 = g.add(v2, v1, "box2");
        Graph<String, String>.Edge edge3 = g.add(v1, v2, "box3");
        assertEquals("Three valid edges", g.edgeSize(), 3);
        assertEquals("Same edges different labels", true,
            !v2.getLabel().equals(v1.getLabel()));
    }
}
