package tex61;

import java.util.ArrayList;
import java.io.PrintWriter;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of Controller.
 *  @author Brian Truong
 */
public class ControllerTest {

    private void setupControl() {
        _out = new PrintWriter(System.out);
        commander = new Controller(_out);
        wordContainer = commander.getScope();
        assembler = commander.getDoLine();
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

    private void addEndArray() {
        commander.getEndArray().add("Published in 1988.");
    }

    @Test
    public void testRefAppend() {
        String ref = "[1]";
        setupControl();
        commander.addRefNum(true);
        assertEquals("wrong ref append", ref, commander.getCurrentWord());
    }

    @Test
    public void testEnvironmentSettingEndNote() {
        setupControl();
        commander.setEnvironment(false);
        commander.setEnvironment(true);
        assertEquals("Wrong setting endnote mode", -4,
            commander.getDoLine().getParIndent());
    }

    @Test
    public void testEnvironmentSettingNormal() {
        setupControl();
        commander.setEnvironment(false);
        assertEquals("Wrong setting normal mode", 3,
            commander.getDoLine().getParIndent());
    }

    @Test
    public void testTurnOffFill() {
        setupControl();
        commander.setEnvironment(false);
        commander.setFill(0);
        assembler.setCommands();
        assertEquals("Fill should be off", 0, assembler.getFill());
    }

    @Test
    public void testEndWordClear() {
        setupControl();
        commander.setEnvironment(false);
        commander.addText("one");
        commander.endWord();
        assertEquals("Current word not cleared", "",
            commander.getCurrentWord());
    }

    @Test
    public void testWordAddText() {
        setupControl();
        commander.setEnvironment(false);
        commander.addText("Mind");
        commander.addRefNum(true);
        commander.addText(":");
        assertEquals("Current word not correct", "Mind[1]:",
            commander.getCurrentWord());
    }

    @Test
    public void testFullLine() {
        setupControl();
        commander.setEnvironment(false);
        makeFullLineWords();
        commander.addText("fullline");
        commander.endWord();
        assertEquals("Should store word in array", 1, wordContainer.size());
    }

    @Test
    public void testFullLine2() {
        setupControl();
        commander.setEnvironment(false);
        makeFullLineWords();
        commander.addText("fullline");
        commander.endWord();
        assertEquals("Should find fullline in array", "fullline",
            wordContainer.get(0));
    }

    @Test
    public void testEndnoteFormatPrepend() {
        setupControl();
        commander.setEnvironment(false);
        addEndArray();
        commander.addRefNum(true);
        commander.formatEndnote("United States");
        assertEquals("Check format endnote", "[1]\\ United States" + " ",
            commander.getEndArray().get(1));
    }

    @Test
    public void testEndnoteFormatPrepend2() {
        setupControl();
        commander.setEnvironment(false);
        commander.addRefNum(true);
        commander.formatEndnote("one");
        commander.addRefNum(true);
        commander.formatEndnote("two");
        assertEquals("Check endnote formatting", "[2]\\ two" + " ",
            commander.getEndArray().get(1));
    }

    @Test
    public void testEndnoteFormatStoring() {
        setupControl();
        commander.setEnvironment(false);
        addEndArray();
        addEndArray();
        commander.formatEndnote("United States");
        assertEquals("Check endnote storing", 3,
            commander.getEndArray().size());
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidValueWidth() {
        commander.setTextWidth(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidValueHeight() {
        commander.setTextHeight(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidValueIndent() {
        commander.setIndentation(-1);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidValueParSkip() {
        commander.setParSkip(-1);
    }

    /** The current word that is being processed. */
    private String currentWord;
    /** The current reference number for endnotes. */
    private int _refNum;
    /** Collects output from a PageAssembler. */
    private PrintWriter _out;
    /** Controller that takes in words and sends them to PrintWriter. */
    private Controller commander;
    /** LineAssembler that collects words and formats the lines. */
    private LineAssembler assembler;
    /** ArrayList of strings that contains all the current words being
     *  processed by controller. */
    private ArrayList<String> wordContainer;
}
