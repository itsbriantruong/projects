package tex61;

import java.util.ArrayList;
import java.util.HashMap;

import static tex61.FormatException.error;

/** An object that receives a sequence of words of text and formats
 *  the words into filled and justified text lines that are sent to a receiver.
 *  @author Brian Truong.
 */
class LineAssembler {

    /** Current value of Indent for the paragraph. */
    private int indent;
    /** Current value of ParIndent for the paragraph. */
    private int parIndent;
    /** Current value of TextWidth for the paragraph. */
    private int textWidth;
    /** Current value of ParSkip for the paragraph. */
    private int parSkip;
    /** Current value of TextHeight for the paragraph. */
    private int textHeight;
    /** Current value of Fill for the paragraph.
     *  Fill is on for 1 and off for 0. */
    private int fill;
    /** Current value of Justify for the paragraph.
     *  Fill is on for 1 and off for 0. */
    private int justify;
    /** Total number of lines of text. */
    private int totalNum = 0;
    /** The N-th sentence of a paragraph, starting from 0. */
    private int nthNum = 1;

    /** A new, empty line assembler with default settings of all
     *  parameters, sending finished lines to PAGES. */
    LineAssembler(PageAssembler pages) {
        _pages = pages;
        lineAccumulate = "";
        runningEnv = new HashMap<String, Integer>();
    }

    /** Set the settings of the commands for the next paragraph.
     *  Pulls from HashMap runningEnv, which holds the live command
     *  changes. Allows for settings that only apply to the next paragraph
     *  even when called in the middle of a paragraph. */
    void setCommands() {
        fill = runningEnv.get("fill");
        justify = runningEnv.get("justify");
        indent = runningEnv.get("indent");
        parIndent = runningEnv.get("parindent");
        textWidth = runningEnv.get("width");
        parSkip = runningEnv.get("parskip");
        textHeight = runningEnv.get("height");
    }

    /** Set the current indentation to VAL. VAL >= 0. */
    void setIndentation(int val) {
        runningEnv.put("indent", val);
    }

    /** Return the current value of INDENT for the
     *  current paragraph. */
    int getIndent() {
        return indent;
    }

    /** Set the current paragraph indentation to VAL. VAL >= 0. */
    void setParIndentation(int val) {
        runningEnv.put("parindent", val);
    }

    /** Return the current value of PARINDENT for the
     *  current paragraph. */
    int getParIndent() {
        return parIndent;
    }

    /** Set the text width to VAL, where VAL >= 0. */
    void setTextWidth(int val) {
        runningEnv.put("width", val);
    }

    /** Return the current value of TEXTWIDTH for the
     *  current paragraph. */
    int getWidth() {
        return textWidth;
    }

    /** Re-set the most recent TEXTWIDTH to apply
     *  to the next line of output. */
    void resetWidth() {
        textWidth = runningEnv.get("width");
    }

    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParSkip(int val) {
        runningEnv.put("parskip", val);
    }

    /** Return the current value of PARSKIP for the
     *  current paragraph. */
    int getParSkip() {
        return parSkip;
    }

    /** Set page height to VAL > 0. */
    void setTextHeight(int val) {
        _pages.setTextHeight(val);
        runningEnv.put("height", val);
    }

    /** Return the current value of TEXTHEIGHT for the
     *  current paragraph. */
    int getHeight() {
        return textHeight;
    }

    /** Iff VAL is 1, set fill mode on, else if 0 turn off fill.
     *  Checks to see if justify is currently on. */
    void setFill(int val) {
        runningEnv.put("fill", val);
    }

    /** Return the current value of FILL for the
     *  current paragraph. 1 for on 0 for off. */
    int getFill() {
        return fill;
    }

    /** Iff VAL is 1, set justify mode on (which is active
     *  only when filling is also on). */
    void setJustify(int val) {
        runningEnv.put("justify", val);
    }

    /** Return the current value of JUSTIFY for the
     *  current paragraph. 1 for on 0 for off. */
    int getJustify() {
        return justify;
    }

    /** Takes in a large size WORD and the number of INDENTS
     *  to account for and formats it into a line. */
    void bigWords(String word, int indents) {
        if (indents != 0) {
            lineAccumulate += String.format("%" + indents + "s", " ");
        }
        lineAccumulate += word;
        outputLine(false);
    }

    /** Justifying formula that returns a number N which is the number
     *  of spaces between 0 and PLACE. Takes in WORDCOUNT and the available
     *  SPACE. */
    int jformula(int space, int place, int wordcount) {
        return (int) Math.floor((0.5 + ((double) space * (double) place)
            / ((double) wordcount - (double) 1)));
    }

