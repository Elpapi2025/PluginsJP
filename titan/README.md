# Proyecto Titan: Documentación Completa

Este README proporciona una visión general completa del proyecto Titan, sus funcionalidades, cómo usarlo y cómo contribuir.

## 1. Visión General del Proyecto

Titan es un plugin de Minecraft diseñado para servidores HCF (Hardcore Factions). Su objetivo principal es mejorar la experiencia de juego con una variedad de características personalizables, desde la gestión de equipos y la economía hasta eventos dinámicos y herramientas para el personal. Desarrollado por Juanpiece, Titan busca ofrecer una solución robusta y flexible para la administración de servidores HCF.

## 2. Instalación

### Requisitos Previos

Para compilar y ejecutar el proyecto Titan, necesitarás:

*   **Java Development Kit (JDK):** Versión 8 o superior.
*   **Apache Maven:** Herramienta de automatización de construcción de proyectos.

### Construcción del Proyecto

Para construir el archivo `.jar` del plugin, sigue estos pasos:

1.  Clona este repositorio:
    ```bash
    git clone <URL_DEL_REPOSITORIO>
    ```
2.  Navega al directorio raíz del proyecto:
    ```bash
    cd /project/workspace/titan
    ```
3.  Ejecuta el comando Maven para compilar:
    ```bash
    mvn clean install
    ```
    Esto generará el archivo `Titan-1.0-SNAPSHOT.jar` (o una versión similar) en el directorio `target/`.

### Instalación del Plugin

1.  Copia el archivo `Titan-1.0-SNAPSHOT.jar` desde el directorio `target/` a la carpeta `plugins/` de tu servidor de Minecraft.
2.  Reinicia o recarga tu servidor para que el plugin se cargue.

## 3. Configuración

El archivo de configuración principal de Titan es `config.yml`, ubicado en `plugins/Titan/config.yml`. Este archivo contiene la mayoría de las opciones configurables del plugin. Además, existen otros archivos de configuración importantes:

*   `abilities.yml`: Configuración de habilidades especiales.
*   `classes.yml`: Definición y configuración de clases PvP.
*   `killstreaks.yml`: Configuración de rachas de asesinatos.
*   `language.yml`: Mensajes y textos personalizables del plugin.
*   `limiters.yml`: Límites y restricciones de juego.
*   `lunar.yml`: Configuración relacionada con la integración de Lunar Client.
*   `reclaims.yml`: Configuración de reclamaciones y recompensas.
*   `schedules.yml`: Horarios y eventos programados.
*   `scoreboard.yml`: Configuración del scoreboard.
*   `tablist.yml`: Configuración de la tablist.
*   `teams.yml`: Configuración de equipos y facciones.
*   `tips.yml`: Consejos y mensajes informativos.

Para modificar la configuración, simplemente edita estos archivos con un editor de texto y guarda los cambios. Algunos cambios pueden requerir un reinicio del servidor o el comando `/titan reload` para aplicarse.

## 4. Características y Uso

Titan ofrece una amplia gama de funcionalidades. A continuación, se detallan las principales categorías y sus comandos/permisos asociados. Para una lista completa de permisos y su descripción, consulta `rangos de titan.md`.

### Características Generales

*   **Funciones de Chat:**
    *   `titan.chat.color`: Permite usar códigos de color en el chat.
    *   `titan.chat.bypass`: Permite evitar restricciones de chat (spam).
    *   `titan.profanity.bypass`: Permite evitar el filtro de groserías.
*   **Fundición Automática:**
    *   `titan.autosmelt`: Funde automáticamente los minerales extraídos.
*   **Ventajas para Donantes:**
    *   `titan.donor`: Permiso general para donantes.
*   **Gestión de Lag:**
    *   `titan.clearlag`: Limpia entidades del suelo para reducir el lag.
*   **Palanca:**
    *   `titan.crowbar`: Permite obtener una palanca.
*   **Estadísticas de Jugador:**
    *   `/lastdeaths`: Muestra las últimas muertes de un jugador.
    *   `/lastkills`: Muestra las últimas muertes de un jugador.
    *   `/endplayers`: Muestra la cantidad de jugadores en el End.
    *   `/netherplayers`: Muestra la cantidad de jugadores en el Nether.
