/**
 * Representa un símbolo que puede mostrar una rueda: un nombre (color), una
 * posición dentro de la lista de símbolos, un tipo y, opcionalmente, un tamaño.
 */
public class Symbol
{
    static final String typeNormal = "Normal";
    static final String typeEphemeral = "ephemeral";
    static final String typeShy = "Shy";

    private static final int collapsedSize = 1;
    private static final int minimumSize = 2;

    private final String name;
    private final String type;
    private int position;
    private int height;
    private int width;

    /**
     * Crea un símbolo con tipo y dimensiones explícitas.
     *
     * @param n nombre o color del símbolo.
     * @param p posición inicial dentro de la lista de símbolos.
     * @param t tipo del símbolo ("Normal", "ephemeral" o "Shy").
     * @param h alto asociado al símbolo.
     * @param w ancho asociado al símbolo.
     */
    public Symbol(String n, int p, String t, int h, int w)
    {
        name = n;
        position = p;
        type = t;
        height = h;
        width = w;
    }

    /**
     * Crea un símbolo de tipo "Normal" sin dimensiones.
     *
     * @param n nombre o color del símbolo.
     * @param p posición inicial dentro de la lista de símbolos.
     */
    public Symbol(String n, int p)
    {
        name = n;
        position = p;
        type = typeNormal;
    }

    /**
     * Actualiza la posición del símbolo dentro de la lista.
     *
     * @param y nueva posición (base 0).
     */
    public void setposition(int y)
    {
        position = y;
    }

    /**
     * Obtiene el nombre (color) del símbolo.
     *
     * @return nombre del símbolo.
     */
    public String getname()
    {
        return name;
    }

    /**
     * Obtiene la posición del símbolo dentro de la lista.
     *
     * @return posición actual (base 0).
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * Obtiene el tipo del símbolo.
     *
     * @return "Normal", "ephemeral" o "Shy".
     */
    public String getType()
    {
        return type;
    }

    /**
     * Obtiene el alto del símbolo. Un alto almacenado de 1 se reporta como 2.
     *
     * @return alto efectivo.
     */
    public int getHeight()
    {
        return height == collapsedSize ? minimumSize : height;
    }

    /**
     * Obtiene el ancho del símbolo. Un ancho almacenado de 1 se reporta como 2.
     *
     * @return ancho efectivo.
     */
    public int getWidth()
    {
        return width == collapsedSize ? minimumSize : width;
    }

    /**
     * Establece el alto del símbolo.
     *
     * @param h nuevo alto.
     */
    public void setHeight(int h)
    {
        height = h;
    }

    /**
     * Establece el ancho del símbolo.
     *
     * @param w nuevo ancho.
     */
    public void setWidth(int w)
    {
        width = w;
    }
}