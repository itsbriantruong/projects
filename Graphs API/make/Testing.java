package make;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your make package per se (that is, it must be
 * possible to remove them and still have your package work). */

import org.junit.Test;
import ucb.junit.textui;
import static org.junit.Assert.*;

/** Unit tests for the make package. */
public class Testing {

    /** Run all JUnit tests in the make package. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(make.Testing.class));
    }

    @Test
    public void testCheckWord() {
        String word1 = "He:llo";
        assertEquals(false, Main.checkWord(word1));
        String word2 = "Hello";
        assertEquals(true, Main.checkWord(word2));
        String word3 = "#invalid";
        assertEquals(false, Main.checkWord(word3));
        String word4 = "wh=at";
        assertEquals(false, Main.checkWord(word4));
    }

    @Test
    public void testCheckTarget() {
        String target1 = "Food:";
        assertEquals(Main.checkTarget(target1), true);
        String target2 = "Target";
        assertEquals(Main.checkTarget(target2), false);
        String target3 = "Tar#get:";
        assertEquals(Main.checkTarget(target3), false);
    }

    @Test
    public void testCheckTarget2() {
        String target1 = "Deliciousness:";
        assertEquals(Main.checkTarget(target1), true);
        String target2 = "Manager:";
        assertEquals(Main.checkTarget(target2), true);
        String target3 = "T:arget:";
        assertEquals(Main.checkTarget(target3), false);
    }
}
