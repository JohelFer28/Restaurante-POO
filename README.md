# Sistema de atención y menú inteligente para restaurantes

Proyecto de Programación Orientada a Objetos (Tec). Equipo de 2 personas.

## Supuestos declarados
- **Lenguaje:** Java 17+ (compila con JDK 17 o 21)
- **Construcción:** Maven
- **Interfaz gráfica:** Swing (Parte 7)
- **Persistencia:** JSON con Gson (Parte 6); por ahora el menú se precarga desde `DatosDemo`
- **Pruebas:** JUnit 5
- **Dinero:** enteros en colones (sin decimales, sin errores de redondeo)
- **Filtro calórico:** una opción por categoría activa, más la proteína elegida
- **Impuestos/vuelto:** no se incluyen; el total es la suma de precios del menú

## Estructura
| Paquete | Responsabilidad |
|---|---|
| `restaurante.dominio` | Clases con las reglas de negocio (Menu, OpcionMenu, Categoria...) |
| `restaurante.excepciones` | Excepciones propias del sistema |
| `restaurante.util` | Utilidades (formato de dinero, texto sin tildes) |
| `restaurante.datos` | Datos de demostración |
| `restaurante.servicios` | (Partes siguientes) coordinan casos de uso |
| `restaurante.repositorio` | (Partes siguientes) guardar/cargar JSON |
| `restaurante.gui` | (Partes siguientes) interfaz Swing |

## Cómo ejecutar
Desde IntelliJ: abrir la carpeta (detecta `pom.xml`), ejecutar `restaurante.Main`.
Pruebas: `mvn test` o clic derecho en `src/test` > Run.

## Progreso
- [x] Parte 0: estructura del proyecto
- [x] Parte 1: menú, categorías, CRUD, 4 búsquedas, excepciones
- [ ] Parte 2: mesas y visitas (State)
- [ ] Parte 3: pedidos
- [ ] Parte 4: cuenta, pagos y cierre
- [ ] Parte 5: filtro calórico (Strategy)
- [ ] Parte 6: estadísticas (Observer) y persistencia
- [ ] Parte 7: interfaz gráfica
- [ ] Parte 8: pruebas finales y video
