# Market-To-Go

Marketplace local con entrega a domicilio, desarrollado en **Java Swing** con persistencia en **MySQL**.

## Contexto del proyecto

Este proyecto nace como el **proyecto final** del curso **Programación Cliente-Servidor Concurrente**, de la carrera de Ingeniería en Seguridad Informática en la **Universidad Fidélitas**. El curso permite elegir un tema libre (modalidad "FideTank"), y la idea de Market-To-Go surgió de una pregunta simple: ¿cómo sería un marketplace local en el que **la propia plataforma se encarga de la entrega**, en lugar de dejarle esa logística al comprador o al vendedor?

De ahí nació el concepto: una app de compra-venta de artículos entre vecinos/usuarios de una misma zona, con un rol de **repartidor** integrado al sistema, una **comisión del 5%** por transacción para sostener la plataforma, y la posibilidad de **hacer trueques** además de comprar y vender.

El desarrollo se organizó en avances (entregas incrementales) a lo largo del curso:

- **Avance 1**: diseño de la solución — entidades, historias de usuario y prototipos de pantallas.
- **Avance 2**: implementación funcional del sistema, evidenciando los conceptos de Programación Orientada a Objetos vistos en el curso (herencia, polimorfismo, excepciones, colecciones, serialización y multihilos).

## ¿Qué resuelve?

En un mercado local pequeño, quien vende un artículo normalmente también tiene que coordinar la entrega por su cuenta (WhatsApp, redes sociales, etc.), lo cual es informal y poco confiable. Market-To-Go centraliza todo el flujo:

1. El vendedor publica un artículo.
2. Un comprador lo adquiere desde el catálogo.
3. La plataforma cobra el pago, retiene su comisión y asigna un repartidor.
4. El repartidor actualiza el estado de la entrega hasta que llega al comprador.

Adicionalmente, si dos usuarios tienen artículos que le interesan al otro, pueden proponerse un **trueque** en vez de una compra-venta tradicional.

## Roles del sistema

| Rol | Puede hacer |
|---|---|
| **Administrador** | Gestionar usuarios, categorías, ver todas las transacciones y asignar repartidores a los pedidos. |
| **Comprador / Vendedor** | Publicar artículos, comprar artículos de otros usuarios, proponer y aceptar trueques, ver sus pedidos. |
| **Repartidor** | Ver los pedidos que se le asignaron y actualizar su estado (en camino / entregado). |

## Características principales

- Catálogo de artículos con búsqueda por texto y filtro por categoría.
- Compra con cálculo automático de comisión (5%) y generación de comprobante.
- Reserva de artículos **segura ante concurrencia**: si dos compradores intentan comprar el mismo artículo al mismo tiempo, solo uno de los dos lo consigue (evita condiciones de carrera reales, no solo en teoría).
- Sistema de trueques entre usuarios.
- Notificaciones dentro de la app (ventas, pagos, pedidos, trueques).
- Sesión persistente: si cierras la aplicación con la sesión abierta, al volver a abrirla no te vuelve a pedir el login.
- Panel de administración con métricas del negocio (usuarios, pedidos, transacciones, comisiones cobradas, categoría más activa).

## Conceptos de Programación Orientada a Objetos evidenciados

Este proyecto no solo implementa la funcionalidad, sino que la usa como vehículo para demostrar los temas del curso:

- **Herencia y polimorfismo**: `Usuario` es una clase abstracta de la que heredan `Administrador`, `CompradorVendedor` y `Repartidor`, cada una con su propia implementación de `getPermisos()` y `getDescripcionRol()`.
- **Excepciones personalizadas**: jerarquía `MarketToGoException` → `ArticuloNoDisponibleException` / `OperacionNoAutorizadaException`, usada en la reserva atómica de artículos al momento de comprar.
- **Colecciones**: uso de `List` y `Map` (con `Collectors.groupingBy`) para agrupar y presentar información, por ejemplo la categoría con más artículos publicados.
- **Serialización**: la sesión del usuario autenticado se serializa a disco (`Usuario implements Serializable`) para poder restaurarla al reabrir la aplicación.
- **Multihilos**: autenticación en un `SwingWorker` para no bloquear la interfaz, y un `Thread` en segundo plano que revisa notificaciones nuevas periódicamente.

## Tecnologías

- **Java 21** (Swing para la interfaz gráfica)
- **MySQL 8.x** como base de datos
- **JDBC** (MySQL Connector/J) para la conexión, sin ORM ni frameworks externos
- **Maven** para la gestión del proyecto y la compilación
- Patrón **DAO** para el acceso a datos

## Estructura del proyecto

```
MarketToGo/
├── database/                  # Script SQL para crear la base de datos y datos iniciales
├── lib/                        # Driver JDBC de MySQL
├── src/markettogo/
│   ├── conexion/                # Conexión a la base de datos (singleton)
│   ├── dao/                     # Acceso a datos (CRUD por entidad)
│   ├── excepciones/              # Excepciones personalizadas del dominio
│   ├── modelo/                   # Entidades y jerarquía de Usuario
│   ├── util/                     # Validaciones, estilos, sesión, comprobantes
│   └── vistas/                    # Pantallas Swing (login, catálogo, admin, repartidor, trueques)
├── pom.xml
└── Configurar_MySQL.bat        # Script para preparar MySQL en Windows
```

## Cómo ejecutarlo

### Requisitos

- JDK 21
- MySQL Server 8.x
- NetBeans IDE (recomendado) o cualquier IDE con soporte para Maven

### Pasos

1. Cloná el repositorio y abrí la carpeta `MarketToGo` como proyecto Maven en tu IDE.
2. Asegurate de tener MySQL corriendo en `localhost:3306`. En Windows podés usar `Configurar_MySQL.bat` para levantarlo y crear la base de datos automáticamente a partir de `database/markettogo.sql`.
3. Revisá las credenciales de conexión en `src/markettogo/conexion/Conexion.java` (por defecto usuario `root`, contraseña `admin123`) y ajustalas si tu instalación de MySQL usa otras.
4. Compilá y ejecutá la clase `markettogo.Main` (o `mvn compile exec:java` desde la terminal).

### Usuario administrador por defecto

```
Correo:      admin@markettogo.com
Contraseña:  admin123
```
