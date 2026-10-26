import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Prueba de Aceptacion (Acceptance Test) para el Simulador de Slot Machine.
 * 
 * Valida de extremo a extremo los flujos principales de negocio y requisitos
 * de usuario descritos en los casos de uso:
 * - Creacion y parametrizacion de la maquina tragamonedas (User).
 * - Gestion completa de ruedas: insercion, tipos, eliminacion, bloqueo, desbloqueo e intercambio.
 * - Gestion y consulta de simbolos, configuraciones y jackpot.
 * - Resolucion y simulacion automatica del concurso (Solver - Problem I ICPC).
 * - Manejo robusto de entradas invalidas sin fallos catastroficos (ok == false).
 * - Verificacion de la capa grafica y componentes de dibujo (Canvas, Circle, Triangle, Rectangle).
 */
public class SlotMachineAcceptanceTest {

    private SlotMachine machine;

    @BeforeEach
    public void setUp() {
        new Wheel().makeInvisible(); // Limpia la lista estatica compartida de simbolos
        machine = new SlotMachine(3);
        machine.makeInvisible();
    }

    @AfterEach
    public void tearDown() {
        if (machine != null) {
            machine.makeInvisible();
        }
    }

    @Test
    @DisplayName("Prueba de Aceptacion: Flujo completo de ciclo de vida de la maquina y juego")
    public void shouldExecuteFullGameAcceptanceScenario() {
        // 1. Creacion y estado inicial
        assertTrue(machine.ok(), "La maquina debe nacer en estado operacional");
        assertEquals(3, machine.configuration().size(), "Por defecto debe tener 3 ruedas");
        assertEquals(3, machine.distinctSymbols(), "Debe contar con 3 simbolos iniciales");

        // 2. Gestion de ruedas: adicion normal y con tipo
        machine.addWheel(2);
        assertTrue(machine.ok(), "Debe permitir insertar una rueda en posicion 2");
        assertEquals(4, machine.configuration().size());

        machine.addWheel("normal", 1);
        assertTrue(machine.ok(), "Debe permitir insertar rueda especificando tipo normal");
        assertEquals(5, machine.configuration().size());

        // 3. Gestion de simbolos
        int initialDistinct = machine.distinctSymbols();
        machine.addSymbol("#112233", 1);
        assertTrue(machine.ok(), "Debe permitir agregar un simbolo de color valido");
        assertEquals(initialDistinct + 1, machine.distinctSymbols());

        // 4. Bloqueo, desbloqueo y rotacion
        machine.lock(1);
        assertTrue(machine.ok(), "Debe bloquear exitosamente la rueda 1");

        // Rueda 1 no debe girar al estar bloqueada
        ArrayList<String> configBefore = new ArrayList<>(machine.configuration());
        machine.spin(1);
        assertTrue(machine.ok());
        assertEquals(configBefore.get(0), machine.configuration().get(0), "Rueda bloqueada no cambia simbolo");

        // Desbloquear
        machine.unlock(1);
        assertTrue(machine.ok(), "Debe desbloquear exitosamente la rueda 1");

        // 5. Intercambio de ruedas (swap)
        machine.swap(1, 2);
        assertTrue(machine.ok(), "Debe intercambiar ruedas 1 y 2");

        // 6. Consulta de jackpot y asignacion forzada
        String[] preset = new String[machine.configuration().size()];
        String targetColor = "#AABBCC";
        for (int i = 0; i < preset.length; i++) {
            preset[i] = targetColor;
        }
        machine.spin(preset);
        assertTrue(machine.ok(), "Debe permitir asignar simbolos directamente");
        assertTrue(machine.isJackpot(), "Al tener todos los simbolos identicos debe ser Jackpot");

        // 7. Visibilidad
        machine.makeVisible();
        assertTrue(machine.ok());
        machine.makeInvisible();
        assertTrue(machine.ok());
    }

