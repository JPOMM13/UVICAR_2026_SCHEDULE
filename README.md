# scheduler-base

Base Spring Boot (Java 17) con:
- `@Scheduled` (cada 5 min y 2 veces al día)
- **ShedLock** para evitar ejecuciones duplicadas si corres varias instancias/pods
- H2 en memoria solo para demo (cambia a tu DB real en prod)

## Ejecutar
```bash
mvn spring-boot:run
```

## Cambiar horarios
Edita `src/main/resources/application.yml`:

- `jobs.sample.every5min-cron`
- `jobs.sample.twiceDaily-cron`
- `jobs.timezone`

Ejemplo (cada 10 min):
```yaml
jobs:
  sample:
    every5min-cron: "0 */10 * * * *"
```
