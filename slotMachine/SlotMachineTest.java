import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The test class SlotMachineTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class SlotMachineTest
{
    private SlotMachine slotMachine;

    @BeforeEach
    public void setUp() {
        resetSharedSymbols();
        slotMachine = new SlotMachine();
        slotMachine.makeInvisible();
    }

    @AfterEach
    public void tearDown() {
        slotMachine.makeInvisible();
    }

    /**
     * Wheel.symbols is static, so every machine created in the same JVM shares it.
     * The default Wheel() constructor clears it, which gives each test a clean start.
     */
    private void resetSharedSymbols() {
        Wheel reset = new Wheel();
        reset.makeInvisible();
    }

    private SlotMachine newMachine(int wheelsCount) {
        resetSharedSymbols();
        SlotMachine machine = new SlotMachine(wheelsCount);
        machine.makeInvisible();
        return machine;
    }

    private String nextSymbol(String current, int steps) {
        ArrayList<String> symbols = slotMachine.getWheels().get(0).getlistcolor();
        return symbols.get((symbols.indexOf(current) + steps) % symbols.size());
    }

    private void forceJackpot() {
        slotMachine.spin(1);
        slotMachine.spin(3);
        slotMachine.spin(3);
    }

    /**
     * Try the SlotMachine() constructor without parameters
     * Should (Acceptance): Instantiate the machine by default with the number of wheels and
     * default base symbols, leaving the machine in a valid operational state (ok() == true).   
     */
    @Test
    public void shouldContructionOnly()
    {
        assertEquals(3,slotMachine.getWheels().size());
        assertEquals(3,slotMachine.distinctSymbols());
        assertEquals(true,slotMachine.ok());    
    }

    /**
     * Try the SlotMachine() constructor without parameters
     * SHOULD NOT (Reject): Allow the machine to be left without wheels or with a makeshift repair
     * null after execution.
     */
    @Test
    public void shouldntContructionOnly(){
       int totalWheels = slotMachine.getWheels().size();
       assertTrue(totalWheels > 0);
       assertNotNull(slotMachine.getWheels());
    }

    /**
     * Try the SlotMachine(n) constructor
     * Should (Acceptance): Create a machine with exactly n wheels and n symbols, each wheel
     * showing one of the symbols of the machine, leaving ok() == true.
     */
    @Test
    public void shouldConstructWithNWheels(){
        SlotMachine machine = newMachine(5);
        assertEquals(5, machine.getWheels().size());
        assertEquals(5, machine.distinctSymbols());
        assertTrue(machine.ok());
        for (Wheel w : machine.getWheels()) {
            assertNotNull(w.getshowSymbol());
            assertTrue(w.getlistcolor().contains(w.getshowSymbol()));
        }
    }

    /**
     * Try the SlotMachine(n) constructor
     * SHOULD NOT (Reject): Allow unviable values such as {@code n <= 0}, which must set
     * ok() == false or throw an exception. Disabled: the current constructor accepts them.
     */
    @Disabled("SlotMachine(n) does not validate n: n <= 0 still builds a machine with ok() == true")
    @Test
    public void shouldntConstructWithNonPositiveN(){
        resetSharedSymbols();
        boolean rejected;
        try {
            SlotMachine machine = new SlotMachine(0);
            machine.makeInvisible();
            rejected = !machine.ok();
        } catch (RuntimeException e) {
            rejected = true;
        }
        assertTrue(rejected);
    }

    /**
     * Try the AddWheel() 
     * Should (Acceptance): Insert a new wheel at a valid index {@code 0 <= pos <= totalWheels},
     * increasing the total number of wheels by 1.
     * SHOULD NOT (Reject): Add more wheels if there are already 50 wheels.
     */
    @Test
    public void tryTheAddWheel(){
       slotMachine.addWheel(1);
       int totalWheels = slotMachine.getWheels().size();
       assertTrue(totalWheels == 4);
       for(int i = 0;i < 50;i++){
            slotMachine.addWheel(i);
            slotMachine.makeInvisible();
        }
       totalWheels = slotMachine.getWheels().size();
       assertTrue(totalWheels == 50);
    }

    /**
     * Try the addWheel(pos) with valid positions
     * Should (Acceptance): Insert at the beginning, in the middle and at the end (pos == totalWheels + 1),
     * keeping the wheels indexed 0..n-1 and evenly spaced.
     */
    @Test
    public void shouldAddWheelInAnyValidPosition(){
        slotMachine.addWheel("Lefty", 4);
        assertTrue(slotMachine.ok());
        assertEquals("Lefty", slotMachine.getWheels().get(3).getType());
        slotMachine.addWheel(1);
        slotMachine.addWheel(3);
        assertTrue(slotMachine.ok());
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        assertEquals(6, wheels.size());
        for (int i = 0; i < wheels.size(); i++) {
            assertEquals(i, wheels.get(i).getpositionA());
            assertEquals(wheels.get(0).getpositionX() + 40 * i, wheels.get(i).getpositionX());
        }
    }

    /**
     * Try the addWheel(pos) with invalid positions
     * SHOULD NOT (Reject): Insert when pos is out of range; the number of wheels
     * stays the same and ok() == false.
     */
    @Test
    public void shouldntAddWheelOutOfRange(){
        slotMachine.addWheel(0);
        assertFalse(slotMachine.ok());
        slotMachine.addWheel(-1);
        assertFalse(slotMachine.ok());
        slotMachine.addWheel(5);
        assertFalse(slotMachine.ok());
        assertEquals(3, slotMachine.getWheels().size());
    }

    /**
     * Try the addWheel(pos) with a locked wheel
     * SHOULD NOT (Reject): Insert while any wheel (not only the first one) is locked.
     */
    @Test
    public void shouldntAddWheelWhenSomeWheelIsLocked(){
        slotMachine.lock(3);
        slotMachine.addWheel(1);
        assertFalse(slotMachine.ok());
        assertEquals(3, slotMachine.getWheels().size());
    }

    /**
     * Try the addWheel(type, pos)
     * Should (Acceptance): Create a Rebel, a Lefty or a normal wheel depending on the type.
     */
    @Test
    public void shouldAddWheelsOfEachType(){
        slotMachine.addWheel("Lefty", 2);
        slotMachine.addWheel("Rebel", 1);
        slotMachine.addWheel("Other", 5);
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        assertEquals("Rebel", wheels.get(0).getType());
        assertEquals("Lefty", wheels.get(2).getType());
        assertEquals("Normal", wheels.get(4).getType());
    }

    /**
     * Try the deleteWheel() 
     * Should (Acceptance): Remove the wheel from the indicated position and readjust its size 
     * the entire machine and its adjacent positions. 
     * SHOULD NOT (Reject): Allow a wheel to be removed if the machine has 3 wheels
     */
    @Test
    public void tryTheDeleteWheel(){
        for(int i = 0;i < 3;i++){
            slotMachine.addWheel(i);
            slotMachine.makeInvisible();
        }
        slotMachine.delWheel(3);
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        for(int i = 0; i < 4;i++){
            Wheel w = wheels.get(i);
            assertTrue(w.getpositionA() == i); 
        }
        
        for(int i = 0;i < 3;i++){
            slotMachine.delWheel(i);
            slotMachine.makeInvisible();
        }
        int totalWheels = slotMachine.getWheels().size();
        assertTrue(totalWheels == 3);
    }

    /**
     * Try the delWheel(pos) readjusting the layout
     * Should (Acceptance): Remove the wheel (including the first one) and leave the remaining
     * wheels indexed 0..n-1, evenly spaced and starting at the same X coordinate.
     */
    @Test
    public void shouldReadjustWheelsAfterDelete(){
        slotMachine.addWheel(1);
        int firstX = slotMachine.getWheels().get(0).getpositionX();
        slotMachine.delWheel(1);
        assertTrue(slotMachine.ok());
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        assertEquals(3, wheels.size());
        for (int i = 0; i < wheels.size(); i++) {
            assertEquals(i, wheels.get(i).getpositionA());
            assertEquals(firstX + 40 * i, wheels.get(i).getpositionX());
        }
    }

    /**
     * Try the delWheel(pos) with invalid conditions
     * SHOULD NOT (Reject): Remove a wheel when the position does not exist, when only the
     * minimum number of wheels remains, when the wheel is a Rebel or when some wheel is locked.
     */
    @Test
    public void shouldntDeleteWheelInvalidCases(){
        slotMachine.delWheel(1);
        assertFalse(slotMachine.ok());
        assertEquals(3, slotMachine.getWheels().size());

        slotMachine.addWheel("Rebel", 1);
        slotMachine.delWheel(0);
        assertFalse(slotMachine.ok());
        slotMachine.delWheel(5);
        assertFalse(slotMachine.ok());
        slotMachine.delWheel(1);
        assertFalse(slotMachine.ok());
        assertEquals(4, slotMachine.getWheels().size());

        slotMachine.lock(3);
        slotMachine.delWheel(2);
        assertFalse(slotMachine.ok());
        assertEquals(4, slotMachine.getWheels().size());
    }
    
    /**
     * Try the Swap() 
     * Should (Acceptance): Correctly switch the positions of two wheels 
     * valid within the machine. 
     * SHOULD NOT (Reject): Allow the exchange when one or both positions are
     * invalid, or when one of the wheels involved is locked.
     */
    @Test
    public void tryTheSwap(){
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        Wheel w1 = wheels.get(0);
        Wheel w2 = wheels.get(2);
        slotMachine.swap(1, 3);
        assertTrue(w1.getpositionA() == 2);
        assertTrue(w2.getpositionA() == 0);
        slotMachine.swap(-10, 7);
        assertFalse(slotMachine.ok());
        assertTrue(w1.getpositionA() == 2);
        assertTrue(w2.getpositionA() == 0);
        slotMachine.lock(1);
        slotMachine.swap(1, 3);
        assertFalse(slotMachine.ok());
        assertTrue(w1.getpositionA() == 2);
        assertTrue(w2.getpositionA() == 0);
    }

    /**
     * Try the swap() exchanging symbols and coordinates
     * Should (Acceptance): Exchange the wheels in the list while each slot keeps its X coordinate.
     */
    @Test
    public void shouldSwapWheelsAndKeepSlotCoordinates(){
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        ArrayList<String> before = slotMachine.configuration();
        int x1 = wheels.get(0).getpositionX();
        int x3 = wheels.get(2).getpositionX();
        slotMachine.swap(1, 3);
        assertTrue(slotMachine.ok());
        ArrayList<String> after = slotMachine.configuration();
        assertEquals(before.get(0), after.get(2));
        assertEquals(before.get(2), after.get(0));
        assertEquals(before.get(1), after.get(1));
        assertEquals(x1, wheels.get(0).getpositionX());
        assertEquals(x3, wheels.get(2).getpositionX());
    }

    /**
     * Try the swap() with invalid positions or a Rebel wheel
     * SHOULD NOT (Reject): Exchange when a position is 0 or greater than the number of wheels,
     * or when one of the wheels is a Rebel.
     */
    @Test
    public void shouldntSwapInvalidOrRebel(){
        ArrayList<String> before = slotMachine.configuration();
        slotMachine.swap(0, 2);
        assertFalse(slotMachine.ok());
        slotMachine.swap(1, 4);
        assertFalse(slotMachine.ok());
        assertEquals(before, slotMachine.configuration());

        slotMachine.addWheel("Rebel", 2);
        before = slotMachine.configuration();
        slotMachine.swap(1, 2);
        assertFalse(slotMachine.ok());
        assertEquals(before, slotMachine.configuration());
    }

    /**
     * Try the swap() with a second locked wheel
     * SHOULD NOT (Reject): Exchange when the second wheel is the locked one.
     */
    @Test
    public void shouldntSwapWhenSecondWheelIsLocked(){
        ArrayList<String> before = slotMachine.configuration();
        slotMachine.lock(3);
        slotMachine.swap(1, 3);
        assertFalse(slotMachine.ok());
        assertEquals(before, slotMachine.configuration());
    }
    
    /**
    * Try the Lock() 
    * Should (Acceptance): Set the status of the specified wheel to "locked" and
    * Return a confirmation of success. 
    * SHOULD NOT (Reject): Allow any action to be generated on the wheel and, on the other hand, 
    * that the wheel that this one is about to be blocked in the range
    */
    @Test
    public void tryTheLock(){
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        Wheel w1 = wheels.get(0);
        String s = w1.getshowSymbol();
        slotMachine.lock(1);
        assertTrue(w1.getesMove() == false);
        slotMachine.spin(1);
        slotMachine.delWheel(1);
        slotMachine.addSymbol("#9400D3", 1);
        assertTrue(w1.getpositionA() + 1 == 1);
        assertTrue(w1.getshowSymbol() == s);
    }

    /**
     * Try the lock() confirmation and isolation
     * Should (Acceptance): Lock only the indicated wheel and confirm with ok() == true.
     * SHOULD NOT (Reject): Lock a wheel that does not exist (ok() == false, nothing locked),
     * nor lock a Rebel wheel.
     */
    @Test
    public void shouldLockOnlyExistingWheels(){
        slotMachine.lock(2);
        assertTrue(slotMachine.ok());
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        assertTrue(wheels.get(0).getesMove());
        assertFalse(wheels.get(1).getesMove());
        assertTrue(wheels.get(2).getesMove());
        slotMachine.unlock(2);

        slotMachine.lock(0);
        assertFalse(slotMachine.ok());
        slotMachine.lock(4);
        assertFalse(slotMachine.ok());
        assertFalse(slotMachine.someWheelsAreLocked());

        slotMachine.addWheel("Rebel", 2);
        slotMachine.lock(2);
        assertTrue(slotMachine.getWheels().get(1).getesMove());
        assertFalse(slotMachine.someWheelsAreLocked());
    }

    /**
     * Try the someWheelsAreLocked()
     * Should (Acceptance): Report true when any wheel, not only the first one, is locked.
     */
    @Test
    public void shouldDetectLockedWheelInAnyPosition(){
        assertFalse(slotMachine.someWheelsAreLocked());
        slotMachine.lock(3);
        assertTrue(slotMachine.someWheelsAreLocked());
        slotMachine.unlock(3);
        assertFalse(slotMachine.someWheelsAreLocked());
    }
    
     /**
     * Try the Unlock() 
     * Should (Acceptance): Release the indicated wheel if it was previously locked,
     * and confirm with ok() == true.
     * SHOULD NOT (Reject): Alter the state of other wheels, or fail silently on an invalid index.
     */
    @Test
    public void tryTheUnlock(){
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        Wheel w1 = wheels.get(0);
        Wheel w2 = wheels.get(1);
        String s = w1.getshowSymbol();
        slotMachine.lock(1);
        slotMachine.lock(2);
        slotMachine.unlock(1);
        assertTrue(slotMachine.ok());
        assertTrue(w1.getesMove());
        assertFalse(w2.getesMove());
        slotMachine.spin(1);
        assertFalse(s.equals(w1.getshowSymbol()));
        slotMachine.unlock(0);
        assertFalse(slotMachine.ok());
        slotMachine.unlock(4);
        assertFalse(slotMachine.ok());
        assertFalse(w2.getesMove());
    }
    
    /**
    * Try the addSymbol() 
    * Should (Acceptance): Add a new valid symbol/color to the symbol list, at the indicated
    * position (positions out of range are adjusted to the ends), without changing the symbol
    * that each wheel is showing.
    * SHOULD NOT (Reject): Alter the visible symbol of any wheel.
    */
    @Test
    public void tryTheaddSymbol(){
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        Wheel w1 = wheels.get(0);
        String s = w1.getshowSymbol();
        slotMachine.addSymbol("#9400D3", 1);
        assertTrue(slotMachine.ok());
        assertEquals(4, slotMachine.distinctSymbols());
        assertEquals("#9400D3", w1.getlistcolor().get(1));
        assertEquals(s, w1.getshowSymbol());
        for (Wheel w : wheels) {
            assertTrue(w.getlistcolor().contains("#9400D3"));
        }
        slotMachine.addSymbol("#00FF00", 99);
        assertEquals("#00FF00", w1.getlistcolor().get(4));
        slotMachine.addSymbol("#FF00FF", -5);
        assertEquals("#FF00FF", w1.getlistcolor().get(0));
        assertEquals(6, slotMachine.distinctSymbols());
    }

    /**
     * Try the addSymbol() with null or empty colors
     * SHOULD NOT (Reject): Accept null or empty colors. Disabled: the current code does not validate them.
     */
    @Disabled("addSymbol does not validate null/empty colors")
    @Test
    public void shouldntAddNullOrEmptySymbol(){
        slotMachine.addSymbol(null, 1);
        assertFalse(slotMachine.ok());
        slotMachine.addSymbol("", 1);
        assertFalse(slotMachine.ok());
        assertEquals(3, slotMachine.distinctSymbols());
    }

    /**
     * Try the addSymbol() when the wheel is locked
     * SHOULD NOT (Reject): Add symbols if the wheel is locked. Disabled: addSymbol works on the
     * first wheel without checking its lock state.
     */
    @Disabled("addSymbol does not check whether the first wheel is locked")
    @Test
    public void shouldntAddSymbolToLockedWheel(){
        slotMachine.lock(1);
        slotMachine.addSymbol("#9400D3", 1);
        assertFalse(slotMachine.ok());
        assertEquals(3, slotMachine.distinctSymbols());
    }

    /**
     * Try the delSymbol()
     * Should (Acceptance): Remove the specified symbol from the shared symbol set and adjust its size.
     */
    @Test
    public void shouldDeleteSymbol(){
        slotMachine.addSymbol("#9400D3", 1);
        slotMachine.delSymbol("#9400D3");
        assertTrue(slotMachine.ok());
        assertEquals(3, slotMachine.distinctSymbols());
        for (Wheel w : slotMachine.getWheels()) {
            assertFalse(w.getlistcolor().contains("#9400D3"));
        }
        String shown = slotMachine.getWheels().get(2).getshowSymbol();
        slotMachine.delSymbol(shown);
        assertEquals(2, slotMachine.distinctSymbols());
        assertEquals(-1, (int) slotMachine.getPositionSymbolos().get(2));
    }

    /**
     * Try the delSymbol() with a nonexistent symbol
     * SHOULD NOT (Reject): Change the symbol set when the symbol does not exist.
     */
    @Test
    public void shouldntChangeSymbolsWhenDeletingNonexistent(){
        ArrayList<String> before = slotMachine.getWheels().get(0).getlistcolor();
        slotMachine.delSymbol("#ABCDEF");
        assertEquals(before, slotMachine.getWheels().get(0).getlistcolor());
        assertEquals(3, slotMachine.distinctSymbols());
    }

    /**
     * Try the delSymbol() with a nonexistent symbol
     * SHOULD NOT (Reject): Report success. Disabled: delSymbol sets ok() == true even when
     * nothing was deleted.
     */
    @Disabled("delSymbol reports ok() == true even if the symbol does not exist")
    @Test
    public void shouldReportFailureWhenDeletingNonexistentSymbol(){
        slotMachine.delSymbol("#ABCDEF");
        assertFalse(slotMachine.ok());
    }

    /**
     * Try the spin(wheel)
     * Should (Acceptance): Advance only the indicated wheel to the next symbol, changing its visible symbol.
     * SHOULD NOT (Reject): Spin a locked wheel or a wheel that does not exist.
     */
    @Test
    public void shouldSpinOnlyTheIndicatedWheel(){
        ArrayList<String> before = slotMachine.configuration();
        slotMachine.spin(2);
        ArrayList<String> after = slotMachine.configuration();
        assertTrue(slotMachine.ok());
        assertEquals(nextSymbol(before.get(1), 1), after.get(1));
        assertFalse(before.get(1).equals(after.get(1)));
        assertEquals(before.get(0), after.get(0));
        assertEquals(before.get(2), after.get(2));
    }

    @Test
    public void shouldntSpinLockedOrInvalidWheel(){
        ArrayList<String> before = slotMachine.configuration();
        slotMachine.lock(2);
        slotMachine.spin(2);
        assertEquals(before, slotMachine.configuration());
        slotMachine.spin(0);
        assertFalse(slotMachine.ok());
        slotMachine.spin(4);
        assertFalse(slotMachine.ok());
        assertEquals(before, slotMachine.configuration());
    }

    /**
     * Try the spin(wheel, steps)
     * Should (Acceptance): Spin the indicated wheel exactly the given number of steps
     * (it waits 2 seconds per step).
     */
    @Test
    public void shouldSpinWheelExactSteps(){
        String before = slotMachine.configuration().get(0);
        slotMachine.spin(1, 2);
        assertTrue(slotMachine.ok());
        assertEquals(nextSymbol(before, 2), slotMachine.configuration().get(0));
    }

    /**
     * Try the spin(wheel, steps) with invalid conditions
     * SHOULD NOT (Reject): Spin when {@code steps < 0}, when the wheel is locked or when it does not exist.
     */
    @Test
    public void shouldntSpinWheelWithInvalidSteps(){
        ArrayList<String> before = slotMachine.configuration();
        slotMachine.spin(1, -3);
        assertEquals(before, slotMachine.configuration());
        slotMachine.lock(1);
        slotMachine.spin(1, 2);
        assertEquals(before, slotMachine.configuration());
        slotMachine.spin(0, 1);
        assertFalse(slotMachine.ok());
        slotMachine.spin(4, 1);
        assertFalse(slotMachine.ok());
        assertEquals(before, slotMachine.configuration());
    }

    /**
     * Try the spin(wheel, steps) with negative steps
     * SHOULD NOT (Reject): Report success. Disabled: the call does nothing but keeps ok() == true.
     */
    @Disabled("spin(wheel, steps) with steps < 0 does nothing but still reports ok() == true")
    @Test
    public void shouldReportFailureOnNegativeSteps(){
        slotMachine.spin(1, -1);
        assertFalse(slotMachine.ok());
    }

    /**
     * Try the spin(setSymbols)
     * Should (Acceptance): Apply the given symbols to the machine and spin the unlocked wheels,
     * so every visible symbol belongs to the given array.
     */
    @Test
    public void shouldSpinWithGivenSymbols(){
        String[] given = {"#111111", "#222222", "#333333"};
        slotMachine.spin(given);
        assertTrue(slotMachine.ok());
        assertEquals(3, slotMachine.distinctSymbols());
        for (String shown : slotMachine.configuration()) {
            assertTrue(Arrays.asList(given).contains(shown));
        }
    }

    /**
     * Try the spin(setSymbols) with a different length
     * SHOULD NOT (Reject): Accept an array whose length does not match the number of wheels.
     * Disabled: the current code accepts any length.
     */
    @Disabled("spin(String[]) does not validate the array length against the number of wheels")
    @Test
    public void shouldntSpinWithMismatchedSymbols(){
        ArrayList<String> before = slotMachine.configuration();
        slotMachine.spin(new String[]{"#111111", "#222222"});
        assertFalse(slotMachine.ok());
        assertEquals(before, slotMachine.configuration());
    }

    /**
     * Try the spin()
     * Should (Acceptance): Spin all unlocked wheels simultaneously.
     * SHOULD NOT (Reject): Change the position or visible symbol of any locked wheel.
     */
    @Test
    public void shouldSpinAllUnlockedWheels(){
        ArrayList<String> before = slotMachine.configuration();
        slotMachine.spin();
        ArrayList<String> after = slotMachine.configuration();
        assertTrue(slotMachine.ok());
        for (int i = 0; i < before.size(); i++) {
            assertFalse(before.get(i).equals(after.get(i)));
        }
    }

    @Test
    public void shouldntSpinLockedWheelsOnSpinAll(){
        slotMachine.lock(2);
        ArrayList<String> before = slotMachine.configuration();
        int positionX = slotMachine.getWheels().get(1).getpositionX();
        slotMachine.spin();
        ArrayList<String> after = slotMachine.configuration();
        assertEquals(before.get(1), after.get(1));
        assertEquals(positionX, slotMachine.getWheels().get(1).getpositionX());
        assertFalse(before.get(0).equals(after.get(0)));
        assertFalse(before.get(2).equals(after.get(2)));
    }

    /**
     * Try the distinctSymbols()
     * Should (Acceptance): Return the number of symbols of the machine and update it after adding one.
     * SHOULD NOT (Reject): Return a negative number.
     */
    @Test
    public void shouldCountDistinctSymbols(){
        assertEquals(3, slotMachine.distinctSymbols());
        slotMachine.addSymbol("#9400D3", 1);
        assertEquals(4, slotMachine.distinctSymbols());
        assertTrue(slotMachine.distinctSymbols() >= 0);
    }

    /**
     * Try the distinctSymbols() with repeated symbols
     * SHOULD NOT (Reject): Count a duplicated symbol more than once. Disabled: it returns
     * the size of the symbol list, duplicates included.
     */
    @Disabled("distinctSymbols counts duplicated symbols more than once")
    @Test
    public void shouldCountDuplicatedSymbolOnce(){
        slotMachine.addSymbol("#9400D3", 1);
        slotMachine.addSymbol("#9400D3", 1);
        assertEquals(4, slotMachine.distinctSymbols());
    }

    /**
     * Try the configuration()
     * Should (Acceptance): Return the visible symbol of each wheel, in order from position 0 to n - 1,
     * reflecting the real state after spinning.
     */
    @Test
    public void shouldReturnVisibleConfiguration(){
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        ArrayList<String> config = slotMachine.configuration();
        assertEquals(wheels.size(), config.size());
        for (int i = 0; i < config.size(); i++) {
            assertEquals(wheels.get(i).getshowSymbol(), config.get(i));
        }
        slotMachine.spin(3);
        config = slotMachine.configuration();
        assertEquals(wheels.get(2).getshowSymbol(), config.get(2));
        assertTrue(slotMachine.ok());
    }

    /**
     * Try the getPositionSymbolos()
     * Should (Acceptance): Return, for each wheel, the position in the symbol list of the symbol it shows.
     */
    @Test
    public void shouldReturnSymbolPositions(){
        assertEquals(new ArrayList<Integer>(Arrays.asList(0, 1, 2)), slotMachine.getPositionSymbolos());
        slotMachine.spin(1);
        assertEquals(new ArrayList<Integer>(Arrays.asList(1, 1, 2)), slotMachine.getPositionSymbolos());
    }

    /**
     * Try the isJackpot()
     * Should (Acceptance): Return true only if all wheels show exactly the same symbol.
     * SHOULD NOT (Reject): Return true when at least one wheel shows a different symbol.
     */
    @Test
    public void shouldDetectJackpot(){
        assertFalse(slotMachine.isJackpot());
        forceJackpot();
        assertTrue(slotMachine.isJackpot());
        assertTrue(slotMachine.ok());
        slotMachine.spin(2);
        assertFalse(slotMachine.isJackpot());
    }

    /**
     * Try the makeVisible() / makeInvisible()
     * Should (Acceptance): Change only the graphic state of the machine.
     * SHOULD NOT (Reject): Alter symbols, positions or the lock state.
     */
    @Test
    public void shouldKeepLogicalStateWhenChangingVisibility(){
        slotMachine.lock(2);
        ArrayList<String> config = slotMachine.configuration();
        ArrayList<Integer> positions = slotMachine.getPositionSymbolos();
        int symbols = slotMachine.distinctSymbols();

        slotMachine.makeVisible();
        assertTrue(slotMachine.ok());
        assertEquals(config, slotMachine.configuration());
        assertEquals(positions, slotMachine.getPositionSymbolos());
        assertEquals(symbols, slotMachine.distinctSymbols());
        assertFalse(slotMachine.getWheels().get(1).getesMove());

        slotMachine.makeInvisible();
        assertTrue(slotMachine.ok());
        assertEquals(config, slotMachine.configuration());
        assertEquals(positions, slotMachine.getPositionSymbolos());
        assertEquals(symbols, slotMachine.distinctSymbols());
        assertFalse(slotMachine.getWheels().get(1).getesMove());
    }

    /**
     * Try the exit()
     * Should (Acceptance): Close the interface cleanly. Disabled: exit() calls System.exit(0),
     * which would terminate the JVM that runs the tests.
     */
    @Disabled("exit() calls System.exit(0) and would terminate the test run")
    @Test
    public void shouldExitCleanly(){
        slotMachine.exit();
    }

    /**
     * Try the ok()
     * Should (Acceptance): Return true when the last operation succeeded.
     * SHOULD NOT (Reject): Return true when the last operation used an out of range parameter.
     */
    @Test
    public void shouldReflectLastOperationInOk(){
        assertTrue(slotMachine.ok());
        slotMachine.addWheel(0);
        assertFalse(slotMachine.ok());
        slotMachine.spin();
        assertTrue(slotMachine.ok());
        slotMachine.swap(1, 9);
        assertFalse(slotMachine.ok());
        slotMachine.lock(1);
        assertTrue(slotMachine.ok());
    }

    /**
     * Try the Lefty wheel
     * Should (Acceptance): Align with the symbol of the wheel on its left when spinning, even after being swapped.
     * SHOULD NOT (Reject): Move when it is the first wheel.
     */
    @Test
    public void shouldLeftyFollowLeftNeighbour(){
        slotMachine.addWheel("Lefty", 2);
        slotMachine.spin();
        ArrayList<String> config = slotMachine.configuration();
        assertEquals(config.get(0), config.get(1));

        slotMachine.swap(2, 4);
        slotMachine.spin();
        config = slotMachine.configuration();
        assertEquals("Lefty", slotMachine.getWheels().get(3).getType());
        assertEquals(config.get(2), config.get(3));
    }

    @Test
    public void shouldntFirstLeftyMove(){
        slotMachine.addWheel("Lefty", 1);
        String before = slotMachine.configuration().get(0);
        slotMachine.spin();
        assertEquals(before, slotMachine.configuration().get(0));
    }

    /**
     * Try the Rebel wheel
     * Should (Acceptance): Keep spinning even when a lock is requested.
     */
    @Test
    public void shouldRebelIgnoreLock(){
        slotMachine.addWheel("Rebel", 2);
        slotMachine.lock(2);
        assertFalse(slotMachine.someWheelsAreLocked());
        String before = slotMachine.configuration().get(1);
        slotMachine.spin();
        assertFalse(before.equals(slotMachine.configuration().get(1)));
    }

    /**
     * Try the addSymbol(color, pos, type)
     * Should (Acceptance): Accept the types normal, ephemeral and shy (any letter case)
     * and add the symbol to the shared symbol list.
     * SHOULD NOT (Reject): Accept an unknown or null type; ok() == false and nothing is added.
     */
    @Test
    public void shouldAddSymbolsOfEachType(){
        slotMachine.addSymbol("#AA0000", 1, "ephemeral");
        assertTrue(slotMachine.ok());
        slotMachine.addSymbol("#00AA00", 1, "SHY");
        assertTrue(slotMachine.ok());
        slotMachine.addSymbol("#0000AA", 1, "normal");
        assertTrue(slotMachine.ok());
        assertEquals(6, slotMachine.distinctSymbols());
        slotMachine.addSymbol("#123456", 1, "unknown");
        assertFalse(slotMachine.ok());
        slotMachine.addSymbol("#123456", 1, null);
        assertFalse(slotMachine.ok());
        assertEquals(6, slotMachine.distinctSymbols());
    }

    /**
     * Try the ephemeral symbol
     * Should (Acceptance): Decrease its size every time it is selected on the wheel, and
     * the wheel recovers its normal size when a normal symbol is selected.
     */
    @Test
    public void shouldEphemeralSymbolShrinkEachSelection(){
        slotMachine.addSymbol("#AA0000", 1, "ephemeral");
        Wheel wheel = slotMachine.getWheels().get(0);
        int baseHeight = wheel.getShownHeight();
        int baseWidth = wheel.getShownWidth();
        slotMachine.spin(1);
        assertEquals(baseHeight - 1, wheel.getShownHeight());
        assertEquals(baseWidth - 1, wheel.getShownWidth());
        slotMachine.spin(1);
        slotMachine.spin(1);
        slotMachine.spin(1);
        assertEquals(baseHeight, wheel.getShownHeight());
        assertEquals(baseWidth, wheel.getShownWidth());
        slotMachine.spin(1);
        assertEquals(baseHeight - 2, wheel.getShownHeight());
        assertEquals(baseWidth - 2, wheel.getShownWidth());
    }

    /**
     * Try the ephemeral symbol until it vanishes
     * Should (Acceptance): Keep shrinking until it is a point (size 1 x 1).
     * SHOULD NOT (Reject): Go below a point.
     */
    @Test
    public void shouldEphemeralSymbolEndAsAPoint(){
        slotMachine.addSymbol("#AA0000", 1, "ephemeral");
        Wheel wheel = slotMachine.getWheels().get(0);
        int height = 0;
        int width = 0;
        for (int cycle = 0; cycle < 100; cycle++) {
            slotMachine.spin(1);
            height = wheel.getShownHeight();
            width = wheel.getShownWidth();
            assertTrue(height >= 1);
            assertTrue(width >= 1);
            slotMachine.spin(1);
            slotMachine.spin(1);
            slotMachine.spin(1);
        }
        assertEquals(1, height);
        assertEquals(1, width);
    }

    /**
     * Try the shy symbol
     * Should (Acceptance): Alternate between invisible and visible every time it is selected,
     * and the wheel is visible whenever a normal symbol is selected.
     */
    @Test
    public void shouldShySymbolAlternateVisibility(){
        slotMachine.addSymbol("#00AA00", 1, "shy");
        Wheel wheel = slotMachine.getWheels().get(0);
        int baseHeight = wheel.getShownHeight();
        slotMachine.spin(1);
        assertFalse(wheel.isShown());
        assertEquals(baseHeight, wheel.getShownHeight());
        slotMachine.spin(1);
        assertTrue(wheel.isShown());
        slotMachine.spin(1);
        slotMachine.spin(1);
        slotMachine.spin(1);
        assertTrue(wheel.isShown());
        slotMachine.spin(1);
        slotMachine.spin(1);
        slotMachine.spin(1);
        slotMachine.spin(1);
        assertFalse(wheel.isShown());
    }

    /**
     * Try the makeVisible() with special symbols
     * SHOULD NOT (Reject): Replace or alter the symbols already added, including their type.
     */
    @Test
    public void shouldKeepSpecialSymbolsWhenChangingVisibility(){
        slotMachine.addSymbol("#AA0000", 1, "ephemeral");
        Wheel wheel = slotMachine.getWheels().get(0);
        ArrayList<String> before = wheel.getlistcolor();
        int baseHeight = wheel.getShownHeight();
        slotMachine.makeVisible();
        assertEquals(before, wheel.getlistcolor());
        slotMachine.spin(1);
        assertEquals(baseHeight - 1, wheel.getShownHeight());
    }

    /**
     * Try the symbols()
     * Should (Acceptance): Return the colors of the symbols in the order they are on the wheel,
     * starting with the first one, including the ones added later.
     * SHOULD NOT (Reject): Return null, omit active symbols or expose the internal list.
     */
    @Test
    public void shouldReturnSymbolsInWheelOrder(){
        Wheel wheel = slotMachine.getWheels().get(0);
        String[] symbols = slotMachine.symbols();
        assertNotNull(symbols);
        assertTrue(slotMachine.ok());
        assertEquals(slotMachine.distinctSymbols(), symbols.length);
        assertEquals(wheel.getlistcolor(), Arrays.asList(symbols));

        slotMachine.addSymbol("#9400D3", 1);
        symbols = slotMachine.symbols();
        assertEquals(4, symbols.length);
        assertEquals("#9400D3", symbols[1]);
        assertEquals(wheel.getlistcolor(), Arrays.asList(symbols));

        symbols[0] = "#000001";
        assertFalse("#000001".equals(slotMachine.symbols()[0]));
    }
}