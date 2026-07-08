GUIA RAPIDA PARA INICIAR MARKETTOGO
===================================

Este proyecto necesita:

1. Apache NetBeans
2. JDK 11 o superior
3. MySQL Server 8.x


PASO 1 - ABRIR EL PROYECTO
--------------------------

En NetBeans:

1. Ir a File > Open Project
2. Seleccionar la carpeta:

   MarketToGo

3. Esperar a que NetBeans cargue el proyecto.


PASO 2 - CONFIGURAR MYSQL AUTOMATICAMENTE
-----------------------------------------

Antes de ejecutar el programa, correr este archivo con doble clic:

   Configurar_MySQL.bat

Ese archivo intenta:

1. Verificar que MySQL este corriendo en el puerto 3306.
2. Iniciar MySQL si esta apagado.
3. Importar la base de datos desde:

   database\markettogo.sql

4. Crear o verificar la base de datos:

   markettogo

5. Crear o verificar el usuario administrador inicial.


PASO 3 - USUARIO Y CONTRASENA DE MYSQL
--------------------------------------

El programa esta configurado para conectarse asi:

   Servidor: localhost
   Puerto:   3306
   Base:     markettogo
   Usuario:  root
   Password: admin123

Esta configuracion esta en:

   src\markettogo\conexion\Conexion.java

Si la computadora del profesor usa otra contrasena de MySQL, debe cambiar esta linea:

   private static final String PASSWORD = "admin123";

por la contrasena correcta de su MySQL.


PASO 4 - EJECUTAR EN NETBEANS
-----------------------------

En NetBeans:

1. Click derecho sobre el proyecto MarketToGo.
2. Seleccionar Clean and Build.
3. Luego seleccionar Run.


PASO 5 - USUARIO INICIAL DEL SISTEMA
------------------------------------

Para entrar como administrador:

   Email:      admin@markettogo.com
   Contrasena: admin123


OPCION ALTERNATIVA SI NETBEANS FALLA
------------------------------------

Si NetBeans no ejecuta bien el proyecto, usar doble clic en:

   Ejecutar_MarketToGo.bat

Ese archivo:

1. Verifica MySQL.
2. Compila el codigo.
3. Ejecuta la aplicacion con el conector MySQL incluido.


NOTAS IMPORTANTES
-----------------

- MySQL debe estar instalado.
- El conector JDBC ya esta incluido en:

  lib\mysql-connector-j.jar

- Si aparece error de conexion, revisar:

  1. Que MySQL este corriendo.
  2. Que la base markettogo exista.
  3. Que el usuario root y la contrasena en Conexion.java sean correctos.
  4. Ejecutar nuevamente Configurar_MySQL.bat.

