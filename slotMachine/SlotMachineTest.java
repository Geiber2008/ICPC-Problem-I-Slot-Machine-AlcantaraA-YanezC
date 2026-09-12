import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SlotMachine} class.
 * Evaluates core behaviors based on "What it SHOULD do" vs. "What it SHOULD NOT do".
 * 
 * @author Alcantara-Yanez
 * @version v1.0
 */
public class SlotMachineTest {

    private SlotMachine machine;

    /**
     * Set up a fresh SlotMachine instance before each test execution.
     * By default, the constructor initializes 3 wheels.
     */
    @BeforeEach
    public void setUp() {
        machine = new SlotMachine();
    }

    // =========================================================================
    // MINICYCLE 1: SlotMachine (Initialization)
    // =========================================================================
    
    /**
     * Verify that the SlotMachine initializes correctly in memory with 3 wheels 
     * and a valid initial state.
     */
    @Test
    @DisplayName("MC1: Should initialize the machine with 3 default wheels and ok=true")
    public void testSlotMachineInit_ShouldCreateCorrectly() {
        assertTrue(machine.ok(), "The machine status should be true upon creation.");
        assertEquals(3, machine.configuration().size(), "The machine should initialize with exactly 3 wheels.");
    }

    // =========================================================================
    // MINICYCLE 2: addWheel & delWheel
    // =========================================================================

    /**
     * Verify that adding a wheel increases the total wheel count and keeps the machine status valid.
     */
    @Test
    @DisplayName("MC2: Should insert a new wheel at the specified position")
    public void testAddWheel_ShouldInsertAtCorrectPosition() {
        int initialSize = machine.configuration().size();
        machine.addWheel(2); // 1-based index position
        
        assertEquals(initialSize + 1, machine.configuration().size(), "Wheel count should increase by 1.");
        assertTrue(machine.ok(), "The ok() status should remain true after adding a wheel.");
    }

    /**
     * Verify that attempting to delete a wheel using an out-of-bounds index 
     * correctly sets the ok status to false without crashing.
     */
    @Test
    @DisplayName("MC2: Should not alter wheel list when attempting to delete an invalid index")
    public void testDelWheel_ShouldNotFailOnInvalidIndex() {
        int initialSize = machine.configuration().size();
        
        // Out of bounds (upper)
        machine.delWheel(99); 
        assertFalse(machine.ok(), "The ok() status should set to false after deleting an invalid upper index.");
        assertEquals(initialSize, machine.configuration().size(), "Wheel collection size should remain unchanged.");
        
        // Out of bounds (lower)
        machine.delWheel(-1); 
        assertFalse(machine.ok(), "The ok() status should set to false after deleting a negative index.");
    }

    // =========================================================================
    // MINICYCLE 3: addSymbol, delSymbol & distinctSymbols
    // =========================================================================

    /**
     * Verify that adding symbols to the first wheel updates symbol count metrics.
     */
    @Test
    @DisplayName("MC3: Should add a symbol and reflect it in distinct symbols count")
    public void testAddSymbol_ShouldAddAndReflectInDistinctSymbols() {
        machine.addSymbol("blue", 1);
        assertTrue(machine.ok(), "Adding a valid symbol should result in ok=true.");
        assertTrue(machine.distinctSymbols() > 0, "Distinct symbols count should be greater than zero.");
    }

    /**
     * Verify that performing symbol operations on an empty machine gracefully 
     * sets ok status to false.
     */
    @Test
    @DisplayName("MC3: Should set ok=false when operating on a machine with no wheels")
    public void testSymbolOperations_ShouldFailOnEmptyMachine() {
        // Clear all wheels from the machine
        machine.delWheel(1);
        machine.delWheel(1);
        machine.delWheel(1);
        
        machine.addSymbol("red", 1);
        assertFalse(machine.ok(), "addSymbol on an empty machine should set ok=false.");

        machine.delSymbol("red");
        assertFalse(machine.ok(), "delSymbol on an empty machine should set ok=false.");

        assertEquals(0, machine.distinctSymbols(), "distinctSymbols should return 0 when no wheels exist.");
        assertFalse(machine.ok(), "distinctSymbols on an empty machine should set ok=false.");
    }

    // =========================================================================
    // MINICYCLE 4: spin (Standard & Single-wheel spin)
    // =========================================================================

    /**
     * Verify that triggering a general spin executes smoothly across all wheels.
     */
    @Test
    @DisplayName("MC4: Should rotate all unlocked wheels without throwing exceptions")
    public void testSpin_ShouldRotateAllWheels() {
        assertDoesNotThrow(() -> machine.spin(), "Global spin should execute without exceptions.");
        assertTrue(machine.ok(), "A successful global spin should leave ok=true.");
    }

