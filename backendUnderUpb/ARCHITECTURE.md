## Backend UnderUpb - Arquitectura Hexagonal

### Descripción General
Backend completo del videojuego UnderUpb basado en una universidad donde jugadores responden preguntas a través de niveles. Implementa arquitectura **hexagonal (ports & adapters)** para separación de capas.

### Estructura de Capas

#### 1. **Domain Layer** (Entidades)
Ubicación: `com.artemisia_corp.artemisia.entity.*`

Entidades con JPA:
- `AuditableEntity` (base con createdDate, updatedDate)
- `User` → tabla `players` (UUID id, name, lifePoints, score, currentLevel, inventory)
- `Level` → tabla `levels` (name, description, requiredXp, orderIndex)
- `CharacterEntity` → tabla `characters` (name, description, abilities, requiredLevel)
- `Question` → tabla `questions` (text, level, optionsJson, answer, description)
- `SaveGame` → tabla `saves` (userId, stateJson, version)
- `LeaderboardEntry` → tabla `leaderboard` (userId, score, createdAt)
- `Decision` → tabla `decisions` (questionId, payloadJson, description)
- `Enemy` → tabla `enemies` (name, damage, totalLife, level, behaviorJson)

#### 2. **Repository Layer** (Data Access)
Ubicación: `com.artemisia_corp.artemisia.repository.*`

Interfaces JPA Repository:
- `UserRepository extends JpaRepository<User, UUID>`
- `LevelRepository extends JpaRepository<Level, UUID>`
- `CharacterRepository extends JpaRepository<CharacterEntity, UUID>`
- `QuestionRepository` + findByLevel(Integer)
- `SaveRepository` + findByUserIdOrderByUpdatedDateDesc(UUID)
- `LeaderboardRepository` + findTopEntries(Pageable), findByUserIdOrderByScoreDesc(UUID)
- `DecisionRepository` + findByQuestionId(UUID)
- `EnemyRepository` + findByLevel(Integer)

#### 3. **Application Layer** (Lógica de Negocio)

##### 3a. Port In (Service Interfaces)
Ubicación: `com.artemisia_corp.artemisia.application.port.in.*`

Interfaces:
- `UserService` → CRUD usuarios + verificación por JWT
- `LevelService` → CRUD niveles + levelUpUser(UUID, Integer)
- `CharacterService` → CRUD personajes
- `QuestionService` → CRUD preguntas + getByLevel(Integer)
- `SaveGameService` → guardar/cargar estado + versionado
- `LeaderboardService` → top entries, recordScore(), getUserScores()
- `DecisionService` → CRUD decisiones + getByQuestion()
- `EnemyService` → CRUD enemigos + getByLevel()
- `PurchaseService` → crear orden, webhook, completar/fallar compra
- `MatchService` → calculateFinalScore(), endMatch()

##### 3b. Service Implementations
Ubicación: `com.artemisia_corp.artemisia.application.service.impl.*`

Implementaciones:
- `UserServiceImpl` → transaccional, con validaciones
- `LevelServiceImpl` → transaccional, con levelUp
- `CharacterServiceImpl` → transaccional
- `QuestionServiceImpl` → transaccional, con búsqueda por nivel
- `SaveGameServiceImpl` → transaccional, con versionado
- `LeaderboardServiceImpl` → transaccional, score recording
- `DecisionServiceImpl` → transaccional
- `EnemyServiceImpl` → transaccional
- `PurchaseServiceImpl` → crear orden, webhook (TODO: persistir en DB)
- `MatchServiceImpl` → cálculo de score final, grabación en leaderboard

##### 3c. Mappers
Ubicación: `com.artemisia_corp.artemisia.application.mapper.*`

Mappers estáticos:
- `UserMapper` → Entity ↔ DTO
- `LevelMapper` → Entity ↔ DTO
- `CharacterMapper` → Entity ↔ DTO
(QuestionMapper, EnemyMapper, etc. se generan inline en servicios)

#### 4. **DTO Layer** (Data Transfer Objects)
Ubicación: `com.artemisia_corp.artemisia.entity.dto.*`

DTOs Request/Response:
- `UserRequestDto` / `UserResponseDto`
- `LevelRequestDto` / `LevelResponseDto`
- `CharacterRequestDto` / `CharacterResponseDto`
- `QuestionRequestDto` / `QuestionResponseDto`
- `SaveGameRequestDto` / `SaveGameResponseDto`
- `LeaderboardResponseDto`
- `DecisionRequestDto` / `DecisionResponseDto`
- `EnemyRequestDto` / `EnemyResponseDto`
- `PurchaseOrderRequestDto` / `PurchaseOrderResponseDto`

#### 5. **Adapter Layer** (REST Controllers)
Ubicación: `com.artemisia_corp.artemisia.adapter.rest.controller.*`

