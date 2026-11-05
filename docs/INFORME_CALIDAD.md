# Informe de calidad — Ciclo 5

## Herramientas y ejecución

- JDK: Java 17
- Maven Wrapper: Maven 3.9.9
- JUnit Jupiter: 5.11.4
- JaCoCo: 0.8.12
- PMD: 7.7.0
- Comando reproducible: `.\mvnw.cmd clean verify`

El comando anterior compila la aplicación, ejecuta las pruebas, crea ambos informes y aplica los
umbrales de entrega. La cobertura se mide por líneas a nivel del proyecto; el límite configurado
es 75 %. PMD falla el proceso si queda alguna violación de prioridad 1.

## Comparación inicial y final

| Medición | Inicial | Final | Meta | Resultado |
|---|---:|---:|---:|---|
| JaCoCo — cubrimiento de líneas | 658/751 — 87,62 % | 663/759 — 87,35 % | Más de 75 % | Cumplida |
| PMD — prioridad 1 | 38 | 0 | 0 | Cumplida |
| PMD — violaciones totales | 428 | 392 | Corregir las de prioridad alta | Cumplida |
| PMD — errores de configuración | 1 | 0 | 0 | Corregida |
| Pruebas de unidad y aceptación | 215 ejecutadas; 1 omitida | 216 ejecutadas; 0 omitidas | Todas pasan | Cumplida |

La cobertura final disminuyó 0,27 puntos porcentuales por el aumento de líneas al endurecer la
validación de `placeSymbol`; el total de líneas cubiertas aumentó de 658 a 663 y el resultado final
sigue por encima del objetivo. La prueba omitida inicialmente correspondía al ciclo infinito al
intentar ubicar un símbolo inexistente; se corrigió el defecto y la prueba ahora se ejecuta con
límite de tiempo.

La prueba de aceptación ejecutó 4 casos, todos aprobados. El resultado total final fue 216 pruebas,
sin fallos, errores ni omisiones.

## Decisiones tomadas después del resultado inicial

1. **Conservar una línea base verificable.** Se guardaron los informes iniciales de JaCoCo, PMD y
   Surefire antes de cambiar el código.
2. **No ampliar las pruebas solo para alcanzar el porcentaje.** La línea base ya superaba 75 %;
   se mantuvo ese nivel y se agregaron casos para el defecto de bloqueo detectado.
3. **Eliminar las reglas PMD de prioridad 1 sin romper compatibilidad.** Se normalizaron nombres
   de constantes internas, se declararon finales las clases sin extensión prevista, se evitó
   invocar un método sobreescribible desde el constructor de `Wheel` y se corrigió el nombre del
   parámetro de `placeSymbol`. Se conservaron `Rectangle.EDGES`, `Wheel.Moveposition` y
   `Wheel.Symbols` por compatibilidad con el modelo/código existentes, con supresiones PMD
   localizadas para esos identificadores heredados.
4. **Corregir el bloqueo de `placeSymbol`.** La operación ahora rechaza símbolos inexistentes,
   limita los intentos de giro y deja `ok` en `false` cuando la rueda no puede alcanzar el símbolo.
   Esto también evita ciclos infinitos en ruedas `Lefty`.
5. **Resolver la configuración inválida de PMD.** Se excluyó la regla `LoosePackageCoupling`, que
   requería una lista de paquetes/clases que no aplica al proyecto en el paquete por defecto. El
   informe final queda libre de errores de configuración.

Las 392 violaciones restantes de PMD son de prioridades 2, 3 o 4; ninguna es de prioridad 1. Se
conserva el informe completo para priorizar mejoras posteriores sin ampliar esta entrega.

## Informes adjuntos

- JaCoCo inicial: [HTML](../reports/initial/jacoco/index.html), [CSV](../reports/initial/jacoco/jacoco.csv)
- PMD inicial: [HTML](../reports/initial/pmd.html), [XML](../reports/initial/pmd.xml)
- Pruebas iniciales: [Surefire](../reports/initial/surefire-reports)
- JaCoCo final: [HTML](../reports/final/jacoco/index.html), [CSV](../reports/final/jacoco/jacoco.csv)
- PMD final: [HTML](../reports/final/pmd.html), [XML](../reports/final/pmd.xml)
- Pruebas finales: [Surefire](../reports/final/surefire-reports)
