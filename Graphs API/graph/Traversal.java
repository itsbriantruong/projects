package graph;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.Stack;
import java.util.LinkedList;
import java.util.ArrayList;

/** Implements a generalized traversal of a graph.  At any given time,
 *  there is a particular set of untraversed vertices---the "fringe."
 *  Traversal consists of repeatedly removing an untraversed vertex
 *  from the fringe, visting it, and then adding its untraversed
 *  successors to the fringe.  The client can dictate an ordering on
 *  the fringe, determining which item is next removed, by which kind
 *  of traversal is requested.
 *     + A depth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at one end.  It also revisits the node
 *       itself after traversing all successors by calling the
 *       postVisit method on it.
 *     + A breadth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at different ends.  It also revisits the node
 *       itself after traversing all successors as for depth-first
 *       traversals.
 *     + A general traversal treats the fringe as an ordered set, as
 *       determined by a Comparator argument.  There is no postVisit
 *       for this type of traversal.
 *  As vertices are added to the fringe, the traversal calls a
 *  preVisit method on the vertex.
 *
 *  Generally, the client will extend Traversal, overriding the visit,
 *  preVisit, and postVisit methods, as desired (by default, they do nothing).
 *  Any of these methods may throw StopException to halt the traversal
 *  (temporarily, if desired).  The preVisit method may throw a
 *  RejectException to prevent a vertex from being added to the
 *  fringe, and the visit method may throw a RejectException to
 *  prevent its successors from being added to the fringe.
 *  @author Brian Truong.
 */
public class Traversal<VLabel, ELabel> {

    /** Perform a traversal of G over all vertices reachable from V.
     *  ORDER determines the ordering in which the fringe of
     *  untraversed vertices is visited.  The effect of specifying an
     *  ORDER whose results change as a result of modifications made during the
     *  traversal is undefined. */
    public void traverse(Graph<VLabel, ELabel> G,
                         Graph<VLabel, ELabel>.Vertex v,
                         final Comparator<VLabel> order) {
        _comparator = order;
        Comparator<Graph<VLabel, ELabel>.Vertex> vcomparator
            = new Comparator<Graph<VLabel, ELabel>.Vertex>() {
                    @Override
                    public int compare(Graph<VLabel, ELabel>.Vertex v1,
                        Graph<VLabel, ELabel>.Vertex v2) {
                        int result = order.compare(v1.getLabel(),
                            v2.getLabel());
                        if (result != 0) {
                            return result;
                        } else {
                            return -1;
                        }
                    }
                };
        TreeSet<Graph<VLabel, ELabel>.Vertex> fringe
            = new TreeSet<Graph<VLabel, ELabel>.Vertex>(vcomparator);
        _graph = G;
        _traversal = "general";
        fringe.add(v);
        while (!fringe.isEmpty()) {
            try {
                _finalVertex = fringe.pollFirst();
                visit(_finalVertex);
                _finalVertex.setMarked();
            } catch (StopException e) {
                return;
            } catch (RejectException e) {
                continue;
            }
            if (_graph.outDegree(_finalVertex) > 0) {
                for (Graph<VLabel, ELabel>.Edge etmp
                    : _graph.outEdges(_finalVertex)) {
                    try {
                        if (!etmp.getV(_finalVertex).getMarked()) {
                            _finalEdge = etmp;
                            preVisit(etmp, _finalVertex);
                            fringe.add(etmp.getV1());
                        }
                    } catch (StopException e) {
                        return;
                    } catch (RejectException e) {
                        System.out.println("Edge is not traversed.");
                    }
                }
            }
            fringe.remove(_finalVertex);
        }
    }

