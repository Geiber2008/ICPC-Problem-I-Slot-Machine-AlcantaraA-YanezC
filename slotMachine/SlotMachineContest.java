import java.util.ArrayList;

/**
 * Representa un concurso de tragamonedas que extienda las funcionalidades
 * básicas de {@link SlotMachine} para calcular y simular el alineamiento
 * de sus ruedas.
 */
public class SlotMachineContest extends SlotMachine {   
    
    /**
     * Construye una nueva instancia del concurso de tragamonedas.
     *
     * @param n El número de ruedas o la configuración inicial de la tragamonedas.
     */
    public SlotMachineContest(int n) {
        super(n);   
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
    public ArrayList<int[]> solve(int n) {   
        ArrayList<int[]> steps = new ArrayList<>();
        this.makeInvisible();
        ArrayList<Wheel> wheels = this.getWheels();
        if (wheels == null || wheels.isEmpty()) {
            return steps;
        }
        Wheel primeraRueda = wheels.get(0);
        int posObjetivo = primeraRueda.getPositionSymbol(primeraRueda.getshow_symbol());
        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            int posActual = w.getPositionSymbol(w.getshow_symbol());
            int giros = Math.abs(posObjetivo - posActual);
            
            steps.add(new int[]{i, giros});
        } 
        
        return steps;
    }

    /**
     * Ejecuta la simulación visual aplicando los giros calculados por el método {@link #solve(int)}.
     *
     * @param n Parámetro de configuración enviado al método {@link #solve(int)}.
     * @throws InterruptedException Si el hilo de ejecución es interrumpido durante las animaciones de giro.
     */
    public void simulate(int n) throws InterruptedException {
        for(int i = 0;i < 2;i++){
            ArrayList<int[]> steps = solve(n);
            this.makeVisible();
            for (int[] step : steps) {
                int ruedaIndex = step[0];
                int cantidadGiros = step[1];
                
                if (cantidadGiros > 0) {
                    this.spin(ruedaIndex, cantidadGiros);
                }
            }
        }
    }
}