import java.util.ArrayList;

/**
 * Representa una rueda (Wheel) dentro de la máquina tragamonedas.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Wheel
{
    private int positionX;
    private int positionY;
    private int positionA;
    private ArrayList<Symbol> symbols; // Corregido: ya no es 'static'
    private Rectangle Vwheel;
    private String show_symbol; 

    /**
     * Constructor principal para la clase Wheel
     */
    public Wheel(int h, int w, int x, int y, String s, int pos)
    {
        Vwheel = new Rectangle(h, w, x, y, s);
        positionX = x;
        positionY = y;
        positionA = pos;
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

    public Wheel()
    {
        Vwheel = new Rectangle(80, 30, 110, 90, "red");
        Vwheel.makeVisible();
        symbols = new ArrayList<>();
    }

    public int getpositionX(){
        return positionX;
    }

    public int getpositionA(){
        return positionA;
    }

    public void setposition(int pos, int x)
    {
        positionA = pos;
        positionX = x;
        Vwheel.setpositionX(x);
    }

    public void delete(){
        Vwheel.makeInvisible();
        Vwheel = null;
    }

    public void addSymbols(String color, int pos){
        ArrayList<String> sname = Symbols();
        if (sname.contains(color)){
            System.out.println("Ya existe el símbolo: " + color);
            return;
        }

        if (pos < 0) {
            pos = 0;
        } else if (pos > symbols.size()) {
            pos = symbols.size();
        }
        
        Symbol s = new Symbol(color, pos);
        symbols.add(pos, s); // Corregido: inserta en la posición 'pos'
        
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            symbol.setposition(i);
        } 
    }

    public void deleteSymbols(String color){
        ArrayList<String> sname = Symbols();
        // Corregido: negación ! para verificar si NO existe
        if (!sname.contains(color)){ 
            System.out.println("No existe el símbolo: " + color);
            return;
        }
        
        // Corregido: buscar el índice en la lista de nombres
        int pos = sname.indexOf(color); 
        symbols.remove(pos);        
        
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            symbol.setposition(i);
        } 
    }

    public ArrayList<String> Symbols(){
        ArrayList<String> listSymbols = new ArrayList<>();
        for (Symbol s : symbols){
            listSymbols.add(s.getname());
        }
        return listSymbols;
    }

    public int distinctSymbols(){
        return symbols.size();        
    }

    public String getshow_symbol(){
        return show_symbol;        
    }

    public void rotate(){

    }
}