# AJCashStocks - Suite E2E con Appium, WebdriverIO y TypeScript

Esta carpeta contiene la infraestructura de pruebas móviles automatizadas **End-to-End (E2E)** para la aplicación Android **AJCashStocks**, implementada con el estándar **WebdriverIO (v9) + Appium 2 + TypeScript** bajo el patrón de diseño **Screen Object Model (POM)**.

---

## 🛠️ Requisitos Previos

Antes de ejecutar las pruebas en tu ordenador, asegúrate de tener instalado:

1. **Node.js** (versión 18 o superior).
2. **Java JDK 17 o 21** configurado (`JAVA_HOME`).
3. **Android SDK** configurado (`ANDROID_HOME`).
4. **Dispositivo físico con Depuración USB activa** o un **Emulador de Android Studio** en ejecución.

---

## 🚀 Pasos para Ejecutar

### 1. Compilar el APK de la aplicación
Desde la raíz del proyecto principal de Android, compila el APK en modo debug:

```bash
./gradlew assembleDebug
```
*(El archivo generado quedará en `app/build/outputs/apk/debug/app-debug.apk`)*.

---

### 2. Instalar dependencias del proyecto de automatización
Entra en esta carpeta e instala las dependencias de Node.js y los drivers de Appium:

```bash
cd appium-wdio-tests
npm install
```

Si es la primera vez que usas Appium en tu equipo, instala el driver oficial de Android:
```bash
npx appium driver install uiautomator2
```

---

### 3. Ejecutar las pruebas E2E
Con tu emulador o móvil Android encendido y conectado por USB:

```bash
npm run test:appium
```

WebdriverIO levantará automáticamente la sesión de Appium, instalará el APK en el dispositivo y ejecutará los 4 casos de prueba de forma visible.

---

## 📁 Estructura del Proyecto

```text
appium-wdio-tests/
├── package.json               # Dependencias de WebdriverIO, Appium y TypeScript
├── tsconfig.json              # Configuración del compilador TypeScript
├── wdio.conf.ts               # Capacidades de Android (UiAutomator2, APK path, etc.)
└── src/
    ├── screens/               # Screen Object Model (POM)
    │   ├── DashboardScreen.ts # Acciones y selectores de la pantalla de inicio
    │   └── InventoryScreen.ts # Acciones y selectores del inventario de repuestos
    └── specs/
        └── inventory.spec.ts  # Casos de prueba automatizados (TC01 a TC04)
```

---

## 🧪 Casos de Prueba Cubiertos
- **TC01:** Inicio de la Activity y renderizado del Dashboard corporativo.
- **TC02:** Selección de vehículo de guardia (Furgoneta 1) y confirmación del técnico.
- **TC03:** Búsqueda en tiempo real por término `"TPV"` y validación de los resultados filtrados en Jetpack Compose.
- **TC04:** Navegación hacia atrás y retorno exitoso al Dashboard.
