# jpcommand - Comandos Personalizados Avanzados para Spigot 1.8.9

[![Version](https://img.shields.io/badge/version-1.0-blue.svg)](https://www.spigotmc.org/resources/jpcommand.XXXXX/) 
[![Minecraft Version](https://img.shields.io/badge/minecraft-1.8.9-green.svg)](https://www.spigotmc.org/wiki/spigot-1-8-9-api/)
[![License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)

## 📝 Descripción

**jpcommand** es un potente plugin de Spigot diseñado para servidores de Minecraft 1.8.9 que te permite crear comandos personalizados con una flexibilidad sin precedentes. Olvídate de los comandos predefinidos y da rienda suelta a tu creatividad, configurando acciones complejas y añadiendo un sistema de cooldown para un control total.

Este plugin ha sido desarrollado pensando en la eficiencia y la seguridad, incluyendo ofuscación de código para proteger tu inversión.

## ✨ Características Principales

*   **Comandos Totalmente Personalizables:** Define tus propios comandos y las acciones que ejecutarán.
*   **Múltiples Tipos de Acciones:**
    *   `console`: Ejecuta comandos desde la consola del servidor.
    *   `player`: Ejecuta comandos como si el jugador los escribiera.
    *   `message`: Envía mensajes personalizados al jugador que ejecuta el comando.
    *   `broadcast`: Envía mensajes a todos los jugadores conectados.
    *   `teleport`: Teleporta al jugador a coordenadas específicas o a otro jugador.
    *   `sound`: Reproduce sonidos personalizados para el jugador.
*   **Sistema de Cooldown Integrado:** Configura tiempos de espera para cada comando personalizado, evitando el spam y el uso excesivo.
*   **Soporte Completo de Códigos de Color:** Utiliza códigos de color de Minecraft (`&a`, `&b`, etc.) en todos tus mensajes y configuraciones para una experiencia visual atractiva.
*   **Ofuscación de Código:** El JAR compilado está ofuscado con ProGuard para una mayor protección contra la descompilación.

## 🚀 Instalación

1.  Descarga la última versión de **AxCommands.jar** desde SpigotMC.
2.  Coloca el archivo `AxCommands.jar` en la carpeta `plugins` de tu servidor de Minecraft.
3.  Reinicia o recarga tu servidor (`/reload` o `stop` y `start`).
4.  El archivo `config.yml` y `plugin.yml` se generarán automáticamente en la carpeta `plugins/AxCommands/`.

## ⚙️ Configuración (config.yml)

El archivo `config.yml` es donde definirás tus comandos personalizados. Aquí tienes un ejemplo de cómo estructurar tus comandos y las nuevas acciones:

```yaml
# Configuración principal de AxCommands

# Mensaje que se muestra cuando un comando está en cooldown.
# Usa %time% para mostrar el tiempo restante.
cooldown-message: "&c¡Espera %time% segundos antes de usar este comando de nuevo!"

commands:
  # Ejemplo de comando simple con mensaje y cooldown
  mi-primer-comando:
    description: "Un comando de ejemplo simple."
    permission: "axcommands.primercomando" # Permiso requerido para usar el comando
    cooldown: 5 # Cooldown en segundos (0 para sin cooldown)
    actions:
      - type: "message"
        value: "&a¡Hola, %player_name%! Este es tu primer comando personalizado."
      - type: "console"
        value: "say %player_name% ha usado el primer comando."

  # Ejemplo de comando con acción de broadcast
  anuncio:
    description: "Envía un anuncio global."
    permission: "axcommands.anuncio"
    cooldown: 10
    actions:
      - type: "broadcast"
        value: "&6[ANUNCIO]&r &f¡Un mensaje importante para todos!"

  # Ejemplo de comando de teletransporte
  ir-spawn:
    description: "Te teletransporta al spawn."
    permission: "axcommands.spawn"
    cooldown: 0
    actions:
      - type: "teleport"
        value: "world,0,64,0" # Formato: mundo,x,y,z
      - type: "message"
        value: "&b¡Has sido teletransportado al spawn!"

  # Ejemplo de comando para reproducir un sonido
  sonido-ding:
    description: "Reproduce un sonido de ding."
    permission: "axcommands.ding"
    cooldown: 3
    actions:
      - type: "sound"
        value: "NOTE_PLING,1.0,1.0" # Formato: SOUND_NAME,volume,pitch (ver lista de sonidos de Spigot)
      - type: "message"
        value: "&e¡Ding! Sonido reproducido."

  # Ejemplo de comando con múltiples acciones
  kit-inicial:
    description: "Te da un kit inicial."
    permission: "axcommands.kit"
    cooldown: 3600 # 1 hora de cooldown
    actions:
      - type: "console"
        value: "give %player_name% diamond 1"
      - type: "player"
        value: "effect %player_name% speed 10 2"
      - type: "message"
        value: "&a¡Has recibido tu kit inicial y un impulso de velocidad!"
```

### **Variables Disponibles en `value`:**

*   `%player_name%`: Nombre del jugador que ejecuta el comando.
*   `%player_uuid%`: UUID del jugador que ejecuta el comando.
*   `%time%`: (Solo para `cooldown-message`) Tiempo restante del cooldown.

### **Códigos de Color**

Puedes usar los códigos de color estándar de Minecraft (ej. `&a` para verde claro, `&c` para rojo, `&l` para negrita, etc.) en todos los mensajes y descripciones.

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Si encuentras un error o tienes una idea para una nueva característica, no dudes en abrir un 'issue' o enviar un 'pull request' en el repositorio de GitHub (si aplica).

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.

---