Controladores REST con anotaciones OpenAPI:
- `UserController` → POST/GET/PUT/PATCH/DELETE `/api/users`
- `LevelController` → CRUD `/api/levels` + POST `/api/levels/{id}/users/{uid}/levelup`
- `CharacterController` → CRUD `/api/characters`
- `QuestionController` → CRUD `/api/questions` + GET `/api/questions/level/{level}`
- `SaveGameController` → CRUD `/api/save` + GET `/api/save/users/{uid}/latest`
- `LeaderboardController` → GET `/api/leaderboard/top` + pagination + user scores
- `DecisionController` → CRUD `/api/decisions` + GET `/api/decisions/questions/{qid}`
- `EnemyController` → CRUD `/api/enemies` + GET `/api/enemies/level/{level}`
- `PurchaseController` → POST `/api/purchases/orders` + webhook callback
- `MatchController` → GET `/api/match/users/{uid}/score` + POST `/api/match/users/{uid}/end`

#### 6. **Exception Layer**
Ubicación: `com.artemisia_corp.artemisia.exception.*`

Excepciones existentes (reutilizadas):
- `NotDataFoundException`
- `ClientNotFoundException`
- `InvalidJwtAuthenticationException`
- `UnauthorizedAccessException`
- `GlobalExceptionHandler` (manejo centralizado)

### Flujos Principales

#### 1. **Crear Usuario y Jugar**
```
POST /api/users → UserController → UserServiceImpl.createUser()
→ UserRepository.save() → UserResponseDto
```

#### 2. **Obtener Preguntas de un Nivel**
```
GET /api/questions/level/1 → QuestionController → QuestionServiceImpl.getQuestionsByLevel(1)
→ QuestionRepository.findByLevel(1) → List<QuestionResponseDto>
```

#### 3. **Guardar Progreso**
```
POST /api/save → SaveGameController → SaveGameServiceImpl.saveGame()
→ SaveRepository.save() + version increment → SaveGameResponseDto
```

#### 4. **Terminar Partida y Registrar Score**
```
POST /api/match/users/{uid}/end?finalScore=500 → MatchController 
→ MatchServiceImpl.endMatch() → LeaderboardServiceImpl.recordScore()
→ LeaderboardRepository.save() → LeaderboardResponseDto
```

#### 5. **Ver Top 10 Leaderboard**
```
GET /api/leaderboard/top?limit=10 → LeaderboardController 
→ LeaderboardServiceImpl.getTopEntries(10) → List<LeaderboardResponseDto>
```

#### 6. **Compra de Items**
```
POST /api/purchases/orders → PurchaseController → PurchaseServiceImpl.createPurchaseOrder()
→ Retorna orderId + status PENDING
→ Webhook desde UPBToken → POST /api/purchases/webhook/payment-callback
→ PurchaseServiceImpl.validateWebhook() + completePurchase() o failPurchase()
```

### Validaciones Implementadas

- **Null checks** en DTOs de entrada
- **JWT token validation** (usando `JwtTokenProvider` existente para compatibilidad)
- **Entity existence checks** antes de CRUD
- **Versioning en SaveGame** para conflictos
- **Transactional boundaries** (@Transactional en servicios)
- **Exception handling** con `GlobalExceptionHandler`

### Integración con Externos

#### NoMancos (Wiki de tutoriales)
- Endpoint público: `GET /api/characters` (ya disponible, paginated)
- Retorna: CharacterResponseDto con abilities JSON

#### UPost (Social media)
- Al terminar partida: webhook a UPost con score + usuario
- Leaderboard endpoint: `GET /api/leaderboard/top`

#### UPBToken (Pagos cripto)
- POST `/api/purchases/orders` genera orderId
- Webhook: POST `/api/purchases/webhook/payment-callback` con signature X-Signature
- Completa compra si webhook válido

#### Snack (Cupones)
- Endpoint: `GET /api/purchases/coupons/{code}` (TODO: implementar)
- Al completar nivel → emit event → generar cupón

### Dependencias Usadas

- `spring-boot-starter-data-jpa` (JPA + Hibernate)
- `spring-boot-starter-web` (REST)
- `spring-boot-starter-security` (JWT token filter)
- `postgresql` (driver)
- `lombok` (reducir boilerplate)
- `springdoc-openapi-starter-webmvc-ui` (Swagger/OpenAPI)
- `io.jsonwebtoken:jjwt-*` (JWT)

### Próximas Mejoras

1. **Persistencia de compras:** Entity `PurchaseOrder` + `PurchaseOrderRepository`
2. **Eventos de dominio:** DomainEvent + EventPublisher para level up, purchase complete
3. **Caching:** Redis para leaderboard top 10 (caché caliente)
4. **Transaccionalidad de webhooks:** Idempotencia en handlers de pago
5. **Tests:** Unit tests por servicio, integration tests para controllers
6. **Documentación Swagger:** Mejorar con examples y validations
7. **Seguridad:** Verificar JWT token en cada request (SecurityContextHolder)

### Cómo Ejecutar

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# O desde IDE (Spring Boot)
click Run en BackendUnderUpbApplication.java
```

Acceso a Swagger: `http://localhost:8080/swagger-ui.html`

### Ejemplo de Request/Response

**POST /api/users**
```json
{
  "name": "Player 1",
  "lifePoints": 3,
  "score": 0,
  "currentLevel": 1,
  "inventory": "{}"
}
```

**Response 201**
```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "name": "Player 1",
  "lifePoints": 3,
  "score": 0,
  "currentLevel": 1,
  "inventory": "{}",
  "createdDate": "2025-11-28T10:00:00Z",
  "updatedDate": "2025-11-28T10:00:00Z"
}
```
