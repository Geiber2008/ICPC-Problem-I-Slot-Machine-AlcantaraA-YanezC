import java.util.ArrayList;

/**
 * Represents an individual wheel inside the slot machine.
 * Holds symbols and manages its own rotation and visual representation.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Wheel
{
    private int positionX;
    private int positionY;
    private int positionA;
    private static ArrayList<Symbol> symbols;
    private Rectangle Vwheel;
    private String show_symbol; 
    private boolean esMove;

    /**
     * Main constructor for objects of class Wheel.
     *
     * @param h Height of the wheel rectangle.
     * @param w Width of the wheel rectangle.
     * @param x X-coordinate position.
     * @param y Y-coordinate position.
     * @param s Initial symbol/color shown.
     * @param pos Position index of the wheel.
     */
    public Wheel(int h, int w, int x, int y, String s, int pos)
    {
        Vwheel = new Rectangle(h, w, x, y, s);
        positionX = x;
        positionY = y;
        positionA = pos;
        esMove = true;
        Vwheel.makeVisible();
        
        symbols = new ArrayList<>();
        Symbol cruz = new Symbol("red", 1); 
        Symbol corazon = new Symbol("yellow", 1); 
        Symbol picas = new Symbol("green", 1);
        
        symbols.add(cruz); 
        symbols.add(corazon); 
        symbols.add(picas);
        
        show_symbol = s;
    }

    /**
     * Default constructor for objects of class Wheel.
     */
    public Wheel()
    {
        Vwheel = new Rectangle(80, 30, 110, 90, "red");
        Vwheel.makeVisible();
        symbols = new ArrayList<>();
        show_symbol = "red";
    }

    /**
     * Gets the current X position.
     *
     * @return X position coordinate.
     */
    public int getpositionX(){
        return positionX;
    }

    /**
     * Gets the current index position of the wheel.
     *
     * @return Index position.
     */
    public int getpositionA(){
        return positionA;
    }

    /**
     * Updates the position index and X coordinate of the wheel.
     *
     * @param pos New index position.
     * @param x New X coordinate.
     */
    public void setposition(int pos, int x)
    {
        positionA = pos;
        positionX = x;
        if (Vwheel != null) {
            Vwheel.setpositionX(x);
        }
    }
    
    /**
     * Updates the position index and X coordinate of the wheel.
     *
     * @param pos New index position.
     * @param x New X coordinate.
     */
    public void Moveposition(int pos, int x)
    {
        positionA = pos;
        if (Vwheel != null) {
            Vwheel.moveHorizontal(positionX-x);
        }
        positionX = x;
    }

    /**
     * Deletes and hides the visual representation of the wheel.
     */
    public void delete(){
        if (Vwheel != null) {
            Vwheel.makeInvisible();
            Vwheel = null;
        }
    }

    /**
     * Adds a new symbol at a specific index in the wheel.
     *
     * @param color The name or color of the symbol.
     * @param pos The index where the symbol should be added.
     */
    public void addSymbols(String color, int pos){
        ArrayList<String> sname = Symbols();
        if (sname.contains(color)){
            System.out.println("Symbol already exists: " + color);
            return;
        }
        
        pos = limit(pos);
        
        Symbol s = new Symbol(color, pos);
        symbols.add(pos, s);
        
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            symbol.setposition(i);
        } 
    }

    /**
     * Removes a symbol by its color/name from the wheel.
     *
     * @param color The name or color of the symbol to delete.
     */
    public void deleteSymbols(String color){
        ArrayList<String> sname = Symbols();
        if (!sname.contains(color)){ 
            System.out.println("Symbol does not exist: " + color);
            return;
        }
        
        int pos = sname.indexOf(color); 
        symbols.remove(pos);        
        
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            symbol.setposition(i);
        } 
    }

    /**
     * Retrieves the names of all symbols in the wheel.
     *
     * @return List of symbol names.
     */
    public ArrayList<String> Symbols(){
        ArrayList<String> listSymbols = new ArrayList<>();
        for (Symbol s : symbols){
            listSymbols.add(s.getname());
        }
        return listSymbols;
    }

    /**
     * Gets the total count of distinct symbols available in the wheel.
     *
     * @return Number of symbols.
     */
    public int distinctSymbols(){
        return symbols.size();        
    }

    /**
     * Gets the name of the currently visible symbol.
     *
     * @return Currently shown symbol.
     */
    public String getshow_symbol(){
        return show_symbol;        
    }

    /**
     * Rotates the wheel to the next available symbol in the list.
     * Updates both the logical state and the visual representation.
     */
    public void rotate() {
        if (symbols == null || symbols.isEmpty()) {
            System.out.println("No symbols available to rotate.");
            return;
        }

        ArrayList<String> symbolNames = Symbols();
        int currentIndex = symbolNames.indexOf(this.show_symbol);

        // Advance to the next index sequentially (wrapping around if needed)
        int nextIndex = (currentIndex + 1) % symbols.size();

        Symbol nextSymbol = symbols.get(nextIndex);
        this.show_symbol = nextSymbol.getname();

        if (Vwheel != null) {
            Vwheel.changeColor(this.show_symbol);
        }
    }
    
    /**
     * Rotates the wheel to the next available symbol in the list.
     * Updates both the logical state and the visual representation.
     */
    public void makeVisible() {
        Vwheel.makeVisible();
    }
    
    /**
     * Rotates the wheel to the next available symbol in the list.
     * Updates both the logical state and the visual representation.
     */
    public void makeInvisible() {
        Vwheel.makeInvisible();
    }
    
    /**
     * Rotates the wheel to the next available symbol in the list.
     * Updates both the logical state and the visual representation.
     */
    public boolean getesMove() {
        return esMove;
    }
    
    /**
     * Rotates the wheel to the next available symbol in the list.
     * Updates both the logical state and the visual representation.
     */
    public void lock(){
        esMove = false;
    }
    
    /**
     * Rotates the wheel to the next available symbol in the list.
     * Updates both the logical state and the visual representation.
     */
    public void unlock(){
        esMove = true;
    }
    
    /**
     * Rotates the wheel to the next available symbol in the list.
     * Updates both the logical state and the visual representation.
     */
    public void setSymbols(ArrayList<String> setSymbols){
        symbols.clear();
        int p = 0;
        for(String newS: setSymbols){
            Symbol s = new Symbol(newS,p);
            symbols.add(s);
            p+= 1;
        }
    }
    
    private int limit (int x){
        if (x < 0) {
            x = 0;
        } else if (x > symbols.size()) {
            x = symbols.size();
        }
        return x;
    }
}
