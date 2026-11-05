import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * CYCLE 2 (new methods): swap(wheel1,wheel2), lock(wheel), unlock(wheel),
 * spin(wheel,steps), spin(String[] setSymbols).
 *
 * Also tests how lock affects cycle 1 methods
 * (addWheel, delWheel, placeSymbol, spin), as that is the purpose of locking.
 *
 * Convention: should... (positive) / shouldNot... (negative) / limit... (boundary).
 * NOTE: spin(wheel,steps) waits 2 seconds per spin, so some tests take longer.
 */
public class SlotMachine2Test
{
    private static final int FIRST_X = 110;
    private static final int SPACING = 40;

    private SlotMachine machine;

    @BeforeEach
    public void setUp()
    {
        machine = freshMachine(3);
    }

    @AfterEach
    public void tearDown()
    {
        machine.makeInvisible();
        Thread.interrupted(); // clears any potential interruption left by a test
    }

    private static SlotMachine freshMachine(int n)
    {
        new Wheel().makeInvisible();
        return new SlotMachine(n);
    }

    private void growTo(int n)
    {
        while (machine.getWheels().size() < n) {
            machine.addWheel(machine.getWheels().size() + 1);
        }
    }

    private void assertWheelsArranged()
    {
        ArrayList<Wheel> w = machine.getWheels();
        for (int i = 0; i < w.size(); i++) {
            assertEquals(FIRST_X + SPACING * i, w.get(i).getpositionX(), "Wheel " + i + " X");
            assertEquals(i, w.get(i).getpositionA(), "Wheel " + i + " index");
        }
    }

