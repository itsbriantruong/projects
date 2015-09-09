package graph;

import java.util.List;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Comparator;
import java.util.ArrayList;

/** Assorted graph algorithms.
 *  @author Brian Truong.
 */
public final class Graphs {

    /* A* Search Algorithms */

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the edge weighter EWEIGHTER.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, uses VWEIGHTER to set the weight of vertex v
     *  to the weight of a minimal path from V0 to v, for each v in
     *  the returned path and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *              < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.  If V1 is
     *  unreachable from V0, returns null and sets the minimum path weights of
     *  all reachable nodes.  The distance to a node unreachable from V0 is
     *  Double.POSITIVE_INFINITY. */
    public static <VLabel, ELabel> List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G, Graph<VLabel, ELabel>.Vertex V0,
        Graph<VLabel, ELabel>.Vertex V1, Distancer<? super VLabel> h,
        Weighter<? super VLabel> vweighter,
        Weighting<? super ELabel> eweighter) {
        Comparator<Graph<VLabel, ELabel>.Vertex> vcomparator
            = pathComparator1(vweighter);
        PriorityQueue<Graph<VLabel, ELabel>.Vertex> open
            = new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(G.vertexSize(),
                vcomparator);
        HashMap<Graph<VLabel, ELabel>.Vertex, Graph<VLabel, ELabel>.Vertex>
            camefrom = new HashMap<Graph<VLabel, ELabel>.Vertex,
            Graph<VLabel, ELabel>.Vertex>();
        ArrayList<Graph<VLabel, ELabel>.Vertex> closed
            = new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        HashMap<Graph<VLabel, ELabel>.Vertex, Double> gscore
            = new HashMap<Graph<VLabel, ELabel>.Vertex, Double>();
        vweighter.setWeight(V0.getLabel(), 0.0 + h.dist(V0.getLabel(),
                V1.getLabel()));
        gscore.put(V0, 0.0);
        open.add(V0);
        while (open.size() != 0) {
            Graph<VLabel, ELabel>.Vertex curr = open.poll();
            if (curr.equals(V1)) {
                return finishPath(G.reconstructPath(camefrom, V1), eweighter);
            }
            closed.add(curr);
            for (Iteration<Graph<VLabel, ELabel>.Edge> iter
                    = G.outEdges(curr); iter.hasNext();) {
                Graph<VLabel, ELabel>.Edge etmp = iter.next();
                Graph<VLabel, ELabel>.Vertex vtmp = etmp.getV(curr);
                double g =  gscore.get(curr)
                                + eweighter.weight(etmp.getLabel());
                double f = g + h.dist(vtmp.getLabel(), V1.getLabel());
                if (closed.contains(vtmp)) {
                    if (f >= vweighter.weight(vtmp.getLabel())) {
                        continue;
                    }
                }
                if (!open.contains(vtmp)
                        || f < vweighter.weight(vtmp.getLabel())) {
                    camefrom.put(vtmp, curr);
                    gscore.put(vtmp, g);
                    vweighter.setWeight(vtmp.getLabel(), f);
                    if (!open.contains(vtmp)) {
                        open.add(vtmp);
                    }
                }
            }
        }
        return null;
    }

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the weights of its edge labels.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, sets the weight of vertex v to the weight of
     *  a minimal path from V0 to v, for each v in the returned path
     *  and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *           < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.
     *
     *  This function has the same effect as the 6-argument version of
     *  shortestPath, but uses the .weight and .setWeight methods of
     *  the edges and vertices themselves to determine and set
     *  weights. If V1 is unreachable from V0, returns null and sets
     *  the minimum path weights of all reachable nodes.  The distance
     *  to a node unreachable from V0 is Double.POSITIVE_INFINITY. */
    public static <VLabel extends Weightable, ELabel extends Weighted>
    List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G, Graph<VLabel, ELabel>.Vertex V0,
        Graph<VLabel, ELabel>.Vertex V1, Distancer<? super VLabel> h) {
        Comparator<Graph<VLabel, ELabel>.Vertex> vcom = pathComparator2();
        PriorityQueue<Graph<VLabel, ELabel>.Vertex> open
            = new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(G.vertexSize(),
                vcom);
        HashMap<Graph<VLabel, ELabel>.Vertex, Graph<VLabel, ELabel>.Vertex>
            camefrom = new HashMap<Graph<VLabel, ELabel>.Vertex,
            Graph<VLabel, ELabel>.Vertex>();
        ArrayList<Graph<VLabel, ELabel>.Vertex> closed
            = new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        HashMap<Graph<VLabel, ELabel>.Vertex, Double> gscore
            = new HashMap<Graph<VLabel, ELabel>.Vertex, Double>();
        (V0.getLabel()).setWeight(0.0 + h.dist(V0.getLabel(), V1.getLabel()));
        gscore.put(V0, 0.0);
        open.add(V0);
        while (open.size() != 0) {
            Graph<VLabel, ELabel>.Vertex curr = open.poll();
            if (curr.equals(V1)) {
                return finishPath(G.reconstructPath(camefrom, V1));
            }
            closed.add(curr);
            for (Iteration<Graph<VLabel, ELabel>.Edge> iter
                    = G.outEdges(V0); iter.hasNext();) {
                Graph<VLabel, ELabel>.Edge etmp = iter.next();
                Graph<VLabel, ELabel>.Vertex vtmp = etmp.getV(curr);
                double g =  gscore.get(curr) + (etmp.getLabel()).weight();
                double f = g + h.dist(vtmp.getLabel(), V1.getLabel());
                if (closed.contains(vtmp)) {
                    if (f >= (vtmp.getLabel()).weight()) {
                        continue;
                    }
                }
                if (!open.contains(vtmp) || f < (vtmp.getLabel()).weight()) {
                    camefrom.put(vtmp, curr);
                    gscore.put(vtmp, g);
                    (vtmp.getLabel()).setWeight(f);
                    if (!open.contains(vtmp)) {
                        open.add(vtmp);
                    }
                }
            }
        }
        return null;
    }

    /** Takes in VWEIGHTER and returns a comparator with a compare
     *  method that utilizes the weight of vertices' label. Takes in
     *  types VLABEL and ELABEL. */
    static <VLabel, ELabel> Comparator<Graph<VLabel, ELabel>.Vertex>
    pathComparator1(final Weighter<? super VLabel> vweighter) {
        Comparator<Graph<VLabel, ELabel>.Vertex> comparator
            = new Comparator<Graph<VLabel, ELabel>.Vertex>() {
                @Override
                public int compare(Graph<VLabel, ELabel>.Vertex v1,
                    Graph<VLabel, ELabel>.Vertex v2) {
                    return Double.compare(vweighter.weight(v1.getLabel()),
                            vweighter.weight(v2.getLabel()));
                }
            };
        return comparator;
    }

    /** Returns a comparator with a compare method that utilizes the weight
     *  of vertices' label by extension. Takes in types VLABEL that
     *  extends weightable and ELABEL that extends weighted. */
    static <VLabel extends Weightable, ELabel extends Weighted>
    Comparator<Graph<VLabel, ELabel>.Vertex>
    pathComparator2() {
        Comparator<Graph<VLabel, ELabel>.Vertex> comparator
            = new Comparator<Graph<VLabel, ELabel>.Vertex>() {
                @Override
                public int compare(Graph<VLabel, ELabel>.Vertex v1,
                    Graph<VLabel, ELabel>.Vertex v2) {
                    return Double.compare((v1.getLabel()).weight(),
                            (v2.getLabel()).weight());
                }
            };
        return comparator;
    }

    /** Returns arraylist of edges for optimal path. Takes in arraylist
     *  of arraylist of edges, EDGES, and stores only the optimal edge with
     *  the lowest weight when there are multiple edges with the same
     *  vertices (multi-graph). Uses EWEIGHTER to obtain the weight of
     *  the edges. Takes in types VLABEL and ELABEL. */
    static <VLabel, ELabel> ArrayList<Graph<VLabel, ELabel>.Edge>
    finishPath(ArrayList<ArrayList<Graph<VLabel, ELabel>.Edge>> edges,
        Weighting<? super ELabel> eweighter) {
        ArrayList<Graph<VLabel, ELabel>.Edge> result
            = new ArrayList<Graph<VLabel, ELabel>.Edge>();
        for (ArrayList<Graph<VLabel, ELabel>.Edge> similar : edges) {
            Graph<VLabel, ELabel>.Edge contain = similar.remove(0);
            for (Graph<VLabel, ELabel>.Edge e : similar) {
                if (eweighter.weight(contain.getLabel())
                        > eweighter.weight(e.getLabel())) {
                    contain = e;
                }
            }
            result.add(contain);
        }
        return result;
    }

    /** Returns arraylist of edges for optimal path. Takes in arraylist
     *  of arraylist of edges, EDGES, and stores only the optimal edge with
     *  the lowest weight when there are multiple edges with the same
     *  vertices (multi-graph). Uses EWEIGHTER to obtain the weight of
     *  the edges. Takes in types VLABEL that extends weightable and
     *  ELABEL that extends weighted. */
    static <VLabel extends Weightable, ELabel extends Weighted>
    ArrayList<Graph<VLabel, ELabel>.Edge>
    finishPath(ArrayList<ArrayList<Graph<VLabel, ELabel>.Edge>> edges) {
        ArrayList<Graph<VLabel, ELabel>.Edge> result
            = new ArrayList<Graph<VLabel, ELabel>.Edge>();
        for (ArrayList<Graph<VLabel, ELabel>.Edge> similar : edges) {
            Graph<VLabel, ELabel>.Edge contain = similar.remove(0);
            for (Graph<VLabel, ELabel>.Edge e : similar) {
                if (contain.getLabel().weight() > e.getLabel().weight()) {
                    contain = e;
                }
            }
            result.add(contain);
        }
        return result;
    }

    /** Returns a distancer whose dist method always returns 0. */
    public static final Distancer<Object> ZERO_DISTANCER =
        new Distancer<Object>() {
            @Override
            public double dist(Object v0, Object v1) {
                return 0.0;
            }
        };
}