    /** Formulate an end of paragraph line with elements from INPUTS.
     *  LASTLINE tells the outputLine function if this line is
     *  the final line of a paragraph and or page. */
    void beginLine(ArrayList<String> inputs, boolean lastline) {
        if (inputs.size() > 0) {
            int indents = indent;
            if (nthNum == 1) {
                indents += parIndent;
            }
            if (indents != 0) {
                lineAccumulate += String.format("%" + indents + "s", " ");
            }
            lineAccumulate += inputs.get(0);
            for (int i = 1; i < inputs.size(); i += 1) {
                lineAccumulate += " ";
                lineAccumulate += inputs.get(i);
            }
            outputLine(lastline);
        } else {
            error("INPUTS: %s should not be empty", inputs);
        }
    }

    /** Formulate each line with justifications. Taking in INPUTS, the number
     *  of available SPACE, N words and number of INDENTS to apply the
     *  justification formula with the current number of indents necessary
     *  for the beginning of this line. */
    void beginLine(ArrayList<String> inputs, int space, int n, int indents) {
        int total = 0;
        if (indents != 0) {
            lineAccumulate += String.format("%" + indents + "s", " ");
        }
        if (space >= 3 * (n - 1)) {
            addThree(inputs, space, n, indents);
        } else {
            if (n == 1) {
                lineAccumulate += inputs.get(0);
            } else {
                for (int i = 1; i <= n - 1; i += 1) {
                    lineAccumulate += inputs.get(i - 1);
                    if (jformula(space, i, n) > 0) {
                        lineAccumulate += String.format("%"
                            + (jformula(space, i, n) - total) + "s", " ");
                        total = jformula(space, i, n);
                    }
                }
                lineAccumulate += inputs.get(n - 1);
            }
            outputLine(false);
        }
    }

    /** Formulate a line with no justifying. Adding one space between
     *  each word. Takes in INPUTS and uses WORDCOUNT and INDENTS to
     *  add the necessary number of spaces. */
    void fillNoJustify(ArrayList<String> inputs, int wordcount, int indents) {
        if (indents != 0) {
            lineAccumulate += String.format("%" + indents + "s", " ");
        }
        lineAccumulate += inputs.get(0);
        for (int i = 1; i <= wordcount - 1; i += 1) {
            lineAccumulate += " ";
            lineAccumulate += inputs.get(i);
        }
        outputLine(false);
    }

    /** Adds three spaces between each word N in INPUTS when there
     *  are only a few words and a large text width. SPACE
     *  is the available space and INDENTS is the number of indents
     *  needed to be added to the beginning of this line. */
    void addThree(ArrayList<String> inputs, int space, int n, int indents) {
        lineAccumulate += inputs.get(0);
        for (int i = 1; i < n; i += 1) {
            lineAccumulate += String.format("%" + 3 + "s", " ");
            lineAccumulate += inputs.get(i);
        }
        outputLine(false);
    }

    /** Process the end of the current input line.  No effect if
     *  current line accumulator is empty or in fill mode.  Otherwise,
     *  Clears the line accumulator. */
    void newLine() {
        if (!lineAccumulate.equals("")) {
            lineAccumulate = "";
        }
    }

    /** If there is a current unfinished paragraph pending, close it
     *  out and start a new one. */
    void endParagraph() {
        _pages.addLine(lineAccumulate);
        totalNum += 1;
        nthNum = 1;
        newLine();
    }

    /** Checks to see if currently at the beginning of a paragraph.
     *  If so add paragraph skips if necessary. Else send the line
     *  to page collector and depending on if this is the end of
     *  paragraph. If so reset certain counters. LASTLINE indicates
     *  the last line of a paragraph. */
    private void outputLine(boolean lastLine) {
        if (nthNum == 1 && totalNum != 0) {
            for (int i = parSkip; i > 0; i -= 1) {
                _pages.addLine("null");
            }
            nthNum += 1;
        }
        if (lastLine) {
            endParagraph();
        } else {
            _pages.addLine(lineAccumulate);
            totalNum += 1;
            nthNum += 1;
            newLine();
        }
    }

    /** Destination given in constructor for formatted lines. */
    private final PageAssembler _pages;

    /** String that holds a complete formatted line of words. */
    private String lineAccumulate;

    /** HashMap with all commands that changes live (as the InputParser
     *  processes the commands). The key holds the commands, while the
     *  value holds the respective argument. */
    private HashMap<String, Integer> runningEnv;

}
