# Aprendizaje — Mini Tasks

Registro de lo construido, de los ciclos TDD y de las decisiones tomadas.
El proyecto se trabaja en `main`, sin fork ni plantilla.

## Sesión 2 — Entidad `Task` y primer test

### Qué se pidió

Una entidad `Task` con `id`, `title` y `completed`, con `completed = false` al
crearse, y la migración Flyway correspondiente.

### Ciclo TDD

1. **Rojo.** Escribí `TaskTest` antes de crear la entidad:

   ```java
   var task = new Task("Comprar leche");
   assertThat(task.isCompleted()).isFalse();
   ```

   `mvn test` falló porque `Task` no existía:

   ```text
   cannot find symbol: class Task
   ```

2. **Verde.** Creé la entidad JPA mínima (`@Entity`, `@Id`,
   `@GeneratedValue`, `@Column`) con un constructor `Task(String title)` que
   fija `completed = false`, y un constructor sin argumentos requerido por JPA.

3. **Refactor / persistencia.** No había lógica que refactorizar. Añadí:
   - `V1__create_tasks_table.sql` con la tabla `tasks`.
   - `application.yaml` con H2, `ddl-auto: validate` y Flyway activo.
   - `MiniTaskApplicationTests` para comprobar el arranque completo.

### Resultado

`TaskTest` y el test de contexto pasan. Flyway aplica la migración:

```text
Successfully applied 1 migration to schema "PUBLIC", now at version v1
```

### Decisión técnica de Spring Boot 4

Añadir solo `flyway-core` no activa Flyway en Boot 4: la auto-configuración está
modularizada. Es necesario `spring-boot-starter-flyway`. Con `flyway-core` a
secas, Hibernate fallaba con `Schema validation: missing table [tasks]`.

## Sesión 3 — Controladores V1/V2 y `GET /v1/tasks/{id}`

### Punto de partida real del repositorio

El repositorio no tenía todavía `TaskControllerV1`, `TaskControllerV2`,
`SimpleTask` ni `CreateTaskDTO`. Para poder realizar el avance de la sesión 3
los creé como **base mínima**, sin copiar un starter:

- `model/SimpleTask`: tarea en memoria con `id`, `title` y `done`.
- `dto/CreateTaskDTO`: record con el título recibido en el POST.
- `web/TaskControllerV1`: lista y crea tareas en memoria. El POST responde
  `201` mediante `@ResponseStatus`.
- `web/TaskControllerV2`: igual, pero usando `ResponseEntity` y devolviendo la
  cabecera `Location` en la creación.

Los tests de esa base (`TaskControllerV1Test` para listar/crear y
`TaskControllerV2Test` para el `201 + Location`) documentan el contrato, pero
**no son evidencia de TDD**: se escribieron después de la implementación.

### Contrato disponible

| Método | Ruta | Respuesta |
|---|---|---|
| `GET` | `/v1/tasks` | `200` con el array de tareas |
| `POST` | `/v1/tasks` | `201` con la tarea creada y `done=false` |
| `GET` | `/v1/tasks/{id}` | `200` con la tarea o `404` sin cuerpo |
| `GET` | `/v2/tasks` | `200` con el array de tareas |
| `POST` | `/v2/tasks` | `201`, `Location: /v2/tasks/{id}` y la tarea |

### Ciclo TDD 1 — consultar una tarea existente

**Rojo.** Añadí `getsExistingTaskById`: crea una tarea por HTTP dentro del mismo
test, lee su `id` de la respuesta y hace `GET /v1/tasks/{id}` esperando `200`.

```text
Status expected:<200> but was:<404>
```

Falló porque el controlador solo tenía `GET /v1/tasks` y `POST /v1/tasks`: no
existía ninguna ruta para `/{id}`.

**Verde.** Añadí el método mínimo:

```java
@GetMapping("/{id}")
public ResponseEntity<SimpleTask> get(@PathVariable Long id) {
    SimpleTask task = tasks.stream()
            .filter(candidate -> candidate.getId().equals(id))
            .findFirst()
            .orElseThrow();
    return ResponseEntity.ok(task);
}
```

El test pasó. Todavía no estaba resuelto qué hacer cuando la tarea no existe.

### Ciclo TDD 2 — id inexistente

**Rojo.** Añadí `missingTaskReturns404`, que pide `/v1/tasks/999` y espera `404`
con cuerpo vacío.

```text
jakarta.servlet.ServletException: Request processing failed:
java.util.NoSuchElementException: No value present
```

Falló porque `orElseThrow()` lanzaba una excepción sin traducción a HTTP. El
cliente recibía un error `500`, no un `404`.

**Verde.** Sustituí el cuerpo del método por el mapeo a `ResponseEntity`:

```java
@GetMapping("/{id}")
public ResponseEntity<SimpleTask> get(@PathVariable Long id) {
    return tasks.stream()
            .filter(candidate -> candidate.getId().equals(id))
            .findFirst()
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

El `findFirst()` vacío ya no lanza: se convierte en `404` sin cuerpo.

### Resultado de la sesión 3

```text
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

