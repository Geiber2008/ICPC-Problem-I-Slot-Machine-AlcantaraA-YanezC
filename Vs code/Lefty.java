/**
 * Rueda "zurda": al girar se alinea con el símbolo que muestra la rueda
 * ubicada inmediatamente a su izquierda. La primera rueda no gira.
 */
public class Lefty extends Wheel
{
    /**
     * Crea una rueda zurda con los mismos parámetros que {@link Wheel}.
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
    public Lefty(int h, int w, int x, int y, String s, int pos, SlotMachine m, boolean inicio)
    {
        super(h, w, x, y, s, pos, m, inicio);
    }

    /**
     * Gira la rueda hasta mostrar el mismo símbolo que su vecina izquierda.
     * No hace nada si está bloqueada, si es la primera rueda o si el símbolo
     * de la vecina no existe en su lista de símbolos.
     */
    @Override
    public void rotate()
    {
        if (!getesMove() || getpositionA() == 0) {
            return;
        }
        String target = getMachine().getWheels().get(getpositionA() - 1).getshowSymbol();
        if (!getlistcolor().contains(target)) {
            return;
        }
        while (!target.equals(getshowSymbol())) {
            super.rotate();
        }
    }

    /**
     * Identifica el tipo de rueda.
     *
     * @return "Lefty".
     */
    @Override
    public String getType()
    {
        return TYPE_LEFTY;
    }
}