package lab.snake;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class SnakeGameTest {
    @Test void normalMovePreservesLength() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);g.tick(Direction.RIGHT);
        assertEquals(new Cell(4,2),g.head());assertEquals(3,g.body().size());assertEquals(SnakeGame.Status.RUNNING,g.status());
    }
    @Test void foodGrowsSnakeAndAddsTen() {
        SnakeGame g=new SnakeGame(Scenario.PICNIC);g.tick(Direction.RIGHT);
        assertEquals(4,g.body().size());assertEquals(10,g.score());assertFalse(g.body().contains(g.food()));
    }
    @Test void reverseDirectionIsIgnored() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);g.tick(Direction.LEFT);
        assertEquals(new Cell(4,2),g.head());assertEquals(Direction.RIGHT,g.direction());
    }
    @Test void topWallEndsRound() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);
        g.tick(Direction.UP);g.tick(Direction.UP);g.tick(Direction.UP);
        assertEquals(SnakeGame.Status.WALL,g.status());assertEquals(new Cell(3,0),g.head());
    }
    @Test void endedRoundDoesNotMove() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);
        g.tick(Direction.UP);g.tick(Direction.UP);g.tick(Direction.UP);Cell head=g.head();
        g.tick(Direction.RIGHT);assertEquals(head,g.head());assertEquals(SnakeGame.Status.WALL,g.status());
    }
    @Test void restartRestoresStartingBody() {
        SnakeGame g=new SnakeGame(Scenario.NORMAL);g.tick(Direction.RIGHT);g.reset();
        assertEquals(Scenario.NORMAL.snake,g.body());assertEquals(Scenario.NORMAL.food,g.food());assertEquals(SnakeGame.Status.RUNNING,g.status());
    }
    @Test void loopMoveRightKeepsTrail() {
        SnakeGame g=new SnakeGame(Scenario.LOOP);
        g.tick(Direction.RIGHT);
        assertEquals(new Cell(3,2),g.head());
        assertEquals(List.of(new Cell(3,2),new Cell(2,2),new Cell(1,2),new Cell(1,3)),g.body());
        assertEquals(SnakeGame.Status.RUNNING,g.status());
    }
    @Test void enteringOldTailCellIsLegal() {
        SnakeGame g=new SnakeGame(Scenario.LOOP);
        g.tick(Direction.DOWN);
        assertEquals(new Cell(2,3),g.head());assertEquals(SnakeGame.Status.RUNNING,g.status());assertEquals(4,g.body().size());
    }
    @Test void nonTailBodyCellStillEndsRound() {
        SnakeGame g=new SnakeGame(Scenario.LOOP);
        for (char k : "DSDAASA".toCharArray()) g.tick(Direction.fromKey(k));
        assertEquals(5,g.body().size());
        Cell headBefore=g.head();
        g.tick(Direction.UP);
        assertEquals(headBefore,g.head());assertEquals(SnakeGame.Status.SELF,g.status());
    }
}