*   **Utilidades de Jugador (Staff):**
    *   `titan.echest`: Abre el cofre de ender propio.
    *   `titan.echest.other`: Abre el cofre de ender de otro jugador.
    *   `titan.enchant`: Encanta objetos.
    *   `titan.feed`: Alimenta a jugadores.
    *   `titan.gamemode`: Cambia el modo de juego.
    *   `titan.gmc`: Cambia el modo de juego a creativo.
    *   `titan.gms`: Cambia el modo de juego a supervivencia.
    *   `titan.heal`: Cura a jugadores.
    *   `titan.invsee`: Ve el inventario de otro jugador.
    *   `titan.kill`: Mata a jugadores.
    *   `titan.rename`: Renombra objetos.
    *   `titan.repair`: Repara objetos.
    *   `titan.repair.all`: Repara todos los objetos del inventario.
    *   `titan.stack`: Apila objetos a 64.
    *   `titan.top`: Teletransporta al bloque más alto.
    *   `titan.tpall`: Teletransporta a todos los jugadores a tu ubicación.
    *   `titan.teleport`: Teletransporta a otro jugador.
    *   `titan.teleporthere`: Teletransporta a un jugador a tu ubicación.
    *   `titan.tploc`: Teletransporta a coordenadas específicas.
    *   `titan.tprandom`: Teletransporta a una ubicación aleatoria.
    *   `titan.world`: Teletransporta a un mundo específico.

### Funcionalidades de Staff

*   `titan.staff`: Permiso general de staff.
*   `titan.staff.other`: Ver información de otros staff.
*   `titan.vanish`: Hacerse invisible.
*   `titan.vanish.message`: Enviar mensajes en vanish.
*   `titan.lockclaim.bypass`: Entrar en claims bloqueados.
*   `titan.copyinv`: Copiar inventario de otro jugador.
*   `titan.editmenu`: Editar menús del servidor.
*   `titan.invto`: Copiar tu inventario al de otro jugador.
*   `titan.livesmanage`: Gestionar vidas de jugadores.
*   `titan.managebasetoken`: Gestionar tokens de base.
*   `titan.managefalltraptoken`: Gestionar tokens de trampa de caída.
*   `titan.manageskybasetoken`: Gestionar tokens de skybase.
*   `titan.resetredeem`: Resetear canjes.
*   `titan.restore`: Restaurar inventarios.
*   `titan.setdeaths`: Establecer muertes de un jugador.
*   `titan.setend`: Establecer puntos de salida en el End.
*   `titan.setkills`: Establecer asesinatos de un jugador.
*   `titan.setkillstreak`: Establecer racha de asesinatos.
*   `titan.setrepair`: Establecer puntos de reparación.
*   `titan.setspawn`: Establecer punto de aparición.
*   `titan.spawn.admin`: Teletransportar al spawn o a otros al spawn.
*   `titan.staffchat`: Usar chat de staff.
*   `titan.reload`: Recargar configuración del plugin.
*   `titan.broadcast`: Enviar mensajes de difusión.
*   `titan.clearchat`: Limpiar chat del servidor.
*   `titan.clear`: Limpiar inventario de un jugador.
*   `titan.craft`: Abrir mesa de trabajo virtual.
*   `titan.ecomanage`: Gestionar economía del servidor.
*   `titan.systeam`: Gestionar equipos del sistema.

### Funcionalidades de Equipos

*   `titan.team.teleporthere`: Teletransportar miembros de un equipo a tu ubicación.
*   `titan.team.teleport`: Teletransportarse a la base de un equipo.
*   `titan.team.adddtr`: Añadir DTR a un equipo.
*   `titan.team.forcedemote`: Degradar a un miembro de un equipo.
*   `titan.team.forcedisband`: Disolver un equipo.
*   `titan.team.forcejoin`: Unirse a un equipo.
*   `titan.team.forcekick`: Expulsar a un jugador de un equipo.
*   `titan.team.forceleader`: Establecer el líder de un equipo.
*   `titan.team.forcepromote`: Ascender a un miembro de un equipo.
*   `titan.team.forcerename`: Renombrar un equipo.
*   `titan.team.setbal`: Establecer el saldo de un equipo.
*   `titan.team.setcaps`: Establecer las capturas de KOTH de un equipo.
*   `titan.team.setdtr`: Establecer el DTR de un equipo.
*   `titan.team.setpoints`: Establecer los puntos de un equipo.
*   `titan.team.setraidablepoints`: Establecer los puntos de raidable de un equipo.
*   `titan.team.setregen`: Establecer el tiempo de regeneración de DTR de un equipo.

