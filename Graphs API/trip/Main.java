package trip;

import graph.Graph;
import graph.Graphs;
import graph.DirectedGraph;
import graph.Weighting;
import graph.Weighter;
import graph.Distancer;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.FileNotFoundException;

/** Initial class for the 'trip' program.
 *  @author Brian Truong.
 */
public final class Main {

    /** Entry point for the CS61B trip program.  ARGS may contain options
     *  and targets:
     *      [ -m MAP ] [ -o OUT ] [ REQUEST ]
     *  where MAP (default Map) contains the map data, OUT (default standard
     *  output) takes the result, and REQUEST (default standard input) contains
     *  the locations along the requested trip.
     */
    public static void main(String... args) {
        String mapFileName;
        String outFileName;
        String requestFileName;

        mapFileName = "Map";
        outFileName = requestFileName = null;

        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-m")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    mapFileName = args[a];
                }
            } else if (args[a].equals("-o")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    outFileName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }

        if (a == args.length - 1) {
            requestFileName = args[a];
        } else if (a > args.length) {
            usage();
        }

        if (requestFileName != null) {
            try {
                System.setIn(new FileInputStream(requestFileName));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s.%n", requestFileName);
                System.exit(1);
            }
        }

        if (outFileName != null) {
            try {
                System.setOut(new PrintStream(new FileOutputStream(outFileName),
                                              true));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s for writing.%n",
                                  outFileName);
                System.exit(1);
            }
        }
        processRequests();
        trip(mapFileName);
    }

    /** Process and store each request line by line. */
    private static void processRequests() {
        Scanner file = new Scanner(System.in);
        while (file.hasNextLine()) {
            String line = file.nextLine();
            if (!line.equals("")) {
                requests.add(line);
            }
        }
        file.close();
    }

    /** Print a trip for the request on the standard input to the standard
     *  output, using the map data in MAPFILENAME.
     */
    private static void trip(String mapFileName) {
        try {
            Scanner input = new Scanner(new FileInputStream(mapFileName));
            DirectedGraph<String, String> d
                = new DirectedGraph<String, String>();
            while (input.hasNextLine()) {
                String line = (input.nextLine()).trim();
                if (!line.equals("")) {
                    processLine(line, d);
                }
            }
            for (String s : requests) {
                String[] line = s.trim().split("\\,*\\s+");
                System.out.printf("From %s:%n%n", line[0]);
                counter = 0;
                performTrip(line, d);
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.err.println("nope");
            System.exit(1);
        }
    }

    /** Process a line of INPUT from the mapFileName. Adds edges and
     *  vertices to the graph D depending on whether or not the input is
     *  a line for road or location. */
    private static void processLine(String input,
        DirectedGraph<String, String> d) {
        String[] current = input.split("\\s+");
        String vlabel = current[1];
        try {
            if (("L").equals(current[0])) {
                vertexoflabel.put(vlabel, d.add(vlabel));
                double x = Double.parseDouble(current[2]);
                double y = Double.parseDouble(current[3]);
                Double[] location = new Double[2];
                location[0] = x;
                location[1] = y;
                coordinates.put(vlabel, location);
            } else if (("R").equals(current[0])) {
                String elabel = makeElabel(current[2], current[1], current[5],
                    current[3]);
                Graph<String, String>.Edge e1
                    = d.add(vertexoflabel.get(current[1]),
                        vertexoflabel.get(current[5]), elabel);
                edgeoflabel.put(elabel, e1);
                d.add(vertexoflabel.get(current[5]),
                    vertexoflabel.get(current[1]), elabel);
                edgeweights.put(elabel, Double.parseDouble(current[3]));
                edgetodirection.put(e1, current[4]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error in input line.");
            System.exit(1);
        }
    }

    /** Takes the INPUT for each line of request and finds the optimal
     *  and shortest path for each location to next in graph D. */
    private static void performTrip(String[] input,
                    DirectedGraph<String, String> d) {
        ArrayList<String> locations = new ArrayList<String>();
        Collections.addAll(locations, input);
        String prev = locations.remove(0);
        while (locations.size() > 0) {
            String curr = locations.remove(0);
            ArrayList<Graph<String, String>.Edge> shortroute
                = (ArrayList<Graph<String, String>.Edge>)
                Graphs.shortestPath(d, vertexoflabel.get(prev),
                vertexoflabel.get(curr), TRIPDISTANCE, VWEIGHTER, EWEIGHTER);
            directions(shortroute, curr);
            prev = curr;
        }
    }

    /** Main controller that takes in an arraylist of edges ROADS that contains
     *  all the optimal roads that are used in order to form the shortest
     *  path to the given DESTINATION. Also combines similar direction
     *  i.e travel US6 5 miles NS and US6 2 miles SN equates to
     *  US6 3 miles NS. The condensed instructions are printed. */
    private static void directions(ArrayList<Graph<String, String>.Edge> roads,
                String destination) {
        while (roads.size() > 0) {
            String roadname = (roads.get(0)).getLabel();
            String[] roadelem = (roadname.trim()).split("\\s+");
            String roaddirection = directioner(
                edgetodirection.get(edgeoflabel.get(roadname)));
            if (!edgetodirection.containsKey(roads.get(0))) {
                roaddirection = reverse(roaddirection);
            }
            if (currentroad.equals("") && currentdirection.equals("")) {
                currentroad = roadelem[0];
                currentdirection = roaddirection;
            }
            if (roadelem[0].equals(currentroad)
                    && !currentdirection.equals("")
                    && alongPath(roaddirection, currentdirection)) {
                if (currentdirection.equals("south")
                    || currentdirection.equals("north")) {
                    if (roaddirection.equals("south")) {
                        totalmiles -= edgeweights.get(roadname);
                    } else {
                        totalmiles += edgeweights.get(roadname);
                    }
                } else {
                    if (roaddirection.equals("west")) {
                        totalmiles -= edgeweights.get(roadname);
                    } else {
                        totalmiles += edgeweights.get(roadname);
                    }
                }
                roads.remove(0);
                if (roads.size() == 0) {
                    printGoalRoad(destination);
                }
            } else {
                printRoad();
            }
        }
    }

    /** Helper function to directions that calculates, formats and
     *  prints the net travel mileage (with direction) on a particular road. */
    private static void printRoad() {
        counter += 1;
        if (currentdirection.equals("south")
                || currentdirection.equals("north")) {
            if (totalmiles > 0.0) {
                currentdirection = "north";
            } else if (totalmiles < 0.0) {
                currentdirection = "south";
                totalmiles = Math.abs(totalmiles);
            }
        } else if (currentdirection.equals("west")
                || currentdirection.equals("east")) {
            if (totalmiles > 0.0) {
                currentdirection = "east";
            } else if (totalmiles < 0.0) {
                currentdirection = "west";
                totalmiles = Math.abs(totalmiles);
            }
        }
        System.out.printf("%d. Take %s %s for %.1f miles.%n",
            counter, currentroad, currentdirection, totalmiles);
        totalmiles = 0.0;
        currentroad = "";
        currentdirection = "";
    }

    /** Unique version of printRoad that is called when DESTINATION
     *  has been reached. Formats and prints the destination arrival line. */
    private static void printGoalRoad(String destination) {
        counter += 1;
        if (currentdirection.equals("south")
                || currentdirection.equals("north")) {
            if (totalmiles > 0.0) {
                currentdirection = "north";
            } else if (totalmiles < 0.0) {
                currentdirection = "south";
                totalmiles = Math.abs(totalmiles);
            }
        } else if (currentdirection.equals("west")
                || currentdirection.equals("east")) {
            if (totalmiles > 0.0) {
                currentdirection = "east";
            } else if (totalmiles < 0.0) {
                currentdirection = "west";
                totalmiles = Math.abs(totalmiles);
            }
        }
        System.out.printf("%d. Take %s %s for %.1f miles to %s.%n",
            counter, currentroad, currentdirection, totalmiles, destination);
        totalmiles = 0.0;
        currentroad = "";
        currentdirection = "";
    }

    /** Returns true iff ROAD is along the same direction as CURRENT.
     *  i.e. north - south, or west - east (undirected). */
    static boolean alongPath(String road, String current) {
        if (current.equals("south") || current.equals("north")) {
            return road.equals("south") || road.equals("north");
        } else {
            return road.equals("east") || road.equals("west");
        }
    }

    /** Switch function that takes in a string INPUT that contains
     *  the directions {'NS', 'SN', 'EW', 'WE'} and returns the
     *  direction on a map that it correlates to. */
    static String directioner(String input) {
        switch (input) {
        case "NS":
            return "south";
        case "SN":
            return "north";
        case "EW":
            return "west";
        case "WE":
            return "east";
        default:
            System.err.println("Invalid direction entered");
            System.exit(1);
            return null;
        }
    }

    /** Takes in string INPUT of a direction {'north', 'south', 'east', 'west'}
     *  and returns the opposite direction relative to a map. */
    static String reverse(String input) {
        switch (input) {
        case "south":
            return "north";
        case "north":
            return "south";
        case "east":
            return "west";
        case "west":
            return "east";
        default:
            System.err.println("Invalid direction entered");
            System.exit(1);
            return null;
        }
    }

    /** Takes in a ROAD a starting point FROM, an end point TO, and a
     *  LENGTH. Returns a string of their concatenation with a space
     *  in between each word. */
    static String makeElabel(String road, String from, String to,
            String length) {
        return road + " " + from + " " + to + " " + length;
    }

    /** Weighter implementation that overrides setweight and weight
     *  to work for locations. Stores the weights as values of a hashmap. */
    public static final Weighter<String> VWEIGHTER = new Weighter<String>() {
        private HashMap<String, Double> vertexweights
            = new HashMap<String, Double>();

        @Override
        public void setWeight(String x, double v) {
            vertexweights.put(x, v);
        }

        @Override
        public double weight(String x) {
            return vertexweights.get(x);
        }

    };

    /** Weighting implementation that overrides weight to work for roads.
     *  The weighter pulls weights from a hashmap edgeweights and returns
     *  the value when weight is called. */
    public static final Weighting<String> EWEIGHTER = new Weighting<String>() {
        @Override
        public double weight(String x) {
            return edgeweights.get(x);
        }
    };

    /** Distancer implementation that overrides dist and uses the
     *  distance formula between two sets of coordinates as the heuristic
     *  value. */
    public static final Distancer<String> TRIPDISTANCE
        = new Distancer<String>() {
                @Override
                public double dist(String v0, String v1) {
                    return Math.sqrt(
                        Math.pow((coordinates.get(v0))[0]
                            - (coordinates.get(v1))[0], 2.0)
                        + Math.pow((coordinates.get(v0))[1]
                            - (coordinates.get(v1))[1], 2.0));
                }
            };

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.out.println("java trip.Main [ -m MAP ] [ -o OUT ] [ REQUESTS ]");
        System.exit(1);
    }

    /** Total miles traveled on a single given road. */
    private static double totalmiles = 0.0;
    /** Current road that is being traveled. */
    private static String currentroad = "";
    /** Current direction that our road is heading. */
    private static String currentdirection = "";
    /** Counter of instruction lines. Resets for every new trip. */
    private static int counter = 0;
    /** Arraylist that contains single lines of request. */
    private static ArrayList<String> requests = new ArrayList<String>();
    /** HashMap with road (edge) as keys and one of the four directions
     *  {'NS', 'SN', 'WE', 'EW'} as the value. */
    private static HashMap<Graph<String, String>.Edge, String> edgetodirection
        = new HashMap<Graph<String, String>.Edge, String>();
    /** HashMap with the name of a road as the key and the road
     *  length as the value (weight). */
    private static HashMap<String, Double> edgeweights
        = new HashMap<String, Double>();
    /** HashMap of coordinates where a VLabel is the key and the value
     *  is an array of length two with. */
    private static HashMap<String, Double[]> coordinates
        = new HashMap<String, Double[]>();
    /** HashMap with location names as keys and the vertex with that
     *  name in the graph as values. */
    private static HashMap<String, Graph<String, String>.Vertex> vertexoflabel
        = new HashMap<String, Graph<String, String>.Vertex>();
    /** HashMap with road names as keys and the edge with that
     *  name in the graph as values. */
    private static HashMap<String, Graph<String, String>.Edge> edgeoflabel
        = new HashMap<String, Graph<String, String>.Edge>();

}
