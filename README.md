# Sistema de atención y menú inteligente para restaurantes

Proyecto de Programación Orientada a Objetos (TEC). Equipo de 2 personas:
- **Johel A. Fernández Madrigal**
- **Isaac Esquivel Alfaro**

## Supuestos declarados
- **Lenguaje:** Java 17+ (compila con JDK 17 o 21)
- **Construcción:** Maven
- **Interfaz gráfica:** Swing (Parte 7)
- **Persistencia:** En memoria con precarga desde `DatosDemo` (incluye menú completo y visitas históricas cerradas para estadísticas)
- **Pruebas:** JUnit 5
- **Dinero:** enteros en colones (sin decimales, sin errores de redondeo)
- **Alias de comensales:** únicos dentro de la visita, sin importar mayúsculas ni tildes
- **Estados de la mesa:** Disponible, Ocupada, Pendiente de pago (sigue ocupada hasta cerrar la visita)
- **Pedidos:** solo se pueden hacer o anular mientras la mesa está Ocupada; al solicitar la cuenta ya no se agrega nada. Cantidad de 1 a 99 por renglón
- **Anulación:** el pedido no se borra, queda marcado como anulado con fecha y motivo, y no cuenta en los totales
- **Pagos:** cuenta conjunta o pagos por persona (se elige al solicitar la cuenta). El monto debe ser positivo y no puede exceder el saldo; no se maneja vuelto
- **Métodos de pago:** efectivo, tarjeta y SINPE Móvil (todos ficticios, con referencia simulada)
- **Filtro calórico:** una opción por categoría activa, más la proteína elegida. Las categorías requeridas son todas las activas menos proteínas, o las que el cliente escoja
- **Impuestos/vuelto:** no se incluyen; el total es la suma de precios del menú

## Estructura
| Paquete | Responsabilidad |
|---|---|
| `restaurante.dominio` | Clases con las reglas de negocio (Menu, OpcionMenu, Categoria...) |
| `restaurante.excepciones` | Excepciones propias del sistema |
| `restaurante.recomendacion` | Filtro calórico: interfaz Strategy, dos algoritmos y el servicio |
| `restaurante.observador` | Patrón Observer: eventos del restaurante y bitácora de auditoría |
| `restaurante.estadisticas` | Consultas estadísticas por rango de fechas |
| `restaurante.util` | Utilidades (formato de dinero, texto sin tildes) |
| `restaurante.datos` | Datos de demostración precargados |
| `restaurante.gui` | (Parte 7) interfaz Swing |

## Cómo ejecutar
Desde IntelliJ: abrir la carpeta (detecta `pom.xml`), ejecutar `restaurante.Main`.
Pruebas: `mvn test` o clic derecho en `src/test` > Run.

## Progreso
- [x] Parte 0: estructura del proyecto
- [x] Parte 1: menú, categorías, CRUD, 4 búsquedas, excepciones
- [x] Parte 2: mesas y visitas (patrón State)
- [x] Parte 3: pedidos y subtotales
- [x] Parte 4: cuenta, pagos y cierre
- [x] Parte 5: filtro calórico (patrón Strategy)
- [x] Parte 6: estadísticas (Observer) y datos precargados
- [ ] Parte 7: interfaz gráfica
- [ ] Parte 8: pruebas finales y video
