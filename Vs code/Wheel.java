import java.util.ArrayList;

/**
 * Rueda individual de la máquina tragamonedas. Muestra un símbolo (color) a la
 * vez y gira recorriendo la lista de símbolos, que es compartida por todas las
 * ruedas (campo estático).
 */
public class Wheel
{
    static final String TYPE_NORMAL = "Normal";
    static final String TYPE_REBEL = "Rebel";
    static final String TYPE_LEFTY = "Lefty";

    private static final int DEFAULT_HEIGHT = 80;
    private static final int DEFAULT_WIDTH = 30;
    private static final int DEFAULT_X = 110;
    private static final int DEFAULT_Y = 90;
    private static final String DEFAULT_COLOR = "red";
    private static final int SYMBOL_NOT_FOUND = -1;

    private static final ArrayList<Symbol> SYMBOLS = new ArrayList<>();

    private int positionX;
    private int positionA;
    private Rectangle visualWheel;
    private String showSymbol;
    private boolean movable = true;
    private SlotMachine machine;
    private int baseHeight;
    private int baseWidth;
    private int shownHeight;
    private int shownWidth;
    private boolean shown;

    /**
     * Crea una rueda visible con un símbolo inicial.
     *
     * @param h alto del rectángulo.
     * @param w ancho del rectángulo.
     * @param x coordenada X.
     * @param y coordenada Y.
     * @param s color o símbolo inicial mostrado.
     * @param pos índice de la rueda en la máquina (base 0).
     * @param m máquina a la que pertenece.
     * @param inicio true para registrar el color inicial en la lista compartida de símbolos.
     */
    public Wheel(int h, int w, int x, int y, String s, int pos, SlotMachine m, boolean inicio)
    {
        visualWheel = new Rectangle(h, w, x, y, s);
        visualWheel.makeVisible();
        baseHeight = h;
        baseWidth = w;
        shownHeight = h;
        shownWidth = w;
        shown = true;
        positionX = x;
        positionA = pos;
        machine = m;
        if (inicio) {
            insertSymbol(new Symbol(s, SYMBOLS.size(), Symbol.TYPE_NORMAL, h, w), SYMBOLS.size());
        }
        showSymbol = s;
    }

    /**
     * Crea una rueda roja por defecto y reinicia la lista compartida de símbolos.
     */
    public Wheel()
    {
        visualWheel = new Rectangle(DEFAULT_HEIGHT, DEFAULT_WIDTH, DEFAULT_X, DEFAULT_Y, DEFAULT_COLOR);
        visualWheel.makeVisible();
        baseHeight = DEFAULT_HEIGHT;
        baseWidth = DEFAULT_WIDTH;
        shownHeight = DEFAULT_HEIGHT;
        shownWidth = DEFAULT_WIDTH;
        shown = true;
        SYMBOLS.clear();
        showSymbol = DEFAULT_COLOR;
    }

    /**
     * Obtiene la coordenada X actual de la rueda.
     *
     * @return coordenada X.
     */
    public int getpositionX()
    {
        return positionX;
    }

    /**
     * Obtiene el índice de la rueda dentro de la máquina.
     *
     * @return índice (base 0).
     */
    public int getpositionA()
    {
        return positionA;
    }

    /**
     * Reubica la rueda actualizando su índice y su coordenada X. No hace nada
     * si la rueda está bloqueada.
     *
     * @param pos nuevo índice (base 0).
     * @param x nueva coordenada X.
     */
    public void setposition(int pos, int x)
    {
        if (!movable) {
            return;
        }
        positionA = pos;
        positionX = x;
        if (visualWheel != null) {
            visualWheel.setpositionX(x);
        }
    }

    /**
     * Desplaza horizontalmente la rueda hacia la coordenada X indicada y
     * actualiza su índice. No hace nada si la rueda está bloqueada.
     *
     * @param pos nuevo índice (base 0).
     * @param x coordenada X de destino.
     */
    @SuppressWarnings("PMD.MethodNamingConventions")
    public void Moveposition(int pos, int x)
    {
        if (!movable) {
            return;
        }
        positionA = pos;
        if (visualWheel != null) {
            visualWheel.moveHorizontal(positionX - x);
        }
        positionX = x;
    }

    /**
     * Oculta y libera la representación gráfica de la rueda. No hace nada si
     * la rueda está bloqueada.
     */
    public void delete()
    {
        if (!movable) {
            return;
        }
        if (visualWheel != null) {
            display(false);
            visualWheel = null;
        }
    }

