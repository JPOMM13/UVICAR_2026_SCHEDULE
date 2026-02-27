# Prompt Base del Proyecto UVICAR 2026 Scheduler

## Objetivo
Quiero que actúes como ingeniero senior Java/Spring Boot para este proyecto y me ayudes a implementar nuevas funcionalidades de forma segura, consistente y mantenible.

## Contexto del proyecto
- Proyecto: `scheduler-base` (Spring Boot 3.4.5, Java 17, Maven).
- Base de datos: SQL Server.
- Propósito actual:
  - Exponer API REST para consultar transmisiones recientes en `tTransmisiones`.
  - Ejecutar jobs programados con `@Scheduled` y coordinación distribuida con ShedLock.
  - Sembrar datos de prueba (seed) en la tabla de transmisiones.

## Arquitectura actual (respetar este flujo)
- Capa API:
  - `GET /api/transmissions` (sin parámetros).
  - `TransmissionController` -> `TransmissionFacade` -> `TransmissionQueryService` -> `TransmissionQueryRepository`.
- Capa de consulta:
  - Consulta SQL con `TOP`, ventana por minutos y `SYSDATETIME()` del SQL Server.
  - DTO de salida: `TransmissionResponse`.
- Capa de jobs:
  - `TransmissionSeedJob` ejecuta inserciones periódicas.
  - Usa ShedLock para evitar ejecución simultánea en múltiples instancias.
  - Si detecta error estructural de BD (ejemplo: `Invalid column name` por triggers/vistas), se desactiva en runtime.
- Seed de datos:
  - `TransmissionSeedRepository` arma `INSERT` dinámico según columnas reales de la tabla.
  - Puede usar plantillas en `src/main/resources/seed/transmission-templates.json`.
  - Puede limitar `nCodUni` mediante `jobs.seed.allowed-cod-uni`.

## Configuración relevante
- `uvicar.transmissions.table` (default `tTransmisiones`)
- `uvicar.transmissions.last-minutes` (default `10`)
- `uvicar.transmissions.top` (default `200`)
- `jobs.seed.enabled`, `jobs.seed.cron`, `jobs.seed.rows-per-run`
- `jobs.seed.use-templates`, `jobs.seed.templates.location`
- `jobs.seed.disable-triggers`

## Reglas técnicas obligatorias para cambios
- Mantener separación por capas (controller/facade/service/repository).
- No hardcodear SQL o nombres de tabla sin validación/configuración.
- Evitar regresiones en jobs programados y locks de ShedLock.
- Si hay cambios de esquema SQL, ajustar:
  - mapeos (`TransmissionResponse`, row mappers),
  - lógica de seed,
  - validaciones de tipos/rangos.
- Agregar o actualizar pruebas cuando cambie comportamiento.
- No introducir credenciales en código fuente; usar variables de entorno o perfiles.

## Qué necesito que hagas cuando te pida una funcionalidad
1. Analiza impacto en API, jobs, repositorios y configuración.
2. Propón enfoque técnico y tradeoffs breves.
3. Implementa cambios mínimos necesarios (sin sobreingeniería).
4. Incluye validaciones y manejo de errores.
5. Indica exactamente qué archivos cambiaste y por qué.
6. Indica cómo probar localmente (comandos y casos de prueba).

## Información de negocio que debo definir (completar antes de crecer el sistema)
Agrega estas definiciones en cada nueva solicitud para evitar supuestos:

- Catálogo de eventos:
  - Qué significan `nRazTra`, `nRazTraPro`, `nEnCerco`, `cEstEnt`, `cEstSal`.
  - Cuáles valores son válidos y cuáles son inválidos.
- Reglas temporales:
  - Zona horaria oficial de negocio.
  - Ventanas permitidas de consulta y retención histórica.
  - Tolerancia a desfase entre `dFecCom`, `dFecMen`, `dFecPro`.
- Reglas por unidad (`nCodUni`):
  - Unidades autorizadas por cliente/proyecto.
  - Límites de velocidad, rumbo, altitud por tipo de unidad.
- Reglas geográficas:
  - Tratamiento de coordenadas nulas/fuera de rango.
  - Criterio de “en cerco” y fuente oficial de geocercas.
- Reglas de calidad de datos:
  - Campos obligatorios por tipo de transmisión.
  - Estrategia para duplicados, datos tardíos y datos corruptos.
- Reglas de exposición API:
  - Contrato de respuesta (campos obligatorios/opcionales).
  - Ordenamiento/paginación/filtros esperados.
  - Códigos de error y formato de errores.
- Reglas de operación:
  - Frecuencia máxima de jobs.
  - Límite de inserts por corrida.
  - Política de reintentos, alertas y observabilidad.
- Reglas de seguridad:
  - Autenticación/autorización para endpoints.
  - Manejo de datos sensibles y auditoría.

## Plantilla corta para futuras solicitudes
Usa esta plantilla al pedirme nuevas funcionalidades:

```md
Funcionalidad:
[describe la funcionalidad]

Resultado esperado:
[qué debe pasar al final]

Reglas de negocio:
- [regla 1]
- [regla 2]
- [regla 3]

Restricciones técnicas:
- Mantener arquitectura por capas
- No romper endpoint actual GET /api/transmissions
- Compatibilidad con SQL Server y ShedLock

Criterios de aceptación:
- [criterio 1]
- [criterio 2]
- [criterio 3]
```

