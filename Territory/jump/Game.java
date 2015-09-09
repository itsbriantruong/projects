package jump61;

import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.ArrayList;

import java.util.Scanner;
import java.util.Random;

import static jump61.Color.*;

/** Main logic for playing (a) game(s) of Jump61.
 *  @author Brian Truong.
 */
class Game {

    /** The current player who has the next move. */
    private Color _player = RED;

    /** HashMap that stores RED and BLUE as keys and their values as
     *  as booleans that represents whether or not the color is
     *  currently an AI (automated). */
    private HashMap<Color, Boolean> automation = new HashMap<Color, Boolean>();

    /** HashMap that stores RED and BLUE as keys and their values as
     *  instances of the Player Class. Allows switching between instances
     *  of HumanPlayer and AI player under the same key. */
    private HashMap<Color, Player> control = new HashMap<Color, Player>();

    /** Quit command that is true iff the program is to be closed down. */
    private boolean _quit = false;

    /** Name of resource containing help message. */
    private static final String HELP = "jump61/Help.txt";

    /** A new Game that takes command/move input from INPUT, prints
     *  normal output on OUTPUT, prints prompts for input on PROMPTS,
     *  and prints error messages on ERROROUTPUT. The Game now "owns"
     *  INPUT, PROMPTS, OUTPUT, and ERROROUTPUT, and is responsible for
     *  closing them when its play method returns. */
    Game(Reader input, Writer prompts, Writer output, Writer errorOutput) {
        _board = new MutableBoard(Defaults.BOARD_SIZE);
        _readonlyBoard = new ConstantBoard(_board);
        _prompter = new PrintWriter(prompts, true);
        _inp = new Scanner(input);
        _inp.useDelimiter("(?m)\\p{Blank}*$|^\\p{Blank}*|\\p{Blank}+");
        _out = new PrintWriter(output, true);
        _err = new PrintWriter(errorOutput, true);
        _playing = false;
    }

    /** Returns a readonly view of the game board.  This board remains valid
     *  throughout the session. */
    Board getBoard() {
        return _readonlyBoard;
    }

    /** Returns the mutable board for this game. */
    Board getMutBoard() {
        return _board;
    }

    /** Returns the PrinterWriter out for this game. */
    PrintWriter getOut() {
        return _out;
    }

    /** Play a session of Jump61.  This may include multiple games,
     *  and proceeds until the user exits.  Returns an exit code: 0 is
     *  normal; any positive quantity indicates an error.  */
    int play() {
        _out.println("Welcome to " + Defaults.VERSION);
        _out.flush();
        control.put(RED, new HumanPlayer(this, RED));
        control.put(BLUE, new AI(this, BLUE));
        automation.put(RED, false);
        automation.put(BLUE, true);
        while (automation.get(_player) || promptForNext()) {
            if (_playing) {
                if (automation.get(_player) || _inp.hasNextInt()) {
                    if (_quit) {
                        break;
                    }
                    (control.get(_player)).makeMove();
                    checkForWin();
                    if (_playing) {
                        _player = _board.whoseMove();
                    }
                } else {
                    readExecuteCommand();
                    if (_quit) {
                        break;
                    }
                }
            } else {
                if (automation.get(_player)) {
                    promptForNext();
                }
                readExecuteCommand();
                if (_quit) {
                    break;
                }
            }
        }
        _prompter.close();
        _inp.close();
        _out.close();
        _err.close();
        return 0;
    }

    /** Get a move from my input and place its row and column in
     *  MOVE. Used for manual players. */
    void getMove(int[] move) {
        if (_move[0] == 0) {
            readExecuteCommand();
            if (_move[0] == 0 && !_quit) {
                while (_playing && _move[0] == 0 && promptForNext()) {
                    readExecuteCommand();
                    if (_quit) {
                        break;
                    }
                }
            }
        }
        if (_move[0] > 0 && !_quit) {
            move[0] = _move[0];
            move[1] = _move[1];
            _move[0] = 0;
        }
    }

