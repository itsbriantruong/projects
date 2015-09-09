package tex61;

import java.io.PrintWriter;
import java.util.ArrayList;

import static tex61.FormatException.reportError;
import static tex61.FormatException.error;

/** Receives (partial) words and commands, performs commands, and
 *  accumulates and formats words into lines of text, which are sent to a
 *  designated PageAssembler.  At any given time, a Controller has a
 *  current word, which may be added to by addText, a current list of
 *  words that are being accumulated into a line of text, and a list of
 *  lines of endnotes.
 *  @author Brian Truong.
 */


class Controller {

    /** Array of current words that will fit in the
     *  next line of output. */
    private ArrayList<String> scope = new ArrayList<String>();

    /** Array that collects endnote arguments. Used iff
     * endnote mode is active. */
    private ArrayList<String> endArray = new ArrayList<String>();

    /** Array of complete lines to be sent to PrintWriter.
     *  Will send all lines to _out through PagePrinter
     *  once the endnotes have been processed and collected. */
    private ArrayList<String> fullLines = new ArrayList<String>();

    /** PageCollector that takes in finished lines and adds
     *  them to arraylist fullLines that it takes in.
     *  Concrete class of PageAssembler abstract class.*/
    private PageAssembler collect;

    /** LineAssembler that creates complete lines and sends
     *  them to the PageCollector fullLines that it takes in. */
    private LineAssembler doLine;

    /** PagePrinter that writes finished lines to the PrintWriter
     *  output OUT. */
    private PagePrinter printer;

    /** Finished pages of lines gets sent to PrintWriter _out. */
    private PrintWriter _out;

    /** Current word that is held until the endWord function is
     *  called. This occurs when the word is complete and the
     *  Controller is done adding text to the word. */
    private String currentWord = "";

    /** Number of next endnote. */
    private int _refNum = 0;

    /** Current line of a given paragraph. Refreshes at the
     *  end of each paragraph. */
    private int lineCount = 1;

    /** Number of characters in the array SCOPE at a given time.
     *  Used to calculate the numbers of words that will fit
     *  on a given line. */
    private int charCount = 0;

    /** The n-th word of the paragraph. */
    private int _nthWord = 1;

   /** True iff we are currently processing an endnote. */
    private boolean _endnoteMode = false;

    /** True iff the end of a line has been reached. */
    private boolean _endOfLine = false;

    /** A new Controller that sends formatted output to OUT. */
    Controller(PrintWriter out) {
        _out = out;
        printer = new PagePrinter(out);
        collect = new PageCollector(fullLines);
        doLine = new LineAssembler(collect);
    }

    /** Add TEXT to the end of the word of formatted text currently
     *  being accumulated. */
    void addText(String text) {
        currentWord += text;
    }

    /** Add TEXT to the array scope, which hold the
     *  next words to be output. */
    void addScope(String text) {
        scope.add(text);
    }

    /** Appends reference number to the current word when STATUS is true.
     *  This only applies to the original location of where an endnote command
     *  is called. */
    void addRefNum(boolean status) {
        _refNum += 1;
        addText("[" + _refNum + "]");
    }

    /** Finish any current word of text and, if present, add to the
     *  list of words for the next line.  Has no effect if no unfinished
     *  word is being accumulated. */
    void endWord() {
        if (_nthWord == 1) {
            doLine.setCommands();
            _endOfLine = false;
        }
        int indents = doLine.getIndent();
        if (lineCount == 1) {
            indents += doLine.getParIndent();
        }
        int ifSize = doLine.getWidth()
            - (charCount + currentWord.length()) - indents;
        if (doLine.getFill() == 1) {
            if (doLine.getWidth() - indents < currentWord.length()
                && scope.size() == 0) {
                doLine.bigWords(currentWord, indents);
                currentWord = "";
                lineCount += 1;
                _nthWord += 1;
            } else if (!(ifSize >= scope.size())) {
                if (doLine.getJustify() == 1) {
                    doLine.beginLine(scope, ifSize + currentWord.length(),
                        scope.size(), indents);
                } else {
                    doLine.fillNoJustify(scope, scope.size(), indents);
                }
                scope.clear();
                addScope(currentWord);
                doLine.resetWidth();
                charCount = currentWord.length();
                currentWord = "";
                lineCount += 1;
                _nthWord += 1;
            } else {
                addScope(currentWord);
                charCount += currentWord.length();
                currentWord = "";
                _nthWord += 1;
            }
        } else {
            noFillEndWord();
        }
    }

    /** Does the end of a word when fill mode is OFF (0). */
    void noFillEndWord() {
        if (_endOfLine && scope.size() > 0) {
            doLine.beginLine(scope, false);
            _endOfLine = false;
            scope.clear();
            addScope(currentWord);
            charCount = currentWord.length();
            lineCount += 1;
        } else {
            addScope(currentWord);
            charCount += currentWord.length();

        }
        _nthWord += 1;
        currentWord = "";
    }

    /** End the current paragraph or section if no more words
     *  remain. Sends words that remain in our array to
     *  LineAssembler and resets necessary counters. */
    void endSection() {
        if (scope.size() > 0) {
            doLine.beginLine(scope, true);
            scope.clear();
        }
        charCount = 0;
        lineCount = 1;
        _nthWord = 1;
    }

