package tex61;

import java.util.ArrayList;
import java.io.PrintWriter;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of LineAssembler and Page Collector process.
 *  @author Brian Truong
 */
public class AssembleCollectTest {

    private void setupControl() {
        _out = new PrintWriter(System.out);
        commander = new Controller(_out);
        assembler = commander.getDoLine();
        fullLines = commander.getFullLines();
        scope = commander.getScope();
    }

    private void addTextAndEndWord(String text) {
        commander.addText(text);
        commander.endWord();
    }

    private void makeFullLineWords() {
        assembler.setTextWidth(13);
        assembler.setCommands();
        addTextAndEndWord("This");
        addTextAndEndWord("is");
        addTextAndEndWord("a");
    }

    private void fillWords() {
        addTextAndEndWord("This");
        addTextAndEndWord("is");
        addTextAndEndWord("a");
        addTextAndEndWord("finished");
    }

    private void fillDefaultWidth() {
        addTextAndEndWord("superfluous");
        addTextAndEndWord("extrapolate");
        addTextAndEndWord("dumbfounded");
    }

    private void fillWords2() {
        assembler.setTextWidth(16);
        assembler.setCommands();
        addTextAndEndWord("Oh");
        addTextAndEndWord("Noes!");
    }

    private void addGiantWordEndSection() {
        commander.addText("giganticismo");
        commander.endWord();
        commander.endSection();
    }

    @Test
    public void testSpacing() {
        setupControl();
        commander.setEnvironment(false);
        makeFullLineWords();
        commander.addText("fulllines");
        commander.endWord();
        assertEquals("Wrong length for line", 13, fullLines.get(0).length());
    }

    @Test
    public void testDefaultLineCapacity() {
        setupControl();
        commander.setEnvironment(false);
        fillDefaultWidth();
        assertEquals("Should have three large words", 3, scope.size());
    }

    @Test
    public void testSingleLine() {
        setupControl();
        commander.setEnvironment(false);
        makeFullLineWords();
        commander.addText("fulllines");
        commander.endWord();
        assertEquals("Wrong line", "   This  is a", fullLines.get(0));
    }

    @Test
    public void testNoParIndent() {
        setupControl();
        commander.setEnvironment(false);
        assembler.setParIndentation(0);
        assembler.setCommands();
        makeFullLineWords();
        commander.addText("superfluous");
        commander.endWord();
        assertEquals("Wrong line", "This   is   a", fullLines.get(0));
    }

    @Test
    public void testChangeIndent() {
        setupControl();
        commander.setEnvironment(false);
        assembler.setIndentation(1);
        assembler.setCommands();
        makeFullLineWords();
        commander.addText("fulllines");
        commander.endWord();
        assertEquals("Wrong line", "    This is a", fullLines.get(0));
    }

    @Test
    public void testChangeIndentOverFlow() {
        setupControl();
        commander.setEnvironment(false);
        assembler.setIndentation(3);
        assembler.setCommands();
        makeFullLineWords();
        assertEquals("Wrong line", "      This is", fullLines.get(0));
    }

    @Test
    public void testChangeIndentOverFlow2() {
        setupControl();
        commander.setEnvironment(false);
        assembler.setIndentation(3);
        assembler.setCommands();
        makeFullLineWords();
        commander.addText("fulllines");
        commander.endWord();
        assertEquals("Wrong size current scope", 2, fullLines.size());
    }

    @Test
    public void testNotEnoughWords() {
        setupControl();
        commander.setEnvironment(false);
        fillWords();
        commander.addText("line.");
        commander.endWord();
        assertEquals("Different number words", 5, scope.size());
    }

    @Test
    public void testLargeWords() {
        setupControl();
        commander.setEnvironment(false);
        fillWords2();
        commander.addText("giganticismo");
        commander.endWord();
        assertEquals("Different lines", "   Oh   Noes!", fullLines.get(0));
    }

    @Test
    public void testLargeWordsEmptyScope() {
        setupControl();
        commander.setEnvironment(false);
        fillWords2();
        commander.addText("giganticismo");
        commander.endWord();
        assertEquals("Scope should only have one word", 1, scope.size());
    }

    @Test
    public void testLargeWordsStore() {
        setupControl();
        commander.setEnvironment(false);
        fillWords2();
        commander.addText("giganticismo");
        commander.endWord();
        assertEquals("Didn't store big word", "giganticismo", scope.get(0));
    }

    @Test
    public void testEndSection() {
        setupControl();
        commander.setEnvironment(false);
        fillWords2();
        addGiantWordEndSection();
        fillWords();
        assertEquals("Not the correct line", "giganticismo", fullLines.get(1));
    }

    @Test
    public void testParSkipping() {
        setupControl();
        commander.setEnvironment(false);
        fillWords2();
        addGiantWordEndSection();
        fillWords();
        assertEquals("No ParSkip Found", "null", fullLines.get(2));
    }

    @Test
    public void testLargerParSkipping() {
        setupControl();
        commander.setEnvironment(false);
        fillWords2();
        assembler.setParSkip(3);
        assembler.setCommands();
        addGiantWordEndSection();
        fillWords();
        assertEquals("Should have three ParSkips(null)", "null",
            fullLines.get(4));
    }

    @Test
    public void testNoParSkip() {
        setupControl();
        commander.setEnvironment(false);
        fillWords2();
        assembler.setParSkip(0);
        assembler.setCommands();
        addGiantWordEndSection();
        fillWords();
        assertEquals("Wrong line found", "   This   is   a",
            fullLines.get(2));
    }

    /** The current word that is being processed. */
    private String currentWord;
    /** Collects output from a PageAssembler. */
    private PrintWriter _out;
    /** Controller that takes in words and sends them to PrintWriter. */
    private Controller commander;
    /** LineAssembler that collects words and formats the lines. */
    private LineAssembler assembler;
    /** Container that stores all the completed lines to be sent to
     *  PagePrinter. */
    private ArrayList<String> fullLines;
    /** Container that stores all the completed words to be sent to
     *  LineAssembler. */
    private ArrayList<String> scope;
}
