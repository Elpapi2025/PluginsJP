# 💎 Repositorio de Plugins de Juanpiece 💎

¡Hola y bienvenido! 👋

Este es el repositorio central donde alojo todos mis plugins de Minecraft desarrollados a medida. Mi objetivo es crear herramientas únicas y de alta calidad para mejorar la experiencia en los servidores de Minecraft.

---

## ✨ ¿Qué encontrarás aquí?

Este repositorio contiene el código fuente completo de varios proyectos, incluyendo:

*   **`plugins_source_code/`**: El corazón de mis plugins más pequeños y utilitarios.
    *   `jpcommands`: Comandos personalizados y esenciales.
    *   `jpmoney`: Un sistema de economía simple.
    *   `jprewardsplugins`: Plugins para recompensas.
    *   `TitanHCF`: El núcleo para un servidor Hardcore Factions.
*   **`titan/`**: El proyecto principal "Titan", una suite de HCF más completa y robusta.
*   **`titan_backup/`**: Copias de seguridad y versiones archivadas.

Cada proyecto está estructurado como un proyecto de Maven independiente para facilitar su gestión y compilación.

---

## 🚀 Compilación

Si deseas compilar los plugins tú mismo, necesitarás tener instalado [Apache Maven](https://maven.apache.org/download.cgi).

Una vez que tengas Maven configurado, sigue estos pasos:

1.  Clona este repositorio en tu máquina local.
2.  Navega al directorio del plugin que deseas compilar. Por ejemplo, para `TitanHCF`:
    ```bash
    cd plugins_source_code/TitanHCF
    ```
3.  Ejecuta el siguiente comando de Maven para compilar el plugin:
    ```bash
    mvn clean install
    ```
4.  El archivo `.jar` compilado se encontrará en el directorio `target/` dentro de la carpeta del proyecto.

---

## 🐞 Reporte de Errores

Si encuentras algún problema o bug en alguno de mis plugins, por favor, no dudes en abrir un **Issue** en este repositorio. Asegúrate de incluir la siguiente información:

*   Versión del servidor (Spigot, Paper, etc.).
*   Versión del plugin.
*   Pasos detallados para reproducir el error.
*   Cualquier log o mensaje de error que aparezca en la consola.

---

## ❤️ Agradecimientos

¡Gracias por visitar mi repositorio! Espero que mis plugins te sean de gran utilidad.

**Creado y mantenido por Juanpiece.**
