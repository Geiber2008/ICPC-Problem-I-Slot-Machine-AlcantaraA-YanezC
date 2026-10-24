
/**
 * Write a description of class Lefty here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Lefty extends Wheel
{
    // instance variables - replace the example below with your own

    /**
     * Constructor for objects of class Lefty
     */
    public Lefty(int h, int w, int x, int y, String s, int pos,SlotMachine m,boolean inicio)
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
    public void rotate()
    {   
        if(getpositionA() != 0){
            int position = getpositionA() - 1;
            String symbol = getMachine().getWheels().get(position).getshowSymbol();
            while (symbol != getshowSymbol()){
                super.rotate();
            }
        }
    }
    
    @Override
    public String getType(){
        return "Lefty";
    }
}