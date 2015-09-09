package jump61;

import java.util.Formatter;
import java.util.HashMap;
import java.util.ArrayList;

import static jump61.Color.*;

/** Represents the state of a Jump61 game.  Squares are indexed either by
 *  row and column (between 1 and size()), or by square number, numbering
 *  squares by rows, with squares in row 1 numbered 0 - size()-1, in
 *  row 2 numbered size() - 2*size() - 1, etc.
 *  @author Brian Truong.
 */
abstract class Board {

    /** Number of blue spots on the board. */
    private int _blueCount = 0;
    /** Number of red spots on the board. */
    private int _redCount = 0;
    /** Two-dimensional int array that is the size of an NxN board
     *  and has access to each square via Rows and Columns.
     *  i.e. rcContainer[0][0] will yield square 0. */
    private int[][] rcContainer;
    /** HashMap that has squares as keys and SpotsAndColors objects
     *  as their respective values. SpotsAndColors is a class that
     *  stores and has access to the square's current spot count
     *  and the color of the square. */
    private HashMap<Integer, SpotsAndColors> _squares;
    /** True iff there is a winner on the board. */
    private boolean _haswinner = false;
    /** Holds numMoves in constant boards for the undo function. */
    private int constantMoves;

    /** (Re)initialize me to a cleared board with N squares on a side. Clears
     *  the undo history and sets the number of moves to 0. */
    void clear(int N) {
        unsupported("clear");
    }

    /** Copy the contents of BOARD into me. */
    void copy(Board board) {
        unsupported("copy");
    }

    /** Instantiates the Container and HashMap to be used for
     *  storing squares in ConstantBoards, which will be stored
     *  in the undo history. */
    void setupDefault() {
        _squares = new HashMap<Integer, SpotsAndColors>();
        rcContainer = new int[size()][size()];
    }

    /** Takes in a size N and sets all default datastructures to this size.
     *  Additionally, clears any winners and defaults the entire board. */
    void setFresh(int N) {
        _squares = new HashMap<Integer, SpotsAndColors>();
        rcContainer = new int[N][N];
        setHasWinner(false);
        int count = 0;
        for (int i = 0; i < N; i += 1) {
            for (int k = 0; k < N; k += 1) {
                rcContainer[i][k] = count;
                _squares.put(count, new SpotsAndColors(WHITE, 0));
                count += 1;
            }
        }
    }

    /** Returns the Color of the player who would be next to move.  If the
     *  game is won, this will return the loser (assuming legal position). */
    Color whoseMove() {
        if (numMoves() % 2 == 1) {
            return RED;
        } else {
            return BLUE;
        }
    }

    /** Returns the number of red squares on the board. */
    int getRed() {
        return _redCount;
    }

    /** Returns the number of blue squares on the board. */
    int getBlue() {
        return _blueCount;
    }

    /** Recounts the board for blue squares and sets _bluecount. */
    void setBlueCount() {
        _blueCount = 0;
        for (int i = 0; i < size() * size(); i += 1) {
            if (_squares.get(i).getColor() == BLUE) {
                _blueCount += 1;
            }
        }
    }

    /** Recounts the board for red squares and sets _redcount. */
    void setRedCount() {
        _redCount = 0;
        for (int i = 0; i < size() * size(); i += 1) {
            if (_squares.get(i).getColor() == RED) {
                _redCount += 1;
            }
        }
    }

    /** Recounts both colors on the board and resets their respective
     *  counts. */
    void reCountColors() {
        setBlueCount();
        setRedCount();
    }

    /** Returns the HashMap that contains squares as keys
     *  and SpotsAndColors as values (contains num of spots
     *  and the square's color). */
    HashMap<Integer, SpotsAndColors> getSquares() {
        return _squares;
    }

    /** Returns the int array rcContainer that contains
     *  all squares relatives to row x column. */
    int[][] getrcContainer() {
        return rcContainer;
    }