    /** Returns the HashMap automation. Used for testing. */
    HashMap<Color, Boolean> getAutomation() {
        return automation;
    }

    /** Returns true iff _quit mode is on. */
    boolean getQuit() {
        return _quit;
    }

    /** Returns true iff _playing. Used for testing. */
    boolean getPlaying() {
        return _playing;
    }

    /** Switch the current player looking at moves. */
    void setPlayer() {
        _player = _board.whoseMove();
    }

    /** Begin accepting moves for game.  If the game is won,
     *  immediately print a win message and end the game. */
    void restartPlaying() {
        _playing = true;
    }

    /** Returns the current player. */
    Color getPlayer() {
        return _player;
    }

    /** Add a spot to R C, if legal to do so. */
    void makeMove(int r, int c) {
        if (_board.isLegal(_player, r, c)) {
            _board.addSpot(_player, r, c);
        }
    }

    /** Add a spot to square #N, if legal to do so. */
    void makeMove(int n) {
        if (_board.isLegal(_player, n)) {
            _board.addSpot(_player, n);
        }
    }

    /** Return a random integer in the range [0 .. N), uniformly
     *  distributed.  Requires N > 0. */
    int randInt(int n) {
        return _random.nextInt(n);
    }

    /** Send a message to the user as determined by FORMAT and ARGS, which
     *  are interpreted as for String.format or PrintWriter.printf. */
    void message(String format, Object... args) {
        _out.printf(format, args);
        _out.flush();
    }

    /** Check whether we are playing and there is an unannounced winner.
     *  If so, announce and stop play. */
    private void checkForWin() {
        if (_board.getHasWinner()) {
            announceWinner();
            _playing = false;
        }
    }

    /** Send announcement of winner to my user output. */
    private void announceWinner() {
        _out.println(_board.getWinner().toCapitalizedString() + " wins.");
    }

    /** Make PLAYER an AI for subsequent moves. */
    private void setAuto(String player) {
        if (player.equals("RED") || player.equals("BLUE")) {
            Color colorplayer = parseColor(player);
            control.put(colorplayer, new AI(this, colorplayer));
            automation.put(colorplayer, true);
        } else {
            reportError("syntax error in 'auto' command");
        }
    }

    /** Duplicate of setAuto with PLAYER used for testing purposes. */
    void testSetAuto(String player) {
        if (player.equals("RED") || player.equals("BLUE")) {
            Color colorplayer = parseColor(player);
            control.put(colorplayer, new AI(this, colorplayer));
            automation.put(colorplayer, true);
        } else {
            reportError("syntax error in 'auto' command");
        }
    }

    /** Make PLAYER take manual input from the user for subsequent moves. */
    private void setManual(String player) {
        if (player.equals("RED") || player.equals("BLUE")) {
            Color colorplayer = parseColor(player);
            control.put(colorplayer, new HumanPlayer(this, colorplayer));
            automation.put(colorplayer, false);
        } else {
            reportError("syntax error in 'manual' command");
        }
    }

    /** Duplicate of setManual with PLAYER used for testing purposes. */
    void testSetManual(String player) {
        if (player.equals("RED") || player.equals("BLUE")) {
            Color colorplayer = parseColor(player);
            control.put(colorplayer, new HumanPlayer(this, colorplayer));
            automation.put(colorplayer, false);
        } else {
            reportError("syntax error in 'manual' command");
        }
    }

    /** Stop any current game and clear the board to its initial
     *  state. */
    private void clear() {
        _playing = false;
        _board.clear(_board.size());
        _player = _board.whoseMove();
    }

    /** Duplicate of clear for testing purposes. */
    void testClear() {
        _playing = false;
        _board.clear(_board.size());
        _player = _board.whoseMove();
    }

    /** Print the current board using standard board-dump format. */
    private void dump() {
        _out.println(_board);
    }

