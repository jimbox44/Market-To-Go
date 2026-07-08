-- ============================================================
--  Market-To-Go  |  Script de base de datos MySQL
-- ============================================================
CREATE DATABASE IF NOT EXISTS markettogo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE markettogo;

-- ─── USUARIOS ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    apellido      VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    contrasena    VARCHAR(255)  NOT NULL,
    telefono      VARCHAR(20),
    direccion     VARCHAR(255),
    rol           ENUM('ADMINISTRADOR','COMPRADOR_VENDEDOR','REPARTIDOR') NOT NULL DEFAULT 'COMPRADOR_VENDEDOR',
    activo        TINYINT(1)    NOT NULL DEFAULT 1,
    fecha_registro DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ─── CUENTAS BANCARIAS ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS cuentas_bancarias (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id     INT          NOT NULL,
    tipo_cuenta    VARCHAR(50)  NOT NULL,
    numero_cuenta  VARCHAR(30)  NOT NULL,
    banco          VARCHAR(100) NOT NULL,
    titular        VARCHAR(200) NOT NULL,
    activa         TINYINT(1)   NOT NULL DEFAULT 1,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ─── CATEGORÍAS ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS categorias (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activa      TINYINT(1)   NOT NULL DEFAULT 1
);

-- ─── ARTÍCULOS ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS articulos (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    vendedor_id         INT            NOT NULL,
    categoria_id        INT            NOT NULL,
    titulo              VARCHAR(200)   NOT NULL,
    descripcion         TEXT,
    precio              DECIMAL(12,2)  NOT NULL,
    ubicacion           VARCHAR(200),
    estado              ENUM('DISPONIBLE','RESERVADO','VENDIDO','INACTIVO') NOT NULL DEFAULT 'DISPONIBLE',
    disponible_trueque  TINYINT(1)     NOT NULL DEFAULT 0,
    fecha_publicacion   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vendedor_id)  REFERENCES usuarios(id),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- ─── PEDIDOS ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS pedidos (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    comprador_id      INT          NOT NULL,
    articulo_id       INT          NOT NULL,
    repartidor_id     INT,
    estado_pedido     ENUM('PENDIENTE','CONFIRMADO','EN_CAMINO','ENTREGADO','CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
    direccion_entrega VARCHAR(255) NOT NULL,
    notas             TEXT,
    fecha_pedido      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega     DATETIME,
    FOREIGN KEY (comprador_id)  REFERENCES usuarios(id),
    FOREIGN KEY (articulo_id)   REFERENCES articulos(id),
    FOREIGN KEY (repartidor_id) REFERENCES usuarios(id)
);

-- ─── TRANSACCIONES ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS transacciones (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id       INT            NOT NULL,
    comprador_id    INT            NOT NULL,
    vendedor_id     INT            NOT NULL,
    monto           DECIMAL(12,2)  NOT NULL,
    comision        DECIMAL(12,2)  NOT NULL,
    monto_vendedor  DECIMAL(12,2)  NOT NULL,
    estado          ENUM('PROCESANDO','COMPLETADA','FALLIDA','REVERTIDA') NOT NULL DEFAULT 'PROCESANDO',
    referencia      VARCHAR(50)    NOT NULL UNIQUE,
    fecha           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id)    REFERENCES pedidos(id),
    FOREIGN KEY (comprador_id) REFERENCES usuarios(id),
    FOREIGN KEY (vendedor_id)  REFERENCES usuarios(id)
);

-- ─── TRUEQUES ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS trueques (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    solicitante_id          INT  NOT NULL,
    receptor_id             INT  NOT NULL,
    articulo_ofrecido_id    INT  NOT NULL,
    articulo_solicitado_id  INT  NOT NULL,
    estado                  ENUM('PROPUESTO','ACEPTADO','RECHAZADO','CANCELADO','COMPLETADO') NOT NULL DEFAULT 'PROPUESTO',
    mensaje                 TEXT,
    fecha_solicitud         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta         DATETIME,
    FOREIGN KEY (solicitante_id)         REFERENCES usuarios(id),
    FOREIGN KEY (receptor_id)            REFERENCES usuarios(id),
    FOREIGN KEY (articulo_ofrecido_id)   REFERENCES articulos(id),
    FOREIGN KEY (articulo_solicitado_id) REFERENCES articulos(id)
);

-- ─── NOTIFICACIONES ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notificaciones (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  INT          NOT NULL,
    titulo      VARCHAR(150) NOT NULL,
    mensaje     TEXT         NOT NULL,
    leida       TINYINT(1)   NOT NULL DEFAULT 0,
    tipo        ENUM('PEDIDO','TRUEQUE','PAGO','SISTEMA') NOT NULL DEFAULT 'SISTEMA',
    fecha       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ─── DATOS INICIALES ────────────────────────────────────────
INSERT INTO usuarios (nombre, apellido, email, contrasena, rol, activo) VALUES
('Admin', 'MarketToGo', 'admin@markettogo.com', MD5('admin123'), 'ADMINISTRADOR', 1)
ON DUPLICATE KEY UPDATE
    nombre='Admin',
    apellido='MarketToGo',
    contrasena=MD5('admin123'),
    rol='ADMINISTRADOR',
    activo=1;

INSERT INTO categorias (nombre, descripcion)
SELECT 'Electronica', 'Celulares, computadoras, tablets y accesorios'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Electronica');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Ropa y Calzado', 'Prendas de vestir y zapatos para toda la familia'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Ropa y Calzado');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Hogar', 'Muebles, decoracion y articulos del hogar'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Hogar');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Deportes', 'Equipos deportivos y ropa de ejercicio'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Deportes');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Vehiculos', 'Carros, motos y bicicletas'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Vehiculos');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Libros', 'Libros, revistas y material educativo'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Libros');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Juguetes', 'Juguetes y juegos para ninos'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Juguetes');

INSERT INTO categorias (nombre, descripcion)
SELECT 'Otros', 'Articulos varios no categorizados'
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE nombre='Otros');
