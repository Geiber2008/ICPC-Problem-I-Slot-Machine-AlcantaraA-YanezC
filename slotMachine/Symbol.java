
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
    private int height;
    private int width;
    private String type; 

    /**
     * Constructor for objects of class Symbol
     */
    public Symbol(String n,int p,String t,int h, int w)
    {
        name = n;
        position = p;
        type = t;
        height = h;
        width = w;
    }
    
    public Symbol(String n,int p)
    {
        name = n;
        position = p;
        type = "Normal";
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
    
    public int getPosition(){
        return position;
    }
    
    public String getType(){ return "Normal";}
    public int getHeight(){ if(height == 1){ return 2;} return height;}
    public int getWidth(){ if(width == 1){ return 2;} return width;}
    public void setHeight(int h){ height = h;}
    public void setWidth(int w){ width = w;}
}