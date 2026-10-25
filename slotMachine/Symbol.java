/**
 * Representa un símbolo que puede mostrar una rueda: un nombre (color), una
 * posición dentro de la lista de símbolos, un tipo y un tamaño. Hay tres tipos:
 * "Normal", "ephemeral" (reduce su tamaño en cada selección hasta quedar como
 * un punto) y "Shy" (alterna entre visible e invisible en cada selección).
 */
public class Symbol
{
    static final String typeNormal = "Normal";
    static final String typeEphemeral = "ephemeral";
    static final String typeShy = "Shy";

    private static final int pointSize = 1;

    private final String name;
    private final String type;
    private int position;
    private int height;
    private int width;
    private boolean visible = true;

    /**
     * Crea un símbolo con tipo y dimensiones explícitas.
     *
     * @param n nombre o color del símbolo.
     * @param p posición inicial dentro de la lista de símbolos.
     * @param t tipo del símbolo ("Normal", "ephemeral" o "Shy").
     * @param h alto inicial del símbolo.
     * @param w ancho inicial del símbolo.
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
     * Convierte un nombre de tipo, sin importar mayúsculas o minúsculas, en el
     * nombre canónico del tipo de símbolo.
     *
     * @param type nombre del tipo ("normal", "ephemeral" o "shy").
     * @return el tipo canónico, o null si el tipo no existe.
     */
    static String normalizeType(String type)
    {
        if (typeNormal.equalsIgnoreCase(type)) {
            return typeNormal;
        }
        if (typeEphemeral.equalsIgnoreCase(type)) {
            return typeEphemeral;
        }
        if (typeShy.equalsIgnoreCase(type)) {
            return typeShy;
        }
        return null;
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
     * Obtiene el alto actual del símbolo.
     *
     * @return alto actual.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Obtiene el ancho actual del símbolo.
     *
     * @return ancho actual.
     */
    public int getWidth()
    {
        return width;
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

    /**
     * Reduce en una unidad el alto y el ancho del símbolo, sin bajar de un
     * punto (tamaño 1).
     */
    public void shrink()
    {
        height = Math.max(pointSize, height - 1);
        width = Math.max(pointSize, width - 1);
    }

    /**
     * Indica si el símbolo está en estado visible.
     *
     * @return true si está visible.
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Alterna el estado del símbolo entre visible e invisible.
     */
    public void toggleVisible()
    {
        visible = !visible;
    }
}