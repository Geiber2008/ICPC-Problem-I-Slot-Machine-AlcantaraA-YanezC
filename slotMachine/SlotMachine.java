import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Máquina tragamonedas compuesta por un marco y varias ruedas. Permite girar
 * ruedas, agregarlas, eliminarlas, bloquearlas, intercambiarlas, administrar
 * los símbolos y verificar si hay premio (jackpot). Después de cada operación,
 * {@link #ok()} indica si terminó con éxito.
 */
public class SlotMachine
{
    private static final int maxWheels = 50;
    private static final int minWheels = 3;
    private static final int defaultWheels = 3;
    private static final int frameHeight = 100;
    private static final int frameX = 100;
    private static final int frameY = 80;
    private static final int frameBaseWidth = 10;
    private static final String frameColor = "#000000";
    private static final int firstWheelX = 110;
    private static final int wheelY = 90;
    private static final int wheelHeight = 80;
    private static final int wheelWidth = 30;
    private static final int wheelSpacing = 40;
    private static final int colorChannelValues = 254;
    private static final long spinDelayMillis = 2000;
    private static final Random random = new Random();

    private final ArrayList<Wheel> wheels = new ArrayList<>();
    private final ArrayList<String> colors = new ArrayList<>();
    private final Rectangle machine;
    private int frameWidth = frameBaseWidth;
    private boolean ok;

    /**
     * Crea una máquina con n ruedas normales de colores aleatorios y un marco
     * ajustado a su cantidad.
     *
     * @param n número de ruedas iniciales.
     */
    public SlotMachine(int n)
    {
        machine = new Rectangle(frameHeight, frameWidth, frameX, frameY, frameColor);
        machine.makeVisible();
        frameWidth += wheelSpacing * n;
        machine.changeSize(frameHeight, frameWidth);
        int x = firstWheelX;
        addInitialWheel(0, x).setSymbols(colors);
        x += wheelSpacing;
        for (int i = 1; i < n; i++) {
            addInitialWheel(i, x);
            x += wheelSpacing;
        }
        ok = true;
    }

    /**
     * Crea una máquina con el número de ruedas por defecto (3).
     */
    public SlotMachine()
    {
        this(defaultWheels);
    }

    /**
     * Muestra el marco y todas las ruedas sin alterar símbolos ni posiciones.
     */
    public void makeVisible()
    {
        machine.makeVisible();
        for (Wheel w : wheels) {
            w.makeVisible();
        }
        ok = true;
    }

    /**
     * Oculta el marco y todas las ruedas.
     */
    public void makeInvisible()
    {
        machine.makeInvisible();
        for (Wheel w : wheels) {
            w.makeInvisible();
        }
        ok = true;
    }

    /**
     * Oculta la máquina y termina la aplicación.
     */
    public void exit()
    {
        makeInvisible();
        System.exit(0);
    }

    /**
     * Indica si la última operación terminó con éxito.
     *
     * @return true si tuvo éxito; false en caso contrario.
     */
    public boolean ok()
    {
        return ok;
    }

    /**
     * Gira una rueda varias veces, con una pausa de 2 segundos antes de cada
     * giro. Si la rueda está bloqueada no gira, pero la operación se considera
     * exitosa. Si el hilo es interrumpido, se detiene y ok queda en false.
     *
     * @param wheel posición de la rueda (base 1).
     * @param steps cantidad de giros.
     */
    public void spin(int wheel, int steps)
    {
        if (!isValidWheel(wheel)) {
            ok = false;
            return;
        }
        Wheel target = wheels.get(wheel - 1);
        if (target.getesMove()) {
            for (int i = 0; i < steps; i++) {
                if (!pause()) {
                    ok = false;
                    return;
                }
                target.rotate();
            }
        }
        ok = true;
    }

    /**
     * Gira una rueda una sola vez, si no está bloqueada.
     *
     * @param wheel posición de la rueda (base 1); si es inválida, ok queda en false.
     */
    public void spin(int wheel)
    {
        if (!isValidWheel(wheel)) {
            ok = false;
            return;
        }
        Wheel target = wheels.get(wheel - 1);
        if (target.getesMove()) {
            target.rotate();
        }
        ok = true;
    }

    /**
     * Actualiza los símbolos de la primera rueda con los colores dados y luego
     * gira una vez todas las ruedas desbloqueadas.
     *
     * @param setSymbols colores de los símbolos a aplicar.
     */
    public void spin(String[] setSymbols)
    {
        if (!wheels.isEmpty()) {
            wheels.get(0).setSymbols(new ArrayList<>(Arrays.asList(setSymbols)));
        }
        spin();
    }

    /**
     * Gira una vez todas las ruedas desbloqueadas.
     */
    public void spin()
    {
        for (Wheel w : wheels) {
            if (w.getesMove()) {
                w.rotate();
            }
        }
        ok = true;
    }

    /**
     * Agrega una rueda normal en la posición indicada.
     *
     * @param pos posición de inserción (base 1), entre 1 y la cantidad de ruedas + 1.
     */
    public void addWheel(int pos)
    {
        addWheel(Wheel.typeNormal, pos);
    }

    /**
     * Agrega una rueda del tipo indicado y reubica las demás. Falla (ok = false)
     * si la posición es inválida, se alcanzó el máximo de ruedas o alguna rueda
     * está bloqueada.
     *
     * @param type "Rebel", "Lefty" o cualquier otro valor para una rueda normal.
     * @param pos posición de inserción (base 1).
     */
    public void addWheel(String type, int pos)
    {
        if (pos < 1 || pos > wheels.size() + 1 || wheels.size() >= maxWheels || someWheelsAreLocked()) {
            ok = false;
            return;
        }
        frameWidth += wheelSpacing;
        machine.changeSize(frameHeight, frameWidth);
        int startX = wheels.get(0).getpositionX();
        wheels.add(pos - 1, createWheel(type, pos - 1));
        arrangeWheels(startX);
        ok = true;
    }

    /**
     * Elimina una rueda y reubica las demás. Falla (ok = false) si la posición
     * es inválida, solo quedan las ruedas mínimas, hay ruedas bloqueadas o la
     * rueda es de tipo Rebel.
     *
     * @param pos posición de la rueda a eliminar (base 1).
     */
    public void delWheel(int pos)
    {
        if (wheels.size() <= minWheels || !isValidWheel(pos) || someWheelsAreLocked()
            || isRebel(wheels.get(pos - 1))) {
            ok = false;
            return;
        }
        frameWidth -= wheelSpacing;
        machine.changeSize(frameHeight, frameWidth);
        int startX = wheels.get(0).getpositionX();
        Wheel removed = wheels.remove(pos - 1);
        removed.delete();
        arrangeWheels(startX);
        ok = true;
    }

    /**
     * Agrega un símbolo normal a la lista compartida mediante la primera rueda.
     *
     * @param color color del nuevo símbolo.
     * @param pos índice de inserción en la lista de símbolos.
     */
    public void addSymbol(String color, int pos)
    {
        addSymbol(color, pos, Symbol.typeNormal);
    }

    /**
     * Agrega un símbolo del tipo indicado a la lista compartida mediante la
     * primera rueda. Los símbolos "ephemeral" reducen su tamaño cada vez que
     * una rueda los selecciona, hasta quedar como un punto; los "shy" alternan
     * entre visible e invisible cada vez que una rueda los selecciona. Falla
     * (ok = false) si el tipo no existe.
     *
     * @param color color del nuevo símbolo.
     * @param pos índice de inserción en la lista de símbolos.
     * @param type tipo del símbolo: "normal", "ephemeral" o "shy" (sin distinguir mayúsculas).
     */
    public void addSymbol(String color, int pos, String type)
    {
        String symbolType = Symbol.normalizeType(type);
        if (wheels.isEmpty() || symbolType == null) {
            ok = false;
            return;
        }
        wheels.get(0).addSymbols(color, pos, symbolType, wheelHeight, wheelWidth);
        ok = true;
    }

    /**
     * Elimina un símbolo (su primera aparición) mediante la primera rueda.
     *
     * @param color color del símbolo a eliminar.
     */
    public void delSymbol(String color)
    {
        if (wheels.isEmpty()) {
            ok = false;
            return;
        }
        wheels.get(0).deleteSymbols(color);
        ok = true;
    }

    /**
     * Obtiene la cantidad de símbolos registrados según la primera rueda.
     *
     * @return número de símbolos, o 0 si no hay ruedas.
     */
    public int distinctSymbols()
    {
        if (wheels.isEmpty()) {
            ok = false;
            return 0;
        }
        ok = true;
        return wheels.get(0).distinctSymbols();
    }

    /**
     * Obtiene los colores de los símbolos en el orden en que están en la rueda,
     * empezando por el primero. Devuelve una copia: modificarla no altera la máquina.
     *
     * @return arreglo con los colores de los símbolos, o vacío si no hay ruedas.
     */
    public String[] symbols()
    {
        if (wheels.isEmpty()) {
            ok = false;
            return new String[0];
        }
        ok = true;
        return wheels.get(0).Symbols().toArray(new String[0]);
    }

    /**
     * Obtiene el símbolo visible de cada rueda, de izquierda a derecha.
     *
     * @return lista de colores visibles.
     */
    public ArrayList<String> configuration()
    {
        ArrayList<String> listSymbols = new ArrayList<>();
        for (Wheel w : wheels) {
            listSymbols.add(w.getshowSymbol());
        }
        ok = true;
        return listSymbols;
    }

    /**
     * Indica si todas las ruedas muestran el mismo símbolo.
     *
     * @return true si hay premio; false en caso contrario.
     */
    public boolean isJackpot()
    {
        ok = true;
        for (int i = 0; i < wheels.size() - 1; i++) {
            if (!wheels.get(i).getshowSymbol().equals(wheels.get(i + 1).getshowSymbol())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Intercambia la posición de dos ruedas. Falla (ok = false) si alguna
     * posición es inválida, si alguna rueda está bloqueada o si alguna es Rebel.
     *
     * @param wheel1 posición de la primera rueda (base 1).
     * @param wheel2 posición de la segunda rueda (base 1).
     */
    public void swap(int wheel1, int wheel2)
    {
        if (!isValidWheel(wheel1) || !isValidWheel(wheel2)) {
            ok = false;
            return;
        }
        Wheel first = wheels.get(wheel1 - 1);
        Wheel second = wheels.get(wheel2 - 1);
        if (!first.getesMove() || !second.getesMove() || isRebel(first) || isRebel(second)) {
            ok = false;
            return;
        }
        int firstX = first.getpositionX();
        int secondX = second.getpositionX();
        first.setposition(wheel2 - 1, secondX);
        second.setposition(wheel1 - 1, firstX);
        Collections.swap(wheels, wheel1 - 1, wheel2 - 1);
        ok = true;
    }

    /**
     * Bloquea una rueda para que no gire ni se reubique (las Rebel lo ignoran).
     *
     * @param wheel posición de la rueda (base 1); si es inválida, ok queda en false.
     */
    public void lock(int wheel)
    {
        if (!isValidWheel(wheel)) {
            ok = false;
            return;
        }
        wheels.get(wheel - 1).lock();
        ok = true;
    }

    /**
     * Desbloquea una rueda para que pueda girar y reubicarse.
     *
     * @param wheel posición de la rueda (base 1); si es inválida, ok queda en false.
     */
    public void unlock(int wheel)
    {
        if (!isValidWheel(wheel)) {
            ok = false;
            return;
        }
        wheels.get(wheel - 1).unlock();
        ok = true;
    }

    /**
     * Obtiene la lista de ruedas de la máquina (no es una copia).
     *
     * @return lista de ruedas, de izquierda a derecha.
     */
    public ArrayList<Wheel> getWheels()
    {
        return wheels;
    }

    /**
     * Obtiene, para cada rueda, la posición en la lista de símbolos del
     * símbolo que muestra.
     *
     * @return lista de posiciones; contiene -1 si el símbolo visible ya no existe.
     */
    public ArrayList<Integer> getPositionSymbolos()
    {
        ArrayList<Integer> listPosition = new ArrayList<>();
        for (Wheel w : wheels) {
            listPosition.add(w.getPositionSymbol(w.getshowSymbol()));
        }
        return listPosition;
    }

    /**
     * Indica si al menos una rueda está bloqueada.
     *
     * @return true si alguna rueda está bloqueada.
     */
    public boolean someWheelsAreLocked()
    {
        for (Wheel w : wheels) {
            if (!w.getesMove()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea una rueda inicial con color aleatorio y la registra en la máquina.
     *
     * @param index índice de la rueda (base 0).
     * @param x coordenada X de la rueda.
     * @return la rueda creada.
     */
    private Wheel addInitialWheel(int index, int x)
    {
        String color = colorRandom();
        Wheel wheel = new Wheel(wheelHeight, wheelWidth, x, wheelY, color, index, this, true);
        wheels.add(wheel);
        colors.add(color);
        return wheel;
    }

    /**
     * Construye una rueda del tipo pedido con un color ya existente.
     *
     * @param type "Rebel", "Lefty" o cualquier otro valor para una rueda normal.
     * @param index índice de la rueda (base 0).
     * @return la rueda creada.
     */
    private Wheel createWheel(String type, int index)
    {
        String color = colorRandomExists();
        if (Wheel.typeRebel.equals(type)) {
            return new Rebel(wheelHeight, wheelWidth, 0, wheelY, color, index, this, false);
        }
        if (Wheel.typeLefty.equals(type)) {
            return new Lefty(wheelHeight, wheelWidth, 0, wheelY, color, index, this, false);
        }
        return new Wheel(wheelHeight, wheelWidth, 0, wheelY, color, index, this, false);
    }

    /**
     * Reasigna índice y coordenada X a todas las ruedas, separadas uniformemente.
     *
     * @param startX coordenada X de la primera rueda.
     */
    private void arrangeWheels(int startX)
    {
        int x = startX;
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).setposition(i, x);
            x += wheelSpacing;
        }
    }

    /**
     * Verifica que la posición corresponda a una rueda existente.
     *
     * @param position posición (base 1).
     * @return true si está entre 1 y la cantidad de ruedas.
     */
    private boolean isValidWheel(int position)
    {
        return position >= 1 && position <= wheels.size();
    }

    /**
     * Indica si una rueda es de tipo Rebel.
     *
     * @param wheel rueda a evaluar.
     * @return true si es Rebel.
     */
    private boolean isRebel(Wheel wheel)
    {
        return Wheel.typeRebel.equals(wheel.getType());
    }

    /**
     * Espera el retardo de giro. Si el hilo es interrumpido, restaura la
     * bandera de interrupción.
     *
     * @return true si la espera terminó; false si fue interrumpida.
     */
    private boolean pause()
    {
        try {
            Thread.sleep(spinDelayMillis);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Genera un color aleatorio en formato hexadecimal "#RRGGBB".
     *
     * @return color generado.
     */
    private String colorRandom()
    {
        return String.format("#%02X%02X%02X", randomChannel(), randomChannel(), randomChannel());
    }

    /**
     * Genera un valor aleatorio de canal de color entre 1 y 254.
     *
     * @return valor del canal.
     */
    private int randomChannel()
    {
        return 1 + random.nextInt(colorChannelValues);
    }

    /**
     * Elige al azar uno de los colores ya presentes en la lista de símbolos.
     *
     * @return color existente.
     */
    private String colorRandomExists()
    {
        ArrayList<String> existing = wheels.get(0).getlistcolor();
        return existing.get(random.nextInt(existing.size()));
    }
}