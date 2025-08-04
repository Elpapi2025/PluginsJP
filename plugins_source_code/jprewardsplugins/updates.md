# Actualizaciones del Plugin JPRewards (Versión 3.0)

Este documento resume las principales actualizaciones y mejoras realizadas en el plugin JPRewards.

## Nuevas Características y Mejoras:

*   **Múltiples Opciones de Almacenamiento de Datos**:
    *   Se añadió soporte para guardar los datos de los jugadores en **JSON**, **SQLite** y **MySQL**. La opción se configura en `config.yml`.
    *   Esto permite una mayor flexibilidad y escalabilidad para la gestión de datos de los jugadores.

*   **Pérdida de Racha Configurable**:
    *   Ahora puedes habilitar/deshabilitar la pérdida de racha por inactividad y configurar el período de tiempo (en horas) después del cual se pierde la racha si no se reclama una recompensa.
    *   Configuración disponible en la sección `streak_settings` de `config.yml`.

*   **Comandos de Administración de Datos de Jugadores**:
    *   Se han añadido nuevos subcomandos para administradores (`jprewards.admin`):
        *   `/rewards check <jugador>`: Para ver los datos de recompensa (último día reclamado y racha actual) de un jugador específico. El "Último día reclamado" ahora se muestra en un formato de fecha y hora legible.
        *   `/rewards setstreak <jugador> <cantidad>`: Para establecer la racha de un jugador a una cantidad específica.
        *   `/rewards reset <jugador>`: Para reiniciar los datos de recompensa (racha y último día reclamado) de un jugador.
        *   `/rewards advance <jugador>`: Para adelantar la recompensa de un jugador al siguiente día disponible, estableciendo su último día reclamado a 0.

*   **Autocompletado de Comandos (Tab Completion)**:
    *   El comando `/rewards` ahora ofrece autocompletado para sus subcomandos (`reload`, `check`, `setstreak`, `reset`, `advance`) y nombres de jugadores en línea, facilitando su uso.

*   **Lógica de Notificaciones Mejorada**:
    *   Se aseguró que el mensaje de chat de "recompensa reclamada" siempre se envíe al jugador.
    *   Se aclaró que las notificaciones de Action Bar y Título requieren implementaciones específicas de NMS o librerías como ProtocolLib para funcionar en Minecraft 1.8.8. Se eliminaron los fallbacks a mensajes de chat para estas notificaciones si no están implementadas correctamente.

*   **Salida de Comandos Oculta**:
    *   Los comandos de recompensa configurados en `config.yml` ahora se ejecutan desde la consola del servidor (`Bukkit.getConsoleSender()`), ocultando su salida a los jugadores para una experiencia más limpia y profesional.

*   **Integración con PlaceholderAPI**:
    *   Se añadió una expansión para PlaceholderAPI, permitiendo el uso de placeholders como `%jprewards_current_streak%` y `%jprewards_last_claimed_day%` en otros plugins compatibles (requiere que PlaceholderAPI esté instalado en el servidor).

*   **Actualización de Versión**:
    *   La versión del plugin ha sido actualizada a `3.0` en `plugin.yml` y `pom.xml` para reflejar estas importantes mejoras.

*   **Ofuscación del Código**:
    *   El plugin ahora se compila con **ProGuard**, ofuscando el código para hacerlo más difícil de de-compilar y proteger la propiedad intelectual. El JAR ofuscado se encuentra en la carpeta `target` con el sufijo `-obfuscated.jar`.

## Versión Anterior (2.8-SNAPSHOT):

La versión anterior del plugin JPRewards ofrecía un sistema básico de recompensas diarias con una GUI, almacenamiento de datos en archivos YAML, y comandos limitados para abrir la GUI y recargar la configuración. La pérdida de racha era fija y no configurable, y carecía de las opciones avanzadas de administración, notificaciones y extensibilidad presentes en la versión 3.0.