    /**
     * Verify that attempting to spin an invalid wheel position sets ok status to false.
     */
    @Test
    @DisplayName("MC4: Should set ok=false when spinning an invalid wheel index")
    public void testSpinByWheel_ShouldFailOnInvalidPosition() throws InterruptedException {
        // Invalid position (1-based index)
        machine.spin(0); 
        assertFalse(machine.ok(), "Spinning wheel 0 should set ok=false.");

        // Position exceeding wheel count
        machine.spin(10); 
        assertFalse(machine.ok(), "Spinning a non-existent wheel should set ok=false.");
    }

    // =========================================================================
    // MINICYCLE 5: isJackpot
    // =========================================================================

    /**
     * Verify that isJackpot correctly identifies whether all visible symbols across wheels match.
     */
    @Test
    @DisplayName("MC5: Should accurately evaluate jackpot state based on visible symbols")
    public void testIsJackpot_ShouldEvaluateCorrectly() {
        boolean jackpotResult = machine.isJackpot();
        assertTrue(machine.ok(), "isJackpot() should execute successfully and leave ok=true.");
        
        ArrayList<String> config = machine.configuration();
        boolean expected = config.get(0).equals(config.get(1)) && config.get(1).equals(config.get(2));
        
        assertEquals(expected, jackpotResult, "isJackpot should return true if and only if all visible symbols match.");
    }

    // =========================================================================
    // MINICYCLE 6: ok (System State Validation)
    // =========================================================================

    /**
     * Verify that ok() reliably reports whether the previous operation succeeded or failed.
     */
    @Test
    @DisplayName("MC6: Should accurately track the status of the last executed operation")
    public void testOk_ShouldTrackLastOperationStatus() throws InterruptedException {
        // Successful operation
        machine.makeVisible();
        assertTrue(machine.ok(), "ok() should be true following makeVisible().");

        // Failed operation
        machine.spin(-5);
        assertFalse(machine.ok(), "ok() should update to false following an invalid spin operation.");

        // Recovery via a subsequent successful operation
        machine.spin();
        assertTrue(machine.ok(), "ok() should recover to true after a subsequent successful spin.");
    }

    // =========================================================================
    // ADVANCED MINICYCLES: swap, lock, unlock, spin(steps), spin(setSymbols)
    // =========================================================================

    /**
     * Verify that swap exchanges the internal array positions of two specified wheels.
     */
    @Test
    @DisplayName("Advanced MC1 (swap): Should swap positional order of two selected wheels")
    public void testSwap_ShouldExchangeWheelPositions() {
        ArrayList<String> initialConfig = machine.configuration();
        String symbolW1 = initialConfig.get(0);
        String symbolW2 = initialConfig.get(1);

        machine.swap(1, 2); // Swap Wheel 1 and Wheel 2

        ArrayList<String> newConfig = machine.configuration();
        assertEquals(symbolW1, newConfig.get(1), "Original symbol of Wheel 1 should now be at index 1.");
        assertEquals(symbolW2, newConfig.get(0), "Original symbol of Wheel 2 should now be at index 0.");
    }

    /**
     * Verify that a locked wheel rejects spin rotations and maintains its visible symbol state.
     */
    @Test
    @DisplayName("Advanced MC2 & MC3 (lock/unlock): Locked wheel should remain static during spin")
    public void testLockUnlock_ShouldPreventAndAllowRotation() throws InterruptedException {
        // Lock wheel 1
        machine.lock(1);
        String symbolBeforeSpin = machine.configuration().get(0);

        // Attempt to spin locked wheel 1
        machine.spin(1);
        String symbolAfterSpin = machine.configuration().get(0);

        assertEquals(symbolBeforeSpin, symbolAfterSpin, "A locked wheel MUST NOT change its visible symbol during a spin.");

        // Unlock wheel 1 and confirm spin capability restores
        machine.unlock(1);
        assertDoesNotThrow(() -> machine.spin(1), "Spinning an unlocked wheel should execute without errors.");
    }

    /**
     * Verify that advanced step-based spinning executes without throwing exceptions.
     */
    @Test
    @DisplayName("Advanced MC4 (spin with steps): Should execute precise rotation steps without errors")
    public void testSpinWithSteps_ShouldExecuteCorrectly() {
        assertDoesNotThrow(() -> machine.spin(1, 2), "Spinning wheel 1 for 2 steps should not throw an exception.");
        assertTrue(machine.ok(), "The ok status should remain true after a step-based spin.");
    }

    /**
     * Verify that setSymbols updates symbol configurations on the first wheel.
     */
    @Test
    @DisplayName("Advanced MC5 (spin with setSymbols): Should update symbol configuration array")
    public void testSpinWithSetSymbols_ShouldUpdateFirstWheel() {
        String[] newSymbols = {"yellow", "green", "red"};
        machine.spin(newSymbols);

        assertTrue(machine.ok(), "spin(setSymbols) should complete with ok=true.");
        assertNotNull(machine.configuration().get(0), "Wheel should maintain a valid visible symbol after updating configuration.");
    }
}