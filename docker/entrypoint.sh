#!/bin/sh
set -eu

set -- java \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -Djava.security.egd=file:/dev/./urandom

if [ "${SQLSERVER_TLS_LEGACY_ENABLED:-false}" = "true" ]; then
  set -- "$@" \
    -Djava.security.properties=/app/java.security.legacy \
    -Djdk.tls.client.protocols=TLSv1,TLSv1.1,TLSv1.2
fi

exec "$@" -jar /app/app.jar
