package jump61;

/** A Player that gets its moves from manual input.
 *  @author Brian Truong.
 */
class HumanPlayer extends Player {

    /** A new player initially playing COLOR taking manual input of
     *  moves from GAME's input source. */
    HumanPlayer(Game game, Color color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        int[] storemove = new int[2];
        Game game = getGame();
        Board board = getBoard();
        if (!game.getQuit()) {
            game.getMove(storemove);
            game.makeMove(storemove[0], storemove[1]);
        }
    }
}