    /**
     * Inserta un símbolo con tipo y dimensiones explícitas.
     *
     * @param color nombre o color del símbolo.
     * @param pos índice de inserción; se ajusta al rango válido.
     * @param t tipo del símbolo.
     * @param h alto asociado.
     * @param w ancho asociado.
     */
    public void addSymbols(String color, int pos, String t, int h, int w)
    {
        insertSymbol(new Symbol(color, pos, t, h, w), pos);
    }

    /**
     * Inserta un símbolo de tipo "Normal" con dimensiones.
     *
     * @param color nombre o color del símbolo.
     * @param pos índice de inserción; se ajusta al rango válido.
     * @param h alto asociado.
     * @param w ancho asociado.
     */
    public void addSymbols(String color, int pos, int h, int w)
    {
        addSymbols(color, pos, Symbol.TYPE_NORMAL, h, w);
    }

    /**
     * Inserta un símbolo de tipo "Normal" sin dimensiones.
     *
     * @param color nombre o color del símbolo.
     * @param pos índice de inserción; se ajusta al rango válido.
     */
    public void addSymbols(String color, int pos)
    {
        insertSymbol(new Symbol(color, pos), pos);
    }

    /**
     * Elimina la primera aparición de un símbolo por su nombre. Si no existe,
     * no modifica la lista.
     *
     * @param color nombre o color del símbolo a eliminar.
     */
    public void deleteSymbols(String color)
    {
        int index = Symbols().indexOf(color);
        if (index < 0) {
            return;
        }
        SYMBOLS.remove(index);
        refreshSymbolPositions();
    }

    /**
     * Obtiene los nombres de todos los símbolos, en orden.
     *
     * @return nueva lista con los nombres de los símbolos.
     */
    @SuppressWarnings("PMD.MethodNamingConventions")
    public ArrayList<String> Symbols()
    {
        ArrayList<String> names = new ArrayList<>();
        for (Symbol s : SYMBOLS) {
            names.add(s.getname());
        }
        return names;
    }

    /**
     * Obtiene la cantidad de símbolos en la lista (incluye repetidos).
     *
     * @return número de símbolos.
     */
    public int distinctSymbols()
    {
        return SYMBOLS.size();
    }

    /**
     * Obtiene el símbolo que la rueda muestra actualmente.
     *
     * @return nombre o color del símbolo visible.
     */
    public String getshowSymbol()
    {
        return showSymbol;
    }

    /**
     * Avanza la rueda al siguiente símbolo de la lista (circular) y actualiza
     * su estado visual según el tipo del símbolo. No hace nada si la rueda
     * está bloqueada o no hay símbolos.
     */
    public void rotate()
    {
        if (!movable || SYMBOLS.isEmpty()) {
            return;
        }
        int currentIndex = Symbols().indexOf(showSymbol);
        int nextIndex = (currentIndex + 1) % SYMBOLS.size();
        Symbol nextSymbol = SYMBOLS.get(nextIndex);
        applyVisualEffect(nextSymbol);
        nextSymbol.setposition(nextIndex);
        showSymbol = nextSymbol.getname();
        visualWheel.changeColor(showSymbol);
    }

    /**
     * Hace visible la rueda en pantalla.
     */
    public void makeVisible()
    {
        display(true);
    }

    /**
     * Oculta la rueda de la pantalla.
     */
    public void makeInvisible()
    {
        display(false);
    }

    /**
     * Indica si la rueda puede girar y moverse.
     *
     * @return true si no está bloqueada.
     */
    public boolean getesMove()
    {
        return movable;
    }

    /**
     * Bloquea la rueda: no girará ni cambiará de posición.
     */
    public void lock()
    {
        movable = false;
    }

    /**
     * Desbloquea la rueda para que pueda girar y moverse.
     */
    public void unlock()
    {
        movable = true;
    }

    /**
     * Sobrescribe, por índice, los primeros símbolos de la lista con los
     * colores dados y agrega los que excedan el tamaño actual. Luego
     * renumera las posiciones.
     *
     * @param setSymbols colores de los nuevos símbolos.
     */
    public void setSymbols(ArrayList<String> setSymbols)
    {
        for (int i = 0; i < setSymbols.size(); i++) {
            Symbol symbol = new Symbol(setSymbols.get(i), i);
            if (i < SYMBOLS.size()) {
                SYMBOLS.set(i, symbol);
            } else {
                SYMBOLS.add(symbol);
            }
        }
        refreshSymbolPositions();
    }

    /**
     * Obtiene los nombres de los símbolos; equivale a {@link #Symbols()}.
     *
     * @return lista con los nombres de los símbolos.
     */
    public ArrayList<String> getlistcolor()
    {
        return Symbols();
    }

