package lab.snake;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

class TerminalSnakeTest {
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @AfterEach void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private String run(String[] args, String stdin) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
        System.setIn(new ByteArrayInputStream(stdin.getBytes()));
        TerminalSnake.main(args);
        return bos.toString();
    }

    @Test void instantiable() {
        assertNotNull(new TerminalSnake());
    }

    @Test void interactiveInputExhaustsWithoutQuit() {
        String out = run(new String[]{}, "1\nW\n");
        assertTrue(out.contains("status=RUNNING"));
        assertTrue(out.contains("head=Cell[x=3, y=1]"));
    }

    @Test void interactiveMultiCharKey() {
        String out = run(new String[]{}, "1\nWW\nQ\n");
        assertTrue(out.contains("Use one direction, R or Q."));
    }

    @Test void replayTickOnly() {
        String out = run(new String[]{"--replay","DD"}, "");
        assertTrue(out.contains("replay=DD"));
        assertTrue(out.contains("key=D"));
        assertTrue(out.contains("head=Cell[x=4, y=2]"));
    }

    @Test void replayWithScenarioArg() {
        String out = run(new String[]{"--scenario","LOOP","--replay","D"}, "");
        assertTrue(out.contains("scenario=LOOP"));
        assertTrue(out.contains("head=Cell[x=3, y=2]"));
    }

    @Test void replayHandlesReset() {
        String out = run(new String[]{"--replay","RD"}, "");
        assertTrue(out.contains("key=R"));
        assertTrue(out.contains("key=D"));
        assertTrue(out.contains("head=Cell[x=4, y=2]"));
    }

    @Test void mainRejectsUnknownArgument() {
        assertThrows(IllegalArgumentException.class, () -> run(new String[]{"bogus"}, ""));
    }

    @Test void interactiveDefaultChoiceQuits() {
        String out = run(new String[]{}, "1\nQ\n");
        assertTrue(out.contains("Choose a starting position [1]"));
        assertTrue(out.contains("status=RUNNING"));
    }

    @Test void interactiveChoiceWall() {
        String out = run(new String[]{}, "2\nQ\n");
        assertTrue(out.contains("status=RUNNING"));
    }

    @Test void interactiveChoiceLoop() {
        String out = run(new String[]{}, "3\nQ\n");
        assertTrue(out.contains("status=RUNNING"));
    }

    @Test void interactiveChoicePicnic() {
        String out = run(new String[]{}, "4\nQ\n");
        assertTrue(out.contains("status=RUNNING"));
    }

    @Test void interactiveRestart() {
        String out = run(new String[]{}, "1\nR\nQ\n");
        assertTrue(out.contains("status=RUNNING"));
    }

    @Test void interactiveContinueOnEmpty() {
        String out = run(new String[]{}, "1\n\nQ\n");
        assertTrue(out.contains("head=Cell[x=4, y=2]"));
    }

    @Test void interactiveWasdMove() {
        String out = run(new String[]{}, "1\nD\nQ\n");
        assertTrue(out.contains("head=Cell[x=4, y=2]"));
    }

    @Test void interactiveInvalidKey() {
        String out = run(new String[]{}, "1\nX\nQ\n");
        assertTrue(out.contains("Use one direction, R or Q."));
    }

    @Test void interactiveRoundEnds() {
        String out = run(new String[]{}, "1\nW\nW\nW\nW\nQ\n");
        assertTrue(out.contains("Round ended. R restarts; Q quits."));
    }
}