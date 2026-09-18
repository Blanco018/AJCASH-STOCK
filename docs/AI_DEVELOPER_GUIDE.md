# Guía Rápida para Desarrolladores y Agentes de IA - AJCASH-STOCK

Documento complementario a `docs/ARQUITECTURA_Y_DESARROLLO_CONTINUIDAD.doc`.

## Resumen Ejecutivo
- **App**: Control de stock de guardia para servicio técnico de TPVs y balanzas (AJ CA$H Zaragoza).
- **Flota**: 4 vehículos oficiales (2 coches y 2 furgonetas).
  - Coche 1: Opel Corsa (0315-JVP)
  - Coche 2: Renault Clio (8268-HVX)
  - Furgoneta 1: Peugeot Bipper (9101-HYL)
  - Furgoneta 2: Renault Express (1025-MJH)

## Stack y Tecnologías
- **UI**: Jetpack Compose con Material 3 y Jetpack Lifecycle Compose (`collectAsStateWithLifecycle`).
- **Arquitectura**: MVVM + Clean Architecture + Offline-First SSOT.
- **Persistencia Local**: Room Database (`StockItem`, `Vehicle`, `RevisionRecord`).
- **Persistencia en la Nube y Tiempo Real**: Firebase Cloud Firestore (`inventory`, `revisions`).
- **Sincronización**: `FirestoreSyncService` conectado a `StockRepository`. Cuando un técnico descuenta stock, se actualiza Room local (0 ms) y se emite la mutación a Firestore. Los demás técnicos suscritos reciben el snapshot y actualizan su vista automáticamente.

## Reglas de Diseño Estrictas
1. Subtítulo de tarjeta de vehículo: `"${vehicle.type} · Matrícula: ${vehicle.plate.replace("-", "")}"`.
2. Las imágenes deben mantenerse despejadas sin insignias artificiales de matrícula en la esquina inferior izquierda.
3. El indicador superior izquierdo sobre la foto solo muestra `"Coche"` o `"Furgoneta"` (sin "Foto Real" ni "Flota AJCASH").

## Verificación y Build
- Compilar: `compile_applet` o `gradle :app:assembleDebug`
- Test: `gradle :app:testDebugUnitTest`
