import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

/**
 * Represents a Slot Machine containing multiple wheels and visual elements.
 * Provides controls for spinning, adding/removing wheels, locking, and checking payouts.
 * 
 * @author Alcantara-Yanez
 * @version v1.0
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
     * Initializes the machine frame and standard set of wheels.
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
     * Makes the slot machine frame and all contained wheels visible on screen.
     */
    public void makeVisible() {
        machine.makeVisible();
        for (Wheel w : wheels) {
            w.makeVisible();
        }
        ok = true;
    }

    /**
     * Hides the slot machine frame and all contained wheels from the display.
     */
    public void makeInvisible() {
        machine.makeInvisible();
        for (Wheel w : wheels) {
            w.makeInvisible();
        }
        ok = true;
    }

    /**
     * Exits the application and closes the graphic window interface.
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }

    /**
     * Indicates whether the last executed operation finished successfully.
     *
     * @return true if the last operation succeeded; false otherwise.
     */
    public boolean ok() {
        return ok;
    }

    /**
     * Spins a single wheel at the specified position for a given number of steps.
     *
     * @param wheel 1-based index position of the wheel to spin.
     * @param steps number of steps/rotations to perform.
     * @throws InterruptedException if the thread execution sleep is interrupted.
     */
    public void spin(int wheel, int steps) throws InterruptedException {
        if (wheel >= 1 && wheel <= wheels.size()) {
            if (wheels.get(wheel - 1).getesMove()){
                for (int i = 0; i < steps; i++){
                    Thread.sleep(2000);
                    wheels.get(wheel - 1).rotate();
                }
            }
            ok = true;
        } else {
            System.out.println("Invalid wheel position: " + wheel);
            ok = false;
        }
    }

    /**
     * Spins a single wheel at the specified index position once.
     *
     * @param wheel 1-based index position of the wheel to spin.
     * @throws InterruptedException if thread execution is interrupted.
     */
    public void spin(int wheel) throws InterruptedException {
        if (wheel >= 1 && wheel <= wheels.size()) {
            if (wheels.get(wheel - 1).getesMove()){
                wheels.get(wheel - 1).rotate();
            }
            ok = true;
        } else {
            System.out.println("Invalid wheel position: " + wheel);
            ok = false;
        }
    }

    /**
     * Assigns new symbol colors to the first wheel and rotates all movable wheels once.
     * 
     * @param setSymbols array of color strings to update symbols on the first wheel.
     */
    public void spin(String[] setSymbols) {
        ArrayList<String> listaSimbolos = new ArrayList<>(java.util.Arrays.asList(setSymbols));

        if (!wheels.isEmpty()) {
            wheels.get(0).setSymbols(listaSimbolos);
        }

        for (Wheel w : wheels) {
            if (w.getesMove()) {
                w.rotate();
            }
        }
        ok = true;
    }

    /**
     * Rotates all unlocked wheels in the slot machine simultaneously once.
     */
    public void spin() {
        for (Wheel w : wheels) {
            if (w.getesMove()){
                w.rotate();
            }
        }
        ok = true;
    }

    /**
     * Adds a new wheel at the specified position and expands the machine frame layout.
     *
     * @param pos 1-based index position where the new wheel should be placed.
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
     * Removes a wheel at the specified position and resizes the machine layout.
     *
     * @param pos 1-based index of the wheel to be deleted.
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
     * Adds a symbol of a specified color to the first wheel at a designated position.
     *
     * @param color the color name of the new symbol.
     * @param pos target index position inside the symbol list.
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
     * Deletes all occurrences of a symbol with the specified color from the first wheel.
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
     * Gets the count of distinct symbol colors available on the first wheel.
     *
     * @return the number of unique symbols on the first wheel, or 0 if no wheels exist.
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
     * Retrieves the current visible symbol color from each wheel in the machine.
     *
     * @return list containing current visible symbol color strings for all wheels.
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
     * Evaluates if there are identical adjacent symbols currently visible across the wheels.
     *
     * @return true if two consecutive wheels display matching symbol colors; false otherwise.
     */  
    public boolean isJackpot() {
        boolean isJackpot = true;
        for (int i = 0; i < wheels.size() - 1; i++) {
            Wheel w1 = wheels.get(i);
            Wheel w2 = wheels.get(i+1);
            if (!(w1.getshow_symbol().equals(w2.getshow_symbol()))) {
                ok = true;
                isJackpot = false;
                return isJackpot;
            }  
        }
        ok = true;
        return isJackpot;
    }

    /**
     * Restricts a given wheel index position within acceptable collection boundaries.
     *
     * @param pos index position to validate.
     * @return clamped position index within valid wheel boundaries.
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
     * Selects a random color string from a predefined array of color options.
     *
     * @return random color string name.
     */
    private String colorRandom(){
        Random random = new Random();
        String[] opciones = {"red", "yellow", "green"};
        return opciones[random.nextInt(opciones.length)];
    } 

    /**
     * Swaps the positions and visual coordinates of two wheels in the machine.
     *
     * @param wheel1 1-based index position of the first wheel.
     * @param wheel2 1-based index position of the second wheel.
     */
    public void swap(int wheel1, int wheel2){
        Wheel wheeln1 = wheels.get(wheel1-1);
        Wheel wheeln2 = wheels.get(wheel2-1);
        int position1 = wheeln1.getpositionX();
        int position2 = wheeln2.getpositionX();
        wheeln1.setposition(wheel2, position2);
        wheeln2.setposition(wheel1, position1);
        Collections.swap(wheels, wheel1-1, wheel2-1);
    } 

    /**
     * Locks a specific wheel to prevent it from rotating during spin actions.
     *
     * @param wheel 1-based index position of the wheel to lock.
     */
    public void lock(int wheel){
        wheels.get(wheel-1).lock();        
    } 

    /**
     * Unlocks a specific wheel allowing it to resume rotating during spin actions.
     *
     * @param wheel 1-based index position of the wheel to unlock.
     */
    public void unlock(int wheel){
        wheels.get(wheel-1).unlock();        
    } 
}