    /** Sets whether or not there is a winner to INPUT. */
    void setHasWinner(boolean input) {
        _haswinner = input;
    }

    /** Returns a boolean TRUE iff there is a winner, else FALSE.*/
    boolean getHasWinner() {
        return _haswinner;
    }

    /** Given a COLOR, return the count of that color on the
     *  current board. */
    int getColorCount(Color color) {
        if (color == RED) {
            return _redCount;
        } else {
            return _blueCount;
        }
    }

    /** Store MOVES into constantMoves. Used in the undo function. */
    void storeConstantMoves(int moves) {
        constantMoves = moves;
    }

    /** Returns the stored moves inside constant boards that are
     *  used in the undo function. */
    int getConstantMoves() {
        return constantMoves;
    }

    /** Return the number of rows and of columns of THIS. */
    abstract int size();

    /** Returns the number of spots in the square at row R, column C,
     *  1 <= R, C <= size (). */
    abstract int spots(int r, int c);

    /** Returns the number of spots in square #N. */
    abstract int spots(int n);

    /** Returns the color of square #N, numbering squares by rows, with
     *  squares in row 1 number 0 - size()-1, in row 2 numbered
     *  size() - 2*size() - 1, etc. */
    abstract Color color(int n);

    /** Returns the color of the square at row R, column C,
     *  1 <= R, C <= size(). */
    abstract Color color(int r, int c);

    /** Returns the total number of moves made (red makes the odd moves,
     *  blue the even ones). */
    abstract int numMoves();

    /** Return true iff row R and column C denotes a valid square. */
    final boolean exists(int r, int c) {
        return 1 <= r && r <= size() && 1 <= c && c <= size();
    }

    /** Return true iff S is a valid square number. */
    final boolean exists(int s) {
        int N = size();
        return 0 <= s && s < N * N;
    }

    /** Return the row number for square #N. */
    final int row(int n) {
        return n / size();
    }

    /** Return the column number for square #N. */
    final int col(int n) {
        return n % size();
    }

