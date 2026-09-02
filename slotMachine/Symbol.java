
/**
 * Write a description of class Symbol here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Symbol
{
    // instance variables - replace the example below with your own
    private String name;
    private int position; 

    /**
     * Constructor for objects of class Symbol
     */
    public Symbol(String n,int p)
    {
        // initialise instance variables
        name = n;
        position = p;
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void setposition(int y)
    {
        position = y;
    }
    
    public String getname()
    {
        return name;
    }
}