    /** Performs a depth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it or removed from it at one end in
     *  an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void depthFirstTraverse(Graph<VLabel, ELabel> G,
                                   Graph<VLabel, ELabel>.Vertex v) {
        Stack<Graph<VLabel, ELabel>.Vertex> fringe
            = new Stack<Graph<VLabel, ELabel>.Vertex>();
        ArrayList<Graph<VLabel, ELabel>.Vertex> postvisited
            = new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        _graph = G;
        _traversal = "depth";
        fringe.push(v);
        while (!fringe.empty()) {
            _finalVertex = fringe.pop();
            if (!_finalVertex.getMarked()) {
                try {
                    _finalVertex.setMarked();
                    visit(_finalVertex);
                } catch (StopException e) {
                    return;
                } catch (RejectException e) {
                    continue;
                }
                fringe.push(_finalVertex);
                for (Iteration<Graph<VLabel, ELabel>.Edge> iter
                        = _graph.outEdges(_finalVertex);
                    iter.hasNext();) {
                    Graph<VLabel, ELabel>.Edge etmp = iter.next();
                    Graph<VLabel, ELabel>.Vertex vtmp
                        = etmp.getV(_finalVertex);
                    try {
                        if (!vtmp.getMarked()) {
                            _finalEdge = etmp;
                            preVisit(etmp, etmp.getV0());
                            fringe.push(vtmp);
                        }
                    } catch (StopException e) {
                        return;
                    } catch (RejectException e) {
                        System.out.println("Edge is not traversed.");
                    }
                }
            } else {
                try {
                    if (!postvisited.contains(_finalVertex)) {
                        postVisit(_finalVertex);
                        postvisited.add(_finalVertex);
                    }
                } catch (StopException e) {
                    return;
                } catch (RejectException e) {
                    continue;
                }
            }
        }
    }

    /** Performs a breadth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it at one end and removed from it at the
     *  other in an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void breadthFirstTraverse(Graph<VLabel, ELabel> G,
                                     Graph<VLabel, ELabel>.Vertex v) {
        LinkedList<Graph<VLabel, ELabel>.Vertex> fringe
            = new LinkedList<Graph<VLabel, ELabel>.Vertex>();
        ArrayList<Graph<VLabel, ELabel>.Vertex> storage
            = new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        _graph = G;
        _traversal = "breadth";
        fringe.add(v);
        visit(v);
        v.setMarked();
        while (fringe.size() != 0) {
            _finalVertex = fringe.removeFirst();
            storage.add(_finalVertex);
            for (Graph<VLabel, ELabel>.Vertex elem : storage) {
                Iteration<Graph<VLabel, ELabel>.Edge> iter
                    = _graph.outEdges(elem);
                while (iter.hasNext()) {
                    Graph<VLabel, ELabel>.Edge etmp = iter.next();
                    Graph<VLabel, ELabel>.Vertex vtmp1 = etmp.getV0();
                    Graph<VLabel, ELabel>.Vertex vtmp2 = etmp.getV1();
                    try {
                        if (!vtmp2.getMarked()) {
                            _finalEdge = etmp;
                            preVisit(etmp, vtmp1);
                            vtmp2.setMarked();
                            visit(vtmp2);
                            fringe.add(vtmp2);
                        }
                    } catch (StopException e) {
                        return;
                    } catch (RejectException e) {
                        System.out.println("Edge is not traversed.");
                    }
                }
            }
        }
        try {
            for (Graph<VLabel, ELabel>.Vertex item : storage) {
                postVisit(item);
            }
        } catch (StopException e) {
            return;
        } catch (RejectException e) {
            System.out.println("RejectException encountered.");
        }
    }

    /** Continue the previous traversal starting from V.
     *  Continuing a traversal means that we do not traverse
     *  vertices that have been traversed previously. */
    public void continueTraversing(Graph<VLabel, ELabel>.Vertex v) {
        if (_traversal.equals("general")) {
            traverse(_graph, v, _comparator);
        } else if (_traversal.equals("depth")) {
            depthFirstTraverse(_graph, v);
        } else if (_traversal.equals("breadth")) {
            breadthFirstTraverse(_graph, v);
        }
    }

    /** If the traversal ends prematurely, returns the Vertex argument to
     *  preVisit, visit, or postVisit that caused a Visit routine to
     *  return false.  Otherwise, returns null. */
    public Graph<VLabel, ELabel>.Vertex finalVertex() {
        return _finalVertex;
    }

    /** If the traversal ends prematurely, returns the Edge argument to
     *  preVisit that caused a Visit routine to return false. If it was not
     *  an edge that caused termination, returns null. */
    public Graph<VLabel, ELabel>.Edge finalEdge() {
        return _finalEdge;
    }

    /** Returns the last graph argument to a traverse routine, or null if none
     *  of these methods have been called. */
    protected Graph<VLabel, ELabel> theGraph() {
        return _graph;
    }

    /** Method to be called when adding the node at the other end of E from V0
     *  to the fringe. If this routine throws a StopException,
     *  the traversal ends.  If it throws a RejectException, the edge
     *  E is not traversed. The default does nothing.
     */
    protected void preVisit(Graph<VLabel, ELabel>.Edge e,
                            Graph<VLabel, ELabel>.Vertex v0) {
    }

    /** Method to be called when visiting vertex V.  If this routine throws
     *  a StopException, the traversal ends.  If it throws a RejectException,
     *  successors of V do not get visited from V. The default does nothing. */
    protected void visit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** Method to be called immediately after finishing the traversal
     *  of successors of vertex V in pre- and post-order traversals.
     *  If this routine throws a StopException, the traversal ends.
     *  Throwing a RejectException has no effect. The default does nothing.
     */
    protected void postVisit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** The Vertex (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Vertex _finalVertex;
    /** The Edge (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Edge _finalEdge;
    /** The last graph traversed. */
    protected Graph<VLabel, ELabel> _graph;
    /** The comparator for traverse. */
    private Comparator<VLabel> _comparator;
    /** The last type of traversal performed. */
    private String _traversal = "";
}
