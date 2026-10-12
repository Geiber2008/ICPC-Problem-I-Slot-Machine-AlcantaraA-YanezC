import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

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
        slotMachine = new SlotMachine();
        slotMachine.makeInvisible();
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
    }
    
    /**
     * Try the AddWheel() 
     * Should (Acceptance): Insert a new wheel at a valid index 0 <= pos <= 
     * totalWheels, increasing the total number of wheels by 1.
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
        assertTrue(w1.getpositionA() == 3);
        assertTrue(w2.getpositionA() == 1);
        slotMachine.swap(-10, 7);
        assertTrue(w1.getpositionA() == 3);
        assertTrue(w2.getpositionA() == 1);
        slotMachine.lock(1);
        slotMachine.swap(1, 3);
        assertTrue(w1.getpositionA() == 3);
        assertTrue(w2.getpositionA() == 1);
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
     * Try the Unlock() 
     * Should (Acceptance): Set the status of the specified wheel to "locked" and
     * Return a confirmation of success. 
     * SHOULD NOT (Reject): Allow any action to be generated on the wheel 
     */
    @Test
    public void tryTheUnlock(){
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        Wheel w1 = wheels.get(0);
        String s = w1.getshowSymbol();
        
    }
    
    /**
    * Try the addSymbol() 
    * Should (Acceptance): Set the status of the specified wheel to "locked" and
    * Return a confirmation of success. 
    * SHOULD NOT (Reject): Allow any action to be generated on the wheel and, on the other hand, 
    * that the wheel that this one is about to be blocked in the range
    */
    @Test
    public void tryTheaddSymbol(){
        ArrayList<Wheel> wheels = slotMachine.getWheels();
        Wheel w1 = wheels.get(0);
        String s = w1.getshowSymbol();
        
    }
}