- `TaskTest`: 1 prueba.
- `MiniTaskApplicationTests`: 1 prueba de contexto (Flyway + JPA).
- `TaskControllerV1Test`: 4 pruebas (crear, listar, consultar por id, 404).
- `TaskControllerV2Test`: 2 pruebas (crear con `Location`, listar).

Comando:

```bash
mvn test
```

## Sesión 4 — Capa de servicios y DTO

### Qué se añadió

El proyecto no tenía `TaskService`, así que lo creé como parte de este avance:

- `repository/TaskRepository`: interfaz que extiende `JpaRepository<Task, Long>`.
- `dto/TaskResponseDTO`: record `(Long id, String title, boolean done)`.
- `service/TaskService`: `createTask(CreateTaskDTO)` guarda la tarea y devuelve
  el DTO de respuesta.
- `TaskServiceTest`: prueba unitaria con el repositorio simulado con Mockito.

`TaskService` lleva `@Service` y `@Transactional`. El test unitario construye el
servicio con `@InjectMocks`, así que no arranca Spring, no usa Flyway ni toca H2.

### Ciclo TDD — normalizar el título

**Rojo.** Escribí `createStripsTitleEdges` antes de implementar la regla:

```java
when(repository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

var result = service.createTask(new CreateTaskDTO("  Repasar Spring Boot  "));

assertThat(result.title()).isEqualTo("Repasar Spring Boot");
```

El servicio inicial guardaba `dto.title()` sin normalizar, por lo que el test
falló:

```text
expected: "Repasar Spring Boot"
 but was: "  Repasar Spring Boot  "
```

**Verde.** Cambié la creación a `new Task(dto.title().strip())`. El test pasó.

**Caso complementario.** Añadí `createKeepsTitleAndStartsNotDone`: con
`"Repasar Spring Boot"` el título se conserva y `done` es `false`.

### Resultado de la sesión 4

```text
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

- `TaskServiceTest`: 2 pruebas.
- Se conservan las 8 pruebas anteriores sin cambios.

### Estado de la checklist de la sesión 4

| Punto | Estado | Evidencia |
|---|---|---|
| Cerrar `GET /v1/tasks/{id}` y `404` | Hecho | `getsExistingTaskById` y `missingTaskReturns404` |
| Crear/localizar `TaskService` | Hecho | `service/TaskService.java`, `repository/TaskRepository.java` |
| Regla del título con TDD | Hecho | Rojo `"  Repasar Spring Boot  "` → verde con `strip()` |
| `createStripsTitleEdges` con repositorio mock | Hecho | `TaskServiceTest`; `save` devuelve su argumento |
| Caso `"Repasar Spring Boot"` y `done=false` | Hecho | `createKeepsTitleAndStartsNotDone` |
| Conservar las pruebas MVC | Hecho | `TaskControllerV1Test` (4) y `TaskControllerV2Test` (2) verdes |
| Registrar rojo, cambio y verde en `aprendizaje.md` | Hecho | Este documento |
| Commits propios | Pendiente | Los cambios de la sesión 4 están sin commitear |
| Declaración de uso de IA | Preparada | `declaracion-uso-ia.md`; faltan datos y firma |

`TaskService` está probado y listo, pero V1/V2 siguen usando sus listas en
memoria: la sección 8 no pide conectarlo todavía a HTTP. Esa conexión queda como
siguiente paso, no como un olvido.

### Decisión: `completed` en la entidad, `done` en el DTO

La entidad JPA mantiene el campo `completed` y la columna de la migración V1,
mientras que el contrato público de las sesiones 3 y 4 usa `done`. `TaskService`
traduce `saved.isCompleted()` a `TaskResponseDTO.done()`. No se modifica una
migración ya aplicada ni se añade otra.

## Decisiones y responsabilidades

- **V1/V2 son en memoria**: no usan JPA ni la tabla `tasks`; el `id` lo genera
  un `AtomicLong`. La persistencia ya existe en `TaskService`/`TaskRepository`,
  pero todavía no está conectada a esas rutas.
- **`done` frente a `completed`**: el contrato de los controladores usa `done`,
  tal como indica el enunciado. La entidad JPA `Task` conserva `completed` de la
  sesión 2 y el DTO traduce el valor a `done`.
- **Sin validación ni OpenAPI todavía**: no se añaden dependencias que el
  avance no pide. `CreateTaskDTO` es un record simple.
- **`404` sin cuerpo**: es el contrato acordado en la sesión 3. El payload
  común de errores llegará con el `@RestControllerAdvice`.
- **Controlador**: rutas, códigos HTTP y cabeceras. **Servicio**: reglas de
  negocio, normalización del título y mapeo a DTO. **Repositorio**: acceso a
  datos. V1/V2 siguen sin servicio porque las tareas viven en memoria.

## Pendiente / siguientes pasos

1. Conectar `TaskService` a un controlador (por ejemplo `/v3/tasks`) y cubrir el
   alta con una prueba MVC que compruebe el título normalizado.
2. Añadir validación de entrada y el tratamiento común de errores.
3. Completar los datos personales y la firma en `autoria.md` y
   `declaracion-uso-ia.md`, y hacer el commit de la sesión 4.
