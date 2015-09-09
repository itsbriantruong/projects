package tex61;

import static tex61.FormatException.error;

/** A PageAssembler accepts complete lines of text (minus any
 *  terminating newlines) and turns them into pages, adding form
 *  feeds as needed.  It prepends a form feed (Control-L  or ASCII 12)
 *  to the first line of each page after the first.  By overriding the
 *  'write' method, subtypes can determine what is done with
 *  the finished lines.
 *  @author Brian Truong.
 */
abstract class PageAssembler {
    /** Count the number of lines currently on a page. */
    private int lineCounter;

    /** Current known page height. */
    private int knownHeight;

    /** True iff it is valid to pass
     *  null through addLine. */
    private boolean okayNull;

    /** Create a new PageAssembler that sends its output to OUT.
     *  Initially, its text height is unlimited. It prepends a form
     *  feed character to the first line of each page except the first. */
    PageAssembler() {
        lineCounter = 0;
        knownHeight = -1;
    }

    /** Add LINE to the current page, starting a new page with it if
     *  the previous page is full. A null LINE indicates a skipped line,
     *  and has no effect at the top of a page. */
    void addLine(String line) {
        if (lineCounter + 1 <= knownHeight) {
            write(line);
            lineCounter += 1;
        } else if (!line.equals("null")) {
            write("\f" + line);
            lineCounter = 1;
        }
    }

    /** Set text height to VAL, where VAL > 0. */
    void setTextHeight(int val) {
        if (val > 0) {
            knownHeight = val;
        } else {
            throw error("VAL: %s not a valid value", val);
        }
    }

    /** Perform final disposition of LINE, as determined by the
     *  concrete subtype. */
    abstract void write(String line);

}
