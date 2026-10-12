import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Representa un concurso de tragamonedas que extienda las funcionalidades
 * básicas de {@link SlotMachine} para calcular y simular el alineamiento
 * de sus ruedas.
 */
public class SlotMachineContest {
    private static SlotMachineContest slotTest;
    private static SlotMachine sltmachine;
    /**
     * Construye una nueva instancia del concurso de tragamonedas.
     *
     * @param n El número de ruedas o la configuración inicial de la tragamonedas.
     */
    private SlotMachineContest(int n) {
         sltmachine = new SlotMachine(n);
    }

    /**
     * Calcula los pasos necesarios para alinear el símbolo visible de cada rueda
     * con el símbolo visible de la primera rueda.
     *
     * @param n Parámetro de configuración o tamaño utilizado para el cálculo.
     * @return Una lista de arreglos de enteros ({@code ArrayList<int[]>}) donde cada
     *         elemento contiene:
     *         <ul>
     *           <li>{@code [0]}: El índice de la rueda (basado en 0).</li>
     *           <li>{@code [1]}: La cantidad de giros necesarios para alinearla.</li>
     *         </ul>
     */
    public static ArrayList<int[]> solve(int n) { 
        if (slotTest != null){
            sltmachine.makeInvisible();
            slotTest = null; sltmachine = null;
            slotTest = new  SlotMachineContest(n);
        }else{
            slotTest = new  SlotMachineContest(n);
        }
        ArrayList<int[]> steps = new ArrayList<>();
        sltmachine.makeInvisible();
        ArrayList<Integer> listPositionsymbols = sltmachine.getPositionSymbolos();
        int mostNum = butFoundInt();
        for (int i = 0; i < listPositionsymbols.size(); i++){
            int Num = listPositionsymbols.get(i);
            if (!(mostNum == Num)){
                int step = Math.abs((sltmachine.distinctSymbols() - mostNum) - Num);
                steps.add(new int[] {i + 1, step });
            }
        }        
        return steps;
    }

    /**
     * Ejecuta la simulación visual aplicando los giros calculados por el método {@link #solve(int)}.
     *
     * @param n Parámetro de configuración enviado al método {@link #solve(int)}.
     * @throws InterruptedException Si el hilo de ejecución es interrumpido durante las animaciones de giro.
     */
    public static void simulate(int n){
        ArrayList<int[]> steps = solve(n);
        sltmachine.makeVisible();
        for (int[] step : steps) {
            int ruedaIndex = step[0];
            int cantidadGiros = step[1];
            if (cantidadGiros > 0) {
                    sltmachine.spin(ruedaIndex, cantidadGiros);
            }
        }
    }
    
    private static int butFoundInt(){
       ArrayList<Integer> listPositionsymbols = sltmachine.getPositionSymbolos();
       int mostInt = 0;
       int mostCant = 0;
       Map<Integer, Integer> ListInt = new HashMap<>();
       for (Integer n: listPositionsymbols){
           if (ListInt.get(n) == null){
               ListInt.put(n,1);
            }else{
               int cantActual = ListInt.get(n);
               ListInt.put(n,cantActual + 1);
               if (mostCant < cantActual + 1){mostInt = n;}
            }
       }
       return mostInt;
    }
}