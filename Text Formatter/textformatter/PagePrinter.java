package tex61;

import java.io.PrintWriter;

/** A PageAssembler that sends lines immediately to a PrintWriter, with
 *  terminating newlines.
 *  @author Brian Truong.
 */

class PagePrinter extends PageAssembler {

    /** A new PagePrinter that sends lines to OUT. */
    PagePrinter(PrintWriter out) {
        _output = out;
    }

    /** Print LINE to my output. */
    @Override
    void write(String line) {
        if (line.equals("null")) {
            _output.append("\n");
        } else {
            _output.append(line + "\n");
        }
    }
    /** Final output. */
    private PrintWriter _output;
}
