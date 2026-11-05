
/**
 * Write a description of class AcceptanceTestC1 here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class AcceptanceTestC1
{   
    private AcceptanceTestC1(){}
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
            slotmachine.addWheel(2);
            Thread.sleep(5000);
            slotmachine.delWheel(2);
            Thread.sleep(5000);
            slotmachine.addSymbol("#0000ff", 1);
            slotmachine.placeSymbol(1, "#0000ff");
            slotmachine.spin();
            Thread.sleep(5000);
            slotmachine.delSymbol("#0000ff");
            slotmachine.spin();
            slotmachine.spin();
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