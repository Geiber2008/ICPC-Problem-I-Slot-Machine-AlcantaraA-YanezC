import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

/**
 * The test class Test_slotMachine.
 *
 * @author AlcantaraA-YanezC
 * @version v1.0
 */
public class Test_slotMachine
{
    private SlotMachine slotMachine;

    /**
     * Default constructor for test class Test_slotMachine
     */
    public Test_slotMachine()
    {
    }

    /**
     * Sets up the test fixture.
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
        slotMachine = new SlotMachine();
    }

    /**
     * Tears down the test fixture.
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
        slotMachine = null;
    }

    @Test
    public void testInitialState()
    {
        ArrayList<String> config = slotMachine.configuration();
        assertEquals(3, config.size(), "The machine should start with 3 wheels.");
        assertTrue(slotMachine.ok(), "ok() should return true after creation.");
    }

    @Test
    public void testAddWheel()
    {
        int initialSize = slotMachine.configuration().size();
        slotMachine.addWheel(1);
        assertEquals(initialSize + 1, slotMachine.configuration().size());
        assertTrue(slotMachine.ok());
    }

    @Test
    public void testDelWheelValid()
    {
        slotMachine.delWheel(1);
        assertEquals(2, slotMachine.configuration().size());
        assertTrue(slotMachine.ok());
    }

    @Test
    public void testDelWheelInvalid()
    {
        slotMachine.delWheel(99);
        assertFalse(slotMachine.ok(), "ok() should return false for out-of-bounds wheel.");
    }

    @Test
    public void testSpinAll()
    {
        slotMachine.spin();
        assertTrue(slotMachine.ok());
        assertEquals(3, slotMachine.configuration().size());
    }

    @Test
    public void testSpinSingleWheelValid()
    {
        slotMachine.spin(1);
        assertTrue(slotMachine.ok());
    }

    @Test
    public void testSpinSingleWheelInvalid()
    {
        slotMachine.spin(-1);
        assertFalse(slotMachine.ok());
    }

    @Test
    public void testIsJackpot()
    {
        boolean jackpot = slotMachine.isJackpot();
        assertTrue(slotMachine.ok());
    }
}