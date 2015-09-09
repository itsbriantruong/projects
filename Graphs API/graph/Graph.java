package graph;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;

/* Do not add or remove public or protected members, or modify the signatures of
 * any public methods.  You may make changes that don't affect the API as seen
 * from outside the graph package:
 *   + You may make methods in Graph abstract, if you want different
 *     implementations in DirectedGraph and UndirectedGraph.
 *   + You may add bodies to abstract methods, modify existing bodies,
 *     or override inherited methods.
 *   + You may change parameter names, or add 'final' modifiers to parameters.
 *   + You may private and package private members.
 *   + You may add additional non-public classes to the graph package.
 */

/** Represents a general graph whose vertices are labeled with a type
 *  VLABEL and whose edges are labeled with a type ELABEL. The
 *  vertices are represented by the inner type Vertex and edges by
 *  inner type Edge.  A graph may be directed or undirected.  For
 *  an undirected graph, outgoing and incoming edges are the same.
 *  Graphs may have self edges and may have multiple edges between vertices.
 *
 *  The vertices and edges of the graph, the edges incident on a
 *  vertex, and the neighbors of a vertex are all accessible by
 *  iterators.  Changing the graph's structure by adding or deleting
 *  edges or vertices invalidates these iterators (subsequent use of
 *  them is undefined.)
 *  @author Brian Truong.
 */
public abstract class Graph<VLabel, ELabel> {

    /** Represents one of my vertices. */
    public class Vertex {

        /** True iff the vertex has been visited already. */
        private boolean _marked = false;

        /** A new vertex with LABEL as the value of getLabel(). */
        Vertex(VLabel label) {
            _label = label;
        }

        /** Returns true iff this vertex is marked. */
        boolean getMarked() {
            return _marked;
        }

        /** Set this vertex to be marked (true). */
        void setMarked() {
            _marked = true;
        }

        /** Returns the label on this vertex. */
        public VLabel getLabel() {
            return _label;
        }

        @Override
        public String toString() {
            return String.valueOf(_label);
        }

        /** The label on this vertex. */
        private final VLabel _label;

    }

    /** Represents one of my edges. */
    public class Edge {

        /** True iff the edge has been visited already. */
        private boolean _marked = false;

        /** An edge (V0,V1) with label LABEL.  It is a directed edge (from
         *  V0 to V1) in a directed graph. */
        Edge(Vertex v0, Vertex v1, ELabel label) {
            _label = label;
            _v0 = v0;
            _v1 = v1;
        }

        /** Returns true iff this edge is marked. */
        boolean getMarked() {
            return _marked;
        }

        /** Set this edge to be marked (true). */
        void setMarked() {
            _marked = true;
        }

        /** Returns the label on this edge. */
        public ELabel getLabel() {
            return _label;
        }

        /** Return the vertex this edge exits. For an undirected edge, this is
         *  one of the incident vertices. */
        public Vertex getV0() {
            return _v0;
        }

        /** Return the vertex this edge enters. For an undirected edge, this is
         *  the incident vertices other than getV1(). */
        public Vertex getV1() {
            return _v1;
        }

        /** Returns the vertex at the other end of me from V.  */
        public final Vertex getV(Vertex v) {
            if (v == _v0) {
                return _v1;
            } else if (v == _v1) {
                return _v0;
            } else {
                throw new
                    IllegalArgumentException("vertex not incident to edge");
            }
        }

        @Override
        public String toString() {
            return String.format("(%s,%s):%s", _v0, _v1, _label);
        }

        /** Endpoints of this edge.  In directed edges, this edge exits _V0
         *  and enters _V1. */
        private final Vertex _v0, _v1;

        /** The label on this edge. */
        private final ELabel _label;

    }

    /*=====  Methods and variables of Graph =====*/

    /** HashMap that has keys as vertices and an arraylist
     *  of edges as its values. */
    private HashMap<Vertex, ArrayList<Edge>> _outward
        = new HashMap<Vertex, ArrayList<Edge>>();

    /** HashMap that has keys as vertices and an arraylist
     *  of edges as its values. */
    private HashMap<Vertex, ArrayList<Edge>> _inward
        = new HashMap<Vertex, ArrayList<Edge>>();

    /** Arraylist that contains all vertices in the graph. */
    private ArrayList<Vertex> _vertices = new ArrayList<Vertex>();
    /** Arraylist that contains all edges in the graph. */
    private ArrayList<Edge> _edges = new ArrayList<Edge>();

    /** Arraylist for undirected graphs that only returns one edge
     *  instead of treating undirected edges as two distinct edges. */
    private ArrayList<Edge> _edgesone = new ArrayList<Edge>();