    /**
     * swap(1, 3). Expected: ok true, wheels swap places in the list, their visible symbols
     * are swapped and location (x and index) is correct.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldSwapEndWheels()
    {
        List<String> before = machine.configuration();
        Wheel first = machine.getWheels().get(0);
        Wheel last = machine.getWheels().get(2);
        machine.swap(1, 3);
        assertTrue(machine.ok());
        assertSame(last, machine.getWheels().get(0));
        assertSame(first, machine.getWheels().get(2));
        assertEquals(before.get(2), machine.configuration().get(0));
        assertEquals(before.get(0), machine.configuration().get(2));
        assertWheelsArranged();
    }

    /**
     * swap(2, 3). Expected: ok true and configuration goes from [a,b,c] to [a,c,b].
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldSwapAdjacentWheels()
    {
        List<String> before = machine.configuration();
        machine.swap(2, 3);
        assertTrue(machine.ok());
        assertEquals(Arrays.asList(before.get(0), before.get(2), before.get(1)), machine.configuration());
        assertWheelsArranged();
    }

    /**
     * swap(3, 1) with inverted arguments. Expected: same result as swap(1, 3).
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldBeSymmetric()
    {
        List<String> before = machine.configuration();
        machine.swap(3, 1);
        assertTrue(machine.ok());
        assertEquals(Arrays.asList(before.get(2), before.get(1), before.get(0)), machine.configuration());
    }

    /**
     * swap(1, 2) twice. Expected: configuration and location return to initial.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldReturnToOriginWhenSwappingTwice()
    {
        List<String> before = machine.configuration();
        machine.swap(1, 2);
        machine.swap(1, 2);
        assertEquals(before, machine.configuration());
        assertWheelsArranged();
    }

    /**
     * swap(1, 3). Expected: symbol list does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAlterSymbolsWhenSwapping()
    {
        String[] before = machine.symbols();
        machine.swap(1, 3);
        assertArrayEquals(before, machine.symbols());
    }

    /**
     * swap(2, 2). Expected: ok true and everything remains the same.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitSwapWheelWithItselfDoesNotChangeAnything()
    {
        List<String> before = machine.configuration();
        machine.swap(2, 2);
        assertTrue(machine.ok());
        assertEquals(before, machine.configuration());
        assertWheelsArranged();
    }

    /**
     * swap(1, 50) with 50 wheels. Expected: ok true, ends swap and location is correct.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldSwapWithFiftyWheels()
    {
        growTo(50);
        List<String> before = machine.configuration();
        machine.swap(1, 50);
        assertTrue(machine.ok());
        assertEquals(before.get(49), machine.configuration().get(0));
        assertEquals(before.get(0), machine.configuration().get(49));
        assertWheelsArranged();
    }

    /**
     * swap with invalid pairs (0, 4, -1, MIN_VALUE, MAX_VALUE in either argument). Expected: ok false
     * and configuration does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSwapIfPositionDoesNotExist()
    {
        List<String> before = machine.configuration();
        int[][] invalid = {{0, 1}, {1, 0}, {4, 1}, {1, 4}, {-1, 2}, {2, -1}, {0, 0}, {4, 4},
            {Integer.MAX_VALUE, 1}, {1, Integer.MIN_VALUE}};
        for (int[] pair : invalid) {
            machine.swap(pair[0], pair[1]);
            assertFalse(machine.ok(), "swap(" + pair[0] + "," + pair[1] + ")");
            assertEquals(before, machine.configuration());
        }
        assertWheelsArranged();
    }

    /**
     * Locks wheel 1 and does swap(1, 2). Expected: ok false and nothing changes.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSwapIfFirstWheelIsLocked()
    {
        List<String> before = machine.configuration();
        machine.lock(1);
        machine.swap(1, 2);
        assertFalse(machine.ok());
        assertEquals(before, machine.configuration());
    }

    /**
     * Locks wheel 2 and does swap(1, 2). Expected: ok false and nothing changes.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSwapIfSecondWheelIsLocked()
    {
        List<String> before = machine.configuration();
        machine.lock(2);
        machine.swap(1, 2);
        assertFalse(machine.ok());
        assertEquals(before, machine.configuration());
    }

    /**
     * Locks wheel 3 and does swap(1, 2). Expected: ok true, because swap only checks the two
     * involved wheels.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSwapIfAnotherDifferentWheelIsLocked()
    {
        List<String> before = machine.configuration();
        machine.lock(3);
        machine.swap(1, 2);
        assertTrue(machine.ok());
        assertEquals(Arrays.asList(before.get(1), before.get(0), before.get(2)), machine.configuration());
    }

    /**
     * Locks, unlocks and does swap(1, 2). Expected: ok true.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAllowSwapAfterUnlock()
    {
        machine.lock(1);
        machine.unlock(1);
        machine.swap(1, 2);
        assertTrue(machine.ok());
    }

    /**
     * lock(2). Expected: ok true, only wheel 2 is locked and someWheelsAreLocked() is true.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldLockSpecifiedWheel()
    {
        machine.lock(2);
        assertTrue(machine.ok());
        assertFalse(machine.getWheels().get(1).getesMove());
        assertTrue(machine.getWheels().get(0).getesMove());
        assertTrue(machine.getWheels().get(2).getesMove());
        assertTrue(machine.someWheelsAreLocked());
    }

    /**
     * Locks wheel 2 and calls spin(). Expected: wheels 1 and 3 change; 2 does not.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSpinLockedWheelWithSpin()
    {
        List<String> before = machine.configuration();
        machine.lock(2);
        machine.spin();
        List<String> after = machine.configuration();
        assertNotEquals(before.get(0), after.get(0));
        assertEquals(before.get(1), after.get(1), "locked wheel should not spin");
        assertNotEquals(before.get(2), after.get(2));
    }

    /**
     * Locks wheel 1 and calls spin(1). Expected: ok true (considered successful) and configuration
     * does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSpinLockedWheelWithWheelSpin()
    {
        List<String> before = machine.configuration();
        machine.lock(1);
        machine.spin(1);
        assertTrue(machine.ok(), "spinning a locked wheel is considered successful");
        assertEquals(before, machine.configuration());
    }

    /**
     * Locks a wheel and calls addWheel(1). Expected: ok false and 3 wheels remain.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAddWheelsWhileAnyIsLocked()
    {
        machine.lock(2);
        machine.addWheel(1);
        assertFalse(machine.ok());
        assertEquals(3, machine.getWheels().size());
    }

    /**
     * With 5 wheels and one locked, delWheel(5). Expected: ok false and 5 wheels remain.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotRemoveWheelsWhileAnyIsLocked()
    {
        growTo(5);
        machine.lock(1);
        machine.delWheel(5);
        assertFalse(machine.ok());
        assertEquals(5, machine.getWheels().size());
    }

    /**
     * With 4 wheels and one locked, placeSymbol. Expected: ok false and configuration does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotPlaceSymbolWhileAnyIsLocked()
    {
        growTo(4);
        machine.lock(4);
        List<String> before = machine.configuration();
        machine.placeSymbol(1, machine.symbols()[2]);
        assertFalse(machine.ok());
        assertEquals(before, machine.configuration());
    }

    /**
     * Locks all 3 wheels and calls spin(). Expected: each lock with ok true and no wheel spins.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldAllowLockingAllWheels()
    {
        for (int i = 1; i <= 3; i++) {
            machine.lock(i);
            assertTrue(machine.ok());
        }
        List<String> before = machine.configuration();
        machine.spin();
        assertEquals(before, machine.configuration(), "with everything locked nothing spins");
    }

    /**
     * lock(1) twice. Expected: ok true and wheel remains locked.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldBeIdempotentWhenLockingTwice()
    {
        machine.lock(1);
        machine.lock(1);
        assertTrue(machine.ok());
        assertFalse(machine.getWheels().get(0).getesMove());
    }

    /**
     * lock with 0, 4, -1, MIN_VALUE and MAX_VALUE. Expected: ok false and no wheel is locked.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotLockNonExistentWheel()
    {
        for (int wheel : new int[] {0, 4, -1, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
            machine.lock(wheel);
            assertFalse(machine.ok(), "wheel=" + wheel);
            assertFalse(machine.someWheelsAreLocked(), "wheel=" + wheel);
        }
    }

    /**
     * lock(1). Expected: symbol list does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAlterSymbolsWhenLocking()
    {
        String[] before = machine.symbols();
        machine.lock(1);
        assertArrayEquals(before, machine.symbols());
    }

    /**
     * lock(1), unlock(1) and spin(1). Expected: ok true, no wheels locked and wheel spins again.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldUnlockAndAllowSpinning()
    {
        machine.lock(1);
        machine.unlock(1);
        assertTrue(machine.ok());
        assertTrue(machine.getWheels().get(0).getesMove());
        assertFalse(machine.someWheelsAreLocked());
        List<String> before = machine.configuration();
        machine.spin(1);
        assertNotEquals(before.get(0), machine.configuration().get(0));
    }

    /**
     * lock(2), unlock(2) and addWheel(1). Expected: ok true and 4 wheels.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAllowAddingWheelsAfterUnlock()
    {
        machine.lock(2);
        machine.unlock(2);
        machine.addWheel(1);
        assertTrue(machine.ok());
        assertEquals(4, machine.getWheels().size());
    }

    /**
     * Locks wheels 1 and 2 and unlocks 1. Expected: wheel 2 remains locked.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldRemainLockedIfOnlyAnotherIsUnlocked()
    {
        machine.lock(1);
        machine.lock(2);
        machine.unlock(1);
        assertTrue(machine.someWheelsAreLocked());
        assertFalse(machine.getWheels().get(1).getesMove());
    }

    /**
     * unlock(1) without having locked. Expected: ok true and no locked wheels.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitUnlockNotLockedWheelIsSuccessfulAndChangesNothing()
    {
        machine.unlock(1);
        assertTrue(machine.ok());
        assertFalse(machine.someWheelsAreLocked());
    }

    /**
     * unlock with 0, 4, -1, MIN_VALUE and MAX_VALUE with wheel 1 locked. Expected: ok false and wheel 1
     * remains locked.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotUnlockNonExistentWheel()
    {
        machine.lock(1);
        for (int wheel : new int[] {0, 4, -1, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
            machine.unlock(wheel);
            assertFalse(machine.ok(), "wheel=" + wheel);
            assertTrue(machine.someWheelsAreLocked(), "wheel 1 should remain locked");
        }
    }

    /**
     * spin(1, 1) measuring time. Expected: ok true, wheel advances one symbol and prior wait
     * is approximately 2 seconds.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldSpinOnceAndAwaitDelay()
    {
        String[] s = machine.symbols();
        long start = System.currentTimeMillis();
        machine.spin(1, 1);
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(machine.ok());
        assertEquals(s[1], machine.configuration().get(0));
        assertTrue(elapsed >= 1900, "should wait ~2s before spin, waited " + elapsed + " ms");
    }

    /**
     * spin(1, 2). Expected: wheel 1 advances two symbols and others do not change.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldSpinSpecifiedWheelMultipleTimes()
    {
        String[] s = machine.symbols();
        machine.spin(1, 2);
        assertTrue(machine.ok());
        assertEquals(s[2], machine.configuration().get(0));
        assertEquals(s[1], machine.configuration().get(1));
        assertEquals(s[2], machine.configuration().get(2));
    }

    /**
     * spin(1, 0). Expected: ok true, no changes and no waiting.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitZeroSpinsShouldNotMoveOrWait()
    {
        List<String> before = machine.configuration();
        long start = System.currentTimeMillis();
        machine.spin(1, 0);
        assertTrue(System.currentTimeMillis() - start < 500);
        assertTrue(machine.ok());
        assertEquals(before, machine.configuration());
    }

    /**
     * spin(1, -5) and spin(1, MIN_VALUE). Expected: ok true, no changes and no waiting.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitNegativeSpinsShouldNotMoveOrWait()
    {
        List<String> before = machine.configuration();
        long start = System.currentTimeMillis();
        machine.spin(1, -5);
        machine.spin(1, Integer.MIN_VALUE);
        assertTrue(System.currentTimeMillis() - start < 500);
        assertTrue(machine.ok());
        assertEquals(before, machine.configuration());
    }

    /**
     * Locks wheel 1 and calls spin(1, 5). Expected: ok true, does not spin and makes no pause.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSpinOrWaitIfWheelIsLocked()
    {
        List<String> before = machine.configuration();
        machine.lock(1);
        long start = System.currentTimeMillis();
        machine.spin(1, 5);
        assertTrue(System.currentTimeMillis() - start < 500, "should not pause if it does not spin");
        assertTrue(machine.ok());
        assertEquals(before, machine.configuration());
    }

    /**
     * spin(wheel, 3) with 0, 4, -1 and MAX_VALUE. Expected: ok false and configuration does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSpinNonExistentWheelSteps()
    {
        List<String> before = machine.configuration();
        for (int wheel : new int[] {0, 4, -1, Integer.MAX_VALUE}) {
            machine.spin(wheel, 3);
            assertFalse(machine.ok(), "wheel=" + wheel);
            assertEquals(before, machine.configuration());
        }
    }

    /**
     * Interrupts thread and calls spin(1, 3). Expected: ok false, interrupt flag is
     * restored and wheel does not spin.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldStopAndLeaveOkFalseIfThreadInterrupted()
    {
        List<String> before = machine.configuration();
        Thread.currentThread().interrupt();
        machine.spin(1, 3);
        boolean flagRestored = Thread.interrupted();
        assertFalse(machine.ok());
        assertTrue(flagRestored, "should restore interrupt flag");
        assertEquals(before, machine.configuration(), "should not spin if interrupted");
    }

    /**
     * spin(new String[]{A, B, C}). Expected: ok true, symbols() is [A,B,C] and, since wheels
     * showed colors that no longer exist, all jump to A (prize).
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldReplaceSymbolsAndSpinAllWheels()
    {
        machine.spin(new String[] {"#AAAAAA", "#BBBBBB", "#CCCCCC"});
        assertTrue(machine.ok());
        assertArrayEquals(new String[] {"#AAAAAA", "#BBBBBB", "#CCCCCC"}, machine.symbols());
        assertEquals(Arrays.asList("#AAAAAA", "#AAAAAA", "#AAAAAA"), machine.configuration());
        assertTrue(machine.isJackpot());
    }

    /**
     * spin with 1-symbol array. Expected: overwrites only the first and list keeps 3 symbols.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitWithFewerSymbolsOverwritesOnlyFirsts()
    {
        String[] before = machine.symbols();
        machine.spin(new String[] {"#AAAAAA"});
        String[] after = machine.symbols();
        assertEquals(3, after.length);
        assertEquals("#AAAAAA", after[0]);
        assertEquals(before[1], after[1]);
        assertEquals(before[2], after[2]);
    }

    /**
     * spin with 5-symbol array. Expected: list ends with 5 symbols and the last is the fifth.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitWithMoreSymbolsAddsExtras()
    {
        machine.spin(new String[] {"#111111", "#222222", "#333333", "#444444", "#555555"});
        assertEquals(5, machine.distinctSymbols());
        assertEquals("#555555", machine.symbols()[4]);
    }

    /**
     * spin(new String[0]). Expected: ok true, symbols do not change and wheels advance one
     * place.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitEmptyArrayDoesNotChangeSymbolsButSpins()
    {
        String[] symbols = machine.symbols();
        List<String> before = machine.configuration();
        machine.spin(new String[0]);
        assertTrue(machine.ok());
        assertArrayEquals(symbols, machine.symbols());
        assertEquals(Arrays.asList(before.get(1), before.get(2), before.get(0)), machine.configuration());
    }

    /**
     * Locks wheel 1 and calls spin(String[]). Expected: symbols update, wheel 1
     * does not spin and others do.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSpinLockedWheelsButUpdatesSymbols()
    {
        String lockedShows = machine.configuration().get(0);
        machine.lock(1);
        machine.spin(new String[] {"#AAAAAA", "#BBBBBB", "#CCCCCC"});
        assertArrayEquals(new String[] {"#AAAAAA", "#BBBBBB", "#CCCCCC"}, machine.symbols());
        assertEquals(lockedShows, machine.configuration().get(0));
        assertEquals("#AAAAAA", machine.configuration().get(1));
    }

    /**
     * spin((String[]) null). Expected: throws NullPointerException.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAcceptNullArray()
    {
        assertThrows(NullPointerException.class, () -> machine.spin((String[]) null));
    }

    /**
     * spin(String[]) with 2 symbols in a 50-wheel machine. Expected: ok true and list
     * remains with 3 symbols (2 of 3 existing are overwritten).
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldApplyWithFiftyWheels()
    {
        growTo(50);
        machine.spin(new String[] {"#AAAAAA", "#BBBBBB"});
        assertTrue(machine.ok());
        assertEquals(2 + 1, machine.distinctSymbols(), "2 of 3 existing symbols overwritten");
    }
}