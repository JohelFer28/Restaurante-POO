# Sistema de Atención y Menú Inteligente para Restaurantes

Proyecto de **Programación Orientada a Objetos** — Instituto Tecnológico de Costa Rica (TEC).

## Equipo de trabajo y distribución
* **Johel A. Fernández Madrigal:** Integración del sistema, patrón Observer, módulo de estadísticas, interfaz gráfica en Swing, pruebas de calidad (QA) y gestión del repositorio en GitHub.
* **Isaac Esquivel Alfaro:** Modelado del dominio OO, patrón State para ciclo de vida de mesas/visitas, patrón Strategy y algoritmo de poda para recomendaciones calóricas y pruebas unitarias.

---

## Supuestos declarados
1. **Lenguaje, plataforma y persistencia:** Desarrollado en Java 17+ con gestión de proyecto Maven (`pom.xml`) e interfaz gráfica en Java Swing (`java.desktop`). La persistencia es en memoria con precarga automática desde `DatosDemo` (8 mesas, 21 opciones en 5 categorías —19 disponibles y 2 desactivadas para pruebas— y 3 visitas históricas cerradas para estadísticas).
2. **Cantidad por categoría en recomendación calórica:** Se genera exactamente **una opción por cada categoría adicional requerida** junto con la proteína elegida (no cantidades múltiples por categoría), evitando combinaciones repetidas y ordenando los resultados de forma determinista por calorías ascendentes y luego por nombre.
3. **Categorías requeridas y administración de activas:** Para cumplir el filtro mínimo, por defecto se seleccionan como requeridas **todas las categorías activas distintas a Proteínas**, permitiendo además que el usuario marque o desmarque categorías activas específicas desde la interfaz. Si una categoría se desactiva en el menú, deja de exigirse y sus opciones no se ofrecen para nuevos pedidos.
4. **Reglas de precios, impuestos y vueltos:** No se implementan impuestos ni cargos adicionales; el total de la visita corresponde exactamente a la suma de precios del menú al momento de pedir. Todos los montos se manejan en unidades enteras de colones (`int`) para evitar errores de redondeo de punto flotante. No se maneja vuelto: cada pago debe ser positivo y no puede superar el saldo pendiente.
5. **Anulación de artículos, corrección de pagos y auditoría:**
   * **Anulación de pedidos:** Se permite anular líneas únicamente mientras la mesa está en estado `Ocupada` (antes de solicitar la cuenta). La línea no se borra: queda marcada como `ANULADO` con fecha, hora y motivo obligatorio, excluyéndose de los subtotales y del saldo.
   * **Corrección de pagos:** Una vez registrado un pago ficticio, **no se permite editarlo ni eliminarlo** para mantener íntegra la auditoría financiera de la visita; cualquier monto inválido (cero, negativo o mayor al saldo pendiente) es rechazado antes de guardarse mediante `PagoInvalidoException`.
   * **Auditoría:** Cada cambio de estado, pedido, anulación, pago y cierre emite un `EventoRestaurante` hacia `BitacoraEventos` (patrón Observer) con marca de tiempo exacta.
6. **Limitaciones conocidas:**
   * El sistema soporta múltiples mesas abiertas en paralelo en la misma sesión, pero **no persiste los datos en disco o base de datos entre ejecuciones** (al cerrar la aplicación se reinicia al estado base de `DatosDemo`).
   * Los pagos son simulados con referencia autogenerada y no se conectan a pasarelas bancarias reales.

---

## Arquitectura y Diseño Orientado a Objetos
| Paquete | Responsabilidad |
|---|---|
| `restaurante.dominio` | Entidades y reglas de negocio (`Restaurante`, `Mesa`, `Visita`, `Menu`, `OpcionMenu`, `Categoria`, `LineaPedido`, `Pago`). |
| `restaurante.excepciones` | Jerarquía de 8 excepciones propias verificadas (`RestauranteException`). |
| `restaurante.recomendacion` | Patrón **Strategy**: interfaz `EstrategiaRecomendacion`, `RecomendacionPorPoda`, `RecomendacionFuerzaBruta`, `ServicioRecomendacion` y `Combinacion`. |
| `restaurante.observador` | Patrón **Observer**: interfaz `ObservadorRestaurante`, `EventoRestaurante`, `TipoEventoRestaurante` y `BitacoraEventos`. |
| `restaurante.estadisticas` | Consultas analíticas por rango de fechas (`ServicioEstadisticas`). |
| `restaurante.gui` | Interfaz gráfica Swing desacoplada del dominio (`VentanaPrincipal` y 5 paneles funcionales). |
| `restaurante.datos` | Precarga de menú y visitas históricas (`DatosDemo`). |
| `restaurante.util` | Formateo monetario (`Formato`) y normalización de texto (`Texto`). |

### Patrones de diseño implementados
1. **State (`EstadoMesa`, `EstadoDisponible`, `EstadoOcupada`, `EstadoPendientePago`):** Encapsula las operaciones permitidas por estado de mesa y bloquea transiciones inválidas.
2. **Strategy (`EstrategiaRecomendacion`, `RecomendacionPorPoda`, `RecomendacionFuerzaBruta`):** Permite intercambiar el algoritmo de búsqueda combinatoria con poda temprana frente a fuerza bruta sin modificar `ServicioRecomendacion` ni la vista.
3. **Observer (`ObservadorRestaurante`, `EventoRestaurante`, `BitacoraEventos`, `VentanaPrincipal`):** Desacopla el modelo de dominio de la interfaz gráfica y de la bitácora de auditoría, refrescando las vistas automáticamente ante cada cambio.

---

## Cómo compilar, probar y ejecutar
* **Desde IntelliJ IDEA:** Abrir la carpeta raíz del proyecto (detecta `pom.xml`), ejecutar `restaurante.Main`. Para correr pruebas: clic derecho sobre `src/test` > **Run 'All Tests'**.
* **Desde terminal con Maven:**
  ```bash
  mvn test
  mvn package
  java -jar target/restaurante-oo-1.0.jar
  ```

## Progreso
- [x] Parte 0: estructura del proyecto
- [x] Parte 1: menú, categorías, CRUD, 4 búsquedas, excepciones
- [x] Parte 2: mesas y visitas (patrón State)
- [x] Parte 3: pedidos y subtotales
- [x] Parte 4: cuenta, pagos y cierre
- [x] Parte 5: filtro calórico (patrón Strategy)
- [x] Parte 6: estadísticas (Observer) y datos precargados
- [x] Parte 7: interfaz gráfica (Swing)
- [X] Parte 8: pruebas finales, empaquetado ejecutable y documentación