    /** Returns the number of vertices in me. */
    public int vertexSize() {
        return _vertices.size();
    }

    /** Returns the number of edges in me. */
    public int edgeSize() {
        if (isDirected()) {
            return _edges.size();
        } else {
            return (_edges.size() / 2);
        }
    }

    /** Returns an arraylist of edges that represent the
     *  optimal path from start to finish. Taks in hashmap
     *  of vertices to vertices CAMEFROM and vertex CURRENT,
     *  which reconstructs a path of current and its predecessors. */
    ArrayList<ArrayList<Edge>> reconstructPath(
        HashMap<Vertex, Vertex> camefrom, Vertex current) {
        vconstruct.add(current);
        return reconstructHelper(camefrom, current);
    }

    /** Helper function to reconstructPath that takes in CAMEFROM hashmap
     *  of vertices and vertex CURRENT and performs recursively to add all
     *  of current's parents to an arraylist of vertices and sent
     *  to connectVertices to be connected into a path. Returns
     *  an arraylist of edges that represent the optimal path from
     *  start to finish. */
    private ArrayList<ArrayList<Edge>> reconstructHelper(
        HashMap<Vertex, Vertex> camefrom, Vertex current) {
        if ((camefrom.keySet()).contains(current)) {
            vconstruct.add(camefrom.get(current));
            return reconstructHelper(camefrom, camefrom.get(current));
        }
        Collections.reverse(vconstruct);
        return connectVertices(vconstruct);
    }

    /** Takes in a list of ordered vertices INPUT and connects the
     *  ajacent vertices to form edges. Returns an arraylist of edges. */
    private ArrayList<ArrayList<Edge>> connectVertices(
        ArrayList<Vertex> input) {
        ArrayList<Vertex> vertices = input;
        ArrayList<ArrayList<Edge>> result = new ArrayList<ArrayList<Edge>>();
        Vertex prev = vertices.remove(0);
        while (vertices.size() > 0) {
            Vertex curr = vertices.remove(0);
            result.add(findEdge(prev, curr));
            prev = curr;
        }
        return result;
    }

    /** Returns the edge in this graph that connects vertex FROM to vertex
     *  TO.*/
    private ArrayList<Edge> findEdge(Vertex from, Vertex to) {
        ArrayList<Edge> similar = new ArrayList<Edge>();
        if (isDirected()) {
            for (Edge e : _edges) {
                if ((e.getV0()).equals(from) && (e.getV1()).equals(to)) {
                    similar.add(e);
                }
            }
        } else {
            for (Edge e : _edgesone) {
                if ((e.getV0()).equals(from) && (e.getV1()).equals(to)
                    || (e.getV0()).equals(to) && (e.getV1()).equals(from)) {
                    similar.add(e);
                }
            }
        }
        return similar;
    }

    /** Returns true iff I am a directed graph. */
    public abstract boolean isDirected();

    /** Returns the number of outgoing edges incident to V. Assumes V is one of
     *  my vertices.  */
    public int outDegree(Vertex v) {
        if (_outward.get(v) == null) {
            return 0;
        } else {
            return (_outward.get(v)).size();
        }
    }

    /** Returns the number of incoming edges incident to V. Assumes V is one of
     *  my vertices. */
    public int inDegree(Vertex v) {
        if (_inward.get(v) == null) {
            return 0;
        } else {
            return (_inward.get(v)).size();
        }
    }

    /** Returns outDegree(V). This is simply a synonym, intended for
     *  use in undirected graphs. */
    public final int degree(Vertex v) {
        return outDegree(v);
    }
    /** Helper function for contains. Returns true iff there is an edge
     *  (U, V) in me with any label. */
    private boolean containsHelper(Vertex u, Vertex v) {
        for (Edge e : _edges) {
            Vertex from = e.getV0();
            Vertex to = e.getV1();
            if (from == u && to == v) {
                return true;
            }
        }
        return false;
    }

