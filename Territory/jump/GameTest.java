package jump61;

import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static jump61.Color.*;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of Game.
 *  @author Brian Truong.
 */
public class GameTest {

    private void setUpCenter() {
        output = new OutputStreamWriter(System.out);
        game = new Game(new InputStreamReader(System.in),
                             output, output,
                             new OutputStreamWriter(System.err));
    }

    @Test
    public void testReadSingleColor() {
        setUpCenter();
        Board B = game.getMutBoard();
        ArrayList<Color> contain = new ArrayList<Color>();
        game.readSingleColor(contain, "r");
        assertEquals("should contain red", RED, contain.get(0));
        assertEquals("should only have one element", 1, contain.size());
        contain.clear();
        game.readSingleColor(contain, "b");
        assertEquals("should contain one valid elements", 1, contain.size());
    }

    @Test
    public void testMakeMove() {
        setUpCenter();
        Board B = game.getMutBoard();
        game.makeMove(1, 1);
        checkBoard("#1", B, 1, 1, 1, RED);
        assertEquals("square should have spot", 1, B.spots(1, 1));
        assertEquals("square should be RED", RED, B.color(1, 1));
    }

    @Test
    public void testMakeMove2() {
        setUpCenter();
        Board B = game.getMutBoard();
        game.makeMove(0);
        checkBoard("#1 ver2", B, 1, 1, 1, RED);
        assertEquals("square should have spot", 1, B.spots(0));
        assertEquals("square should be RED", RED, B.color(0));
    }

    @Test
    public void testClear() {
        setUpCenter();
        Board B = game.getMutBoard();
        game.makeMove(1, 1);
        assertEquals("square should have spot", 1, B.spots(1, 1));
        game.testClear();
        assertEquals("square should not have a spot", 0, B.spots(1, 1));
    }

    @Test
    public void testClear2() {
        setUpCenter();
        Board B = game.getMutBoard();
        game.makeMove(1, 1);
        game.setPlayer();
        game.restartPlaying();
        game.testClear();
        assertEquals("playing should be off", false, game.getPlaying());
    }

    @Test
    public void testClear3() {
        setUpCenter();
        Board B = game.getMutBoard();
        assertEquals("RED should be playing", RED, game.getPlayer());
        game.makeMove(1, 1);
        game.setPlayer();
        assertEquals("BLUE should be playing", BLUE, game.getPlayer());
        game.testClear();
        assertEquals("RED should be playing", RED, game.getPlayer());
        game.setPlayer();
        game.testClear();
        assertEquals("RED should STILL be playing", RED, game.getPlayer());
    }

    @Test
    public void testAutoSet() {
        setUpCenter();
        game.testSetAuto("RED");
        assertEquals("RED should be auto", true,
            (game.getAutomation()).get(RED));
        game.testSetAuto("BLUE");
        assertEquals("BLUE should be auto", true,
            (game.getAutomation()).get(BLUE));
    }

    @Test
    public void testManualSet() {
        setUpCenter();
        game.testSetManual("BLUE");
        assertEquals("BLUE should be manual", false,
            (game.getAutomation()).get(BLUE));
        game.testSetManual("RED");
        assertEquals("RED should be manual", false,
            (game.getAutomation()).get(RED));
    }

    @Test
    public void testPlaying() {
        setUpCenter();
        game.restartPlaying();
        assertEquals("playing should be true", true, game.getPlaying());
        game.testClear();
        assertEquals("clear turns off playing", false, game.getPlaying());
    }

    @Test
    public void testPlaying2() {
        setUpCenter();
        game.restartPlaying();
        assertEquals("playing should be true", true, game.getPlaying());
        game.testSetMoveNumber(2);
        assertEquals("commands turn off playing", false, game.getPlaying());
    }

    @Test
    public void testMoveNumber() {
        setUpCenter();
        Board B = game.getMutBoard();
        game.restartPlaying();
        game.testSetMoveNumber(2);
        assertEquals("move number changes player", BLUE, game.getPlayer());
        game.testSetMoveNumber(1);
        assertEquals("RED should be playing", RED, game.getPlayer());
        assertEquals("wrong move count", 1, B.numMoves());
    }

    @Test
    public void testSizeSet() {
        setUpCenter();
        Board B = game.getMutBoard();
        game.restartPlaying();
        game.testSetSize(4);
        assertEquals("playing should be off", false, game.getPlaying());
        assertEquals("wrong size", 4, B.size());
    }

    private void checkBoard(String msg, Board B, Object... contents) {
        for (int k = 0; k < contents.length; k += 4) {
            String M = String.format("%s at %d %d", msg, contents[k],
                                     contents[k + 1]);
            assertEquals(M, (int) contents[k + 2],
                         B.spots((int) contents[k], (int) contents[k + 1]));
            assertEquals(M, contents[k + 3],
                         B.color((int) contents[k], (int) contents[k + 1]));
        }
        int c;
        c = 0;
        for (int i = B.size() * B.size() - 1; i >= 0; i -= 1) {
            assertTrue("bad white square #" + i,
                       (B.color(i) == WHITE) == (B.spots(i) == 0));
            if (B.color(i) != WHITE) {
                c += 1;
            }
        }
        assertEquals("extra squares filled", contents.length / 4, c);
    }

    /** Source of output for game. */
    private Writer output;
    /** Instance of a jump61 game. */
    private Game game;

}
