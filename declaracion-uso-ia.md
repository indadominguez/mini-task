# Declaración de uso de IA

## 1. Resumen

| Campo | Respuesta |
|---|---|
| Ejercicio/Proyecto | Mini Tasks (UD2a, 15 %) |
| Alumno/a o grupo | [Completar nombre] |
| Fecha | 28/10/2026 |
| ¿Se ha usado IA? | Sí |
| Herramientas | DeepSeek V4.1 Flash Free mediante OpenCode |
| Ámbitos de uso | Código, pruebas, depuración y documentación |

## 2. Registro de usos materiales

| # | Herramienta y propósito | Resultado usado, modificado o rechazado | Verificación realizada y evidencia | Fuentes o criterio de contraste | Archivo, decisión o prueba afectada |
|---|---|---|---|---|---|
| 1 | Configurar el proyecto Spring Boot 4 con JPA, H2 y Flyway | Usado y adaptado: se cambió `flyway-core` por `spring-boot-starter-flyway` tras el error `Schema validation: missing table [tasks]` | `mvn test` en verde y log `Successfully applied 1 migration` | Mensaje de error de Spring Boot, repositorio local de Maven y documentación de Spring Boot 4 | `pom.xml`, `application.yaml`, `V1__create_tasks_table.sql` |
| 2 | Crear la entidad `Task` y su primer test | Usado y revisado: test antes que entidad, constructor sin argumentos para JPA y `completed=false` inicial | Rojo por `cannot find symbol: class Task`; verde con `mvn test` | Enunciado de la sesión 2 y pruebas automatizadas | `domain/Task.java`, `domain/TaskTest.java`, migración V1 |
| 3 | Crear la base de controladores V1/V2 y el `GET /v1/tasks/{id}` | Usado y adaptado: V1/V2 en memoria con `done`; el `404` se implementó con `ResponseEntity.notFound()` | MockMvc: rojo `404` esperando `200`; rojo `NoSuchElementException`; verde final | Enunciado de la sesión 3 y pruebas `TaskControllerV1Test`/`TaskControllerV2Test` | `web/TaskControllerV1.java`, `web/TaskControllerV2.java`, `model/SimpleTask.java` |
| 4 | Crear `TaskService`, `TaskRepository` y `TaskResponseDTO`, y normalizar el título | Usado y adaptado: `dto.title().strip()`; la entidad conserva `completed` y el DTO expone `done` | Rojo `expected: "Repasar Spring Boot" but was: "  Repasar Spring Boot  "`; verde con `TaskServiceTest` y suite completa de 10 pruebas | Enunciado de la sesión 4, Mockito y pruebas automatizadas | `service/TaskService.java`, `repository/TaskRepository.java`, `dto/TaskResponseDTO.java`, `service/TaskServiceTest.java` |
| 5 | Redactar `aprendizaje.md` y esta declaración | Usado y revisado: se registran rojo, cambio, verde y decisiones de diseño | Contraste con el historial de Git y las salidas de `mvn test` | Documentos de las sesiones 2, 3 y 4 | `aprendizaje.md`, `declaracion-uso-ia.md` |

## 3. Explicación y defensa

- **Problema y motivo.** El proyecto se construye desde cero y se apoya en IA para
  generar el esqueleto, entender errores de compilación y contrastar el contrato
  HTTP. El objetivo era aprender la mecánica TDD, no copiar una solución cerrada.
- **Qué se aceptó, cambió o rechazó.** Se aceptaron propuestas de estructura y se
  adaptaron al enunciado: por ejemplo, se rechazó usar solo `flyway-core` porque
  en Boot 4 no activa la auto-configuración, y se mantuvo `completed` en la
  entidad para no romper la migración V1, traduciéndolo a `done` en el DTO.
- **Cómo se comprobó.** Cada paso se validó ejecutando `mvn test`: primero el
  rojo esperado, después el verde. También se comprobó el log de Flyway y el
  arranque del contexto con H2.
- **Fuentes.** Enunciados de las sesiones, mensajes de error de Maven/Spring,
  documentación de Spring Boot 4 y pruebas automatizadas del propio repositorio.

## 4. Declaración

Declaro que:

- he registrado el uso material de IA o he indicado expresamente que no la usé;
- he verificado y comprendo cualquier resultado incorporado;
- puedo localizar, explicar y defender las decisiones, código y pruebas afectados;
- no he introducido secretos, datos personales ni datos privados del proyecto en las consultas a IA.

