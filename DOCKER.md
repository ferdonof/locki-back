# 🐳 Locki Docker Setup

Este documento describe cómo construir y ejecutar los servicios de Locki usando Docker.

## Estructura

```
locki-back/
├── manage-administration/
│   └── Dockerfile          # Build para manage-administration
├── manage-reservations/
│   └── Dockerfile          # Build para manage-reservations
├── docker-compose-full.yaml # Orquestación completa
├── build-docker-images.sh   # Script de utilidad para builds
└── .dockerignore            # Configuración de exclusiones
```

## Requisitos

- Docker 20.10+
- Docker Compose 1.29+
- Al menos 4GB de RAM disponible
- Conexión a internet (para descargar imágenes base)

## Inicio Rápido

### 1. Construir las imágenes

#### Opción A: Construir ambas imágenes
```bash
chmod +x build-docker-images.sh
./build-docker-images.sh
```

#### Opción B: Construir solo una imagen
```bash
./build-docker-images.sh --admin-only
./build-docker-images.sh --reservations-only
```

#### Opción C: Construcción manual con Docker
```bash
# manage-administration
docker build -f manage-administration/Dockerfile -t locki-admin:latest .

# manage-reservations
docker build -f manage-reservations/Dockerfile -t locki-reservations:latest .
```

### 2. Ejecutar los servicios

```bash
docker-compose -f docker-compose-full.yaml up -d
```

### 3. Verificar estado

```bash
# Ver contenedores corriendo
docker-compose -f docker-compose-full.yaml ps

# Ver logs
docker-compose -f docker-compose-full.yaml logs -f locki-admin
docker-compose -f docker-compose-full.yaml logs -f locki-reservations

# Ver logs de PostgreSQL
docker-compose -f docker-compose-full.yaml logs -f postgres-admin
docker-compose -f docker-compose-full.yaml logs -f postgres-reservations
```

## Acceso a los Servicios

### Locki Administration
- **URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Base de Datos**: PostgreSQL en `localhost:5450`

### Locki Reservations
- **URL**: http://localhost:8081
- **Health Check**: http://localhost:8081/actuator/health
- **Metrics**: http://localhost:8081/actuator/metrics
- **Base de Datos**: PostgreSQL en `localhost:5460`

## Detener los Servicios

```bash
docker-compose -f docker-compose-full.yaml down
```

Para eliminar también los volúmenes de datos:
```bash
docker-compose -f docker-compose-full.yaml down -v
```

## Construir con Tag y Registry

```bash
# Con tag específico
./build-docker-images.sh --tag v1.0.0

# Push a registry
./build-docker-images.sh --registry docker.io/myuser --push --tag v1.0.0
```

## Dockerfile Features

### ✅ Optimizaciones Implementadas

1. **Multi-stage builds**: Reduce el tamaño final de la imagen (~150MB vs ~500MB)
2. **Alpine Linux**: Imagen base lightweight (~150MB)
3. **Non-root user**: Ejecuta como usuario `locki` (1000:1000)
4. **Health checks**: Verifica el estado del servicio automáticamente
5. **Seguridad**: Sin vulnerabilidades críticas
6. **Caching**: Aprovecha capas de Docker para builds más rápidos

### Especificaciones

| Aspecto | Valor |
|---------|-------|
| Imagen Base | `eclipse-temurin:21-jre-alpine` |
| Puerto Expuesto | 8080 |
| Usuario | locki (1000:1000) |
| Tamaño Final | ~150MB |
| Health Check Interval | 30s |
| Health Check Timeout | 10s |
| Startup Wait | 40s |

## Configuración de Entorno

### Env vars disponibles

```yaml
# manage-administration
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-admin:5432/locki-admin
SPRING_DATASOURCE_USERNAME: locki
SPRING_DATASOURCE_PASSWORD: locki

# manage-reservations
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-reservations:5432/locki-reservations
SPRING_DATASOURCE_USERNAME: locki
SPRING_DATASOURCE_PASSWORD: locki
```

### Personalizar configuración

Edita `docker-compose-full.yaml` en la sección `environment` de cada servicio.

## Troubleshooting

### Contenedor no inicia

```bash
# Ver logs de error
docker-compose -f docker-compose-full.yaml logs locki-admin
docker-compose -f docker-compose-full.yaml logs locki-reservations

# Verificar conectividad a BD
docker-compose -f docker-compose-full.yaml exec locki-admin \
  curl http://localhost:8080/actuator/health
```

### Base de datos rechaza conexión

```bash
# Verificar que PostgreSQL está running
docker-compose -f docker-compose-full.yaml ps

# Recrear servicio
docker-compose -f docker-compose-full.yaml restart postgres-admin
docker-compose -f docker-compose-full.yaml restart postgres-reservations
```

### Puerto en uso

Si el puerto 8080, 8081, 5450 o 5460 están ocupados:

```bash
# Encontrar qué proceso usa el puerto
lsof -i :8080

# Matar el proceso o cambiar puertos en docker-compose-full.yaml
```

## Desarrollo Local

Para desarrollo sin Docker, usar el `compose.yaml` existente:

```bash
docker-compose -f compose.yaml up -d
mvn clean install
mvn spring-boot:run -pl manage-administration/rest-endpoints/boot
mvn spring-boot:run -pl manage-reservations/manage-reservations-rest-endpoints/boot
```

## Mejoras Futuras

- [ ] Agregar Nginx como reverse proxy
- [ ] Implementar logging centralizado (ELK Stack)
- [ ] Agregar monitoring (Prometheus + Grafana)
- [ ] Configurar CI/CD para builds automáticos
- [ ] Multi-architecture support (amd64, arm64)

## Documentación

Para más información sobre la configuración de Spring Boot:
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)

