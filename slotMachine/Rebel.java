
/**
 * Write a description of class Rebel here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Rebel extends Wheel

{
    /**
     * Constructor for objects of class Rebel
     */
    public Rebel(int h, int w, int x, int y, String s, int pos,SlotMachine m,boolean inicio)
    {
        super(h,w,x,y,s,pos,m,inicio);
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    @Override
    public void lock(){}
    
    @Override
    public String getType(){
        return "Rebel";
    }
}