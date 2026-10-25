import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * CYCLE 1: SlotMachine(), addWheel(pos), delWheel(pos), addSymbol(color,pos),
 * delSymbol(symbol), placeSymbol(wheel,symbol), spin(wheel), spin(), symbols(),
 * distinctSymbols(), configuration(), isJackpot(), makeVisible(),
 * makeInvisible(), exit(), ok().
 *
 * Naming convention:
 *   should...  -> behavior that SHOULD occur (positive test)
 *   shouldNot... -> behavior that SHOULD NOT occur (negative test)
 *   limit...   -> boundary value tests for the program
 *
 * Program constants: minimum 3 wheels, maximum 50 wheels,
 * first wheel at x=110, 40 px spacing between wheels.
 */
public class SlotMachine1Test
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

    /** The symbol list is static (shared); it resets before each machine. */
    private static SlotMachine freshMachine(int n)
    {
        new Wheel().makeInvisible(); // default constructor clears symbols
        return new SlotMachine(n);
    }

    /** Adds normal wheels to the end until reaching n. */
    private void growTo(int n)
    {
        while (machine.getWheels().size() < n) {
            machine.addWheel(machine.getWheels().size() + 1);
        }
    }

    /** Verifies that each wheel is at its expected index and X coordinate. */
    private void assertWheelsArranged()
    {
        ArrayList<Wheel> w = machine.getWheels();
        for (int i = 0; i < w.size(); i++) {
            assertEquals(FIRST_X + SPACING * i, w.get(i).getpositionX(), "Wheel " + i + " X coordinate");
            assertEquals(i, w.get(i).getpositionA(), "Wheel " + i + " index");
        }
    }

    /** Leaves all wheels showing the same symbol (requires more than 3 wheels). */
    private void forceJackpot()
    {
        String target = machine.symbols()[0];
        for (int i = 1; i <= machine.getWheels().size(); i++) {
            machine.placeSymbol(i, target);
        }
    }

    /**
     * Creates the machine with the default constructor. Expected: has 3 wheels and the visible
     * configuration has 3 elements.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCreateThreeWheelsByDefault()
    {
        assertEquals(MIN_WHEELS, machine.getWheels().size());
        assertEquals(MIN_WHEELS, machine.configuration().size());
    }

    /**
     * Builds the machine. Expected: ok() returns true.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldStartWithOkAsTrue()
    {
        assertTrue(machine.ok());
    }

    /**
     * Builds the machine with 3 wheels. Expected: distinctSymbols() and symbols() length are 3.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCreateOneSymbolPerWheel()
    {
        assertEquals(MIN_WHEELS, machine.distinctSymbols());
        assertEquals(MIN_WHEELS, machine.symbols().length);
    }

    /**
     * Compares configuration() with symbols(). Expected: each wheel shows the symbol of its own index.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldShowItsOwnSymbolOnEachWheel()
    {
        assertEquals(Arrays.asList(machine.symbols()), machine.configuration());
    }

    /**
     * Checks initial location. Expected: wheel i at x = 110 + 40*i with index i.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldLocateUniformlySeparatedWheels()
    {
        assertWheelsArranged();
    }

    /**
     * Checks generated colors. Expected: all comply with the uppercase #RRGGBB format.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldGenerateValidHexadecimalColors()
    {
        for (String color : machine.symbols()) {
            assertTrue(color.matches("#[0-9A-F]{6}"), "invalid format: " + color);
        }
    }

    /**
     * Creates 40 machines and checks each R, G, and B channel. Expected: all values are between 1 and
     * 254 (never 0 nor 255).
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitColorChannelsShouldBeBetween1And254()
    {
        for (int k = 0; k < 40; k++) {
            machine = freshMachine(MIN_WHEELS);
            for (String color : machine.symbols()) {
                for (int c = 0; c < 3; c++) {
                    int channel = Integer.parseInt(color.substring(1 + 2 * c, 3 + 2 * c), 16);
                    assertTrue(channel >= 1 && channel <= 254, "channel out of range in " + color);
                }
            }
        }
    }

    /**
     * Builds the machine. Expected: someWheelsAreLocked() is false.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotLockWheelsOnCreation()
    {
        assertFalse(machine.someWheelsAreLocked());
    }

    /**
     * Builds the machine with different colors. Expected: isJackpot() is false.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotStartWithAPrize()
    {
        assertFalse(machine.isJackpot());
    }

    /**
     * addWheel(1) in a machine of 3. Expected: ok true, 4 wheels, the original wheel shifts to
     * index 1 and all are relocated.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAddWheelAtTheBeginning()
    {
        Wheel originalFirst = machine.getWheels().get(0);
        machine.addWheel(1);
        assertTrue(machine.ok());
        assertEquals(4, machine.getWheels().size());
        assertSame(originalFirst, machine.getWheels().get(1), "original wheel should shift");
        assertWheelsArranged();
    }

    /**
     * addWheel(2) in a machine of 3. Expected: ok true, 4 wheels, the original second wheel shifts
     * to position 3 and all are relocated.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAddWheelInTheMiddle()
    {
        Wheel second = machine.getWheels().get(1);
        machine.addWheel(2);
        assertTrue(machine.ok());
        assertEquals(4, machine.getWheels().size());
        assertSame(second, machine.getWheels().get(2));
        assertWheelsArranged();
    }

    /**
     * addWheel(4) in a machine of 3. Expected: ok true, 4 wheels and previous ones do not move.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAddWheelAtTheEnd()
    {
        Wheel last = machine.getWheels().get(2);
        machine.addWheel(4);
        assertTrue(machine.ok());
        assertEquals(4, machine.getWheels().size());
        assertSame(last, machine.getWheels().get(2), "previous ones do not move");
        assertWheelsArranged();
    }

    /**
     * Adds a wheel with addWheel(pos). Expected: it is of type Normal and displays a symbol that already
     * exists in the list.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCreateNormalWheelWithExistingSymbol()
    {
        machine.addWheel(2);
        Wheel added = machine.getWheels().get(1);
        assertEquals("Normal", added.getType());
        assertTrue(Arrays.asList(machine.symbols()).contains(added.getshowSymbol()));
    }

    /**
     * Adds a wheel. Expected: distinctSymbols() remains 3.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotCreateNewSymbolsWhenAddingWheel()
    {
        machine.addWheel(1);
        assertEquals(MIN_WHEELS, machine.distinctSymbols());
    }

    /**
     * addWheel(0). Expected: ok false and wheel count does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAddWheelAtPositionZero()
    {
        machine.addWheel(0);
        assertFalse(machine.ok());
        assertEquals(MIN_WHEELS, machine.getWheels().size());
    }

    /**
     * addWheel(5) with 3 wheels (max valid is 4). Expected: ok false and 3 wheels.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAddWheelPastEndPlusOne()
    {
        machine.addWheel(5);
        assertFalse(machine.ok());
        assertEquals(MIN_WHEELS, machine.getWheels().size());
    }

    /**
     * addWheel with -1, Integer.MIN_VALUE, Integer.MAX_VALUE and 100. Expected: for all, ok false and 3
     * wheels.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldNotAddWheelWithExtremePositions()
    {
        for (int pos : new int[] {-1, Integer.MIN_VALUE, Integer.MAX_VALUE, 100}) {
            machine.addWheel(pos);
            assertFalse(machine.ok(), "pos=" + pos);
            assertEquals(MIN_WHEELS, machine.getWheels().size(), "pos=" + pos);
        }
    }

    /**
     * Adds wheels one by one starting from 3. Expected: all insertions up to 50 have ok true and
     * the machine ends up with 50 well-arranged wheels.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldAllowReachingExactlyFiftyWheels()
    {
        for (int i = MIN_WHEELS; i < MAX_WHEELS; i++) {
            machine.addWheel(machine.getWheels().size() + 1);
            assertTrue(machine.ok(), "failed when moving to " + (i + 1) + " wheels");
        }
        assertEquals(MAX_WHEELS, machine.getWheels().size());
        assertWheelsArranged();
    }

    /**
     * With 50 wheels, tries to add at the beginning and at the end. Expected: ok false and 50 wheels remain.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldNotExceedMaxWheels()
    {
        growTo(MAX_WHEELS);
        machine.addWheel(1);
        assertFalse(machine.ok());
        assertEquals(MAX_WHEELS, machine.getWheels().size());
        machine.addWheel(MAX_WHEELS + 1);
        assertFalse(machine.ok());
        assertEquals(MAX_WHEELS, machine.getWheels().size());
    }

    /**
     * Failed addWheel(0). Expected: configuration and wheel locations do not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAlterMachineWhenItFails()
    {
        List<String> before = machine.configuration();
        machine.addWheel(0);
        assertEquals(before, machine.configuration());
        assertWheelsArranged();
    }

    /**
     * delWheel(1) with 4 wheels. Expected: ok true, 3 wheels, the second becomes the first and
     * all are relocated.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldRemoveWheelAtTheBeginning()
    {
        growTo(4);
        Wheel second = machine.getWheels().get(1);
        machine.delWheel(1);
        assertTrue(machine.ok());
        assertEquals(3, machine.getWheels().size());
        assertSame(second, machine.getWheels().get(0));
        assertWheelsArranged();
    }

    /**
     * delWheel(3) with 5 wheels. Expected: ok true, 4 wheels and all relocated.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldRemoveWheelInTheMiddle()
    {
        growTo(5);
        machine.delWheel(3);
        assertTrue(machine.ok());
        assertEquals(4, machine.getWheels().size());
        assertWheelsArranged();
    }

    /**
     * delWheel(4) with 4 wheels. Expected: ok true, 3 wheels and correct relocation.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldRemoveWheelAtTheEnd()
    {
        growTo(4);
        machine.delWheel(4);
        assertTrue(machine.ok());
        assertEquals(3, machine.getWheels().size());
        assertWheelsArranged();
    }

    /**
     * Removes a wheel. Expected: it is no longer in getWheels() and isShown() is false.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldHideAndRemoveDeletedWheelFromList()
    {
        growTo(4);
        Wheel doomed = machine.getWheels().get(3);
        machine.delWheel(4);
        assertFalse(machine.getWheels().contains(doomed));
        assertFalse(doomed.isShown());
    }

    /**
     * Removes a wheel. Expected: the symbol list remains unchanged.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotModifySymbolsWhenRemovingWheel()
    {
        growTo(4);
        String[] before = machine.symbols();
        machine.delWheel(1);
        assertArrayEquals(before, machine.symbols());
    }

    /**
     * delWheel(1) with 3 wheels (minimum). Expected: ok false and 3 wheels remain.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldNotRemoveBelowMinimumWheels()
    {
        machine.delWheel(1);
        assertFalse(machine.ok());
        assertEquals(MIN_WHEELS, machine.getWheels().size());
    }

    /**
     * With 5 wheels, removes two and then tries another. Expected: the first two ok true; the
     * third ok false and 3 wheels remain.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldRemoveDownToThreeAndThenReject()
    {
        growTo(5);
        machine.delWheel(5);
        assertTrue(machine.ok());
        machine.delWheel(4);
        assertTrue(machine.ok());
        assertEquals(MIN_WHEELS, machine.getWheels().size());
        machine.delWheel(1);
        assertFalse(machine.ok());
        assertEquals(MIN_WHEELS, machine.getWheels().size());
    }

    /**
     * delWheel with 0, 5, -1, MIN_VALUE and MAX_VALUE in a 4-wheel machine. Expected: for all, ok
     * false and 4 wheels remain.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotRemoveNonExistentWheel()
    {
        growTo(4);
        for (int pos : new int[] {0, 5, -1, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
            machine.delWheel(pos);
            assertFalse(machine.ok(), "pos=" + pos);
            assertEquals(4, machine.getWheels().size(), "pos=" + pos);
        }
    }

    /**
     * delWheel(50) with 50 wheels. Expected: ok true, 49 wheels and correct relocation.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldRemoveFromFiftyWheels()
    {
        growTo(MAX_WHEELS);
        machine.delWheel(MAX_WHEELS);
        assertTrue(machine.ok());
        assertEquals(MAX_WHEELS - 1, machine.getWheels().size());
        assertWheelsArranged();
    }

    /**
     * addSymbol(color, 0). Expected: ok true, 4 symbols and the new one is first.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAddSymbolAtTheBeginning()
    {
        machine.addSymbol("#ABCDEF", 0);
        assertTrue(machine.ok());
        assertEquals(4, machine.distinctSymbols());
        assertEquals("#ABCDEF", machine.symbols()[0]);
    }

    /**
     * addSymbol(color, 1). Expected: 4 symbols and the new one is at index 1.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAddSymbolInTheMiddle()
    {
        machine.addSymbol("#ABCDEF", 1);
        assertEquals("#ABCDEF", machine.symbols()[1]);
        assertEquals(4, machine.distinctSymbols());
    }

    /**
     * addSymbol(color, 3). Expected: the new symbol is at index 3 (last).
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAddSymbolAtTheEnd()
    {
        machine.addSymbol("#ABCDEF", 3);
        assertEquals("#ABCDEF", machine.symbols()[3]);
    }

    /**
     * Adds a symbol at the beginning. Expected: getPositionSymbolos() goes from [0,1,2] to [1,2,3].
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldUpdatePositionsOfVisibleSymbols()
    {
        machine.addSymbol("#ABCDEF", 0);
        assertEquals(Arrays.asList(1, 2, 3), machine.getPositionSymbolos());
    }

    /**
     * Adds a symbol. Expected: the visible configuration does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotChangeWhatWheelsDisplay()
    {
        List<String> before = machine.configuration();
        machine.addSymbol("#ABCDEF", 1);
        assertEquals(before, machine.configuration());
    }

    /**
     * addSymbol with position -5 and Integer.MIN_VALUE. Expected: the symbol is inserted at index
     * 0.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitNegativePositionAdjustsToBeginning()
    {
        machine.addSymbol("#ABCDEF", -5);
        assertEquals("#ABCDEF", machine.symbols()[0]);
        machine.addSymbol("#111111", Integer.MIN_VALUE);
        assertEquals("#111111", machine.symbols()[0]);
    }

    /**
     * addSymbol with position 100 and Integer.MAX_VALUE. Expected: the symbol is inserted at the end.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitPositionGreaterThansizeAdjustsToEnd()
    {
        machine.addSymbol("#ABCDEF", 100);
        String[] s = machine.symbols();
        assertEquals("#ABCDEF", s[s.length - 1]);
        machine.addSymbol("#111111", Integer.MAX_VALUE);
        s = machine.symbols();
        assertEquals("#111111", s[s.length - 1]);
    }

    /**
     * Adds an existing color. Expected: ok true and distinctSymbols() counts 4 (counts
     * duplicates).
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAllowRepeatedSymbols()
    {
        String repeated = machine.symbols()[0];
        machine.addSymbol(repeated, 0);
        assertTrue(machine.ok());
        assertEquals(4, machine.distinctSymbols());
    }

    /**
     * Adds 200 symbols at the end. Expected: all with ok true and 203 symbols in total.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldAddManySymbols()
    {
        for (int i = 0; i < 200; i++) {
            machine.addSymbol(String.format("#%06X", i + 1), machine.distinctSymbols());
            assertTrue(machine.ok());
        }
        assertEquals(MIN_WHEELS + 200, machine.distinctSymbols());
    }

    /**
     * delSymbol of an existing symbol. Expected: ok true, 2 symbols and no longer appears in
     * symbols().
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldRemoveExistingSymbol()
    {
        String victim = machine.symbols()[1];
        machine.delSymbol(victim);
        assertTrue(machine.ok());
        assertEquals(2, machine.distinctSymbols());
        assertFalse(Arrays.asList(machine.symbols()).contains(victim));
    }

    /**
     * Removes the symbol displayed by wheel 2. Expected: getPositionSymbolos() returns [0, -1,
     * 1].
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldMarkMinusOneForPositionOfDeletedVisibleSymbol()
    {
        machine.delSymbol(machine.symbols()[1]);
        assertEquals(Arrays.asList(0, -1, 1), machine.getPositionSymbolos());
    }

    /**
     * delSymbol of a non-existent color. Expected: the symbol list does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotRemoveNonExistentSymbol()
    {
        String[] before = machine.symbols();
        machine.delSymbol("#NOEXISTE");
        assertArrayEquals(before, machine.symbols());
    }

    /**
     * Adds #abcdef and removes #ABCDEF. Expected: they do not match (case-sensitive) and 4 symbols remain.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotTreatUppercaseAsLowercaseEqual()
    {
        String lower = "#abcdef";
        machine.addSymbol(lower, 0);
        machine.delSymbol("#ABCDEF");
        assertEquals(4, machine.distinctSymbols());
    }

    /**
     * With a repeated color, delSymbol. Expected: only one occurrence is removed; 3 symbols remain
     * and the color is still present.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldRemoveOnlyFirstOccurrence()
    {
        String repeated = machine.symbols()[0];
        machine.addSymbol(repeated, 3);
        machine.delSymbol(repeated);
        assertEquals(3, machine.distinctSymbols());
        assertTrue(Arrays.asList(machine.symbols()).contains(repeated));
    }

    /**
     * Removes all 3 symbols. Expected: distinctSymbols() is 0 and symbols() is empty.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldAllowRemovingAllSymbols()
    {
        for (String s : machine.symbols()) {
            machine.delSymbol(s);
        }
        assertEquals(0, machine.distinctSymbols());
        assertEquals(0, machine.symbols().length);
    }

    /**
     * Without symbols, executes spin() and spin(1). Expected: no exception thrown, ok true and
     * configuration does not change.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitWithoutSymbolsSpinShouldNotFail()
    {
        for (String s : machine.symbols()) {
            machine.delSymbol(s);
        }
        List<String> before = machine.configuration();
        machine.spin();
        machine.spin(1);
        assertTrue(machine.ok());
        assertEquals(before, machine.configuration());
    }

    /**
     * Clears list and adds a symbol. Expected: list has 1 symbol, the added one.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldAllowAddingSymbolAfterClearingList()
    {
        for (String s : machine.symbols()) {
            machine.delSymbol(s);
        }
        machine.addSymbol("#ABCDEF", 0);
        assertEquals(1, machine.distinctSymbols());
        assertEquals("#ABCDEF", machine.symbols()[0]);
    }

    /**
     * placeSymbol(2, symbol) with 4 wheels. Expected: wheel 2 shows that symbol.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldPlaceSpecifiedSymbolOnWheel()
    {
        growTo(4);
        String target = machine.symbols()[2];
        machine.placeSymbol(2, target);
        assertEquals(target, machine.configuration().get(1));
    }

    /**
     * placeSymbol on wheel 2. Expected: other wheels show what they showed before.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAlterOtherWheels()
    {
        growTo(4);
        List<String> before = machine.configuration();
        machine.placeSymbol(2, machine.symbols()[2]);
        List<String> after = machine.configuration();
        for (int i = 0; i < before.size(); i++) {
            if (i != 1) {
                assertEquals(before.get(i), after.get(i), "wheel " + (i + 1));
            }
        }
    }

    /**
     * placeSymbol of the last symbol (almost a full turn). Expected: the wheel shows it.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldPlaceLastSymbolOfList()
    {
        growTo(4);
        String[] s = machine.symbols();
        machine.placeSymbol(1, s[s.length - 1]);
        assertEquals(s[s.length - 1], machine.configuration().get(0));
    }

    /**
     * placeSymbol with the symbol the wheel is already showing. Expected: does not change.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldLeaveSameIfAlreadyShowingThatSymbol()
    {
        growTo(4);
        String current = machine.configuration().get(0);
        machine.placeSymbol(1, current);
        assertEquals(current, machine.configuration().get(0));
    }

    /**
     * placeSymbol with 3 wheels (minimum). Expected: ok false and configuration does not change.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldNotPlaceWithOnlyMinimumWheels()
    {
        List<String> before = machine.configuration();
        machine.placeSymbol(1, machine.symbols()[2]);
        assertFalse(machine.ok());
        assertEquals(before, machine.configuration());
    }

    /**
     * placeSymbol with wheel 0, 5, -1 and MAX_VALUE. Expected: ok false and configuration does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotPlaceOnNonExistentWheel()
    {
        growTo(4);
        List<String> before = machine.configuration();
        for (int wheel : new int[] {0, 5, -1, Integer.MAX_VALUE}) {
            machine.placeSymbol(wheel, machine.symbols()[0]);
            assertFalse(machine.ok(), "wheel=" + wheel);
            assertEquals(before, machine.configuration(), "wheel=" + wheel);
        }
    }

    /**
     * Leaves ok as false with an invalid operation and then does a valid placeSymbol. Expected: ok
     * remains false (successful placeSymbol does not touch ok, per documentation).
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotModifyOkWhenOperationIsPerformed()
    {
        growTo(4);
        String target = machine.symbols()[1];
        machine.spin(99);
        assertFalse(machine.ok());
        machine.placeSymbol(1, target);
        assertFalse(machine.ok(), "successful placeSymbol does not touch ok (per documentation)");
    }

    /**
     * DISABLED (known defect). placeSymbol with non-existent symbol enters an infinite loop.
     * Ideal expected: finish without hanging.
     * <p>
     * Type: Negative.
     */
    @Test
    @Disabled("KNOWN DEFECT: if symbol does not exist, placeSymbol enters infinite loop. "
        + "Enable only to demonstrate issue (will hang execution).")
    public void shouldNotHangWithNonExistentSymbol()
    {
        growTo(4);
        machine.placeSymbol(1, "#NOEXISTE");
    }

    /**
     * spin(1). Expected: ok true and wheel 1 moves to the next symbol in the list.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAdvanceSpecifiedWheelOneSymbol()
    {
        String[] s = machine.symbols();
        machine.spin(1);
        assertTrue(machine.ok());
        assertEquals(s[1], machine.configuration().get(0));
    }

    /**
     * spin(2). Expected: only wheel 2 changes; wheels 1 and 3 remain the same.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSpinOtherWheels()
    {
        List<String> before = machine.configuration();
        machine.spin(2);
        assertEquals(before.get(0), machine.configuration().get(0));
        assertNotEquals(before.get(1), machine.configuration().get(1));
        assertEquals(before.get(2), machine.configuration().get(2));
    }

    /**
     * spin(1) as many times as there are symbols. Expected: configuration returns to initial.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldReturnToOriginAfterFullTurn()
    {
        List<String> before = machine.configuration();
        for (int i = 0; i < machine.distinctSymbols(); i++) {
            machine.spin(1);
        }
        assertEquals(before, machine.configuration());
    }

    /**
     * spin with 0, 4, -1, MIN_VALUE and MAX_VALUE. Expected: ok false and configuration does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSpinNonExistentWheel()
    {
        List<String> before = machine.configuration();
        for (int wheel : new int[] {0, 4, -1, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
            machine.spin(wheel);
            assertFalse(machine.ok(), "wheel=" + wheel);
            assertEquals(before, machine.configuration(), "wheel=" + wheel);
        }
    }

    /**
     * Invalid spin(0) and then spin(1). Expected: ok goes from false to true.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldRecoverOkAfterSuccessfulOperation()
    {
        machine.spin(0);
        assertFalse(machine.ok());
        machine.spin(1);
        assertTrue(machine.ok());
    }

    /**
     * Leaves a single symbol and spins. Expected: the wheel continues showing that symbol.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitWithSingleSymbolWheelDoesNotChange()
    {
        String keep = machine.symbols()[0];
        machine.delSymbol(machine.symbols()[2]);
        machine.delSymbol(machine.symbols()[1]);
        assertEquals(1, machine.distinctSymbols());
        machine.spin(1);
        assertEquals(keep, machine.configuration().get(0));
    }

    /**
     * Removes symbol shown by wheel 2 and spins that wheel. Expected: jumps to show the first
     * symbol in the list.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldJumpToFirstSymbolIfShownWasRemoved()
    {
        String[] s = machine.symbols();
        machine.delSymbol(s[1]);
        machine.spin(2);
        assertEquals(s[0], machine.configuration().get(1));
    }

    /**
     * spin() without parameters. Expected: ok true and configuration [s0,s1,s2] becomes [s1,s2,s0].
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAdvanceAllWheelsOneSymbol()
    {
        String[] s = machine.symbols();
        machine.spin();
        assertTrue(machine.ok());
        assertEquals(Arrays.asList(s[1], s[2], s[0]), machine.configuration());
    }

    /**
     * spin() as many times as symbols. Expected: configuration returns to initial.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldReturnToOriginAfterAsManySpinsAsSymbols()
    {
        List<String> before = machine.configuration();
        for (int i = 0; i < machine.distinctSymbols(); i++) {
            machine.spin();
        }
        assertEquals(before, machine.configuration());
    }

    /**
     * With active prize, spin(). Expected: prize remains, because all advance equally.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotBreakPrizeWhenSpinningAllWheels()
    {
        growTo(4);
        forceJackpot();
        assertTrue(machine.isJackpot());
        machine.spin();
        assertTrue(machine.isJackpot(), "all advance equally, remain equal");
    }

    /**
     * spin() with 50 wheels. Expected: ok true and configuration changes.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldSpinWithFiftyWheels()
    {
        growTo(MAX_WHEELS);
        List<String> before = machine.configuration();
        machine.spin();
        assertTrue(machine.ok());
        assertNotEquals(before, machine.configuration());
    }

    /**
     * Adds a symbol at beginning and end. Expected: symbols() returns them in that order and
     * ok true.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldReturnSymbolsInOrder()
    {
        machine.addSymbol("#AAAAAA", 0);
        machine.addSymbol("#BBBBBB", 4);
        String[] s = machine.symbols();
        assertEquals("#AAAAAA", s[0]);
        assertEquals("#BBBBBB", s[s.length - 1]);
        assertTrue(machine.ok());
    }

    /**
     * Modifies array returned by symbols(). Expected: machine is not altered (it is a copy).
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAllowAlteringMachineByModifyingCopy()
    {
        String[] copy = machine.symbols();
        String original = copy[0];
        copy[0] = "MODIFIED";
        assertEquals(original, machine.symbols()[0]);
    }

    /**
     * Adds and then removes a symbol. Expected: distinctSymbols() goes from 3 to 4 and back to 3.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCountSymbolsAsTheyAreAddedOrRemoved()
    {
        assertEquals(3, machine.distinctSymbols());
        machine.addSymbol("#AAAAAA", 0);
        assertEquals(4, machine.distinctSymbols());
        machine.delSymbol("#AAAAAA");
        assertEquals(3, machine.distinctSymbols());
        assertTrue(machine.ok());
    }

    /**
     * 50-wheel machine. Expected: distinctSymbols() is 50 and matches symbols().length.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldMatchSymbolsLength()
    {
        machine = freshMachine(MAX_WHEELS);
        assertEquals(MAX_WHEELS, machine.distinctSymbols());
        assertEquals(machine.distinctSymbols(), machine.symbols().length);
    }

    /**
     * Queries configuration(). Expected: has one element per wheel, in order, equal to the visible
     * symbol of each.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldReturnVisibleSymbolOfEachWheelFromLeftToRight()
    {
        List<String> config = machine.configuration();
        assertEquals(MIN_WHEELS, config.size());
        for (int i = 0; i < config.size(); i++) {
            assertEquals(machine.getWheels().get(i).getshowSymbol(), config.get(i));
        }
        assertTrue(machine.ok());
    }

    /**
     * configuration() before and after spin(). Expected: results are different.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldReflectChangeAfterSpinning()
    {
        List<String> before = machine.configuration();
        machine.spin();
        assertNotEquals(before, machine.configuration());
    }

    /**
     * Clears list returned by configuration(). Expected: machine still has 3 elements (it is a copy).
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAllowAlteringMachineByModifyingList()
    {
        List<String> config = machine.configuration();
        config.clear();
        assertEquals(MIN_WHEELS, machine.configuration().size());
    }

    /**
     * Leaves 4 wheels with the same symbol. Expected: isJackpot() true and ok true.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldDetectPrizeWhenAllShowSame()
    {
        growTo(4);
        forceJackpot();
        assertTrue(machine.isJackpot());
        assertTrue(machine.ok());
    }

    /**
     * With prize, spins only one wheel (first, middle or last). Expected: isJackpot() false
     * in each case.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotGivePrizeIfOnlyOneWheelDiffers()
    {
        growTo(4);
        for (int wheel = 1; wheel <= 4; wheel++) {
            forceJackpot();
            machine.spin(wheel);
            assertFalse(machine.isJackpot(), "differs wheel " + wheel);
        }
    }

    /**
     * 10-wheel machine with different symbols. Expected: isJackpot() false.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotGivePrizeWithManyDifferentWheels()
    {
        machine = freshMachine(10);
        assertFalse(machine.isJackpot());
    }

    /**
     * 1-wheel machine. Expected: isJackpot() true (nothing to compare with).
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitSingleWheelIsAlwaysAPrize()
    {
        machine = freshMachine(1);
        assertEquals(1, machine.getWheels().size());
        assertTrue(machine.isJackpot());
    }

    /**
     * Aligns 50 wheels. Expected: isJackpot() true.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldDetectPrizeWithFiftyWheels()
    {
        growTo(MAX_WHEELS);
        forceJackpot();
        assertTrue(machine.isJackpot());
    }

    /**
     * makeInvisible(). Expected: ok true and all wheels have isShown() false.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldHideAllWheels()
    {
        machine.makeInvisible();
        assertTrue(machine.ok());
        for (Wheel w : machine.getWheels()) {
            assertFalse(w.isShown());
        }
    }

    /**
     * makeInvisible() and then makeVisible(). Expected: ok true and all wheels visible.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldShowAllWheels()
    {
        machine.makeInvisible();
        machine.makeVisible();
        assertTrue(machine.ok());
        for (Wheel w : machine.getWheels()) {
            assertTrue(w.isShown());
        }
    }

    /**
     * Hides and shows machine. Expected: configuration, symbols and wheel locations do not
     * change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAlterSymbolsOrPositionsWhenChangingVisibility()
    {
        List<String> config = machine.configuration();
        String[] symbols = machine.symbols();
        machine.makeInvisible();
        machine.makeVisible();
        assertEquals(config, machine.configuration());
        assertArrayEquals(symbols, machine.symbols());
        assertWheelsArranged();
    }

    /**
     * Calls makeVisible() and makeInvisible() twice consecutively. Expected: ok true and machine
     * remains hidden.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldBeIdempotent()
    {
        machine.makeVisible();
        machine.makeVisible();
        machine.makeInvisible();
        machine.makeInvisible();
        assertTrue(machine.ok());
        assertFalse(machine.getWheels().get(0).isShown());
    }

    /**
     * Alternates failed and successful operations. Expected: ok reflects only the result of the
     * last one.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldReflectOnlyLastOperation()
    {
        machine.addWheel(0);
        assertFalse(machine.ok());
        machine.addWheel(1);
        assertTrue(machine.ok());
        machine.delWheel(99);
        assertFalse(machine.ok());
        machine.spin();
        assertTrue(machine.ok());
    }
}