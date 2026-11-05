
/**
 * Write a description of class AcceptanceTestC4 here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class AcceptanceTestC4
{   
    private AcceptanceTestC4(){}
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
            slotmachine.addSymbol("#0000FF", 1, "ephemeral");
            slotmachine.addSymbol("#7f00ff", 1, "Shy");
            Thread.sleep(5000);
            slotmachine.addWheel("Rebel", 1);
            Thread.sleep(5000);
            slotmachine.addWheel("Lefty", 2);
            Thread.sleep(5000);
            slotmachine.spin();
            Thread.sleep(5000);
            slotmachine.spin();
            Thread.sleep(5000);
            slotmachine.spin();
            Thread.sleep(5000);
            slotmachine.spin(3);
            slotmachine.spin(3);
            slotmachine.isJackpot();
            Thread.sleep(5000);
            slotmachine.makeInvisible();
        } catch (InterruptedException e) {}
    }
}
