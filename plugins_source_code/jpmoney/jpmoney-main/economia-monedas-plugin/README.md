# jpmoney - Plugin de Economía para Minecraft

[![Version](https://img.shields.io/badge/version-1.0-blue.svg)](https://www.spigotmc.org/resources/jpmoney.XXXXX/)
[![Minecraft Version](https://img.shields.io/badge/minecraft-1.8.9-green.svg)](https://www.spigotmc.org/wiki/spigot-1-8-9-api/)
[![License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://opensource.org/licenses/MIT)

## 📝 Descripción

**jpmoney** es un plugin de economía esencial para servidores de Minecraft 1.8.9, diseñado para una gestión flexible y avanzada de monedas personalizadas. Permite a los administradores y jugadores interactuar con múltiples sistemas de moneda, ofreciendo una experiencia económica rica y adaptable a las necesidades de tu servidor.

## ✨ Características Principales

*   **Gestión de Múltiples Monedas:** Crea, configura y administra diversas monedas personalizadas para tu servidor.
*   **Comandos Intuitivos:** Conjunto completo de comandos para la manipulación de saldos, creación y eliminación de monedas.
*   **Integración con Vault:** Compatibilidad total con Vault para una fácil integración con otros plugins de economía.
*   **Soporte de Bases de Datos:** Almacenamiento de datos de moneda persistente mediante SQLite y MySQL.
*   **Tiendas Personalizadas:** Funcionalidad para crear tiendas donde los jugadores pueden comprar y vender ítems usando tus monedas personalizadas.

## 🚀 Instalación

1.  Descarga la última versión de **jpmoney.jar** desde SpigotMC.
2.  Coloca el archivo `jpmoney.jar` en la carpeta `plugins` de tu servidor de Minecraft.
3.  Asegúrate de tener el plugin [Vault](https://www.spigotmc.org/resources/vault.34315/) instalado para la funcionalidad completa.
4.  Reinicia o recarga tu servidor (`/reload` o `stop` y `start`).
5.  Los archivos de configuración se generarán automáticamente en la carpeta `plugins/jpmoney/`.

## ⚙️ Configuración

El plugin generará archivos de configuración donde podrás definir tus monedas, tiendas y ajustes de base de datos.

### Ejemplo de `config.yml` (generado automáticamente):

```yaml
# Configuración de la base de datos
database:
  type: "sqlite" # Puede ser "sqlite" o "mysql"
  mysql:
    host: "localhost"
    port: 3306
    database: "minecraft"
    username: "user"
    password: "password"

# Monedas predefinidas
currencies:
  souls:
    symbol: "§d✦" # Símbolo de la moneda (con códigos de color)
    initial-balance: 0
  gold:
    symbol: "§6◎"
    initial-balance: 0
```

### Ejemplo de `shops_para_ti.yml` (generado automáticamente):

```yaml
# Ejemplo de configuración de tiendas
shops:
  default_shop:
    title: "&aTienda Principal"
    size: 54
    sections:
      - name: "Bloques"
        icon: "GRASS"
        items:
          - material: "DIRT"
            amount: 1
            buy-price: 10
            sell-price: 5
            currency: "gold"
```

## 🎮 Comandos

*   `/moneda <subcomando>`: Comando principal para la gestión de monedas.
    *   `/moneda crear <nombre> <cantidad>`: Crea una nueva moneda.
    *   `/moneda eliminar <nombre>`: Elimina una moneda existente.
    *   `/moneda saldo <jugador> [moneda]`: Consulta el saldo de un jugador.
    *   `/moneda dar <jugador> <cantidad> <moneda>`: Da dinero a un jugador.
    *   `/moneda quitar <jugador> <cantidad> <moneda>`: Quita dinero a un jugador.
    *   `/moneda establecer <jugador> <cantidad> <moneda>`: Establece el saldo de un jugador.
*   `/shop <nombre_tienda>`: Abre una tienda personalizada.
*   `/shopreload`: Recarga la configuración de las tiendas.

### Permisos:

*   `jpmoney.admin`: Acceso a todos los comandos de administración de `jpmoney`.
*   `jpmoney.shop.reload`: Permiso para recargar las tiendas.
*   `jpmoney.shop.<nombre_tienda>`: Permiso para acceder a una tienda específica.

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Si encuentras un error o tienes una idea para una nueva característica, no dudes en abrir un 'issue' o enviar un 'pull request' en el repositorio de GitHub (si aplica).

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.

---