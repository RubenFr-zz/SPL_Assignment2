package test.java.bgu.spl.mics;

import main.java.bgu.spl.mics.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    private Future<Integer> future;
    private Integer result = 10;

    @BeforeEach
    public void setUp() {
        future = new Future<>();
    }

    @Test
    public void getTest() {
        future.resolve(result);
        assertEquals(result,future.get());
    }

    @Test
    public void resolveTest() {
        future.resolve(result);
        assertEquals(result,future.get());
    }

    @Test
    public void isDoneTest() {
        assertFalse(future.isDone());
        future.resolve(result);
        assertTrue(future.isDone());
    }

    @Test
    public void getTimeUnitsTest() {
        long timeout = 1000;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        assertNull(future.get(timeout,unit));
        future.resolve(result);
        assertEquals(result,future.get(timeout,unit));
    }

}