    /**
     * Obtiene la posición del primer símbolo con el nombre indicado.
     *
     * @param s nombre o color del símbolo.
     * @return posición del símbolo, o -1 si no existe.
     */
    public int getPositionSymbol(String s)
    {
        for (Symbol symbol : SYMBOLS) {
            if (symbol.getname().equals(s)) {
                return symbol.getPosition();
            }
        }
        return SYMBOL_NOT_FOUND;
    }

    /**
     * Establece directamente el índice de la rueda dentro de la máquina.
     *
     * @param pos nuevo índice (base 0).
     */
    public void setPositionA(int pos)
    {
        positionA = pos;
    }

    /**
     * Obtiene la máquina a la que pertenece la rueda.
     *
     * @return máquina contenedora, o null si se usó el constructor por defecto.
     */
    public final SlotMachine getMachine()
    {
        return machine;
    }

    /**
     * Identifica el tipo de rueda.
     *
     * @return "Normal".
     */
    public String getType()
    {
        return TYPE_NORMAL;
    }

    /**
     * Obtiene el alto con el que se dibuja actualmente la rueda.
     *
     * @return alto dibujado.
     */
    public int getShownHeight()
    {
        return shownHeight;
    }

    /**
     * Obtiene el ancho con el que se dibuja actualmente la rueda.
     *
     * @return ancho dibujado.
     */
    public int getShownWidth()
    {
        return shownWidth;
    }

    /**
     * Indica si la rueda está visible en pantalla.
     *
     * @return true si está visible.
     */
    public boolean isShown()
    {
        return shown;
    }

    /**
     * Aplica el efecto visual del símbolo al que gira la rueda. Un símbolo
     * efímero reduce su tamaño en cada selección hasta quedar como un punto;
     * uno tímido alterna entre visible e invisible en cada selección; y uno
     * normal restaura el tamaño base y muestra la rueda.
     *
     * @param symbol símbolo que pasará a mostrarse.
     */
    private void applyVisualEffect(Symbol symbol)
    {
        if (Symbol.TYPE_EPHEMERAL.equals(symbol.getType())) {
            symbol.shrink();
            resize(symbol.getHeight(), symbol.getWidth());
            display(true);
        } else if (Symbol.TYPE_SHY.equals(symbol.getType())) {
            symbol.toggleVisible();
            resize(baseHeight, baseWidth);
            display(symbol.isVisible());
        } else {
            resize(baseHeight, baseWidth);
            display(true);
        }
    }

    /**
     * Cambia el tamaño dibujado de la rueda si es distinto del actual.
     *
     * @param height nuevo alto.
     * @param width nuevo ancho.
     */
    private void resize(int height, int width)
    {
        if (shownHeight != height || shownWidth != width) {
            visualWheel.changeSize(height, width);
            shownHeight = height;
            shownWidth = width;
        }
    }

    /**
     * Muestra u oculta la rueda y recuerda su estado.
     *
     * @param visible true para mostrarla; false para ocultarla.
     */
    private void display(boolean visible)
    {
        if (visible) {
            visualWheel.makeVisible();
        } else {
            visualWheel.makeInvisible();
        }
        shown = visible;
    }

    /**
     * Inserta un símbolo en la lista compartida y renumera las posiciones.
     *
     * @param symbol símbolo a insertar.
     * @param pos índice deseado; se ajusta al rango válido.
     */
    private void insertSymbol(Symbol symbol, int pos)
    {
        SYMBOLS.add(limit(pos), symbol);
        refreshSymbolPositions();
    }

    /**
     * Asigna a cada símbolo su índice actual en la lista (base 0).
     */
    private void refreshSymbolPositions()
    {
        for (int i = 0; i < SYMBOLS.size(); i++) {
            SYMBOLS.get(i).setposition(i);
        }
    }

    /**
     * Ajusta un índice al rango [0, cantidad de símbolos].
     *
     * @param x índice a ajustar.
     * @return índice dentro del rango válido.
     */
    private int limit(int x)
    {
        return Math.max(0, Math.min(x, SYMBOLS.size()));
    }

    /**
     * Gira la rueda, símbolo a símbolo, hasta que muestre el símbolo indicado.
     * No hace nada si el símbolo no existe o no puede alcanzarse desde esta
     * rueda.
     *
     * @param symbol color del símbolo que debe quedar visible.
     */
    public void placeSymbol(String symbol)
    {
        if (!Symbols().contains(symbol)) {
            return;
        }
        for (int attempts = 0; attempts < SYMBOLS.size() && !showSymbol.equals(symbol); attempts++) {
            rotate();
        }
    }
}