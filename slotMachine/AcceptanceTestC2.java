
/**
 * Write a description of class AcceptanceTestC2 here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class AcceptanceTestC2
{   
    private static String[] colores = {
            "#FF0000", 
            "#0000FF",  
            "#FFFF00"  
        };
    private AcceptanceTestC2(){}
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    static public void goTestAcceptation()
    {
        SlotMachine slotmachine = new SlotMachine();
       try {
            Thread.sleep(5000);
            slotmachine.swap(1, 2);
            Thread.sleep(5000);
            slotmachine.lock(1);
            slotmachine.spin();
            Thread.sleep(5000);
            slotmachine.unlock(1);
            slotmachine.spin();
            Thread.sleep(5000);
            slotmachine.spin(3, 2);
            Thread.sleep(5000);
            slotmachine.spin(colores);
            Thread.sleep(5000);
        } catch (InterruptedException e) {}
    }
}
