import java.util.ArrayList;
import java.util.List;
/**
 * Write a description of class SlotMachine here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class SlotMachine
{
    // instance variables - replace the example below with your own
    private ArrayList<Wheel> wheels;
    private Rectangle machine;
    private int positonW;
    private int sizehorizontal;
    private boolean ok;

    /**
     * Constructor for objects of class SlotMachine
     */
    public SlotMachine(){
        sizehorizontal = 130;
        machine = new Rectangle(100,sizehorizontal,100,80,"blue");
        machine.makeVisible();
        wheels = new ArrayList<>();
        int x = 110;
        for(int i = 1;i < 4 ;i++){
            Wheel w = new Wheel(80,30,x,90,"red",i);
            wheels.add(w);
            x += 40;
        }
}

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void addWheel(int pos) {   
    if (pos < 0) {
        pos = 0;
    } else if (pos > wheels.size()) {
        pos = wheels.size();
    }
    sizehorizontal += 40;
    machine.changeSize(100, sizehorizontal);
    Wheel j = wheels.get(0);
    int startX = j.getpositionX();
    Wheel w = new Wheel(80, 30, 0, 90, "green", pos-1);
    wheels.add(pos-1, w); 
    for (int i = 0; i < wheels.size(); i++) {
        Wheel wheel = wheels.get(i);
        wheel.setposition(i, startX);
        startX += 40;
    }
}

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    
    public void delWheel(int pos) {
    if (pos < 0) {
        pos = 0;
    } else if (pos > wheels.size()) {
        pos = wheels.size();
    }
    sizehorizontal -= 40;
    machine.changeSize(100, sizehorizontal);
    Wheel j = wheels.get(0);
    int startX = j.getpositionX();
    Wheel w = wheels.get(pos-1);
    wheels.remove(pos-1);
    w.delete();
    for (int i = 0; i < wheels.size(); i++) {
        Wheel wheel = wheels.get(i);
        wheel.setposition(i, startX);
        startX += 40;
    } 
} 

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */  
    public void addSymbol(String color, int pos){
        Wheel w = wheels.get(0);
        w.addSymbols(color,pos);
}

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */  
    public void delSymbol(String color){
        Wheel w = wheels.get(0);
        w.deleteSymbols(color);
}

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */  
    public int distinctSymbols(){
        Wheel w = wheels.get(0);
        return w.distinctSymbols();
}    

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */  
    public ArrayList<String> configuration(){
        ArrayList<String> listSymbols = new ArrayList<>();
        for (Wheel w: wheels){
            listSymbols.add(w.getshow_symbol());
        }
        return listSymbols;
} 

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */  
    public boolean isJackpot(){
       ArrayList<String> list = configuration();
       for(int i = 0; i > list.size() - 1;i++){
           if (list.get(i) != list.get(i+1)){
            return false;
            }  
        }
        return true;
} 
    
}