    /** Print a help message. */
    private void help() {
        Main.printHelpResource(HELP, _out);
    }

    /** Stop any current game and set the move number to N. */
    private void setMoveNumber(int n) {
        if (n >= 0) {
            _playing = false;
            _board.setMoves(n);
            _player = _board.whoseMove();
        } else {
            reportError("move number cannot be negative");
        }
    }

    /** Used for testing purposes to check setMoveNumber
     *  on square N. */
    void testSetMoveNumber(int n) {
        if (n >= 0) {
            _playing = false;
            _board.setMoves(n);
            _player = _board.whoseMove();
        } else {
            reportError("move number cannot be negative");
        }
    }

    /** Seed the random-number generator with SEED. */
    private void setSeed(long seed) {
        _random.setSeed(seed);
    }

    /** Place SPOTS spots on square R:C and color the square red or
     *  blue depending on whether COLOR is "r" or "b".  If SPOTS is
     *  0, clears the square, ignoring COLOR.  SPOTS must be less than
     *  the number of neighbors of square R, C. */
    private void setSpots(int r, int c, int spots, String color) {
        ArrayList<Color> realcolor = new ArrayList<Color>();
        readSingleColor(realcolor, color);
        Color spread;
        if (realcolor.size() == 1) {
            if (spots <= _board.neighbors(r, c) && spots >= 0
                && _board.exists(r, c)) {
                if (spots == 0) {
                    spread = WHITE;
                } else {
                    spread = realcolor.get(0);
                }
                _board.set(r, c, spots, spread);
                _playing = false;
            } else {
                reportError("invalid request to put %d spot(s) on square %d %d",
                    spots, r, c);
            }
        }
    }

    /** Stop any current game and set the board to an empty N x N board
     *  with numMoves() == 1.  */
    private void setSize(int n) {
        if (n >= 0) {
            _playing = false;
            _board.clear(n);
        } else {
            reportError("invalid request to change size to %d", n);
        }
    }

    /** Duplicate of setSize used for testing purposes. Sets
     *  size of board to N. */
    void testSetSize(int n) {
        _playing = false;
        _board.clear(n);
    }

    /** Helper function to setSpots function that is used during the 'set'
     *  command. Takes in ArrayList RESULT and string LETTER. If
     *  letter is "r" or "b" place the respective color into the arraylist,
     *  else report error to the user that letter is not valid. */
    void readSingleColor(ArrayList<Color> result, String letter) {
        if (letter.equals("r")) {
            result.add(RED);
        } else if (letter.equals("b")) {
            result.add(BLUE);
        } else {
            reportError("%s is not a valid input", letter);
        }
    }

    /** Read and execute one command.  Leave the input at the start of
     *  a line, if there is more input. */
    private void readExecuteCommand() {
        if (_playing && _inp.hasNextInt()) {
            try {
                int r = Integer.parseInt(_inp.next());
                int c = Integer.parseInt(_inp.next());
                if (_board.exists(r, c)) {
                    _move[0] = r;
                    _move[1] = c;
                } else if (!_board.exists(r, c)) {
                    reportError("move %d %d out of bounds", r, c);
                }
                _inp.nextLine();
            } catch (NumberFormatException e) {
                reportError("syntax error in 'move' command");
            }
        } else if (_inp.hasNextLine()) {
            lineCommand = (_inp.nextLine()).trim().split("\\s+");
            if (!lineCommand[0].equals("")) {
                _command = lineCommand[0];
                executeCommand(_command);
            }
        }
    }

