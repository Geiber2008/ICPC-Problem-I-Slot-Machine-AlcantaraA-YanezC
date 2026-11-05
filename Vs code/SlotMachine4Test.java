import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * CYCLE 4 (new methods): addWheel(type, pos) and addSymbol(color, pos, type).
 *
 * Wheel types:  "Normal", "Rebel" (not locked, not removed, not swapped,
 *               does not support placeSymbol) and "Lefty" (when spinning copies symbol from
 *               wheel to its left; first wheel does not spin).
 * Symbol types: "normal", "ephemeral" (shrinks 1 px on each selection, min 1)
 *               and "shy" (toggles visible/invisible on each selection).
 *
 * NOTE: cycle 4 diagram writes addWheel(type,pos) and addSymbol(type,pos,color);
 * delivered code uses addWheel(type,pos) and addSymbol(color,pos,type). These
 * tests follow the code.
 *
 * Convention: should... (positive) / shouldNot... (negative) / limit... (boundary).
 */
public class SlotMachine4Test
{
    private static final int FIRST_X = 110;
    private static final int SPACING = 40;
    private static final int BASE_H = 80;
    private static final int BASE_W = 30;

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
    }

    private static SlotMachine freshMachine(int n)
    {
        new Wheel().makeInvisible();
        return new SlotMachine(n);
    }

    private void assertWheelsArranged()
    {
        ArrayList<Wheel> w = machine.getWheels();
        for (int i = 0; i < w.size(); i++) {
            assertEquals(FIRST_X + SPACING * i, w.get(i).getpositionX(), "Wheel " + i + " X");
            assertEquals(i, w.get(i).getpositionA(), "Wheel " + i + " index");
        }
    }

    /** Spins wheel (1-based) until it shows specified symbol. */
    private void spinUntilShows(int wheel, String color)
    {
        int guard = 0;
        do {
            machine.spin(wheel);
            assertTrue(++guard < 1000, "wheel never showed " + color);
        } while (!machine.getWheels().get(wheel - 1).getshowSymbol().equals(color));
    }

    /**
     * addWheel with "Rebel", "Lefty" and "Normal". Expected: ok true and each wheel is of requested type,
     * well arranged.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCreateWheelOfRequestedType()
    {
        machine.addWheel("Rebel", 1);
        machine.addWheel("Lefty", 3);
        machine.addWheel("Normal", 5);
        assertTrue(machine.ok());
        assertEquals("Rebel", machine.getWheels().get(0).getType());
        assertEquals("Lefty", machine.getWheels().get(2).getType());
        assertEquals("Normal", machine.getWheels().get(4).getType());
        assertWheelsArranged();
    }

    /**
     * addWheel with "rebel", "REBEL", "lefty", "Other", "", " Rebel" and null. Expected: ok true and
     * always creates a Normal wheel (comparison is case-sensitive).
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotRecognizeUnknownTypesAndTreatThemAsNormal()
    {
        String[] unknown = {"rebel", "REBEL", "lefty", "Other", "", " Rebel", null};
        for (String type : unknown) {
            machine = freshMachine(3);
            machine.addWheel(type, 1);
            assertTrue(machine.ok(), "type=" + type);
            assertEquals("Normal", machine.getWheels().get(0).getType(), "type=" + type);
        }
    }

    /**
     * addWheel("Lefty", pos) with pos from 1 to 4. Expected: ok true and wheel stays at that position.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAddAtAnyValidPosition()
    {
        for (int pos = 1; pos <= 4; pos++) {
            machine = freshMachine(3);
            machine.addWheel("Lefty", pos);
            assertTrue(machine.ok(), "pos=" + pos);
            assertEquals("Lefty", machine.getWheels().get(pos - 1).getType());
            assertWheelsArranged();
        }
    }

    /**
     * addWheel of Rebel and Lefty with 0, 5, -1, MIN_VALUE and MAX_VALUE. Expected: ok false and 3 wheels remain.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAddSpecialWheelAtInvalidPosition()
    {
        for (String type : new String[] {"Rebel", "Lefty"}) {
            for (int pos : new int[] {0, 5, -1, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
                machine.addWheel(type, pos);
                assertFalse(machine.ok(), type + " pos=" + pos);
                assertEquals(3, machine.getWheels().size());
            }
        }
    }

    /**
     * With a locked wheel, addWheel of Rebel and Lefty. Expected: ok false and 3 wheels remain.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAddSpecialWheelIfWheelsAreLocked()
    {
        machine.lock(1);
        machine.addWheel("Rebel", 1);
        assertFalse(machine.ok());
        machine.addWheel("Lefty", 1);
        assertFalse(machine.ok());
        assertEquals(3, machine.getWheels().size());
    }

    /**
     * Adds Normal, Rebel and Lefty wheels interleaved up to 50 and then one more. Expected: reaches
     * 50 with ok true; next fails with ok false.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldReachMaxMixingTypes()
    {
        String[] types = {"Normal", "Rebel", "Lefty"};
        for (int i = 3; i < 50; i++) {
            machine.addWheel(types[i % 3], machine.getWheels().size() + 1);
            assertTrue(machine.ok(), "failed at " + (i + 1));
        }
        assertEquals(50, machine.getWheels().size());
        machine.addWheel("Rebel", 1);
        assertFalse(machine.ok(), "must not exceed 50 wheels regardless of type");
        assertEquals(50, machine.getWheels().size());
        assertWheelsArranged();
    }

    /**
     * Adds a Rebel and a Lefty. Expected: 3 symbols remain and each wheel shows an existing one.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotCreateNewSymbolsWhenAddingSpecialWheels()
    {
        machine.addWheel("Rebel", 1);
        machine.addWheel("Lefty", 2);
        assertEquals(3, machine.distinctSymbols());
        for (Wheel w : machine.getWheels()) {
            assertTrue(Arrays.asList(machine.symbols()).contains(w.getshowSymbol()));
        }
    }

    /**
     * lock on a Rebel wheel. Expected: ok true, but wheel remains movable and
     * someWheelsAreLocked() is false.
     * <p>
     * Type: Positive.
     */
    @Test
    public void rebelShouldIgnoreLock()
    {
        machine.addWheel("Rebel", 4);
        machine.lock(4);
        assertTrue(machine.ok());
        assertTrue(machine.getWheels().get(3).getesMove());
        assertFalse(machine.someWheelsAreLocked());
    }

    /**
     * Tries to lock a Rebel and then addWheel(1). Expected: ok true, because nothing remained
     * locked.
     * <p>
     * Type: Negative.
     */
    @Test
    public void rebelShouldNotPreventOperationsRequiringFreeWheels()
    {
        machine.addWheel("Rebel", 4);
        machine.lock(4);
        machine.addWheel(1);
        assertTrue(machine.ok(), "since it is not left locked, adding can continue");
    }

    /**
     * delWheel on a Rebel. Expected: ok false, 4 wheels remain and Rebel stays.
     * <p>
     * Type: Negative.
     */
    @Test
    public void rebelShouldNotBeRemoved()
    {
        machine.addWheel("Rebel", 4);
        machine.delWheel(4);
        assertFalse(machine.ok());
        assertEquals(4, machine.getWheels().size());
        assertEquals("Rebel", machine.getWheels().get(3).getType());
    }

    /**
     * With a Rebel present, delWheel(1). Expected: ok true, 3 wheels, Rebel stays and everything
     * is well arranged.
     * <p>
     * Type: Negative.
     */
    @Test
    public void rebelShouldNotBlockRemovalOfOtherWheels()
    {
        machine.addWheel("Rebel", 4);
        machine.delWheel(1);
        assertTrue(machine.ok());
        assertEquals(3, machine.getWheels().size());
        assertEquals("Rebel", machine.getWheels().get(2).getType());
        assertWheelsArranged();
    }

    /**
     * swap(2,1), swap(1,2) and swap(2,2) with a Rebel at position 2. Expected: all three ok false and
     * configuration does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void rebelShouldNotSwapAsFirstOrSecondWheel()
    {
        machine.addWheel("Rebel", 2);
        List<String> before = machine.configuration();
        machine.swap(2, 1);
        assertFalse(machine.ok());
        machine.swap(1, 2);
        assertFalse(machine.ok());
        machine.swap(2, 2);
        assertFalse(machine.ok(), "not even with itself");
        assertEquals(before, machine.configuration());
        assertEquals("Rebel", machine.getWheels().get(1).getType());
    }

    /**
     * With a Rebel in the middle, swap(1, 3). Expected: ok true, Rebel stays in the middle and
     * arrangement is correct.
     * <p>
     * Type: Negative.
     */
    @Test
    public void rebelShouldNotAffectNormalWheelSwap()
    {
        machine.addWheel("Rebel", 2);
        machine.swap(1, 3);
        assertTrue(machine.ok());
        assertEquals("Rebel", machine.getWheels().get(1).getType());
        assertWheelsArranged();
    }

    /**
     * placeSymbol on a Rebel. Expected: ok false and configuration does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void rebelShouldNotSupportPlaceSymbol()
    {
        machine.addWheel("Rebel", 4);
        List<String> before = machine.configuration();
        machine.placeSymbol(4, machine.symbols()[0]);
        assertFalse(machine.ok());
        assertEquals(before, machine.configuration());
    }

    /**
     * With a Rebel present, placeSymbol on wheel 1. Expected: wheel 1 shows requested
     * symbol.
     * <p>
     * Type: Negative.
     */
    @Test
    public void rebelShouldNotBlockPlaceSymbolOnOtherWheels()
    {
        machine.addWheel("Rebel", 4);
        String target = machine.symbols()[2];
        machine.placeSymbol(1, target);
        assertEquals(target, machine.configuration().get(0));
    }

    /**
     * spin on a Rebel. Expected: ok true and advances to next symbol in list.
     * <p>
     * Type: Positive.
     */
    @Test
    public void rebelShouldSpinNormally()
    {
        machine.addWheel("Rebel", 4);
        List<String> symbols = Arrays.asList(machine.symbols());
        String shown = machine.configuration().get(3);
        machine.spin(4);
        assertTrue(machine.ok());
        String expected = symbols.get((symbols.indexOf(shown) + 1) % symbols.size());
        assertEquals(expected, machine.configuration().get(3));
    }

    /**
     * Rebel at positions 1 and 5; tries to remove them. Expected: both delWheel ok false and
     * 5 wheels remain.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitRebelAtFirstAndLastPosition()
    {
        machine.addWheel("Rebel", 1);
        machine.addWheel("Rebel", 5);
        machine.delWheel(1);
        assertFalse(machine.ok());
        machine.delWheel(5);
        assertFalse(machine.ok());
        assertEquals(5, machine.getWheels().size());
    }

    /**
     * spin on a Lefty. Expected: ends up showing the same symbol as its left neighbor.
     * <p>
     * Type: Positive.
     */
    @Test
    public void leftyShouldAlignWithLeftNeighborWhenSpinning()
    {
        machine.addWheel("Lefty", 3);
        machine.spin(3);
        assertTrue(machine.ok());
        assertEquals(machine.configuration().get(1), machine.configuration().get(2));
    }

    /**
     * spin() with a Lefty at position 3. Expected: configuration is [s1, s2, s2, s0] (Lefty
     * copies what its neighbor already spun).
     * <p>
     * Type: Positive.
     */
    @Test
    public void leftyShouldCopyAlreadySpunSymbolOfNeighborWhenSpinningAll()
    {
        String[] s = machine.symbols();
        machine.addWheel("Lefty", 3);
        machine.spin();
        assertEquals(Arrays.asList(s[1], s[2], s[2], s[0]), machine.configuration());
    }

    /**
     * Lefty at position 1; spin(1) and spin(). Expected: its symbol does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void leftyShouldNotSpinIfFirstWheel()
    {
        machine.addWheel("Lefty", 1);
        String before = machine.configuration().get(0);
        machine.spin(1);
        assertEquals(before, machine.configuration().get(0));
        machine.spin();
        assertEquals(before, machine.configuration().get(0), "with spin() neither");
    }

    /**
     * Locks a Lefty and spins. Expected: its symbol does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void leftyShouldNotSpinIfLocked()
    {
        machine.addWheel("Lefty", 3);
        machine.lock(3);
        String before = machine.configuration().get(2);
        machine.spin(3);
        machine.spin();
        assertEquals(before, machine.configuration().get(2));
    }

    @Test
    @Timeout(1)
    public void leftyShouldFailToPlaceSymbolItCannotReach()
    {
        machine.addWheel("Lefty", 4);
        machine.spin(new String[] {"#100001", "#200002", "#300003", "#400004"});
        machine.addSymbol("#FFFFFF", 4);

        machine.placeSymbol(4, "#FFFFFF");

        assertFalse(machine.ok());
        assertEquals("#100001", machine.configuration().get(2));
        assertNotEquals("#FFFFFF", machine.configuration().get(3));
    }

    /**
     * Removes symbol shown by neighbor and spins Lefty. Expected: does not spin and does not enter
     * infinite loop.
     * <p>
     * Type: Negative.
     */
    @Test
    public void leftyShouldNotSpinIfNeighborSymbolNoLongerExists()
    {
        machine.addWheel("Lefty", 3);
        String lefty = machine.configuration().get(2);
        String neighbour = machine.configuration().get(1);
        machine.delSymbol(neighbour);
        machine.spin(3);
        assertEquals(lefty, machine.configuration().get(2));
    }

    /**
     * swap(3, 1) with a Lefty. Expected: ok true, Lefty moves to position 1 with index 0 and,
     * being first, no longer spins.
     * <p>
     * Type: Positive.
     */
    @Test
    public void leftyShouldAcceptSwapAndUpdatesPosition()
    {
        machine.addWheel("Lefty", 3);
        machine.swap(3, 1);
        assertTrue(machine.ok());
        assertEquals("Lefty", machine.getWheels().get(0).getType());
        assertEquals(0, machine.getWheels().get(0).getpositionA());
        assertWheelsArranged();
        String before = machine.configuration().get(0);
        machine.spin(1);
        assertEquals(before, machine.configuration().get(0), "now it is first: does not spin");
    }

    /**
     * Adds wheel before Lefty and spins. Expected: its index updates and aligns with
     * its new left neighbor.
     * <p>
     * Type: Positive.
     */
    @Test
    public void leftyShouldFollowNewNeighborAfterAddingAnotherWheelBefore()
    {
        machine.addWheel("Lefty", 3);
        machine.addWheel(1);
        assertEquals(3, machine.getWheels().get(3).getpositionA());
        machine.spin(4);
        assertEquals(machine.configuration().get(2), machine.configuration().get(3));
    }

    /**
     * delWheel on a Lefty. Expected: ok true and 3 wheels remain.
     * <p>
     * Type: Positive.
     */
    @Test
    public void leftyShouldBeRemovable()
    {
        machine.addWheel("Lefty", 4);
        machine.delWheel(4);
        assertTrue(machine.ok());
        assertEquals(3, machine.getWheels().size());
    }

    /**
     * addSymbol with "normal", "ephemeral" and "shy" in various capitalizations. Expected: ok true and
     * list grows by 1 each time.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldAcceptAllThreeTypesRegardlessOfCapitalization()
    {
        String[] valid = {"normal", "Normal", "NORMAL", "ephemeral", "Ephemeral", "EPHEMERAL",
            "shy", "Shy", "SHY"};
        int expected = 3;
        for (String type : valid) {
            machine.addSymbol("#ABCDEF", 0, type);
            expected++;
            assertTrue(machine.ok(), "type=" + type);
            assertEquals(expected, machine.distinctSymbols(), "type=" + type);
        }
    }

    /**
     * addSymbol with "foo", "", " ", "norm", "ephemeral ", " shy", "efimero" and null. Expected: ok
     * false and symbol list does not change.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotAcceptNonExistentTypes()
    {
        String[] invalid = {"foo", "", " ", "norm", "ephemeral ", " shy", "efimero", null};
        String[] before = machine.symbols();
        for (String type : invalid) {
            machine.addSymbol("#ABCDEF", 0, type);
            assertFalse(machine.ok(), "type=" + type);
            assertArrayEquals(before, machine.symbols(), "type=" + type);
        }
    }

    /**
     * addSymbol with invalid type and then valid. Expected: ok goes from false to true.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldRecoverOkWhenAddingValidSymbolAfterInvalid()
    {
        machine.addSymbol("#ABCDEF", 0, "foo");
        assertFalse(machine.ok());
        machine.addSymbol("#ABCDEF", 0, "shy");
        assertTrue(machine.ok());
    }

    /**
     * addSymbol with ephemeral, shy and normal types at positions 0, 2 and 5. Expected: placed in
     * those positions.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldInsertAtSpecifiedPosition()
    {
        machine.addSymbol("#AAAAAA", 0, "ephemeral");
        machine.addSymbol("#BBBBBB", 2, "shy");
        machine.addSymbol("#CCCCCC", 5, "normal");
        String[] s = machine.symbols();
        assertEquals("#AAAAAA", s[0]);
        assertEquals("#BBBBBB", s[2]);
        assertEquals("#CCCCCC", s[5]);
    }

    /**
     * addSymbol with position -10 and 999. Expected: inserted at beginning and end
     * respectively.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitOutOfRangePositionsAdjust()
    {
        machine.addSymbol("#AAAAAA", -10, "shy");
        assertEquals("#AAAAAA", machine.symbols()[0]);
        machine.addSymbol("#BBBBBB", 999, "ephemeral");
        String[] s = machine.symbols();
        assertEquals("#BBBBBB", s[s.length - 1]);
    }

    /**
     * addSymbol with "normal" type and wheel shows it. Expected: base size 80x30 and wheel
     * visible.
     * <p>
     * Type: Positive.
     */
    @Test
    public void normalTypeShouldBehaveLikeTwoParameterVersion()
    {
        machine.addSymbol("#AAAAAA", 3, "normal");
        Wheel w = machine.getWheels().get(2);
        spinUntilShows(3, "#AAAAAA");
        assertEquals(BASE_H, w.getShownHeight());
        assertEquals(BASE_W, w.getShownWidth());
        assertTrue(w.isShown());
    }

    /**
     * Wheel selects an ephemeral symbol twice. Expected: 79x29 first time and 78x28
     * second.
     * <p>
     * Type: Positive.
     */
    @Test
    public void ephemeralShouldShrinkOneUnitOnEachSelection()
    {
        machine.addSymbol("#AAAAAA", 3, "ephemeral");
        Wheel w = machine.getWheels().get(2);
        spinUntilShows(3, "#AAAAAA");
        assertEquals(BASE_H - 1, w.getShownHeight());
        assertEquals(BASE_W - 1, w.getShownWidth());
        spinUntilShows(3, "#AAAAAA");
        assertEquals(BASE_H - 2, w.getShownHeight());
        assertEquals(BASE_W - 2, w.getShownWidth());
    }

    /**
     * After showing ephemeral, wheel switches to normal symbol. Expected: returns to base
     * size 80x30.
     * <p>
     * Type: Negative.
     */
    @Test
    public void ephemeralShouldNotShrinkWhenShowingOtherSymbol()
    {
        machine.addSymbol("#AAAAAA", 3, "ephemeral");
        Wheel w = machine.getWheels().get(2);
        spinUntilShows(3, "#AAAAAA");
        machine.spin(3);
        assertEquals(BASE_H, w.getShownHeight(), "normal symbol restores base size");
        assertEquals(BASE_W, w.getShownWidth());
    }

    /**
     * Two different wheels select same ephemeral. Expected: shrinkage accumulates
     * (79 then 78 height).
     * <p>
     * Type: Positive.
     */
    @Test
    public void ephemeralShouldShareShrinkageBetweenWheels()
    {
        machine.addSymbol("#AAAAAA", 3, "ephemeral");
        Wheel second = machine.getWheels().get(1);
        Wheel third = machine.getWheels().get(2);
        spinUntilShows(2, "#AAAAAA");
        assertEquals(BASE_H - 1, second.getShownHeight());
        spinUntilShows(3, "#AAAAAA");
        assertEquals(BASE_H - 2, third.getShownHeight(), "same symbol, was already shrunk");
    }

    /**
     * Selects ephemeral 100 times. Expected: each selection height = max(1, 80-k) and width =
     * max(1, 30-k); ends at 1x1 and visible.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitEphemeralNeverDropsBelowOnePoint()
    {
        machine.addSymbol("#AAAAAA", 3, "ephemeral");
        Wheel w = machine.getWheels().get(2);
        for (int k = 1; k <= 100; k++) {
            spinUntilShows(3, "#AAAAAA");
            assertEquals(Math.max(1, BASE_H - k), w.getShownHeight(), "height at selection " + k);
            assertEquals(Math.max(1, BASE_W - k), w.getShownWidth(), "width at selection " + k);
        }
        assertEquals(1, w.getShownHeight());
        assertEquals(1, w.getShownWidth());
        assertTrue(w.isShown(), "shrunk ephemeral remains visible (as a dot)");
    }

    /**
     * Wheel selects shy symbol multiple times. Expected: hidden on 1st, visible when
     * showing normal, visible on 2nd and hidden on 3rd.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shyShouldToggleVisibilityOnEachSelection()
    {
        machine.addSymbol("#BBBBBB", 3, "shy");
        Wheel w = machine.getWheels().get(2);
        spinUntilShows(3, "#BBBBBB");
        assertFalse(w.isShown(), "1st selection: hidden");
        machine.spin(3);
        assertTrue(w.isShown());
        spinUntilShows(3, "#BBBBBB");
        assertTrue(w.isShown(), "2nd selection: toggles to visible");
        spinUntilShows(3, "#BBBBBB");
        assertFalse(w.isShown(), "3rd selection: hidden again");
    }

    /**
     * Wheel selects shy symbol. Expected: maintains base size 80x30.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shyShouldNotChangeWheelSize()
    {
        machine.addSymbol("#BBBBBB", 3, "shy");
        Wheel w = machine.getWheels().get(2);
        spinUntilShows(3, "#BBBBBB");
        assertEquals(BASE_H, w.getShownHeight());
        assertEquals(BASE_W, w.getShownWidth());
    }

    /**
     * Wheel hidden by shy and then makeVisible(). Expected: wheel becomes visible again.
     * <p>
     * Type: Positive.
     */
    @Test
    public void hiddenShyShouldShowAgainWithMakeVisible()
    {
        machine.addSymbol("#BBBBBB", 3, "shy");
        Wheel w = machine.getWheels().get(2);
        spinUntilShows(3, "#BBBBBB");
        assertFalse(w.isShown());
        machine.makeVisible();
        assertTrue(w.isShown());
    }

    /**
     * Symbol.normalizeType with various capitalizations. Expected: returns canonical name
     * ("Normal", "ephemeral", "Shy").
     * <p>
     * Type: Positive.
     */
    @Test
    public void symbolNormalizeTypeShouldUnifyCapitalization()
    {
        assertEquals("Normal", Symbol.normalizeType("normal"));
        assertEquals("Normal", Symbol.normalizeType("NORMAL"));
        assertEquals("ephemeral", Symbol.normalizeType("Ephemeral"));
        assertEquals("Shy", Symbol.normalizeType("sHy"));
    }

    /**
     * Symbol.normalizeType with "other", "" and null. Expected: returns null.
     * <p>
     * Type: Negative.
     */
    @Test
    public void symbolNormalizeTypeShouldNotAcceptInvalidValues()
    {
        assertEquals(null, Symbol.normalizeType("other"));
        assertEquals(null, Symbol.normalizeType(""));
        assertEquals(null, Symbol.normalizeType(null));
    }

    /**
     * new Symbol(name, position). Expected: type Normal, visible, with given position and
     * name.
     * <p>
     * Type: Positive.
     */
    @Test
    public void symbolShouldBeCreatedNormalAndVisibleWithShortConstructor()
    {
        Symbol s = new Symbol("#123456", 2);
        assertEquals("Normal", s.getType());
        assertTrue(s.isVisible());
        assertEquals(2, s.getPosition());
        assertEquals("#123456", s.getname());
    }

    /**
     * Repeated shrink() on a 3x2 Symbol. Expected: decreases one by one and never goes below 1x1.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void symbolLimitShrinkShouldNotDropBelowOne()
    {
        Symbol s = new Symbol("x", 0, "ephemeral", 3, 2);
        s.shrink();
        assertEquals(2, s.getHeight());
        assertEquals(1, s.getWidth());
        s.shrink();
        s.shrink();
        s.shrink();
        assertEquals(1, s.getHeight());
        assertEquals(1, s.getWidth());
    }

    /**
     * toggleVisible() twice. Expected: becomes invisible and then visible.
     * <p>
     * Type: Positive.
     */
    @Test
    public void symbolToggleVisibleShouldToggle()
    {
        Symbol s = new Symbol("x", 0, "Shy", 10, 10);
        s.toggleVisible();
        assertFalse(s.isVisible());
        s.toggleVisible();
        assertTrue(s.isVisible());
    }
}