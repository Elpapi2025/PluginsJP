# JPRewards - Recompensas Diarias y Personalizadas para Spigot

[![Version](https://img.shields.io/badge/version-1.0-blue.svg)](https://www.spigotmc.org/resources/jprewards.XXXXX/)
[![Minecraft Version](https://img.shields.io/badge/minecraft-1.8.9-green.svg)](https://www.spigotmc.org/wiki/spigot-1-8-9-api/)
[![License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)

## 📝 Descripción

**JPRewards** es un plugin de Spigot para Minecraft 1.8.9 que permite a los servidores ofrecer sistemas de recompensas diarias y personalizadas a sus jugadores. Con opciones flexibles de almacenamiento de datos y comandos de administración, JPRewards es la solución ideal para mantener a tus jugadores comprometidos y recompensados.

## ✨ Características Principales

*   **Recompensas Diarias Configurables:** Define recompensas para cada día, incentivando la conexión diaria de los jugadores.
*   **Múltiples Opciones de Almacenamiento de Datos:**
    *   Soporte para **JSON**, **SQLite** y **MySQL** para la persistencia de datos de los jugadores.
    *   Configuración sencilla en `config.yml`.
*   **Gestión de Racha de Recompensas:**
    *   Configura la pérdida de racha por inactividad, incluyendo el período de tiempo después del cual se pierde la racha.
*   **Comandos de Administración Completos:**
    *   `/jprewards check <jugador>`: Consulta los datos de recompensa de un jugador.
    *   `/jprewards setstreak <jugador> <cantidad>`: Establece la racha de un jugador.
    *   `/jprewards reset <jugador>`: Reinicia los datos de recompensa de un jugador.
    *   `/jprewards advance <jugador>`: Adelanta la recompensa de un jugador al siguiente día.
*   **Interfaz Gráfica de Usuario (GUI):** Los jugadores pueden reclamar sus recompensas a través de una GUI intuitiva.
*   **Autocompletado de Comandos:** Facilita el uso de los comandos de administración con autocompletado.
*   **Integración con PlaceholderAPI:** Utiliza placeholders como `%jprewards_current_streak%` y `%jprewards_last_claimed_day%` en otros plugins.
*   **Mensajes Personalizables:** Todos los mensajes son configurables y soportan códigos de color de Minecraft (`&`).

## 🚀 Instalación

1.  Descarga la última versión de **JPRewards.jar** desde SpigotMC.
2.  Coloca el archivo `JPRewards.jar` en la carpeta `plugins` de tu servidor de Minecraft.
3.  Reinicia o recarga tu servidor (`/reload` o `stop` y `start`).
4.  Los archivos de configuración (`config.yml`, `playerdata.yml`, `plugin.yml`) se generarán automáticamente en la carpeta `plugins/JPRewards/`.

## ⚙️ Configuración (config.yml)

El archivo `config.yml` te permite personalizar el comportamiento del plugin. Aquí algunos ejemplos de configuraciones clave:

```yaml
# Configuración de la base de datos
database:
  type: "json" # Opciones: "json", "sqlite", "mysql"
  mysql:
    host: "localhost"
    port: 3306
    database: "jprewards_db"
    username: "user"
    password: "password"

# Configuración de la racha
streak_settings:
  enable_inactivity_loss: true
  inactivity_loss_period_hours: 24 # Pierde la racha si no reclama en 24 horas

# Recompensas diarias
rewards:
  '1': # Día 1
    display_item: "DIAMOND"
    display_name: "&bRecompensa del Día 1"
    lore:
      - "&7Un diamante brillante."
    commands:
      - "give %player_name% diamond 1"
  '2': # Día 2
    display_item: "GOLD_INGOT"
    display_name: "&6Recompensa del Día 2"
    lore:
      - "&7Un lingote de oro."
    commands:
      - "give %player_name% gold_ingot 3"
```

## 🎮 Comandos y Permisos

*   `/jprewards <subcomando>`: Comando principal de administración.
    *   `jprewards.admin`: Permiso para todos los subcomandos de administración.
*   `/rewards`: Abre la GUI de recompensas.
    *   `jprewards.use`: Permiso para abrir la GUI.

### Placeholders de PlaceholderAPI:

*   `%jprewards_current_streak%`: Muestra la racha actual del jugador.
*   `%jprewards_last_claimed_day%`: Muestra el último día de recompensa reclamado por el jugador.

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Si encuentras un error o tienes una idea para una nueva característica, no dudes en abrir un 'issue' o enviar un 'pull request' en el repositorio de GitHub.

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.

---