### Funcionalidades de Reclamaciones

*   `titan.claim.bypass`: Reclamar tierras sin restricciones.
*   `titan.claim.nomoney`: Reclamar tierras sin pagar.

### Funcionalidades de Combate

*   `titan.combatblock.bypass`: Evitar el bloqueo de comandos durante el combate.
*   `titan.deathban.bypass`: Evitar la prohibición de muerte.
*   `titan.unrepairable.bypass`: Reparar objetos que normalmente no se pueden reparar.
*   `titan.potionlimit.bypass`: Evitar el límite de pociones.

### Funcionalidades de Eventos

*   `titan.event.entry`: Entrar en eventos.
*   `titan.citadel.entry`: Entrar en la ciudadela.
*   `titan.conquest.entry`: Entrar en la conquista.
*   `titan.sotw.fly.anywhere`: Volar en cualquier lugar durante el SOTW.

### Funcionalidades de Conquista

*   `titan.conquest.start`: Iniciar una Conquista.
*   `titan.conquest.end`: Finalizar una Conquista activa.
*   `titan.conquest.setpoints`: Establecer los puntos de un equipo en la Conquista.
*   `titan.conquest.claim`: Reclamar una zona de Conquista.

### Funcionalidades de KOTH

*   `titan.koth.unclaim`: Desclaimar un KOTH.
*   `titan.koth.teleport`: Teletransportarse a un KOTH.
*   `titan.koth.start`: Iniciar un KOTH.
*   `titan.koth.setremaining`: Establecer el tiempo restante de un KOTH activo.
*   `titan.koth.setminutes`: Establecer la duración de un KOTH.
*   `titan.koth.setcolor`: Establecer el color de un KOTH.
*   `titan.koth.list`: Ver la lista de KOTHs.
*   `titan.koth.end`: Finalizar un KOTH activo.
*   `titan.koth.editloot`: Editar el botín de un KOTH.
*   `titan.koth.delete`: Eliminar un KOTH.
*   `titan.koth.create`: Crear un KOTH.
*   `titan.koth.claim`: Reclamar una zona de KOTH.

### Funcionalidades de Temporizadores

*   `titan.timer`: Gestionar temporizadores de jugadores.
*   `titan.customtimer`: Gestionar temporizadores personalizados.

### Funcionalidades de Habilidades

*   `titan.abilityradius.bypass`: Usar habilidades sin restricciones de radio.

### Funcionalidades de Spawners

*   `titan.spawner.bypass`: Evitar restricciones de spawners.
*   `titan.spawner.break`: Romper spawners.

### Funcionalidades de Letreros Personalizados

*   `titan.customsigns`: Crear y usar letreros personalizados.

### Funcionalidades de Killtags

*   `titan.killtag.<nombre>`: Usar un killtag específico.

## 5. Contribución

¡Agradecemos tus contribuciones! Si deseas contribuir a este proyecto, por favor, sigue estos pasos:

1.  Haz un fork del repositorio.
2.  Crea una nueva rama para tus cambios (`git checkout -b feature/nueva-funcionalidad`).
3.  Realiza tus cambios y commitea (`git commit -am 'feat: Añadir nueva funcionalidad'`).
4.  Sube tus cambios a tu fork (`git push origin feature/nueva-funcionalidad`).
5.  Abre un Pull Request en este repositorio.

## 6. Licencia

Este proyecto está bajo la licencia [LICENSE.txt](LICENSE.txt).

## 7. Contacto y Soporte

Para soporte o preguntas, puedes contactar a Juanpiece a través de:

*   **Discord:** `discord.titan.cc` (según `config.yml`)
*   **Twitter:** `twitter.titan.cc` (según `config.yml`)
*   **TeamSpeak:** `teamspeak.titan.cc` (según `config.yml`)

¡Gracias por usar Titan!
