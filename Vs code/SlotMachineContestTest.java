import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for SlotMachineContest: solve(n) and simulate(n) (and via reflection, private
 * method findMostFrequentPosition).
 *
 * Problem idea: each wheel i shows its own symbol (position i in list of n symbols),
 * so all positions are distinct and NONE repeat.
 * In that case target position is 0 and wheel at position p needs
 * (n - p) spins to reach 0. Therefore expected result of solve(n) is:
 *      [ [2, n-1], [3, n-2], ..., [n, 1] ]
 *
 * Convention: should... (positive) / shouldNot... (negative) / limit... (boundary).
 * NOTE: simulate(n) waits 2 seconds per spin, so those tests take longer.
 */
public class SlotMachineContestTest
{
    @BeforeEach
    public void setUp()
    {
        new Wheel().makeInvisible(); // clears static symbol list
    }

    @AfterEach
    public void tearDown() throws Exception
    {
        SlotMachine m = currentMachine();
        if (m != null) {
            m.makeInvisible();
        }
    }

    private static SlotMachine currentMachine() throws Exception
    {
        Field f = SlotMachineContest.class.getDeclaredField("slotMachine");
        f.setAccessible(true);
        return (SlotMachine) f.get(null);
    }

    private static int mostFrequent(Integer... positions) throws Exception
    {
        Method m = SlotMachineContest.class.getDeclaredMethod("findMostFrequentPosition", ArrayList.class);
        m.setAccessible(true);
        return (Integer) m.invoke(null, new ArrayList<>(Arrays.asList(positions)));
    }

    /** Expected result of solve(n) when all positions are distinct. */
    private static List<List<Integer>> expectedFor(int n)
    {
        List<List<Integer>> expected = new ArrayList<>();
        for (int wheel = 2; wheel <= n; wheel++) {
            expected.add(Arrays.asList(wheel, n - (wheel - 1)));
        }
        return expected;
    }

