import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Concurso de tragamonedas que utiliza {@link SlotMachine} para calcular y
 * simular los giros necesarios para alinear el símbolo visible de todas las
 * ruedas.
 */
public class SlotMachineContest {
    private static SlotMachineContest slotTest;
    private static SlotMachine slotMachine;

    /**
     * Construye una nueva instancia del concurso y crea la tragamonedas que
     * usarán los cálculos.
     *
     * @param n número de ruedas de la tragamonedas.
     */
    private SlotMachineContest(int n) {
        slotMachine = new SlotMachine(n);
    }

    /**
     * Calcula los pasos necesarios para alinear el símbolo visible de cada rueda
     * con la posición de símbolo más frecuente. Siempre parte de una tragamonedas
     * nueva; si había una anterior, la oculta y la reemplaza.
     *
     * @param n número de ruedas de la tragamonedas a analizar.
     * @return una lista de arreglos de enteros ({@code ArrayList<int[]>}) donde cada
     *         elemento contiene:
     *         <ul>
     *           <li>{@code [0]}: el índice de la rueda (basado en 1).</li>
     *           <li>{@code [1]}: la cantidad de giros necesarios para alinearla.</li>
     *         </ul>
     *         Las ruedas que ya están alineadas no aparecen en la lista.
     */
    public static ArrayList<int[]> solve(int n) {
        if (slotTest != null) {
            slotMachine.makeInvisible();
        }
        slotTest = new SlotMachineContest(n);
        slotMachine.makeInvisible();
        ArrayList<int[]> steps = new ArrayList<>();
        ArrayList<Integer> positions = slotMachine.getPositionSymbolos();
        int targetPosition = findMostFrequentPosition(positions);
        int symbolsCount = slotMachine.distinctSymbols();
        for (int i = 0; i < positions.size(); i++) {
            int position = positions.get(i);
            if (targetPosition != position) {
                int turns = Math.abs((symbolsCount - targetPosition) - position);
                steps.add(new int[] {i + 1, turns});
            }
        }
        return steps;
    }

    /**
     * Ejecuta la simulación visual aplicando los giros calculados por el método
     * {@link #solve(int)}. Cada giro espera el retardo definido en
     * {@link SlotMachine#spin(int, int)}.
     *
     * @param n número de ruedas enviado al método {@link #solve(int)}.
     */
    public static void simulate(int n) {
        ArrayList<int[]> steps = solve(n);
        slotMachine.makeVisible();
        for (int[] step : steps) {
            int wheelIndex = step[0];
            int turns = step[1];
            if (turns > 0) {
                slotMachine.spin(wheelIndex, turns);
            }
        }
    }

    /**
     * Busca la posición de símbolo que más se repite en la lista dada. Si ninguna
     * posición se repite, devuelve 0.
     *
     * @param positions posiciones de símbolo visibles de cada rueda.
     * @return la posición más frecuente, o 0 si no hay repetidas.
     */
    private static int findMostFrequentPosition(ArrayList<Integer> positions) {
        int mostFrequent = 0;
        int mostCount = 0;
        Map<Integer, Integer> frequencies = new HashMap<>();
        for (Integer position : positions) {
            if (!frequencies.containsKey(position)) {
                frequencies.put(position, 1);
            } else {
                int updatedCount = frequencies.get(position) + 1;
                frequencies.put(position, updatedCount);
                if (mostCount < updatedCount) {
                    mostCount = updatedCount;
                    mostFrequent = position;
                }
            }
        }
        return mostFrequent;
    }
}