package make;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

import graph.DirectedGraph;
import graph.Graph;

/** Initial class for the 'make' program.
 *  @author Brian Truong.
 */
public final class Main {

    /** Entry point for the CS61B make program.  ARGS may contain options
     *  and targets:
     *      [ -f MAKEFILE ] [ -D FILEINFO ] TARGET1 TARGET2 ...
     */
    public static void main(String... args) {
        String makefileName;
        String fileInfoName;

        if (args.length == 0) {
            usage();
        }

        makefileName = "Makefile";
        fileInfoName = "fileinfo";

        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-f")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    makefileName = args[a];
                }
            } else if (args[a].equals("-D")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    fileInfoName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }

        ArrayList<String> targets = new ArrayList<String>();

        for (; a < args.length; a += 1) {
            targets.add(args[a]);
        }

        make(makefileName, fileInfoName, targets);
    }

    /** Carry out the make procedure using MAKEFILENAME as the makefile,
     *  taking information on the current file-system state from FILEINFONAME,
     *  and building TARGETS, or the first target in the makefile if TARGETS
     *  is empty.
     */
    private static void make(String makefileName, String fileInfoName,
                             List<String> targets) {
        DirectedGraph<String, String> graph
            = new DirectedGraph<String, String>();
        List<String> alltargets = targets;
        processMakefile(makefileName);
        processFileinfo(fileInfoName);
        setupGraph(graph);
        MakeTraversal<String, String> traversal
            = new MakeTraversal<String, String>(targettocommands,
                nametodate, currtime, targettodependents, targetlist);
        if (alltargets.size() == 0) {
            alltargets.add(firsttarget);
        }
        for (String s : alltargets) {
            traversal.checkValidity(s);
            traversal.depthFirstTraverse(graph, targetvertex.get(s));
        }
    }

    /** Processes MAKEFILENAME line by line and performs different
     *  functions depending on whether the line is a command line or
     *  a header. */
    private static void processMakefile(String makefileName) {
        try {
            Scanner input = new Scanner(new FileInputStream(makefileName));
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (!line.trim().equals("")) {
                    if (Character.isWhitespace(line.charAt(0))
                        && !currtarget.equals("")) {
                        processCommands(line);
                    } else if (line.charAt(0) != '#'
                        && line.charAt(0) != ' ') {
                        processHeader(line);
                        if (!prevtarget.equals("")
                            && commandset.size() > 0) {
                            if (targettocommands.containsKey(prevtarget)) {
                                System.err.println("Too many command sets.");
                                System.exit(1);
                            } else {
                                targettocommands.put(prevtarget,
                                    new ArrayList<String>());
                                for (String c : commandset) {
                                    targettocommands.get(prevtarget).add(c);
                                }
                                commandset.clear();
                            }
                        }
                    }
                }
            }
            if (!targettocommands.containsKey(currtarget)
                && commandset.size() > 0) {
                targettocommands.put(currtarget,
                    new ArrayList<String>());
                for (String c : commandset) {
                    targettocommands.get(currtarget).add(c);
                }
                commandset.clear();
            } else if (targettocommands.containsKey(currtarget)
                && commandset.size() > 0) {
                System.err.println("Too many command sets.");
                System.exit(1);
            }
        } catch (FileNotFoundException e) {
            System.err.println("nope");
            System.exit(1);
        }
    }

    /** Processes FILEINFONAME line by line and stores the
     *  current time and items that are already built (stored into
     *  a hashmap with the time they were built). */
    private static void processFileinfo(String fileInfoName) {
        try {
            boolean firstline = true;
            Scanner input = new Scanner(new FileInputStream(fileInfoName));
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (firstline) {
                    currtime = Integer.parseInt(line);
                    firstline = false;
                } else {
                    String[] args = line.trim().split("\\s+");
                    nametodate.put(args[0], Integer.parseInt(args[1]));
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Malformed integer value");
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println("nope");
            System.exit(1);
        }
    }

    /** Takes in GRAPH and creates all the vertices and edges in the graph
     *  with the information processed from makefilename. */
    private static void setupGraph(DirectedGraph<String, String> graph) {
        for (String s : targetlist) {
            if (!targetvertex.containsKey(s)) {
                targetvertex.put(s, graph.add(s));
            }
            for (String d : targettodependents.get(s)) {
                if (!targetvertex.keySet().contains(d)) {
                    Graph<String, String>.Vertex v2 = graph.add(d);
                    graph.add(targetvertex.get(s), v2);
                    targetvertex.put(d, v2);
                } else {
                    graph.add(targetvertex.get(s), targetvertex.get(d));
                }
            }
        }
    }

    /** Returns true iff WORD is a valid word for this client, which
     *  means it does not contain any of the characters specified
     *  in the specs. */
    static boolean checkWord(String word) {
        char[] chars = word.toCharArray();
        for (char c : chars) {
            if (c == ':' || c == '='
                || c == '#' || c == '\\') {
                return false;
            }
        }
        return true;
    }

    /** Returns true iff WORD is a valid target and can be used with this
     *  client. */
    static boolean checkTarget(String word) {
        String target = word.substring(0, word.length() - 1);
        return (checkWord(target) && word.charAt(word.length() - 1) == ':');
    }

    /** Processes the header string INPUT and stores all of the targets
     *  and their dependents (if any). */
    private static void processHeader(String input) {
        boolean firstline = true;
        ArrayList<String> prerequistes = new ArrayList<String>();
        String[] line = input.trim().split("\\s+");
        String target = line[0];
        if (checkTarget(target)) {
            String targettrimmed = target.substring(0, target.length() - 1);
            prevtarget = currtarget;
            currtarget = targettrimmed;
            Collections.addAll(prerequistes, line);
            prerequistes.remove(0);
            for (String p : prerequistes) {
                if (!checkWord(p)) {
                    System.err.println("Malformed prerequiste");
                    System.exit(1);
                }
            }
            if (firstline) {
                firsttarget = targettrimmed;
                firstline = false;
            }
            if (targettodependents.containsKey(targettrimmed)
                && targetlist.contains(targettrimmed)) {
                for (String p : prerequistes) {
                    targettodependents.get(targettrimmed).add(p);
                }
            } else {
                targetlist.add(targettrimmed);
                targettodependents.put(targettrimmed, prerequistes);
            }
        } else {
            System.err.println("Malformed target");
            System.exit(1);
        }
    }

    /** Processes the command string INPUT and stores
     *  the command in a hashmap with the current target. */
    private static void processCommands(String input) {
        String cleaned = input.replaceAll("\\t", "    ");
        if (!cleaned.trim().equals("")) {
            commandset.add(cleaned);
        }
    }

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.out.println("java make.Main [ -f MAKEFILE ]"
            + " [ -D FILEINFO ] [ TARGET ... ]");
        System.exit(1);
    }

    /** First target of the makefileName. */
    private static String firsttarget = "";
    /** Current target being processed. */
    private static String currtarget = "";
    /** Previous target that was processed. */
    private static String prevtarget = "";
    /** Current time given by the first line of fileInfoName. */
    private static int currtime = 0;
    /** List of all the current targets. */
    private static ArrayList<String> targetlist = new ArrayList<String>();
    /** Arraylist of commands set lines to a target. */
    private static ArrayList<String> commandset = new ArrayList<String>();
    /** Map of target as the key and the vertex relative to that target
     *  as the value. */
    private static HashMap<String, Graph<String, String>.Vertex> targetvertex
        = new HashMap<String, Graph<String, String>.Vertex>();
    /** Map of target name to the command of the target that prints
     *  when executed (built). */
    private static HashMap<String, ArrayList<String>> targettocommands
        = new HashMap<String, ArrayList<String>>();
    /** Map of target name to its dependencies. Each target can have
     *  multiple dependencies or none at all. */
    private static HashMap<String, ArrayList<String>> targettodependents
        = new HashMap<String, ArrayList<String>>();
    /** Map of built name to the time the item was compiled. */
    private static HashMap<String, Integer> nametodate
        = new HashMap<String, Integer>();

}
