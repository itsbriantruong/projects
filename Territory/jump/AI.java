package jump61;

import java.util.ArrayList;

/** An automated Player.
 *  @author Brian Truong.
 */
class AI extends Player {

    /** Depth that the minimax function travels. */
    private int _depthlevel;

    /** The square of my next move. */
    private int square = -1;

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Color color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        Game game = getGame();
        Board board = game.getMutBoard();
        if (!board.getHasWinner()) {
            setUp();
            int r = board.row(square);
            int c = board.col(square);
            game.makeMove(square);
            game.getOut().format("%s moves %d %d.%n",
                getColor().toCapitalizedString(), (r + 1), (c + 1));
            game.getOut().flush();
        }
    }

    /** Set up my board to testing mode and run the minimax algorithm
     *  to find the squares with the best score (given they are within
     *  the numerous valid options). */
    void setUp() {
        Game game = getGame();
        Board board = game.getMutBoard();
        _depthlevel = 4;
        ArrayList<Integer> options = checkMoves(board, getColor());
        int n = minmax(getColor(), board, _depthlevel,
            -Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /** Returns a list of all possible moves for COLOR given
     *  the BOARD that will take in the next move. */
    ArrayList<Integer> checkMoves(Board board, Color color) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < board.size() * board.size(); i += 1) {
            if (board.isLegal(color, i)) {
                result.add(i);
            }
        }
        while (result.size() > 9) {
            result.remove(getGame().randInt(result.size()));
        }
        return result;
    }

    /** Minimax algorithm with alpha beta pruning that takes in
     *  the COLOR, current BOARD, level of DEPTH and ALPHA and BETA
     *  values, which are originally called on with -INF to +INF.
     *  Returns the optimal score and sets square to the square that was
     *  found that have this score. */
    private int minmax(Color color, Board board, int depth,
                        int alpha, int beta) {
        int bestSquare = -1;
        if (depth == 0 || board.getHasWinner()) {
            board.setHasWinner(false);
            return staticEval(color, board);
        }
        if (color == getColor()) {
            ArrayList<Integer> allmoves = checkMoves(board, color);
            for (int move : allmoves) {
                int score;
                board.addSpot(color, move);
                score = minmax(color.opposite(), board, depth - 1,
                        alpha, beta);
                if (score > alpha) {
                    alpha = score;
                    bestSquare = move;
                }
                if (alpha >= beta) {
                    board.undo();
                    break;
                }
                board.undo();
            }
            square = bestSquare;
            return alpha;
        } else {
            ArrayList<Integer> allmoves = checkMoves(board, color);
            for (int move : allmoves) {
                int score;
                board.addSpot(color, move);
                score = minmax(color.opposite(), board, depth - 1,
                        alpha, beta);
                if (score < beta) {
                    beta = score;
                    bestSquare = move;
                }
                if (alpha >= beta) {
                    board.undo();
                    break;
                }
                board.undo();
            }
            square = bestSquare;
            return beta;
        }
    }

    /** Returns the number of full squares of a COLOR
     *  on a particular BOARD.*/
    int countFullSquare(Color color, Board board) {
        int count = 0;
        for (int i = 0; i < board.size() * board.size(); i += 1) {
            if (board.color(i) == color) {
                if (board.neighbors(i) == board.spots(i)) {
                    count += 1;
                }
            }
        }
        return count;
    }

    /** Returns heuristic value of board BOARD for player COLOR.
     *  Higher is better for COLOR. */
    private int staticEval(Color color, Board board) {
        int result = 0;
        if (board.getWinner() == color) {
            return (9 * 9 * 9 * 9 * 9);
        }
        int mysquares = board.numOfColor(color);
        int opponentsq = board.numOfColor(color.opposite());
        int myfull = countFullSquare(color, board);
        int oppofull = countFullSquare(color.opposite(), board);
        int myscore = mysquares + myfull;
        int opposcore = opponentsq + oppofull;
        result = myscore - opposcore;
        return result;
    }
}


