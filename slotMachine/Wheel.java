import java.util.ArrayList;
/**
 * Write a description of class Wheel here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Wheel
{
    // instance variables - replace the example below with your own
    private int positionX;
    private int positionY;
    private int positionA;
    private static ArrayList<Symbol> Symbols;   
    private Rectangle Vwheel;
    private String show_symbol; 
    
    /**
     * Constructor for objects of class Wheel
     */
    public Wheel(int h,int w,int x,int y,String s,int pos)
    {
        // initialise instance variables
        Vwheel = new Rectangle(h,w,x,y,s);
        positionX = x;
        positionY = y;
        positionA = pos;
        Vwheel.makeVisible();
        Symbols = new ArrayList<>();
        Symbol cruz = new Symbol("red",1);
        Symbol corazon = new Symbol("yellow",1);
        Symbol picas = new Symbol("green",1);
        Symbols.add(cruz);
        Symbols.add(corazon);
        Symbols.add(picas);
        positionA = pos;
        show_symbol = "red";
    }
    
    public Wheel()
    {
        // initialise instance variables
        Vwheel = new Rectangle(80,30,110,90,"red");
        Vwheel.makeVisible();
    }
    
    public int getpositionX(){
        return positionX;
    }
    
    public int getpositionA(){
        return positionA;
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void setposition(int pos,int x)
    {
        positionA = pos;
        positionX = x;
        Vwheel.setpositionX(x);
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void delete(){
        Vwheel.makeInvisible();
        Vwheel = null;
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void addSymbols(String color,int pos){
        ArrayList<String> sname = Symbols();
        if (sname.contains(color)){
            System.out.println("ya esta el" + color);
            return;
        }
        
        if (pos < 0) {
            pos = 0;
        } else if (pos > Symbols.size()) {
            pos = Symbols.size();
        }
        Symbol s = new Symbol(color,pos);
        Symbols.add(s);
        for (int i = 0; i < Symbols.size(); i++) {
            Symbol symbol = Symbols.get(i);
            symbol.setposition(i);
            } 
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void deleteSymbols(String color){
        ArrayList<String> sname = Symbols();
        if (sname.contains(color)){
            System.out.println("no esta el" + color);
            return;
        }
        int pos = Symbols.indexOf(color);
        Symbols.remove(pos);       
        for (int i = 0; i < Symbols.size(); i++) {
            Symbol symbol = Symbols.get(i);
            symbol.setposition(i);
            } 
    
}
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public ArrayList<String> Symbols(){
        ArrayList<String> listSymbols = new ArrayList<>();
        for (Symbol s : Symbols){
            listSymbols.add(s.getname());
        }
        return listSymbols;
}

     /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public int distinctSymbols(){
        return Symbols.size();        
}

     /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public String getshow_symbol(){
        return show_symbol;        
}
}