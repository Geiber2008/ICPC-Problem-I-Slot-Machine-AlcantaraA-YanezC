/**
 * Rueda "rebelde": no puede bloquearse, no puede eliminarse y no participa
 * en intercambios de posición dentro de la máquina.
 */
public class Rebel extends Wheel
{
    /**
     * Crea una rueda rebelde con los mismos parámetros que {@link Wheel}.
     *
     * @param h alto del rectángulo.
     * @param w ancho del rectángulo.
     * @param x coordenada X.
     * @param y coordenada Y.
     * @param s color o símbolo inicial.
     * @param pos índice de la rueda en la máquina (base 0).
     * @param m máquina a la que pertenece.
     * @param inicio true si debe registrar su color en la lista de símbolos.
     */
    public Rebel(int h, int w, int x, int y, String s, int pos, SlotMachine m, boolean inicio)
    {
        super(h, w, x, y, s, pos, m, inicio);
    }

    /**
     * No hace nada: una rueda rebelde ignora el bloqueo y siempre puede girar.
     */
    @Override
    public void lock()
    {
    }

    /**
     * Identifica el tipo de rueda.
     *
     * @return "Rebel".
     */
    @Override
    public String getType()
    {
        return TYPE_REBEL;
    }
}