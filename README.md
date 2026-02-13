# UVICAR 2026 Scheduler + API (tTransmisiones)

## Endpoint (sin parámetros)
- `GET /api/transmissions`

La consulta calcula la ventana de tiempo con la hora actual del **SQL Server** (`SYSDATETIME()`), sin parámetros de entrada.

## Configuración rápida (SQL Server)
La conexión se configura en `src/main/resources/application.properties`.

Opcionales (puedes ajustar en el mismo `application.properties`):
- `uvicar.transmissions.table` (default: `tTransmisiones`)
- `uvicar.transmissions.last-minutes` (default: `10`)
- `uvicar.transmissions.top` (default: `200`)
- `jobs.seed.enabled` (default: `true`)

## Run
```bash
mvn spring-boot:run
```

## Nota
- `spring.sql.init.mode=never` para que NO intente ejecutar `schema.sql`.

## Seed de data (importante)
Si tu BD tiene triggers/vistas con referencias a columnas que ya no existen, puedes ver errores tipo:
`Invalid column name '...'` aunque el INSERT no mencione esa columna.

En ese caso, el job de seed se deshabilita automáticamente para no spamear logs.
Tienes un script de diagnóstico en: `docs/DB_DIAG.sql`.