    /** Gather arguments and execute command CMND.  Throws GameException
     *  on errors. */
    private void executeCommand(String cmnd) {
        switch (cmnd.toLowerCase()) {
        case "\n": case "\r\n":
            return;
        case "#":
            break;
        case "clear":
            clear();
            break;
        case "start":
            _playing = true;
            break;
        case "quit":
            _quit = true;
            break;
        case "size":
            executeCommand2(cmnd);
            break;
        case "move":
            executeCommand2(cmnd);
            break;
        case "set":
            executeCommand2(cmnd);
            break;
        case "auto":
            if (lineCommand.length >= 2) {
                _playing = false;
                setAuto(lineCommand[1].toUpperCase());
            } else {
                reportError("syntax error in 'auto' command");
            }
            break;
        case "manual":
            if (lineCommand.length >= 2) {
                _playing = false;
                setManual(lineCommand[1].toUpperCase());
            } else {
                reportError("syntax error in 'manual' command");
            }
            break;
        case "dump":
            dump();
            break;
        case "help":
            help();
            _out.flush();
            break;
        case "undo":
            _board.undo();
            break;
        default:
            reportError("bad command: '%s'", cmnd);
        }
    }

    /** Gather arguments and execute command CMND.  Throws GameException
     *  on errors. Extended from executeCommand to hold longer cases. */
    private void executeCommand2(String cmnd) {
        switch (cmnd.toLowerCase()) {
        case "size":
            if (lineCommand.length >= 2) {
                try {
                    int val = Integer.parseInt(lineCommand[1]);
                    if (val >= 0) {
                        _board.clear(val);
                        _playing = false;
                    } else {
                        reportError("size cannot have a negative argument");
                    }
                } catch (NumberFormatException e) {
                    reportError("syntax error in 'size' command");
                }
            } else {
                reportError("syntax error in 'size' command");
            }
            break;
        case "move":
            if (lineCommand.length >= 2) {
                try {
                    setMoveNumber(Integer.parseInt(lineCommand[1]));
                } catch (NumberFormatException e) {
                    reportError("syntax error in 'move' command");
                }
            } else {
                reportError("syntax error in 'move' command");
            }
            break;
        case "set":
            if (lineCommand.length >= 5) {
                try {
                    setSpots(Integer.parseInt(lineCommand[1]),
                        Integer.parseInt(lineCommand[2]),
                        Integer.parseInt(lineCommand[3]),
                        lineCommand[4].toLowerCase());
                } catch (NumberFormatException e) {
                    reportError("syntax error in 'set' command");
                }
            } else {
                reportError("syntax error in 'set' command");
            }
            break;
        default:
            reportError("bad command: '%s'", cmnd);
        }
    }

    /** Print a prompt and wait for input. Returns true iff there is another
     *  token. */
    private boolean promptForNext() {
        if (_playing) {
            checkForWin();
        }
        if (_playing) {
            _prompter.print(_player + "> ");
            _prompter.flush();
        } else {
            _prompter.print("> ");
            _prompter.flush();
        }
        return _inp.hasNext();
    }

    /** Send an error message to the user formed from arguments FORMAT
     *  and ARGS, whose meanings are as for printf. */
    void reportError(String format, Object... args) {
        _err.print("Error: ");
        _err.printf(format, args);
        _err.println();
        _err.flush();
    }

    /** String of the next command to be executed. */
    private String _command = "";
    /** String array of all the commands and args on an input line. */
    private String[] lineCommand;

    /** Writer on which to print prompts for input. */
    private final PrintWriter _prompter;
    /** Scanner from current game input.  Initialized to return
     *  newlines as tokens. */
    private final Scanner _inp;
    /** Outlet for responses to the user. */
    private final PrintWriter _out;
    /** Outlet for error responses to the user. */
    private final PrintWriter _err;
    /** The board on which I record all moves. */
    private final Board _board;
    /** A readonly view of _board. */
    private final Board _readonlyBoard;

    /** A pseudo-random number generator used by players as needed. */
    private final Random _random = new Random();

    /** True iff a game is currently in progress. */
    private boolean _playing;

   /** Used to return a move entered from the console.  Allocated
     *  here to avoid allocations. */
    private final int[] _move = new int[2];
}