    /** Set _endOfLine STATUS to true iff the end of the line has
     *  been reached. Used when filling is off to determine when
     *  to output all accumulating words.  */
    void setEndOfLine(boolean status) {
        _endOfLine = status;
    }

    /** Return the current WORD being processed. */
    String getCurrentWord() {
        return currentWord;
    }

    /** Returns the current status of FILL. 1 if filling is ON
     *  and 0 if filling is OFF. */
    int getCurrentFill() {
        return doLine.getFill();
    }

    /** Returns the LineAssembler class made by controller.
     *  Primarily being used for testing. */
    LineAssembler getDoLine() {
        return doLine;
    }

    /** If valid, process TEXT into an endnote, first appending a reference
     *  to it to the line currently being accumulated. */
    void formatEndnote(String text) {
        endArray.add("[" + _refNum + "]" + "\\ "  + text + " ");
    }

    /** Set the current text height (number of lines per page) to VAL, if
     *  it is a valid setting.  Ignored when accumulating an endnote. */
    void setTextHeight(int val) {
        if (val > 0) {
            doLine.setTextHeight(val);
        } else {
            reportError("VAL: %s is not valid for text height", val);
            throw error("Error: %s invalid", val);
        }
    }

    /** Set the current text width (width of lines including indentation)
     *  to VAL, if it is a valid setting. */
    void setTextWidth(int val) {
        if (val > 0) {
            doLine.setTextWidth(val);
        } else {
            reportError("VAL: %s is not valid for text width", val);
            throw error("Error: %s invalid", val);
        }
    }

    /** Set the current text indentation (number of spaces inserted before
     *  each line of formatted text) to VAL, if it is a valid setting. */
    void setIndentation(int val) {
        if (val >= 0) {
            doLine.setIndentation(val);
        } else {
            reportError("VAL: %s is not valid for indentation", val);
            throw error("Error: %s invalid", val);
        }
    }

    /** Set the current paragraph indentation (number of spaces inserted before
     *  first line of a paragraph in addition to indentation) to VAL, if it is
     *  a valid setting. */
    void setParIndentation(int val) {
        doLine.setParIndentation(val);
    }

    /** Set the current paragraph skip (number of blank lines inserted before
     *  a new paragraph, if it is not the first on a page) to VAL, if it is
     *  a valid setting. */
    void setParSkip(int val) {
        if (val >= 0) {
            doLine.setParSkip(val);
        } else {
            reportError("VAL: %s is not valid for paragraph skip", val);
            throw error("Error: %s invalid", val);
        }
    }

    /** Iff VAL == 1, begin filling lines of formatted text. 0
     *  means that filling is currently off. */
    void setFill(int val) {
        doLine.setFill(val);
    }

    /** Iff VAL == 1, begin justifying lines of formatted text whenever
     *  filling is also on. 0 means justifying is off. */
    void setJustify(int val) {
        doLine.setJustify(val);
    }

    /** Returns TRUE iff endnote mode is currently active, else
     *  returns false. */
    boolean getEndNoteMode() {
        return _endnoteMode;
    }

    /** Returns SCOPE of current words that will fit in the
     *  next line of output. Used for testing. */
    ArrayList<String> getScope() {
        return scope;
    }

    /** Returns ENDARRAY which contains all the arguments of
     *  endnote commands collected while parsing. Used for testing. */
    ArrayList<String> getEndArray() {
        return endArray;
    }

    /** Returns FULLLINES which contains all the finished lines
     *  from PageCollector and Line Assembler. Used for testing. */
    ArrayList<String> getFullLines() {
        return fullLines;
    }

    /** Finish the current formatted document or endnote (depending on mode).
     *  Formats and outputs all pending text. */
    void close() {
        for (String x : fullLines) {
            printer.write(x);
        }
        _out.close();
    }

    /** Set the defualt settings for endnote mode commands, including
     *  filling and justifying. */
    private void setEndnoteMode() {
        _endnoteMode = true;
        doLine.setTextWidth(Defaults.ENDNOTE_TEXT_WIDTH);
        doLine.setIndentation(Defaults.ENDNOTE_INDENTATION);
        doLine.setParIndentation(Defaults.ENDNOTE_PARAGRAPH_INDENTATION);
        doLine.setParSkip(Defaults.ENDNOTE_PARAGRAPH_SKIP);
        doLine.setCommands();
    }

    /** Set the default settings for normal mode commands, including
     *  filling and justifying. */
    private void setNormalMode() {
        _endnoteMode = false;
        doLine.setFill(1);
        doLine.setJustify(1);
        doLine.setTextHeight(Defaults.TEXT_HEIGHT);
        doLine.setTextWidth(Defaults.TEXT_WIDTH);
        doLine.setIndentation(Defaults.INDENTATION);
        doLine.setParIndentation(Defaults.PARAGRAPH_INDENTATION);
        doLine.setParSkip(Defaults.PARAGRAPH_SKIP);
        doLine.setCommands();
    }

    /** Set the current environment depending END. If END is true,
     *  set controller to end note mode, else use normal mode. */
    void setEnvironment(boolean end) {
        if (end) {
            setEndnoteMode();
        } else {
            setNormalMode();
        }
    }
}

