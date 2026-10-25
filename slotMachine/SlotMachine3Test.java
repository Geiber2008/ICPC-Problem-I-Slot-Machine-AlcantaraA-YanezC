import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * CYCLE 3 (new method): SlotMachine(int n).
 *
 * Also tests integration scenarios of cycles 1 and 2 with machines of
 * different sizes (3 and 50 wheel boundaries behave differently depending on how
 * the machine was created).
 *
 * Cycle 3 diagram shows lock(wheel) with int return type; in delivered code
 * lock is void, so tests follow code (void + ok()).
 *
 * Convention: should... (positive) / shouldNot... (negative) / limit... (boundary).
 */
public class SlotMachine3Test
{
    private static final int MIN_WHEELS = 3;
    private static final int MAX_WHEELS = 50;
    private static final int FIRST_X = 110;
    private static final int SPACING = 40;

    private SlotMachine machine;

    @BeforeEach
    public void setUp()
    {
        machine = freshMachine(MIN_WHEELS);
    }

    @AfterEach
    public void tearDown()
    {
        machine.makeInvisible();
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
     * SlotMachine(n) with n = 3, 4, 5, 10, 25 and 50. Expected: has exactly n wheels,
     * configuration() has n elements and ok true.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCreateExactlyNWheels()
    {
        for (int n : new int[] {3, 4, 5, 10, 25, 50}) {
            machine = freshMachine(n);
            assertEquals(n, machine.getWheels().size(), "n=" + n);
            assertEquals(n, machine.configuration().size(), "n=" + n);
            assertTrue(machine.ok(), "n=" + n);
        }
    }

    /**
     * SlotMachine(n) with n = 3, 7 and 50. Expected: there are n symbols.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCreateOneSymbolPerWheel()
    {
        for (int n : new int[] {3, 7, 50}) {
            machine = freshMachine(n);
            assertEquals(n, machine.distinctSymbols(), "n=" + n);
            assertEquals(n, machine.symbols().length, "n=" + n);
        }
    }

    /**
     * SlotMachine(n) with n = 3, 8 and 50. Expected: wheel i at x = 110 + 40*i and index i.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldLocateWheelsUniformlySeparated()
    {
        for (int n : new int[] {3, 8, 50}) {
            machine = freshMachine(n);
            assertWheelsArranged();
        }
    }

    /**
     * SlotMachine(6). Expected: each wheel shows its symbol and getPositionSymbolos() is [0..5].
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldShowItsOwnSymbolOnEachWheel()
    {
        machine = freshMachine(6);
        assertEquals(Arrays.asList(machine.symbols()), machine.configuration());
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5), machine.getPositionSymbolos());
    }

    /**
     * SlotMachine(5). Expected: no wheel locked and all of type Normal.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCreateUnlockedAndNormalWheels()
    {
        machine = freshMachine(5);
        assertFalse(machine.someWheelsAreLocked());
        for (Wheel w : machine.getWheels()) {
            assertEquals("Normal", w.getType());
        }
    }

    /**
     * Compares SlotMachine() with SlotMachine(3). Expected: same number of wheels and symbols.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCreateSameMachineAsDefaultConstructorForN3()
    {
        SlotMachine byDefault = machine;
        machine = freshMachine(3);
        assertEquals(byDefault.getWheels().size(), machine.getWheels().size());
        assertEquals(byDefault.distinctSymbols(), machine.distinctSymbols());
    }

    /**
     * SlotMachine(8). Expected: isJackpot() false.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotStartWithPrizeWithMultipleWheels()
    {
        machine = freshMachine(8);
        assertFalse(machine.isJackpot());
    }

    /**
     * SlotMachine(2). Expected: 2 wheels, 2 symbols and ok true (constructor does not require minimum
     * of 3).
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitWithTwoWheelsCreatesOnlyTwo()
    {
        machine = freshMachine(2);
        assertEquals(2, machine.getWheels().size());
        assertEquals(2, machine.distinctSymbols());
        assertTrue(machine.ok());
    }

    /**
     * SlotMachine(1). Expected: 1 wheel and isJackpot() true.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitWithOneWheelCreatesOnlyOne()
    {
        machine = freshMachine(1);
        assertEquals(1, machine.getWheels().size());
        assertTrue(machine.isJackpot());
    }

    /**
     * SlotMachine(0). Expected: 1 wheel is created, because the first is always created.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitWithZeroWheelsAlwaysCreatesAtLeastOne()
    {
        machine = freshMachine(0);
        assertEquals(1, machine.getWheels().size());
        assertTrue(machine.ok());
    }

    /**
     * SlotMachine(51). Expected: creates 51 wheels (constructor does not check maximum), but
     * addWheel rejects adding more.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitAboveMaxConstructorDoesNotReject()
    {
        machine = freshMachine(MAX_WHEELS + 1);
        assertEquals(MAX_WHEELS + 1, machine.getWheels().size());
        machine.addWheel(1);
        assertFalse(machine.ok(), "addWheel must reject when exceeding 50");
        assertEquals(MAX_WHEELS + 1, machine.getWheels().size());
    }

    /**
     * addWheel(3) in a 2-wheel machine. Expected: ok true, 3 wheels and correct placement.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitTwoWheelMachineCanGrowToThree()
    {
        machine = freshMachine(2);
        machine.addWheel(3);
        assertTrue(machine.ok());
        assertEquals(3, machine.getWheels().size());
        assertWheelsArranged();
    }

    /**
     * delWheel(1) with 1, 2 and 3 wheel machines. Expected: ok false and wheel count does not
     * change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAllowRemovingWheelsInMachineUnderFour()
    {
        for (int n : new int[] {1, 2, 3}) {
            machine = freshMachine(n);
            machine.delWheel(1);
            assertFalse(machine.ok(), "n=" + n);
            assertEquals(n, machine.getWheels().size(), "n=" + n);
        }
    }

    /**
     * placeSymbol in a 2-wheel machine. Expected: ok false and configuration does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAllowPlacingSymbolInMachineUnderFour()
    {
        machine = freshMachine(2);
        List<String> before = machine.configuration();
        machine.placeSymbol(1, machine.symbols()[1]);
        assertFalse(machine.ok());
        assertEquals(before, machine.configuration());
    }

    /**
     * 4-wheel machine: placeSymbol and delWheel. Expected: placeSymbol leaves requested symbol and
     * delWheel(4) has ok true and leaves 3 wheels.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitFourWheelMachineAllowsRemovalAndPlacement()
    {
        machine = freshMachine(4);
        machine.placeSymbol(1, machine.symbols()[3]);
        assertEquals(machine.symbols()[3], machine.configuration().get(0));
        machine.delWheel(4);
        assertTrue(machine.ok());
        assertEquals(3, machine.getWheels().size());
    }

    /**
     * CHARACTERIZATION TEST. Creates two machines without resetting static list. Expected:
     * both share the same 6 symbols. If this design is fixed, update test.
     * <p>
     * Type: Characterization.
     */
    @Test
    public void characterizationSymbolListIsSharedBetweenMachines()
    {
        SlotMachine first = freshMachine(3);
        SlotMachine second = new SlotMachine(3);
        assertEquals(first.distinctSymbols(), second.distinctSymbols());
        assertEquals(6, second.distinctSymbols());
        first.makeInvisible();
        machine = second;
    }

    /**
     * Grows to 50 wheels and removes down to 3. Expected: each deletion ok true and correct
     * placement at each end.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldGrowToMaxAndReturnToMinRelocatingAlways()
    {
        growTo(MAX_WHEELS);
        assertWheelsArranged();
        while (machine.getWheels().size() > MIN_WHEELS) {
            machine.delWheel(1);
            assertTrue(machine.ok());
        }
        assertEquals(MIN_WHEELS, machine.getWheels().size());
        assertWheelsArranged();
    }

    /**
     * With 50 wheels tries to add 5 times. Expected: all ok false and 50 wheels remain.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldMaintainMaxEvenIfTriedMultipleTimes()
    {
        machine = freshMachine(MAX_WHEELS);
        for (int i = 0; i < 5; i++) {
            machine.addWheel(1 + i);
            assertFalse(machine.ok());
        }
        assertEquals(MAX_WHEELS, machine.getWheels().size());
    }

    /**
     * lock(3) and addWheel(1); then unlock(3) and addWheel(1). Expected: first fails; second
     * has ok true, 6 wheels remain and well arranged.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldLockAndUnlockAlternatingWithAdd()
    {
        machine = freshMachine(5);
        machine.lock(3);
        machine.addWheel(1);
        assertFalse(machine.ok());
        machine.unlock(3);
        machine.addWheel(1);
        assertTrue(machine.ok());
        assertEquals(6, machine.getWheels().size());
        assertWheelsArranged();
    }

    /**
     * swap, addWheel and delWheel chained. Expected: all operations ok true and placement
     * remains correct.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldSwapAndKeepRelocatingProperly()
    {
        machine = freshMachine(5);
        machine.swap(1, 5);
        machine.addWheel(3);
        assertTrue(machine.ok());
        machine.delWheel(1);
        assertTrue(machine.ok());
        assertWheelsArranged();
    }

    /**
     * With 5 wheels, aligns all with placeSymbol and then spins one. Expected: prize when aligned
     * and disappears when spinning only one.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldSimulateCompleteGameUntilPrize()
    {
        machine = freshMachine(5);
        assertFalse(machine.isJackpot());
        String target = machine.symbols()[0];
        for (int i = 1; i <= 5; i++) {
            machine.placeSymbol(i, target);
        }
        assertTrue(machine.isJackpot());
        machine.spin(3);
        assertFalse(machine.isJackpot());
    }

    /**
     * With 4 wheels executes invalid operations (addWheel, delWheel, swap, lock, unlock, spin).
     * Expected: 4 wheels remain, well arranged and without locks.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAlterSizeIfAnyOperationFails()
    {
        machine = freshMachine(4);
        machine.addWheel(0);
        machine.delWheel(9);
        machine.swap(0, 1);
        machine.lock(9);
        machine.unlock(9);
        machine.spin(9);
        assertEquals(4, machine.getWheels().size());
        assertWheelsArranged();
        assertFalse(machine.someWheelsAreLocked());
    }
}