    @Test
    @DisplayName("Prueba de Aceptacion: Resolucion de concurso SlotMachineContest (Problem I ICPC)")
    public void shouldExecuteContestSolverAcceptanceScenario() {
        int n = 4;
        ArrayList<int[]> solution = SlotMachineContest.solve(n);
        assertNotNull(solution, "La solucion no debe ser nula");
        assertFalse(solution.isEmpty(), "Para n=4 con posiciones distintas deben requerirse giros");

        // Comprobar formato de acciones [rueda, giros]
        for (int[] action : solution) {
            assertEquals(2, action.length, "Cada accion debe ser el par [rueda, pasos]");
            assertTrue(action[0] >= 1 && action[0] <= n, "El indice de rueda debe ser valido");
            assertTrue(action[1] >= 0, "La cantidad de giros no puede ser negativa");
        }

        // Simular ejecucion con n pequenio
        assertDoesNotThrow(() -> SlotMachineContest.simulate(2),
            "La simulacion no debe arrojar excepciones no controladas");
    }

    @Test
    @DisplayName("Prueba de Aceptacion: Robustez ante operaciones invalidas y limites del sistema")
    public void shouldHandleEdgeCasesAndInvalidOperationsGracefully() {
        // Reducir ruedas hasta el limite minimo (3)
        machine.delWheel(1);
        // La maquina nacio con 3, eliminarla debe ser rechazado porque minWheels es 3
        assertFalse(machine.ok(), "No se debe permitir tener menos de 3 ruedas");
        assertEquals(3, machine.configuration().size());

        // Operaciones con indices fuera de rango
        machine.spin(99);
        assertFalse(machine.ok(), "Girar rueda fuera de rango debe marcar ok = false");

        machine.swap(1, 99);
        assertFalse(machine.ok(), "Intercambiar con indice invalido debe marcar ok = false");

        machine.lock(0);
        assertFalse(machine.ok(), "Bloquear indice 0 debe marcar ok = false");

        machine.unlock(50);
        assertFalse(machine.ok(), "Desbloquear indice fuera de rango debe marcar ok = false");

        machine.placeSymbol(99, "#112233");
        assertFalse(machine.ok(), "Ubicar simbolo en rueda inexistente debe marcar ok = false");
    }

    @Test
    @DisplayName("Prueba de Aceptacion: Cobertura integral de componentes graficos (Circle, Triangle, Rectangle)")
    public void shouldExerciseShapeComponentsForVisualSimulation() {
        // Cobertura Circle
        Circle circle = new Circle();
        circle.changeSize(40);
        circle.changeColor("red");
        circle.moveRight();
        circle.moveLeft();
        circle.moveUp();
        circle.moveDown();
        circle.moveHorizontal(10);
        circle.moveVertical(10);
        circle.slowMoveHorizontal(2);
        circle.slowMoveVertical(2);
        circle.makeVisible();
        circle.makeInvisible();
        assertEquals(3.1416, Circle.PI, 0.0001);

        // Cobertura Triangle
        Triangle triangleDefault = new Triangle();
        Triangle triangleCustom = new Triangle("blue", 50, 60);
        triangleCustom.changeSize(40, 50);
        triangleCustom.changeColor("yellow");
        triangleCustom.moveRight();
        triangleCustom.moveLeft();
        triangleCustom.moveUp();
        triangleCustom.moveDown();
        triangleCustom.moveHorizontal(15);
        triangleCustom.moveVertical(15);
        triangleCustom.slowMoveHorizontal(2);
        triangleCustom.slowMoveVertical(2);
        triangleCustom.makeVisible();
        triangleCustom.makeInvisible();
        triangleDefault.makeVisible();
        triangleDefault.makeInvisible();
        assertEquals(3, Triangle.VERTICES);

        // Cobertura Rectangle metodos de movimiento
        Rectangle rect = new Rectangle();
        rect.moveRight();
        rect.moveLeft();
        rect.moveUp();
        rect.moveDown();
        rect.slowMoveHorizontal(2);
        rect.slowMoveVertical(2);
        rect.changeColor("green");
        rect.makeVisible();
        rect.makeInvisible();
        assertEquals(4, Rectangle.EDGES);

        // Cobertura Canvas wait
        Canvas canvas = Canvas.getCanvas();
        assertNotNull(canvas);
        canvas.wait(10);
    }
}
