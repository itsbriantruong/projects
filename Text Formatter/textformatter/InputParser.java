package tex61;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;

import java.io.Reader;

import static tex61.FormatException.reportError;

/** Reads commands and text from an input source and send the results
 *  to a designated Controller. This essentially breaks the input down
 *  into "tokens"---commands and pieces of text.
 *  @author Brian Truong.
 */
class InputParser {

    /** Matches text between { } in a command, including the last
     *  }, but not the opening {.  When matched, group 1 is the matched
     *  text.  Always matches at least one character against a non-empty
     *  string or input source. If it matches and group 1 is null, the
     *  argument was not well-formed (the final } was missing or the
     *  argument list was nested too deeply). */
    private static final Pattern BALANCED_TEXT =
        Pattern.compile("(?s)((?:\\\\.|[^\\\\{}]"
                        + "|[{](?:\\\\.|[^\\\\{}])*[}])*)"
                        + "\\}"
                        + "|.");

    /** Matches input to the text formatter.  Always matches something
     *  in a non-empty string or input source.  After matching, one or
     *  more of the groups described by *_TOKEN declarations will
     *  be non-null.  See these declarations for descriptions of what
     *  this pattern matches.  To test whether .group(*_TOKEN) is null
     *  quickly, check for .end(*_TOKEN) > -1).  */
    private static final Pattern INPUT_PATTERN =
        Pattern.compile("(?s)(\\p{Blank}+)"
                        + "|(\\r?\\n((?:\\r?\\n)+)?)"
                        + "|\\\\([\\p{Blank}{}\\\\])"
                        + "|\\\\(\\p{Alpha}+)([{]?)"
                        + "|((?:[^\\p{Blank}\\r\\n\\\\{}]+))"
                        + "|(.)");

    /** Symbolic names for the groups in INPUT_PATTERN. */
    private static final int
        /** Blank or tab. */
        BLANK_TOKEN = 1,
        /** End of line or paragraph. */
        EOL_TOKEN = 2,
        /** End of paragraph (>1 newline). EOL_TOKEN group will also
         *  be present. */
        EOP_TOKEN = 3,
        /** \{, \}, \\, or \ .  .group(ESCAPED_CHAR_TOKEN) will be the
         *  character after the backslash. */
        ESCAPED_CHAR_TOKEN = 4,
        /** Command (\<alphabetic characters>).  .group(COMMAND_TOKEN)
         *  will be the characters after the backslash.  */
        COMMAND_TOKEN = 5,
        /** A '{' immediately following a command. When this group is present,
         *  .group(COMMAND_TOKEN) will also be present. */
        COMMAND_ARG_TOKEN = 6,
        /** Segment of other text (none of the above, not including
         *  any of the special characters \, {, or }). */
        TEXT_TOKEN = 7,
        /** A character that should not be here. */
        ERROR_TOKEN = 8;

    /** A new InputParser taking input from READER and sending tokens to
     *  OUT. */
    InputParser(Reader reader, Controller out) {
        _input = new Scanner(reader);
        _out = out;
    }

    /** A new InputParser whose input is TEXT and that sends tokens to
     *  OUT. */
    InputParser(String text, Controller out) {
        _input = new Scanner(text);
        _out = out;
    }

    /** Break all input source text into tokens, and send them to our
     *  output controller.  Finishes by calling .close on the controller.
     */
    void process() {
        if (!_out.getEndArray().isEmpty()) {
            _out.setEnvironment(true);
        } else {
            _out.setEnvironment(false);
        }
        while (_input.findWithinHorizon(INPUT_PATTERN, 0) != null) {
            MatchResult mat = _input.match();
            if (mat.end(BLANK_TOKEN) > -1 || mat.end(EOL_TOKEN) > -1) {
                if (!_out.getCurrentWord().equals("")) {
                    _out.endWord();
                }
            }
            if (mat.end(EOL_TOKEN) > -1 && _out.getCurrentFill() == 0
                && !(mat.end(EOP_TOKEN) > -1)) {
                _out.setEndOfLine(true);
            }

            if (mat.end(EOL_TOKEN) > -1 && mat.end(EOP_TOKEN) > -1
                || !_input.hasNextLine()) {
                _out.endSection();
            }
            if (mat.end(ESCAPED_CHAR_TOKEN) > -1) {
                _out.addText(mat.group(ESCAPED_CHAR_TOKEN));
            }
            if (mat.end(TEXT_TOKEN) > -1) {
                _out.addText(mat.group(TEXT_TOKEN));
            }
            if (mat.end(COMMAND_ARG_TOKEN) > -1
                && mat.group(COMMAND_ARG_TOKEN).equals("{")) {
                if (_input.findWithinHorizon(BALANCED_TEXT, 0) != null) {
                    MatchResult args = _input.match();
                    processCommand(mat.group(COMMAND_TOKEN), args.group(1));
                }
            }
            if (mat.end(COMMAND_ARG_TOKEN) > -1
                && !(mat.group(COMMAND_ARG_TOKEN).equals("{"))) {
                processCommand(mat.group(COMMAND_TOKEN));
            }
        }
    }

    /** Process \COMMAND{ARG} or (if ARG is null) \COMMAND.  Call the
     *  appropriate methods in our Controller (_out). */
    private void processCommand(String command, String arg) {
        try {
            switch (command) {
            case "indent":
                try {
                    _out.setIndentation(Integer.parseInt(arg));
                } catch (NumberFormatException e) {
                    throw FormatException.error("ARG: %s not valid", arg);
                }
                break;
            case "parindent":
                try {
                    _out.setParIndentation(Integer.parseInt(arg));
                } catch (NumberFormatException e) {
                    throw FormatException.error("ARG: %s not valid", arg);
                }
                break;
            case "textwidth":
                try {
                    _out.setTextWidth(Integer.parseInt(arg));
                } catch (NumberFormatException e) {
                    throw FormatException.error("ARG: %s not valid", arg);
                }
                break;
            case "textheight":
                try {
                    if (!_out.getEndNoteMode()) {
                        _out.setTextHeight(Integer.parseInt(arg));
                    }
                } catch (NumberFormatException e) {
                    throw FormatException.error("ARG: %s not valid", arg);
                }
                break;
            case "parskip":
                try {
                    _out.setParSkip(Integer.parseInt(arg));
                } catch (NumberFormatException e) {
                    throw FormatException.error("ARG: %s not valid", arg);
                }
                break;
            case "endnote":
                if (_out.getEndNoteMode()) {
                    reportError("Cannot have nested command: %s", command);
                    System.exit(1);
                }
                _out.addRefNum(true);
                _out.formatEndnote(arg);
                break;
            default:
                reportError("unknown command: %s", command);
                break;
            }
        } catch (FormatException e) {
            reportError("FormatException Error: %s", e);
            System.exit(1);
        }
    }
    /** Process string COMMAND with no arguments. */
    private void processCommand(String command) {
        try {
            switch (command) {
            case "fill":
                _out.setFill(1);
                break;
            case "nofill":
                _out.setFill(0);
                break;
            case "justify":
                _out.setJustify(1);
                break;
            case "nojustify":
                _out.setJustify(0);
                break;
            default:
                reportError("unknown command: %s", command);
                break;
            }
        } catch (FormatException e) {
            reportError("FormatException Error: %s", e);
            System.exit(1);
        }

    }

    /** My input source. */
    private final Scanner _input;
    /** The Controller to which I send input tokens. */
    private Controller _out;
}
