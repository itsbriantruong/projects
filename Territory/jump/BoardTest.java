package jump61;

import static jump61.Color.*;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of Boards.
 *  @author
 */
public class BoardTest {

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testSize() {
        Board B = new MutableBoard(5);
        assertEquals("bad length", 5, B.size());
        ConstantBoard C = new ConstantBoard(B);
        assertEquals("bad length", 5, C.size());
        Board D = new MutableBoard(C);
        assertEquals("bad length", 5, C.size());
    }

    @Test
    public void testSet() {
        Board B = new MutableBoard(5);
        B.set(2, 2, 1, RED);
        B.setMoves(1);
        assertEquals("wrong number of spots", 1, B.spots(2, 2));
        assertEquals("wrong color", RED, B.color(2, 2));
        assertEquals("wrong count", 1, B.numOfColor(RED));
        assertEquals("wrong count", 0, B.numOfColor(BLUE));
        assertEquals("wrong count", 24, B.numOfColor(WHITE));
    }

    @Test
    public void testMove() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 1, 1);
        checkBoard("#1", B, 1, 1, 1, RED);
        B.addSpot(BLUE, 2, 1);
        checkBoard("#2", B, 1, 1, 1, RED, 2, 1, 1, BLUE);
        B.addSpot(RED, 1, 1);
        checkBoard("#3", B, 1, 1, 2, RED, 2, 1, 1, BLUE);
        B.addSpot(BLUE, 2, 1);
        checkBoard("#4", B, 1, 1, 2, RED, 2, 1, 2, BLUE);
        B.addSpot(RED, 1, 1);
        checkBoard("#5", B, 1, 1, 1, RED, 2, 1, 3, RED, 1, 2, 1, RED);
        B.undo();
        checkBoard("#4U", B, 1, 1, 2, RED, 2, 1, 2, BLUE);
        B.undo();
        checkBoard("#3U", B, 1, 1, 2, RED, 2, 1, 1, BLUE);
        B.undo();
        checkBoard("#2U", B, 1, 1, 1, RED, 2, 1, 1, BLUE);
        B.undo();
        checkBoard("#1U", B, 1, 1, 1, RED);
    }

    @Test
    public void testJump1() {
        Board B = new MutableBoard(6);
        B.set(1, 1, 2, RED);
        B.set(1, 2, 3, BLUE);
        B.addSpot(RED, 1, 1);
        checkBoard("Jump #1", B, 1, 1, 2, RED, 1, 2, 1, RED,
            1, 3, 1, RED, 2, 1, 1, RED, 2, 2, 1, RED);
    }

    @Test
    public void testJump2() {
        Board B = new MutableBoard(6);
        B.set(4, 4, 4, RED);
        B.set(1, 1, 1, BLUE);
        B.addSpot(RED, 4, 4);
        checkBoard("Corner jump test", B, 1, 1, 1, BLUE, 4, 4, 1, RED,
            4, 5, 1, RED, 4, 3, 1, RED, 3, 4, 1, RED, 5, 4, 1, RED);
    }

    @Test
    public void testSettings1() {
        Board B = new MutableBoard(6);
        B.set(4, 4, 4, RED);
        assertEquals("Wrong red count", 1, B.getRed());
        B.set(1, 1, 1, BLUE);
        assertEquals("Wrong move count", 1, B.numMoves());
    }

    @Test
    public void testSettings2() {
        Board B = new MutableBoard(6);
        B.set(4, 4, 4, RED);
        B.set(4, 4, 0, RED);
        assertEquals("0 for set should reset the square color",
            WHITE, B.color(4, 4));
        assertEquals("Square should have zero spots", 0, B.spots(4, 4));
    }

    @Test
    public void testSettings3() {
        Board B = new MutableBoard(6);
        B.set(4, 4, 4, RED);
        B.set(4, 4, 0, RED);
        assertEquals("0 for set should reset the square color",
            WHITE, B.color(4, 4));
        assertEquals("Square should have zero spots", 0, B.spots(4, 4));
    }

    @Test
    public void testClearBoard() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 1);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 1);
        B.addSpot(RED, 1, 1);
        B.clear(B.size());
        assertEquals("wrong count", 0, B.getRed());
        assertEquals("wrong count", 0, B.getBlue());
        assertEquals("size should not change", 6, B.size());
        assertEquals("move should be set to 1", 1, B.numMoves());
    }

    @Test
    public void testResizeBoard() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 1);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 1);
        B.addSpot(RED, 1, 1);
        B.clear(3);
        assertEquals("wrong count", 0, B.getRed());
        assertEquals("wrong count", 0, B.getBlue());
        assertEquals("size should change", 3, B.size());
        assertEquals("move should be set to 1", 1, B.numMoves());
    }

    @Test
    public void testCopyBoard() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 1);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 1);
        B.addSpot(RED, 1, 1);
        Board S = new MutableBoard(B);
        assertEquals("wrong count", B.getRed(), S.getRed());
        assertEquals("wrong count", B.getBlue(), S.getBlue());
        assertEquals("size should be the same as Board B", B.size(), S.size());
        assertEquals("move should be set to 1", B.numMoves(), S.numMoves());
        assertEquals("incorrect spots", B.spots(1, 1), S.spots(1, 1));
        assertEquals("incorrect color", B.color(2, 1), S.color(2, 1));
    }

    @Test
    public void testWhoseMove() {
        Board B = new MutableBoard(6);
        B.setMoves(2);
        assertEquals("wrong person playing", BLUE, B.whoseMove());
        B.setMoves(3);
        assertEquals("wrong person playing", RED, B.whoseMove());
    }

    @Test
    public void testNeighbors() {
        Board B = new MutableBoard(6);
        assertEquals("wrong count", 2, B.neighbors(1, 1));
        assertEquals("wrong count", 4, B.neighbors(4, 4));
    }

    @Test
    public void testNeighbors2() {
        Board B = new MutableBoard(6);
        assertEquals("wrong count", 2, B.neighbors(0));
        assertEquals("wrong count", 3, B.neighbors(1));
    }

    @Test
    public void testSqNum() {
        Board B = new MutableBoard(6);
        assertEquals("wrong square num", 1, B.sqNum(1 - 1, 2 - 1));
        assertEquals("wrong square num", 7, B.sqNum(2 - 1, 2 - 1));
    }

    @Test
    public void testIsLegal() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 1);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 1);
        B.setMoves(2);
        assertEquals("check legality", false, B.isLegal(BLUE, 1, 1));
        assertEquals("check legality", true, B.isLegal(BLUE, 2, 1));
        B.setMoves(1);
        assertEquals("check legality", true, B.isLegal(RED, 1, 1));
        assertEquals("check legality", false, B.isLegal(RED, 2, 1));
    }

    @Test
    public void testIsLegal2() {
        Board B = new MutableBoard(6);
        B.set(1, 1, 2, RED);
        B.set(1, 2, 3, BLUE);
        assertEquals("player can move at this point", true,
            B.isLegal(RED));
        assertEquals("player cannot move at this point", false,
            B.isLegal(BLUE));
    }

    @Test
    public void testFreshBoard() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 2, 3);
        B.addSpot(BLUE, 2, 1);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 1);
        B.addSpot(RED, 2, 4);
        B.setFresh(6);
        assertEquals("spot should be cleared", 0, B.spots(2, 3));
        assertEquals("spot should be white", WHITE, B.color(2, 1));
    }

    @Test
    public void testGetWinner() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 2, 3);
        B.addSpot(BLUE, 2, 1);
        B.addSpot(RED, 1, 1);
        assertEquals("there is no winner", null, B.getWinner());
        assertEquals("there are two reds", 2, B.numOfColor(RED));
        assertEquals("there is one blue", 1, B.numOfColor(BLUE));
    }

    @Test
    public void testNumOfColor() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 3);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 4);
        B.addSpot(RED, 1, 1);
        assertEquals("there are three reds", 3, B.numOfColor(RED));
        assertEquals("there are two blues", 2, B.numOfColor(BLUE));
        B.clear(3);
        assertEquals("board was cleared", 0, B.numOfColor(RED));
        assertEquals("board was cleared", 0, B.numOfColor(BLUE));
    }

    @Test
    public void testColorFormatter() {
        Board B = new MutableBoard(6);
        String result1 = B.colorFormatter(RED);
        String result2 = B.colorFormatter(BLUE);
        String result3 = B.colorFormatter(WHITE);
        assertEquals("wrong representation", "r", result1);
        assertEquals("wrong representation", "b", result2);
        assertEquals("wrong representation", "-", result3);
    }

    @Test
    public void testPerformJumps() {
        Board B = new MutableBoard(6);
        B.set(1, 1, 3, RED);
        assertEquals("jump should be true", true,
            B.performJumps(0));
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

}
