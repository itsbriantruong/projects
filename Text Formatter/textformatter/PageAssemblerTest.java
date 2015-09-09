package tex61;

import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of PageAssemblers.
 *  @author Brian Truong
 */
public class PageAssemblerTest {

    private static final String NL = System.getProperty("line.separator");

    private void setupWriter() {
        output = new StringWriter();
        writer = new PrintWriter(output);
    }

    private void setupCollector() {
        outList = new ArrayList<>();
    }

    private void makeTestLines(int n) {
        testLines = new ArrayList<>();
        for (int i = 0; i < n; i += 1) {
            testLines.add("Line " + i);
        }
    }

    private void writeTestLines() {
        for (String L : testLines) {
            pages.addLine(L);
        }
    }

    private String joinLines() {
        StringBuilder S = new StringBuilder();
        for (String L : testLines) {
            S.append(L);
            S.append(NL);
        }
        return S.toString();
    }

    @Test
    public void testPrinterContents1() {
        makeTestLines(20);
        setupWriter();
        pages = new PagePrinter(writer);
        pages.setTextHeight(40);
        writeTestLines();
        writer.close();
        assertEquals("wrong contents: printer", joinLines(), output.toString());
    }

    @Test
    public void testCollectorContents1() {
        makeTestLines(20);
        setupCollector();
        pages = new PageCollector(outList);
        pages.setTextHeight(40);
        writeTestLines();
        assertEquals("wrong contents: collector", testLines, outList);
    }

    @Test
    public void testAddFormFeed() {
        setupWriter();
        pages = new PagePrinter(writer);
        pages.setTextHeight(1);
        pages.addLine("First");
        pages.addLine("new page");
        writer.close();
        assertEquals("Form feed should be prepended to new",
            "First\n\fnew page\n", output.toString());
    }

    @Test
    public void testNullNewLines() {
        setupWriter();
        pages = new PagePrinter(writer);
        pages.setTextHeight(40);
        pages.addLine("First");
        pages.addLine("null");
        pages.addLine("Second");
        writer.close();
        assertEquals("Should have a blank line", "First\n" + "\n"
            + "Second\n", output.toString());
    }

    @Test
    public void testNullNewLines2() {
        setupWriter();
        pages = new PagePrinter(writer);
        pages.setTextHeight(40);
        pages.addLine("null");
        writer.close();
        assertEquals("Should be a new line", "\n",
            output.toString());
    }

    /** Collects output to a PrintWriter. */
    private StringWriter output;
    /** Collects output from a PageAssembler. */
    private PrintWriter writer;
    /** Lines of test data. */
    private List<String> testLines;
    /** Lines from a PageCollector. */
    private List<String> outList;
    /** Target PageAssembler. */
    private PageAssembler pages;

}
