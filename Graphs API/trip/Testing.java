package trip;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your trip package per se (that is, it must be
 * possible to remove them and still have your package work). */

import ucb.junit.textui;
import static org.junit.Assert.*;
import org.junit.Test;

/** Unit tests for the trip package. */
public class Testing {

    /** Run all JUnit tests in the graph package. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(trip.Testing.class));
    }


    @Test
    public void testDirectioner() {
        String s1 = Main.directioner("NS");
        assertEquals(s1, "south");
        String s2 = Main.directioner("EW");
        assertEquals(s2, "west");
        String s3 = Main.directioner("WE");
        assertEquals(s3, "east");
        String s4 = Main.directioner("SN");
        assertEquals(s4, "north");
    }

    @Test
    public void testReverse() {
        String s1 = Main.directioner("NS");
        assertEquals(Main.reverse(s1), "north");
        String s2 = Main.directioner("EW");
        assertEquals(Main.reverse(s2), "east");
        String s3 = Main.directioner("WE");
        assertEquals(Main.reverse(s3), "west");
        String s4 = Main.directioner("SN");
        assertEquals(Main.reverse(s4), "south");
    }

    @Test
    public void testMakeELabel() {
        String road = "Telegraph";
        String from = "P1";
        String to = "P2";
        String length = "2.0";
        String tmp = "Telegraph P1 P2 2.0";
        String result = Main.makeElabel(road, from,
            to, length);
        assertEquals(tmp, result);
    }

    @Test
    public void testAlongPath() {
        String road = "south";
        String currentdirection = "east";
        assertEquals(Main.alongPath(road, currentdirection), false);
    }

    @Test
    public void testAlongPath2() {
        String road = "south";
        String currentdirection = "north";
        assertEquals(Main.alongPath(road, currentdirection), true);
    }
}
