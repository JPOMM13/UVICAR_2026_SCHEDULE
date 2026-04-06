# UVICAR 2026 Scheduler + API (tTransmisiones)

## Endpoint (sin parámetros)
- `GET /api/transmissions`
- `GET /api/reports/clientes-grandes/sin-trans-3dias?codUsuario={id}`

La consulta calcula la ventana de tiempo con la hora actual del **SQL Server** (`SYSDATETIME()`), sin parámetros de entrada.
El endpoint de reporte ejecuta el procedimiento almacenado `pa_RptUniActSinTrans3Dias_ClientesGrandes` sin parámetros y devuelve su result set como JSON.

## Configuración rápida (SQL Server)
La conexión se resuelve desde Azure Key Vault o variables de entorno, según el entorno.

Opcionales (puedes ajustar en el mismo `application.properties`):
- `uvicar.transmissions.table` (default: `tTransmisiones`)
- `uvicar.transmissions.last-minutes` (default: `10`)
- `uvicar.transmissions.top` (default: `200`)
- `uvicar.reports.clientes-grandes.sin-trans-3dias.sp` (default: `pa_RptUniActSinTrans3Dias_ClientesGrandes`)
- `jobs.seed.enabled` (default: `true`)

## Run
```bash
mvn spring-boot:run
```

## Variables para on-premise con Azure Key Vault
Para un servidor fuera de Azure, el patrón recomendado es usar un Service Principal:

```bash
export AZURE_CLIENT_ID=...
export AZURE_TENANT_ID=...
export AZURE_CLIENT_SECRET=...
export AZURE_KEYVAULT_ENDPOINT=https://kv-uvicar-prd-pe.vault.azure.net/
export UVICAR_DB_USERNAME=reporte
```

La aplicación leerá estos secretos del vault:
- `uvischedule-db-url`
- `uvischedule-db-password`

Si necesitas arrancar sin Azure Key Vault, puedes desactivarlo temporalmente:

```bash
export AZURE_KEYVAULT_ENABLED=false
export UVISCHEDULE_DB_URL="jdbc:sqlserver://..."
export UVISCHEDULE_DB_PASSWORD="..."
```

## Docker
Construcción:

```bash
docker build -t uvischedule:latest .
```

Ejecución local:

```bash
docker run --rm -p 8081:8081 \
  -e AZURE_CLIENT_ID=... \
  -e AZURE_TENANT_ID=... \
  -e AZURE_CLIENT_SECRET=... \
  -e AZURE_KEYVAULT_ENDPOINT=https://kv-uvicar-prd-pe.vault.azure.net/ \
  -e UVICAR_DB_USERNAME=reporte \
  uvischedule:latest
```

### Compatibilidad con SQL Server 2014 / TLS 1.0
Este proyecto incluye un modo legacy opcional para clientes Java 17 que necesiten conectarse a una instancia vieja de SQL Server que aun negocia `TLS 1.0`.

En despliegue on-premise actual, el `ConfigMap` de Kubernetes lo deja activado con:

```bash
SQLSERVER_TLS_LEGACY_ENABLED=true
```

Esto reactiva protocolos TLS antiguos del lado cliente dentro del contenedor.
Es una solucion de compatibilidad temporal; la solucion correcta sigue siendo actualizar SQL Server/Windows para soportar `TLS 1.2`.

## K3s / Kubernetes
Se incluye un manifiesto unico de produccion en [k8s-prod.yaml](/Users/johnpaulmanchegomedina/Documents/PROYECTOS/TRABAJO/UVICAR/NUEVOS-SERVICIOS-2026/UVICAR_2026_SCHEDULE/k8s/k8s-prod.yaml) con `Namespace`, `ConfigMap`, `Deployment` y `Service`, todo comentado.

El `Deployment` esta preparado para reutilizar un secreto ya creado en Kubernetes con este nombre:

```bash
azure-keyvault-secret
```

Flujo sugerido:

```bash
docker build -t uvischedule:latest .
docker save uvischedule:latest -o uvischedule.tar
scp uvischedule.tar usuario@tu-servidor:/tmp/
ssh usuario@tu-servidor
sudo k3s ctr images import /tmp/uvischedule.tar
envsubst < k8s/k8s-prod.yaml | kubectl apply -f -
```

Antes de aplicar en el servidor, exporta ahi mismo:

```bash
export AZURE_KEYVAULT_ENDPOINT=https://kv-uvicar-prd-pe.vault.azure.net/
export UVICAR_DB_USERNAME=reporte
export SERVER_PORT=8081
export AZURE_KEYVAULT_ENABLED=true
export JOBS_SEED_ENABLED=true
```

Y crea el secret en el namespace correcto:

```bash
kubectl create namespace uvischedule --dry-run=client -o yaml | kubectl apply -f -
kubectl create secret generic azure-keyvault-secret \
  -n uvischedule \
  --from-literal=AZURE_CLIENT_ID=... \
  --from-literal=AZURE_CLIENT_SECRET=... \
  --from-literal=AZURE_TENANT_ID=...
```

Importante:
- El pod de Kubernetes no hereda automaticamente las variables del host.
- Por eso el manifiesto usa `${...}` y se aplica con `envsubst`, para convertir esas variables del servidor en un `ConfigMap` real dentro de Kubernetes.
- `envsubst` no resuelve defaults tipo `${VAR:-valor}`. Por eso las variables anteriores deben exportarse explicitamente antes de aplicar.
- El secret `azure-keyvault-secret` debe existir en el mismo namespace `uvischedule`; si lo creas en `default`, el Deployment no podra usarlo.

### Consumo desde el exterior sin dominio
El `Service` del proyecto queda como `NodePort` en el puerto `30081`.

Ejemplos:

```bash
curl http://190.119.213.62:30081/actuator/health
curl http://190.119.213.62:30081/api/transmissions
curl "http://190.119.213.62:30081/api/reports/clientes-grandes/sin-trans-3dias?codUsuario=1"
```

Si no responde desde fuera, revisa que el firewall o NAT del servidor permita el puerto `30081/TCP`.

## Nota
- `spring.sql.init.mode=never` para que NO intente ejecutar `schema.sql`.

## Seed de data (importante)
Si tu BD tiene triggers/vistas con referencias a columnas que ya no existen, puedes ver errores tipo:
`Invalid column name '...'` aunque el INSERT no mencione esa columna.

En ese caso, el job de seed se deshabilita automáticamente para no spamear logs.
Tienes un script de diagnóstico en: `docs/DB_DIAG.sql`.
