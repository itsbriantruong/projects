package make;

import graph.Traversal;
import graph.Graph;

import java.util.HashMap;
import java.util.ArrayList;

/** Class extension of traversal that overrides
 *  preVisit, visit and postVisit to run for the make client.
 *  @author Brian Truong.
 */
public class MakeTraversal<VLabel, ELabel>
    extends graph.Traversal<VLabel, ELabel> {

    /** Constructor of MakeTraversal that takes in COMMANDS, which is a
     *  map from target to commands, TIMEMAP between built items
     *  and their build time, map of targets to DEPENDENTS,
     *  all INITIALTARGETS and GIVENTIME of the build. Traverses
     *  the graph and performs implemented visits when necessary. */
    MakeTraversal(HashMap<String, ArrayList<String>> commands,
        HashMap<String, Integer> timemap, int giventime,
        HashMap<String, ArrayList<String>> dependents,
        ArrayList<String> initialtargets) {
        targettocommand = commands;
        nametotime = timemap;
        currtime = giventime;
        targettodependents = dependents;
        targets = initialtargets;
        setupBuilt();

    }

    /** Sets up the built arraylist with the initial names of built items. */
    void setupBuilt() {
        for (String s : nametotime.keySet()) {
            built.add(s);
        }
    }


    /** Takes in vertex (string) S and checks to see if there are any
     *  cycles. When a target has a dependent that when recursively
     *  searching through any exisiting dependents, matches up with the
     *  initial vertex there is an error. */
    void checkCycleDependency(String s) {
        checkValidity(s);
        if (targettodependents.containsKey(s)) {
            havevisited.add(s);
            for (String dependent : targettodependents.get(s)) {
                if (!s.equals(foo) && dependent.equals(foo)
                    || havevisited.contains(dependent)) {
                    System.err.println("Circular dependency found!");
                    System.exit(1);
                }
                checkCycleDependency(dependent);
            }
        }
    }

    /** Check for error. If the makefile does not have a rule
     *  with T as the target, and T does not currently exist, report error. */
    void checkValidity(String t) {
        if (!nametotime.containsKey(t)
            && !built.contains(t) && !targets.contains(t)) {
            System.err.println("Invalid target/dependency: " + t);
            System.exit(1);
        }
    }

    @Override
    protected void preVisit(Graph<VLabel, ELabel>.Edge e,
                            Graph<VLabel, ELabel>.Vertex v0) {
        checkValidity((String) e.getV(v0).getLabel());
    }

    /** Method to be called when visiting vertex V.  If this routine throws
     *  a StopException, the traversal ends.  If it throws a RejectException,
     *  successors of V do not get visited from V. The default does nothing. */
    @Override
    protected void visit(Graph<VLabel, ELabel>.Vertex v) {
        foo = (String) v.getLabel();
        checkCycleDependency((String) v.getLabel());
        havevisited.clear();
    }

    /** Check to see if the vertex V has been built. Will build the vertex
     *  if it has not previously been built, or if the vertex is older
     *  than at least one of its prerequisites. */
    @Override
    protected void postVisit(Graph<VLabel, ELabel>.Vertex v) {
        if (built.contains((String) v.getLabel())) {
            if (targettodependents.containsKey((String) v.getLabel())) {
                for (String s : targettodependents.get((String) v.getLabel())) {
                    if (nametotime.get(s)
                        > nametotime.get((String) v.getLabel())) {
                        buildItem(v);
                    }
                }
            }
        } else {
            buildItem(v);
        }
    }

    /** Compiles the vertex V and will print the command of the target
     *  if there are any. For any new item built, the current time
     *  will be incremented and stored as the time the item was compiled. */
    void buildItem(Graph<VLabel, ELabel>.Vertex v) {
        if (targettocommand.containsKey((String) v.getLabel())) {
            for (String s : targettocommand.get((String) v.getLabel())) {
                System.out.printf("%s%n", s);
            }
            currtime += 10;
            nametotime.put((String) v.getLabel(), currtime);
            built.add((String) v.getLabel());
        }
    }
    /** Contains the target/dependency to check. */
    private String foo = "";

    /** Vertices that have been visited already. Elements will be added
     *  and removed to accomdate for postVisits and will report a
     *  circular dependency error if present (element to visit is already
     *  inside arraylist). */
    private ArrayList<String> havevisited = new ArrayList<String>();

    /** Arraylist of items that do not have built and stored by VLabel. */
    private ArrayList<String> built = new ArrayList<String>();

    /** Map of a target to all of their dependents.*/
    private HashMap<String, ArrayList<String>> targettodependents;

    /** HashMap of targets as keys and commands and values. */
    private HashMap<String, ArrayList<String>> targettocommand;

    /** HashMap of names (targets and dependents) as keys and time as value. */
    private HashMap<String, Integer> nametotime;

    /** List of all valid targets. */
    private ArrayList<String> targets;

    /** Current time in our traversal. */
    private int currtime;

    /** Represents the time during the visit. */
    private int visitedtime;
}
