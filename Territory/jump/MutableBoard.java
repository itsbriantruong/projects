package jump61;


import static jump61.Color.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;

/** A Jump61 board state.
 *  @author Brian Truong.
 */
class MutableBoard extends Board {

    /** Total combined number of moves by both sides. */
    protected int _moves;
    /** Convenience variable: size of board (squares along one edge). */
    private int _N;
    /** Jump boolean that ensures that no neighbors are jumped until
     *  the original jumping call is concluded. When set to true,
     *  overfull neighbors will be stored in prevboard. */
    private boolean _jumping = false;
    /** List that holds all overfull neighbors that are in queue to be
     *  jumped. */
    private ArrayList<Integer> overfull = new ArrayList<Integer>();
    /** Board to which all operations delegated. */
    private Board _board;
    /** LinkedList that stores all previous boards to be used in
     *  the undo function. */
    private LinkedList<Board> prevboard = new LinkedList<Board>();

    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        _N = N;
        setFresh(N);
        _moves = 1;
    }

    /** Copy function that takes in a Board (generally constantboards)
     *  CONSTANT and sets up the datastructures of the board to default.
     *  This allows the board to store all the squares so that the
     *  board can be stored in the undo history. */
    void copyOver(Board constant) {
        constant.setupDefault();
        int count = 0;
        for (int i = 0; i < constant.size(); i += 1) {
            for (int k = 0; k < constant.size(); k += 1) {
                getrcContainer()[i][k] = count;
                constant.getSquares().put(count,
                    new SpotsAndColors(constant.color(count),
                        constant.spots(count)));
                count += 1;
            }
        }
        constant.storeConstantMoves(this.numMoves());
    }

    /** A board whose initial contents are copied from BOARD0. Clears the
     *  undo history. */
    MutableBoard(Board board0) {
        _board = board0;
        setFresh(board0.size());
        copy(board0);
        reCountColors();
        prevboard.clear();
    }

    @Override
    void clear(int N) {
        setFresh(N);
        _N = N;
        _moves = 1;
        reCountColors();
        setHasWinner(false);
        prevboard.clear();
    }

    /** Special copy function for undo HISTORY. Takes in a board from
     *  prevboard arraylist and copies its contents over to me.
     *  Goes back one board stored. */
    void undocopy(Board history) {
        HashMap<Integer, SpotsAndColors> transfer = history.getSquares();
        for (int i = 0; i < history.size() * history.size(); i += 1) {
            getSquares().put(i, transfer.get(i));
        }
        reCountColors();
        setHasWinner(false);
        _N = history.size();
        _moves = history.getConstantMoves();
    }

    @Override
    void copy(Board blueprint) {
        for (int i = 0; i < blueprint.size() * blueprint.size(); i += 1) {
            getSquares().put(i,
                new SpotsAndColors(blueprint.color(i), blueprint.spots(i)));
        }
        _N = blueprint.size();
        _moves = blueprint.numMoves();
    }

    @Override
    int size() {
        return _N;
    }

    @Override
    int spots(int r, int c) {
        int pos = getrcContainer()[r - 1][c - 1];
        return (getSquares().get(pos)).getSpots();
    }

    @Override
    int spots(int n) {
        return (getSquares().get(n)).getSpots();
    }

    @Override
    Color color(int r, int c) {
        int pos = getrcContainer()[r - 1][c - 1];
        return (getSquares().get(pos)).getColor();
    }

    @Override
    Color color(int n) {
        return (getSquares().get(n)).getColor();
    }

    @Override
    int numMoves() {
        return _moves;
    }

    @Override
    int numOfColor(Color color) {
        int result = 0;
        if (color == RED) {
            result = getRed();
        } else if (color == BLUE) {
            result = getBlue();
        } else if (color == WHITE) {
            result = _N * _N - (getRed() + getBlue());
        }
        return result;
    }

    @Override
    void addSpot(Color player, int r, int c) {
        if (!_jumping) {
            Board container = new ConstantBoard(this);
            copyOver(container);
            prevboard.add(container);
        }
        int pos = getrcContainer()[r - 1][c - 1];
        SpotsAndColors currentsq = getSquares().get(pos);
        if (currentsq.getColor() != player) {
            if (currentsq.getColor() == WHITE) {
                currentsq.setColor(player);
            } else if ((currentsq.getColor() == player.opposite())
                && _jumping) {
                currentsq.setColor(player);
            }
        }
        currentsq.addOne();
        if (performJumps(pos)) {
            if (_jumping) {
                for (int i = 0; i < overfull.size(); i += 1) {
                    if (overfull.get(i) == pos) {
                        overfull.remove(i);
                    }
                }
                overfull.add(pos);
            } else {
                jump(pos);
            }
        }
        if (numOfColor(player) == _N * _N) {
            setHasWinner(true);
        }
        if (!_jumping) {
            _moves += 1;
        }
        reCountColors();
    }

    @Override
    void addSpot(Color player, int n) {
        if (!_jumping) {
            Board container = new ConstantBoard(this);
            copyOver(container);
            prevboard.add(container);
        }
        SpotsAndColors currentsq = getSquares().get(n);
        if (currentsq.getColor() != player) {
            if (currentsq.getColor() == WHITE) {
                currentsq.setColor(player);
            } else if ((currentsq.getColor() == player.opposite())
                && _jumping) {
                currentsq.setColor(player);
            }
        }
        currentsq.addOne();
        if (performJumps(n)) {
            if (_jumping) {
                for (int i = 0; i < overfull.size(); i += 1) {
                    if (overfull.get(i) == n) {
                        overfull.remove(i);
                    }
                }
                overfull.add(n);
            } else {
                jump(n);
            }
        }
        if (numOfColor(player) == _N * _N) {
            setHasWinner(true);
        }
        if (!_jumping) {
            _moves += 1;
        }
        reCountColors();
    }

    @Override
    void set(int r, int c, int num, Color player) {
        int pos = getrcContainer()[r - 1][c - 1];
        SpotsAndColors currentsq = getSquares().get(pos);
        currentsq.setSpots(num);
        if (currentsq.getColor() == WHITE && player != WHITE && num > 0) {
            currentsq.setColor(player);
        } else if (currentsq.getColor() == player.opposite() && num > 0) {
            currentsq.setColor(player);
        }
        if (num == 0) {
            currentsq.setColor(WHITE);
        }
        reCountColors();
        prevboard.clear();
    }

    @Override
    void set(int n, int num, Color player) {
        SpotsAndColors currentsq = getSquares().get(n);
        currentsq.setSpots(num);
        if (currentsq.getColor() == WHITE && player != WHITE && num > 0) {
            currentsq.setColor(player);
        } else if (currentsq.getColor() == player.opposite() && num > 0) {
            currentsq.setColor(player);
        }
        if (num == 0) {
            currentsq.setColor(WHITE);
        }
        reCountColors();
        prevboard.clear();
    }

    @Override
    void setMoves(int num) {
        assert num > 0;
        _moves = num;
        prevboard.clear();
    }

    @Override
    void undo() {
        if (prevboard.peekLast() != null) {
            undocopy(prevboard.removeLast());
        }
    }

    /** Do all jumping on this board, assuming that initially, S is the only
     *  square that might be over-full. Jumping boolean makes sure that
     *  cascades (explosions) do not happens until the initial jump is conluded
     *  in order to prevent conflicts and stackoverflow. */
    private void jump(int S) {
        int x = row(S);
        int y = col(S);
        SpotsAndColors currentsq = getSquares().get(S);
        Color spreadcolor = currentsq.getColor();
        if (currentsq.getSpots() > neighbors(S) && !getHasWinner()) {
            _jumping = true;
            if (!(y == 0)) {
                addSpot(spreadcolor, sqNum(x, y - 1));
            }
            if (!(y == _N - 1)) {
                addSpot(spreadcolor, sqNum(x, y + 1));
            }
            if (!(x == 0)) {
                addSpot(spreadcolor, sqNum(x - 1, y));
            }
            if (!(x == _N - 1)) {
                addSpot(spreadcolor, sqNum(x + 1, y));
            }
            currentsq.setSpots(currentsq.getSpots() - neighbors(S));
            _jumping = false;
            cascade();
        }
    }

    /** Does all the jumping of overfull neighbors that resulted from
     *  the initial call to jump. Performs cascades by looking at
     *  arraylist overfull, which holds the squares that needs to be jumped.
     *  Stores and removes elements from overfull as necessary. Cascading
     *  is only activated once the original jump call has concluded. */
    void cascade() {
        while (overfull.size() > 0 && !getHasWinner()) {
            int curr = overfull.get(0);
            int x = row(curr);
            int y = col(curr);
            SpotsAndColors currentsq = getSquares().get(curr);
            Color spreadcolor = currentsq.getColor();
            if (currentsq.getSpots() > neighbors(curr)) {
                _jumping = true;
                if (!(y == 0)) {
                    addSpot(spreadcolor, sqNum(x, y - 1));
                }
                if (!(y == _N - 1)) {
                    addSpot(spreadcolor, sqNum(x, y + 1));
                }
                if (!(x == 0)) {
                    addSpot(spreadcolor, sqNum(x - 1, y));
                }
                if (!(x == _N - 1)) {
                    addSpot(spreadcolor, sqNum(x + 1, y));
                }
                currentsq.setSpots(currentsq.getSpots() - neighbors(curr));
                _jumping = false;
                overfull.remove(0);
            }
        }
        overfull.clear();
    }
}