    /** Returns true iff there is an edge (U, V) in me with any label. */
    public boolean contains(Vertex u, Vertex v) {
        if (isDirected()) {
            return containsHelper(u, v);
        } else {
            if (containsHelper(u, v)) {
                return true;
            } else {
                return containsHelper(v, u);
            }
        }
    }
    /** Helper function for contains. Returns true iff there is an edge
     *  (U, V) in me with label LABEL. */
    private boolean containsHelper(Vertex u, Vertex v, ELabel label) {
        for (Edge e : _edges) {
            Vertex from = e.getV0();
            Vertex to = e.getV1();
            if (from == u && to == v && e.getLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }

    /** Returns true iff there is an edge (U, V) in me with label LABEL. */
    public boolean contains(Vertex u, Vertex v,
                            ELabel label) {
        if (isDirected()) {
            return containsHelper(u, v, label);
        } else {
            if (containsHelper(u, v, label)) {
                return true;
            } else {
                return containsHelper(v, u, label);
            }
        }
    }

    /** Returns a new vertex labeled LABEL, and adds it to me with no
     *  incident edges. */
    public Vertex add(VLabel label) {
        Vertex result = new Vertex(label);
        _vertices.add(result);
        _inward.put(result, new ArrayList<Edge>());
        _outward.put(result, new ArrayList<Edge>());
        return result;
    }

    /** Returns an edge incident on FROM and TO, labeled with LABEL
     *  and adds it to this graph. If I am directed, the edge is directed
     *  (leaves FROM and enters TO). */
    public Edge add(Vertex from,
                    Vertex to,
                    ELabel label) {
        if (_vertices.contains(from) && _vertices.contains(to)) {
            if (isDirected()) {
                Edge result = new Edge(from, to, label);
                _edges.add(result);
                (_inward.get(to)).add(result);
                (_outward.get(from)).add(result);
                return result;
            } else {
                Edge result = new Edge(from, to, label);
                Edge result2 = new Edge(to, from, label);
                _edges.add(result);
                _edges.add(result2);
                _edgesone.add(result);
                (_inward.get(to)).add(result);
                (_outward.get(from)).add(result);
                (_inward.get(from)).add(result2);
                (_outward.get(to)).add(result2);
                return result;
            }
        } else {
            return null;
        }
    }

    /** Returns an edge incident on FROM and TO with a null label
     *  and adds it to this graph. If I am directed, the edge is directed
     *  (leaves FROM and enters TO). */
    public Edge add(Vertex from,
                    Vertex to) {
        if (_vertices.contains(from) && _vertices.contains(to)) {
            if (isDirected()) {
                Edge result = new Edge(from, to, null);
                _edges.add(result);
                (_inward.get(to)).add(result);
                (_outward.get(from)).add(result);
                return result;
            } else {
                Edge result = new Edge(from, to, null);
                Edge result2 = new Edge(to, from, null);
                _edges.add(result);
                _edges.add(result2);
                _edgesone.add(result);
                (_inward.get(to)).add(result);
                (_outward.get(from)).add(result);
                (_inward.get(from)).add(result2);
                (_outward.get(to)).add(result2);
                return result;
            }
        } else {
            return null;
        }
    }

    /** Helper function that takes in vertices V1 and V2 and removes
     *  necessary edges from respective arraylists of _outward and _inward.
     *  Checking _outward of v1 and _inward of v2. */
    private void removeHelper(Vertex v1, Vertex v2) {
        for (Iterator<Edge> e = (_outward.get(v1)).iterator();
            e.hasNext();) {
            Edge curr = e.next();
            if (curr.getV0() == v1 && curr.getV1() == v2) {
                e.remove();
            }
        }
        for (Iterator<Edge> e = (_inward.get(v2)).iterator();
            e.hasNext();) {
            Edge curr = e.next();
            if (curr.getV0() == v1 && curr.getV1() == v2) {
                e.remove();
            }
        }
    }

    /** Remove V and all adjacent edges, if present. */
    public void remove(Vertex v) {
        if (_vertices.contains(v)) {
            _vertices.remove(v);
            (_outward.get(v)).clear();
            (_inward.get(v)).clear();
            for (Iterator<Edge> e = _edges.iterator();
                e.hasNext();) {
                Edge curr = e.next();
                if (curr.getV0() == v) {
                    e.remove();
                    (_inward.get(curr.getV1())).remove(curr);
                } else if (curr.getV1() == v) {
                    e.remove();
                    (_outward.get(curr.getV0())).remove(curr);
                }
            }
            if (!isDirected()) {
                for (Iterator<Edge> eg = _edgesone.iterator();
                    eg.hasNext();) {
                    Edge curr = eg.next();
                    if (curr.getV0() == v || curr.getV1() == v) {
                        eg.remove();
                    }
                }
            }
        }
    }

    /** Remove E from me, if present.  E must be between my vertices,
     *  or the result is undefined.  */
    public void remove(Edge e) {
        Vertex v0 = e.getV0();
        Vertex v1 = e.getV1();
        if (_vertices.contains(v0) && _vertices.contains(v1)
                && _edges.contains(e)) {
            if (isDirected()) {
                _edges.remove(e);
                removeHelper(v0, v1);
            } else {
                removeHelper(v0, v1);
                removeHelper(v1, v0);
                for (Iterator<Edge> eg = _edgesone.iterator();
                    eg.hasNext();) {
                    Edge curr = eg.next();
                    if (curr.getV0() == v0 && curr.getV1() == v1
                        || curr.getV0() == v1 && curr.getV1() == v0) {
                        eg.remove();
                    }
                }
                for (Iterator<Edge> ed = _edges.iterator();
                    ed.hasNext();) {
                    Edge curr = ed.next();
                    if (curr.getV0() == v0 && curr.getV1() == v1
                        || curr.getV1() == v0 && curr.getV0() == v1) {
                        ed.remove();
                    }
                }
            }
        }
    }

    /** Remove all edges from V1 to V2 from me, if present.  The result is
     *  undefined if V1 and V2 are not among my vertices.  */
    public void remove(Vertex v1, Vertex v2) {
        if (_vertices.contains(v1) && _vertices.contains(v2)) {
            if (isDirected()) {
                for (Iterator<Edge> e = _edges.iterator();
                    e.hasNext();) {
                    Edge curr = e.next();
                    if (curr.getV0() == v1 && curr.getV1() == v2) {
                        e.remove();
                    }
                }
                removeHelper(v1, v2);
            } else {
                removeHelper(v1, v2);
                removeHelper(v2, v1);
                for (Iterator<Edge> eg = _edgesone.iterator();
                    eg.hasNext();) {
                    Edge curr = eg.next();
                    if (curr.getV0() == v1 && curr.getV1() == v2
                        || curr.getV1() == v1 && curr.getV0() == v2) {
                        eg.remove();
                    }
                }
                for (Iterator<Edge> ed = _edges.iterator();
                    ed.hasNext();) {
                    Edge curr = ed.next();
                    if (curr.getV0() == v1 && curr.getV1() == v2
                        || curr.getV1() == v1 && curr.getV0() == v2) {
                        ed.remove();
                    }
                }
            }
        }
    }

    /** Returns an Iterator over all vertices in arbitrary order. */
    public Iteration<Vertex> vertices() {
        return Iteration.iteration(_vertices);
    }

    /** Returns an iterator over all successors of V. */
    public Iteration<Vertex> successors(Vertex v) {
        ArrayList<Vertex> result = new ArrayList<Vertex>();
        for (Edge e : _outward.get(v)) {
            result.add(e.getV1());
        }
        return Iteration.iteration(result);
    }

    /** Returns an iterator over all predecessors of V. */
    public Iteration<Vertex> predecessors(Vertex v) {
        ArrayList<Vertex> result = new ArrayList<Vertex>();
        for (Edge e : _inward.get(v)) {
            result.add(e.getV0());
        }
        return Iteration.iteration(result);
    }

    /** Returns successors(V).  This is a synonym typically used on
     *  undirected graphs. */
    public final Iteration<Vertex> neighbors(Vertex v) {
        return successors(v);
    }

    /** Returns an iterator over all edges in me. */
    public Iteration<Edge> edges() {
        if (isDirected()) {
            return Iteration.iteration(_edges);
        } else {
            return Iteration.iteration(_edgesone);
        }
    }

    /** Returns iterator over all outgoing edges from V. */
    public Iteration<Edge> outEdges(Vertex v) {
        return Iteration.iteration(_outward.get(v));
    }

    /** Returns iterator over all incoming edges to V. */
    public Iteration<Edge> inEdges(Vertex v) {
        return Iteration.iteration(_inward.get(v));
    }

    /** Returns outEdges(V). This is a synonym typically used
     *  on undirected graphs. */
    public final Iteration<Edge> edges(Vertex v) {
        return outEdges(v);
    }

    /** Returns the natural ordering on T, as a Comparator.  For
     *  example, if stringComp = Graph.<Integer>naturalOrder(), then
     *  stringComp.compare(x1, y1) is <0 if x1<y1, ==0 if x1=y1, and >0
     *  otherwise. */
    public static <T extends Comparable<? super T>> Comparator<T> naturalOrder()
    {
        return new Comparator<T>() {
            @Override
            public int compare(T x1, T x2) {
                return x1.compareTo(x2);
            }
        };
    }

    /** Cause subsequent calls to edges() to visit or deliver
     *  edges in sorted order, according to COMPARATOR. Subsequent
     *  addition of edges may cause the edges to be reordered
     *  arbitrarily.  */
    public void orderEdges(final Comparator<ELabel> comparator) {
        Comparator<Edge> ecomparator = new Comparator<Edge>() {
            @Override
            public int compare(Edge e1, Edge e2) {
                return comparator.compare(e1.getLabel(), e2.getLabel());
            }
        };
        Collections.sort(_edges, ecomparator);
    }

    /** Arraylist holding all vertices that need to be used to
     *  contruct a path of edges. */
    private ArrayList<Vertex> vconstruct = new ArrayList<Vertex>();
}