    /** Return the square number of row R, column C. */
    final int sqNum(int r, int c) {
        return rcContainer[r][c];
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
        to square at row R, column C. */
    boolean isLegal(Color player, int r, int c) {
        int pos = rcContainer[r - 1][c - 1];
        Color current = color(r, c);
        if (_squares.get(pos).getSpots() <= neighbors(pos)) {
            return (whoseMove() == player
                && (current == WHITE || current == player));
        } else {
            return false;
        }
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
     *  to square #N. */
    boolean isLegal(Color player, int n) {
        Color current = color(n);
        if (_squares.get(n).getSpots() <= neighbors(n)) {
            return (current == WHITE || current == player);
        } else {
            return false;
        }
    }

    /** Returns true iff PLAYER is allowed to move at this point. */
    boolean isLegal(Color player) {
        return whoseMove() == player;
    }

    /** Returns the winner of the current position, if the game is over,
     *  and otherwise null. */
    final Color getWinner() {
        if (_redCount == size() * size()) {
            return RED;
        } else if (_blueCount == size() * size()) {
            return BLUE;
        } else {
            return null;
        }
    }

    /** Return the number of squares of given COLOR. */
    abstract int numOfColor(Color color);

    /** Add a spot from PLAYER at row R, column C.  Assumes
     *  isLegal(PLAYER, R, C). */
    void addSpot(Color player, int r, int c) {
        unsupported("addSpot");
    }

    /** Add a spot from PLAYER at square #N.  Assumes isLegal(PLAYER, N). */
    void addSpot(Color player, int n) {
        unsupported("addSpot");
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white).  Clear the undo
     *  history. */
    void set(int r, int c, int num, Color player) {
        unsupported("set");
    }

    /** Set the square #N to NUM spots (0 <= NUM), and give it color PLAYER
     *  if NUM > 0 (otherwise, white).  Clear the undo history. */
    void set(int n, int num, Color player) {
        unsupported("set");
    }

    /** Set the current number of moves to N.  Clear the undo history. */
    void setMoves(int n) {
        unsupported("setMoves");
    }

    /** Undo the effects one move (that is, one addSpot command).  One
     *  can only undo back to the last point at which the undo history
     *  was cleared, or the construction of this Board. */
    void undo() {
        unsupported("undo");
    }

    /** Boolean that takes in a square N and returns TRUE iff the square
     *  needs to be jumped, else FALSE. */
    boolean performJumps(int n) {
        SpotsAndColors currentsq = _squares.get(n);
        Color spreadcolor = currentsq.getColor();
        return (currentsq.getSpots() > neighbors(n));
    }

    /** Returns a string representation, of a color INPUT,
     *  that is a single character string used for toString.*/
    String colorFormatter(Color input) {
        if (input == RED) {
            return "r";
        } else if (input == BLUE) {
            return "b";
        } else {
            return "-";
        }
    }

    /** Returns a string representation, of a SpotsAndColors VALUES,
     *  that displays both colors and spots in a single character
     *  string that is used for toString. */
    String squareFormatter(SpotsAndColors values) {
        String spots = "";
        if (values.getSpots() == 0) {
            spots += "-";
        } else {
            spots += values.getSpots();
        }
        String color = colorFormatter(values.getColor());
        return spots + color;
    }

    /** Returns my dumped representation. */
    @Override
    public String toString() {
        ArrayList<String> container = new ArrayList<String>();
        String boardline = String.format("%4s", "");
        Formatter out = new Formatter();
        container.add("===");
        for (int i = 0; i < size() * size(); i += 1) {
            boardline += squareFormatter(_squares.get(i));
            if ((i + 1) % size() == 0) {
                container.add(boardline);
                boardline = String.format("%4s", "");
            } else {
                boardline += String.format("%1s", "");
            }
        }
        for (String x : container) {
            out.format("%s%n", x);
        }
        out.format("%s", "===");
        return out.toString();
    }

    /** Returns the number of neighbors of the square at row R, column C. */
    int neighbors(int r, int c) {
        int count = 4;
        if (c == 1) {
            count -= 1;
        }
        if (c == size()) {
            count -= 1;
        }
        if (r == 1) {
            count -= 1;
        }
        if (r == size()) {
            count -= 1;
        }
        return count;
    }

    /** Returns the number of neighbors of square #N. */
    int neighbors(int n) {
        int count = 4;
        if (col(n) == 0) {
            count -= 1;
        }
        if (col(n) == size() - 1) {
            count -= 1;
        }
        if (row(n) == 0) {
            count -= 1;
        }
        if (row(n) == size() - 1) {
            count -= 1;
        }
        return count;
    }

    /** Indicate fatal error: OP is unsupported operation. */
    private void unsupported(String op) {
        String msg = String.format("'%s' operation not supported", op);
        throw new UnsupportedOperationException(msg);
    }

    /** The length of an end of line on this system. */
    private static final int NL_LENGTH =
        System.getProperty("line.separator").length();

    /** Represents the values of each square, which includes the
     *  square's color and the number of spots in the square.
     *  Each square on the board has its own instance of this class. */
    class SpotsAndColors {
        /** Number of spots in the square. */
        private int _spots;
        /** Color of the square. */
        private Color _color;

        /** Takes in COLOR and SPOTS and stores them in _spots
         *  and _color. */
        SpotsAndColors(Color color, int spots) {
            _spots = spots;
            _color = color;
        }

        /** Sets the current color of the square to COLOR. */
        void setColor(Color color) {
            _color = color;
        }

        /** Returns the current color of the square. */
        Color getColor() {
            return _color;
        }

        /** Sets the current number of spots of the square to SPOTS. */
        void setSpots(int spots) {
            _spots = spots;
        }

        /** Returns the current number of spots in the square. */
        int getSpots() {
            return _spots;
        }

        /** Add a single spot to the square. */
        void addOne() {
            _spots += 1;
        }
    }
}
