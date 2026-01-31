# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LifeGraf is a personal data aggregation platform built with Spring Boot 3.5.8 and Java 21. It collects and stores activity data from multiple sources (Bilibili, Steam, ActivityWatch, electricity usage, GPS, LeetCode) using scheduled synchronization tasks.

## Project Structure

```
lifegraf-parent/                    # Maven parent POM
├── lifegraf-common/                # Common module (shared configs, utilities)
├── lifegraf-collector/             # Collector modules (Spring Modulith)
│   ├── lifegraf-bilibili/          # Bilibili video platform
│   ├── lifegraf-steam/             # Steam gaming platform
│   ├── lifegraf-activitywatch/     # ActivityWatch desktop tracking
│   ├── lifegraf-electricity/       # Electricity consumption
│   ├── lifegraf-gps/               # GPS location tracking
│   └── lifegraf-leetcode/          # LeetCode submissions
└── lifegraf-boot/                  # Main application launcher
    └── src/main/resources/
        ├── db/migration/           # Flyway migrations
        ├── mapper/                 # MyBatis XML mappers
        └── templates/              # Freemarker templates
```

## Common Commands

### Build and Run
```bash
# Build the entire project
./mvnw clean compile

# Build a specific module (from root)
./mvnw clean compile -pl lifegraf-common

# Run the application (from boot module)
./mvnw spring-boot:run -pl lifegraf-boot

# Package the application
./mvnw clean package -DskipTests

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=SteamServiceImplTest

# Skip tests during build
./mvnw clean package -DskipTests
```

### Database
```bash
# Flyway migrations are automatically applied on startup
# Migration scripts are in lifegraf-boot/src/main/resources/db/migration
```

### Docker
```bash
# Build and start all services (PostgreSQL + LifeGraf)
docker-compose up -d --build

# View logs
docker-compose logs -f lifegraf

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Rebuild without cache
docker-compose build --no-cache
```

### Environment Variables
Create a `.env` file to customize configuration:
```env
# Admin password
ADMIN_PASSWORD=your_secure_password

# Module enable/disable
STEAM_ENABLED=true
ELECTRICITY_ENABLED=true
BILIBILI_ENABLED=true

# Steam configuration
STEAM_API_KEY=your_steam_api_key
STEAM_MY_ID=your_steam_id

# Electricity configuration
ELECTRICITY_ID=your_electricity_id
ELECTRICITY_USERNAME=your_username

# LeetCode configuration
LEETCODE_USER_SLUG=your_username
```

## Module Architecture

### Common Module (lifegraf-common)
Contains shared configurations:
- `MybatisPlusConfig.java` - MyBatis-Plus configuration with `@MapperScan`

### Collector Modules (lifegraf-collector/*)
Each collector module follows this structure:
```
cn.kiroe.lifegraf.<module>/
├── config/              # *Properties classes with @ConfigurationPropertiesScan
├── controller/          # REST endpoints and view controllers
├── service/             # Business logic (interface + impl/)
├── mapper/              # MyBatis-Plus mappers (extends BaseMapper)
├── model/               # Database entities
├── converter/           # MapStruct DTO-to-entity mappers
├── remote/              # External API clients
│   ├── *ClientConfig    # WebClient configuration
│   ├── *Client/*ApiClient # HTTP service interfaces with @HttpExchange
│   └── dto/             # API response DTOs
├── scheduler/           # @Scheduled cron jobs for data sync
└── enums/               # Enumerations
```

### Module Configuration
Each module has its own `*Properties` class with `@ConfigurationProperties(prefix = "app.<module>")`:

| Module | Properties Class | Prefix |
|--------|-----------------|--------|
| `lifegraf-bilibili` | `BilibiliProperties` | `app.bilibili` |
| `lifegraf-steam` | `SteamProperties` | `app.steam` |
| `lifegraf-activitywatch` | `ActivityWatchProperties` | `app.activity-watch` |
| `lifegraf-electricity` | `ElectricityProperties` | `app.electricity` |
| `lifegraf-leetcode` | `LeetCodeProperties` | `app.leetcode` |

### Module List
| Module | Description |
|--------|-------------|
| `lifegraf-bilibili` | Bilibili video platform (QR code login, watch history) |
| `lifegraf-steam` | Steam gaming platform (player info, gaming sessions) |
| `lifegraf-activitywatch` | ActivityWatch desktop activity tracking |
| `lifegraf-electricity` | Electricity consumption monitoring |
| `lifegraf-gps` | GPS location tracking |
| `lifegraf-leetcode` | LeetCode submission tracking |

## Configuration

### Application Properties
Each module has its own configuration prefix:
```yaml
app:
  steam:
    base-url: ${STEAM_BASE_URL:steamapi.kiroe.cn}
    api-key: ${STEAM_API_KEY:...}
    my-steam-id: ${MY_STEAM_ID:...}
    cron: ${STEAM_CRON:0 0/5 * * * ?}
  electricity:
    base-url: ${ELECTRICITY_BASE_URL:...}
    cron: ${ELECTRICITY_CRON:0 0/10 * * ?}
  activity-watch:
    base-url: ${ACTIVITY_WATCH_BASE_URL:...}
    cron: ${ACTIVITY_WATCH_CRON:0 0/10 * * * ?}
  bilibili:
    cron: ${BILIBILI_CRON:0 0/10 * * * ?}
  leetcode:
    base-url: ${LEETCODE_BASE_URL:...}
    user-slug: ${LEETCODE_USER_SLUG:...}
```

## Database
- **PostgreSQL** with **TimescaleDB** extension for time-series data
- **Flyway** migrations for schema management
- **MyBatis-Plus** for ORM (BaseMapper for CRUD operations)
- Mapper interfaces use `@Mapper` annotation (no package scanning)

## Key Technologies
- **Lombok** - Reduces boilerplate (`@Data`, `@Slf4j`, etc.)
- **MapStruct** - Type-safe object mapping between DTOs and entities
- **Spring WebClient** - Non-blocking HTTP clients for external APIs
- **Freemarker** - Web view templates
- **Caffeine** - In-memory caching
- **Spring Scheduling** - Cron-based data synchronization (thread pool: 4)

## External API Integration Pattern
1. Create `*ClientConfig` class to configure WebClient with base URL and codecs
2. Create `*Client` or `*ApiClient` service interface with `@HttpExchange` methods
3. Create DTO classes in `remote/dto/` for API responses
4. Use `@Mapper` (MapStruct) to convert DTOs to domain entities
5. Create service layer to orchestrate API calls and database persistence
6. Use `@Scheduled` methods in scheduler/ to periodically fetch and store data

## Annotation Processing
The project uses multiple annotation processors:
- Spring Boot Configuration Processor
- Lombok
- MapStruct
- Lombok-MapStruct Binding

## Important Notes

- All external API credentials are configurable via environment variables
- Scheduled tasks use a thread pool of size 4 (`spring.task.scheduling.pool.size`)
- Collector modules are organized under `lifegraf-collector/` (Spring Modulith structure)
- New modules should follow the existing collector module pattern
- Database changes require new Flyway migration scripts in `lifegraf-boot/src/main/resources/db/migration`
- WebClient is configured per-module in `remote/*ClientConfig` classes
- Each module has its own `*Properties` class scanned by `@ConfigurationPropertiesScan`
- Module packages follow `cn.kiroe.lifegraf.<module>` pattern