    private static List<List<Integer>> asLists(ArrayList<int[]> steps)
    {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] s : steps) {
            assertEquals(2, s.length, "each step must be [wheel, spins]");
            result.add(Arrays.asList(s[0], s[1]));
        }
        return result;
    }

    /**
     * solve(3). Expected: [[2,2],[3,1]].
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCalculateStepsForThreeWheels()
    {
        assertEquals(Arrays.asList(Arrays.asList(2, 2), Arrays.asList(3, 1)),
            asLists(SlotMachineContest.solve(3)));
    }

    /**
     * solve(4). Expected: [[2,3],[3,2],[4,1]].
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldCalculateStepsForFourWheels()
    {
        assertEquals(Arrays.asList(Arrays.asList(2, 3), Arrays.asList(3, 2), Arrays.asList(4, 1)),
            asLists(SlotMachineContest.solve(4)));
    }

    /**
     * solve(n) with n = 2, 3, 5, 8, 12 and 25. Expected: [[2,n-1],[3,n-2],...,[n,1]].
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldFulfillFormulaForDifferentSizes()
    {
        for (int n : new int[] {2, 3, 5, 8, 12, 25}) {
            new Wheel().makeInvisible();
            assertEquals(expectedFor(n), asLists(SlotMachineContest.solve(n)), "n=" + n);
        }
    }

    /**
     * solve(10). Expected: 9 steps, wheels in increasing order between 2 and 10, without wheel 1 and with
     * at least 1 spin each.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotIncludeFirstWheelNorNonPositiveSpins()
    {
        ArrayList<int[]> steps = SlotMachineContest.solve(10);
        assertEquals(9, steps.size());
        int previousWheel = 1;
        for (int[] s : steps) {
            assertTrue(s[0] > previousWheel, "wheels must be in order and not repeated");
            assertTrue(s[0] >= 2 && s[0] <= 10, "wheel out of range: " + s[0]);
            assertTrue(s[1] >= 1, "must require at least one spin");
            previousWheel = s[0];
        }
    }

    /**
     * solve(7). Expected: (current position + spins) mod (symbol count) is 0 for each
     * step.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldTakeEachWheelToTargetPosition() throws Exception
    {
        ArrayList<int[]> steps = SlotMachineContest.solve(7);
        SlotMachine m = currentMachine();
        List<Integer> positions = m.getPositionSymbolos();
        int count = m.distinctSymbols();
        for (int[] s : steps) {
            assertEquals(0, (positions.get(s[0] - 1) + s[1]) % count, "wheel " + s[0]);
        }
    }

    /**
     * solve(1). Expected: empty list.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitSingleWheelNeedsNoSteps()
    {
        assertTrue(SlotMachineContest.solve(1).isEmpty());
    }

    /**
     * solve(0). Expected: empty list; internal machine has 1 wheel.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitZeroWheelsNeedsNoSteps() throws Exception
    {
        assertTrue(SlotMachineContest.solve(0).isEmpty());
        assertEquals(1, currentMachine().getWheels().size());
    }

    /**
     * solve(2). Expected: [[2,1]].
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitTwoWheelsNeedSingleSpin()
    {
        assertEquals(Arrays.asList(Arrays.asList(2, 1)), asLists(SlotMachineContest.solve(2)));
    }

    /**
     * solve(50). Expected: 49 steps complying with formula.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitFiftyWheelsIsProjectMaximum()
    {
        ArrayList<int[]> steps = SlotMachineContest.solve(50);
        assertEquals(49, steps.size());
        assertEquals(expectedFor(50), asLists(steps));
    }

    /**
     * solve(3), solve(4) and solve(3) consecutively without resetting static list. Expected: each
     * call returns expected result for its n.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldGiveSameResultInSuccessiveCallsWithoutResettingSymbols()
    {
        assertEquals(expectedFor(3), asLists(SlotMachineContest.solve(3)));
        assertEquals(expectedFor(4), asLists(SlotMachineContest.solve(4)));
        assertEquals(expectedFor(3), asLists(SlotMachineContest.solve(3)));
    }

    /**
     * solve(3) and then solve(5). Expected: machine is different, has 5 wheels and previous wheels
     * remain hidden.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldReplaceAndHidePreviousMachine() throws Exception
    {
        SlotMachineContest.solve(3);
        SlotMachine first = currentMachine();
        SlotMachineContest.solve(5);
        SlotMachine second = currentMachine();
        assertNotSame(first, second);
        assertEquals(5, second.getWheels().size());
        for (Wheel w : first.getWheels()) {
            assertFalse(w.isShown(), "previous machine must remain hidden");
        }
    }

    /**
     * solve(4). Expected: all wheels of internal machine are hidden.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotShowMachineWhenSolving() throws Exception
    {
        SlotMachineContest.solve(4);
        for (Wheel w : currentMachine().getWheels()) {
            assertFalse(w.isShown());
        }
    }

    /**
     * solve(4). Expected: positions remain at [0,1,2,3] and no prize.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotSpinAnyWheelWhenSolving() throws Exception
    {
        SlotMachineContest.solve(4);
        SlotMachine m = currentMachine();
        assertEquals(Arrays.asList(0, 1, 2, 3), m.getPositionSymbolos());
        assertFalse(m.isJackpot());
    }

    /**
     * simulate(3) (~6 s). Expected: upon completion there is a prize and all show first symbol.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldLeaveAllWheelsAlignedWhenSimulating() throws Exception
    {
        SlotMachineContest.simulate(3);
        SlotMachine m = currentMachine();
        assertTrue(m.isJackpot(), "after simulating, all wheels show same symbol");
        assertEquals(m.symbols()[0], m.configuration().get(0));
    }

    /**
     * simulate(2) (~2 s). Expected: wheels remain visible and aligned.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldShowMachineWhenSimulating() throws Exception
    {
        SlotMachineContest.simulate(2);
        for (Wheel w : currentMachine().getWheels()) {
            assertTrue(w.isShown());
        }
        assertTrue(currentMachine().isJackpot());
    }

    /**
     * simulate(1). Expected: finishes in under 1 second and there is a prize.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitSingleWheelShouldNotSpinOrWait() throws Exception
    {
        long start = System.currentTimeMillis();
        SlotMachineContest.simulate(1);
        assertTrue(System.currentTimeMillis() - start < 1000, "no steps means no pauses");
        assertTrue(currentMachine().isJackpot());
    }

    /**
     * simulate(2) (1 spin). Expected: takes between 1.9 s and 3.5 s.
     * <p>
     * Type: Negative.
     */
    @Test
    public void shouldNotWaitMoreThanDelayPerSpin() throws Exception
    {
        long start = System.currentTimeMillis();
        SlotMachineContest.simulate(2);
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed >= 1900, "must wait ~2s, waited " + elapsed);
        assertTrue(elapsed < 3500, "must not wait too long, waited " + elapsed);
    }

    /**
     * findMostFrequentPosition with [1,1,2], [0,2,2,2,1] and [5,5,5]. Expected: 1, 2 and 5.
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldFindMostFrequentPosition() throws Exception
    {
        assertEquals(1, mostFrequent(1, 1, 2));
        assertEquals(2, mostFrequent(0, 2, 2, 2, 1));
        assertEquals(5, mostFrequent(5, 5, 5));
    }

    /**
     * findMostFrequentPosition with [3,1,2] and [7]. Expected: 0 (no repeats).
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitWithoutRepeatsReturnsZero() throws Exception
    {
        assertEquals(0, mostFrequent(3, 1, 2));
        assertEquals(0, mostFrequent(7));
    }

    /**
     * findMostFrequentPosition with empty list. Expected: 0.
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitEmptyListReturnsZero() throws Exception
    {
        assertEquals(0, mostFrequent());
    }

    /**
     * findMostFrequentPosition with [1,1,2,2] and [2,2,1,1]. Expected: 1 and 2 (first to reach count wins).
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitTieFirstToReachMaxWins() throws Exception
    {
        assertEquals(1, mostFrequent(1, 1, 2, 2));
        assertEquals(2, mostFrequent(2, 2, 1, 1));
    }

    /**
     * findMostFrequentPosition with [3,3,0,0,0]. Expected: 0 (outweighs 3 with three occurrences).
     * <p>
     * Type: Positive.
     */
    @Test
    public void shouldChangeWinnerIfAnotherExceeds() throws Exception
    {
        assertEquals(0, mostFrequent(3, 3, 0, 0, 0));
    }

    /**
     * findMostFrequentPosition with [-1,-1,0]. Expected: -1 (symbol already removed).
     * <p>
     * Type: Boundary.
     */
    @Test
    public void limitShouldAcceptNegativePositions() throws Exception
    {
        assertEquals(-1, mostFrequent(-1, -1, 0));
    }
}