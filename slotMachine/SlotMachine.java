import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a Slot Machine containing multiple wheels and visual elements.
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class SlotMachine
{
    private ArrayList<Wheel> wheels;
    private Rectangle machine;
    private int positonW;
    private int sizehorizontal;
    private boolean ok;

    /**
     * Constructor for objects of class SlotMachine.
     * Initializes the machine frame and default wheels.
     */
    public SlotMachine(){
        sizehorizontal = 130;
        machine = new Rectangle(100, sizehorizontal, 100, 80, "blue");
        machine.makeVisible();
        wheels = new ArrayList<>();
        int x = 110;
        for(int i = 1; i < 4; i++){
            Wheel w = new Wheel(80, 30, x, 90, colorRandom(), i);
            wheels.add(w);
            x += 40;
        }
        ok = true;
    }

    /**
     * Makes the slot machine frame and all its wheels visible.
     */
    public void makeVisible() {
        machine.makeVisible();
        for (Wheel w : wheels) {
            // Assumes Wheel has a makeVisible() or handles visibility through setposition/Vwheel
            // If Wheel exposes Vwheel or a makeVisible method:
            w.setposition(w.getpositionA(), w.getpositionX());
        }
        ok = true;
    }

    /**
     * Makes the slot machine frame and all its wheels invisible.
     */
    public void makeInvisible() {
        machine.makeInvisible();
        for (Wheel w : wheels) {
            w.delete();
        }
        ok = true;
    }

    /**
     * Exits the application or closes the machine interface.
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }

    /**
     * Indicates whether the last executed action was successful.
     *
     * @return true if the last operation succeeded; false otherwise.
     */
    public boolean ok() {
        return ok;
    }

    /**
     * Spins a single wheel at the specified index/position.
     *
     * @param wheel position index of the wheel to spin (1-based index).
     */
    public void spin(int wheel) {
        if (wheel >= 1 && wheel <= wheels.size()) {
            wheels.get(wheel - 1).rotate();
            ok = true;
        } else {
            System.out.println("Invalid wheel position: " + wheel);
            ok = false;
        }
    }

    /**
     * Spins all wheels contained in the slot machine.
     */
    public void spin() {
        for (Wheel w : wheels) {
            w.rotate();
        }
        ok = true;
    }

    /**
     * Adds a new wheel at the specified position and adjusts the machine UI.
     *
     * @param pos index where the wheel should be inserted.
     */
    public void addWheel(int pos) {   
        pos = limit(pos);
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
        ok = true;
    }

    /**
     * Removes a wheel at the specified position and updates layout.
     *
     * @param pos index of the wheel to be deleted.
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty() || pos < 1 || pos > wheels.size()) {
            ok = false;
            return;
        }
        pos = limit(pos);
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
        ok = true;
    } 

    /**
     * Adds a symbol of a specified color to the first wheel.
     *
     * @param color the color name of the symbol.
     * @param pos index position where to add the symbol.
     */
    public void addSymbol(String color, int pos){
        if (!wheels.isEmpty()) {
            Wheel w = wheels.get(0);
            w.addSymbols(color, pos);
            ok = true;
        } else {
            ok = false;
        }
    }

    /**
     * Deletes a symbol of a specified color from the first wheel.
     *
     * @param color the color name of the symbol to delete.
     */
    public void delSymbol(String color){
        if (!wheels.isEmpty()) {
            Wheel w = wheels.get(0);
            w.deleteSymbols(color);
            ok = true;
        } else {
            ok = false;
        }
    }

    /**
     * Gets the number of distinct symbols on the first wheel.
     *
     * @return count of distinct symbols.
     */
    public int distinctSymbols(){
        if (!wheels.isEmpty()) {
            Wheel w = wheels.get(0);
            ok = true;
            return w.distinctSymbols();
        }
        ok = false;
        return 0;
    }    

    /**
     * Gets current symbol configuration across all wheels.
     *
     * @return list containing current visible symbol color for each wheel.
     */
    public ArrayList<String> configuration(){
        ArrayList<String> listSymbols = new ArrayList<>();
        for (Wheel w: wheels){
            listSymbols.add(w.getshow_symbol());
        }
        ok = true;
        return listSymbols;
    } 

    /**
     * Checks whether two consecutive elements in the machine are equal.
     *
     * @return true if there are two identical contiguous elements; false otherwise.
     */  
    public boolean isJackpot() {
        ArrayList<String> list = configuration();
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).equals(list.get(i + 1))) {
                ok = true;
                return true;
            }  
        }
        ok = true;
        return false;
    }

    /**
     * Limits the position boundary to valid ranges for wheels list.
     *
     * @param pos input index.
     * @return capped position within bounds.
     */
    private int limit(int pos){
        if (pos < 0) {
            pos = 0;
        } else if (pos > wheels.size()) {
            pos = wheels.size();
        }
        return pos;
    }

    /**
     * Generates a random color for wheel setup.
     *
     * @return random color string.
     */
    private String colorRandom(){
        Random random = new Random();
        String[] opciones = {"red", "yellow", "naranja"};
        return opciones[random.nextInt(opciones.length)];
    }    
}
