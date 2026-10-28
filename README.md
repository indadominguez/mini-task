
# Ejercicio 02 — Book Catalog API

## Objetivo

Construir una API REST para un catálogo de libros de forma incremental, aplicando TDD y buenas prácticas de diseño en Spring Boot. Cada entrega añade una capa de complejidad: desde un controlador que responde texto plano hasta una API completa con ResponseEntity, capa de servicios, manejo de excepciones y un `@RestControllerAdvice` global.

## Contenidos y Recursos

Este ejercicio está diseñado como **GitHub Classroom** con tests semilla para cada entrega:

- `recursos/book-catalog-template/`: Proyecto base con el controlador inicial.
- `recursos/book-catalog-tests-seed/`: Tests para cada entrega (usados por GitHub Classroom para evaluar automáticamente).

## Tareas (entregas progresivas)

Realiza commits siguiendo el formato `Entrega X: ...`:

1. **Entrega 1** — Echo text: el controlador devuelve texto plano.
2. **Entrega 2** — JSON: crea la entidad `Book` y DTOs, responde en JSON.
3. **Entrega 3** — ResponseEntity: usa `ResponseEntity` para códigos HTTP precisos (201 Created, 204 No Content, Location header).
4. **Entrega 4** — Servicio: extrae la lógica de negocio a una capa de servicios.
5. **Entrega 5** — Excepciones: define excepciones propias y lanza errores desde el servicio.
6. **Entrega 6** — Global handler: implementa `@RestControllerAdvice` para manejo centralizado de errores.

### Ampliación evaluable: contrato REST verificable

Al completar las seis entregas, el grupo debe añadir pruebas MockMvc de las
operaciones creadas y justificar en el README los verbos, códigos de estado,
cabeceras `Location`, DTOs y errores JSON. La API debe responder de forma
coherente ante alta, consulta, modificación o borrado según las operaciones que
implemente el starter. No se exige aún autenticación, migraciones ni OpenAPI:
se añaden en Gestión de eventos.

## Entregables

- Repositorio Git con los 6 commits en el formato indicado.
- Declaración de uso de IA cumplimentada (ver plantilla en `00-recursos-comunes/plantillas/`).

## Requisitos técnicos

- Spring Boot 4.0.5+, Java 25
- TDD: los tests se proporcionan como semilla — deben pasar antes de pasar a la siguiente entrega
- Cada entrega debe compilar y pasar los tests correspondientes antes de avanzar

## Política de IA

| Aspecto | |
|---------|-|
| Uso de IA permitido | Sí, para comprender errores de compilación o explicar conceptos |
| Declaración obligatoria | Sí |
| Herramientas permitidas | ChatGPT, Claude, Gemini, GitHub Copilot |
| Qué NO está permitido | Copiar la solución completa sin entender cada entrega |

## Evaluación

Ver `rubrica.md` y `ra-ce.md` en este directorio.

## Relación con Battleship

Este ejercicio alcanza las sesiones 1 a 6 de Battleship: TDD, controladores,
servicios, DTOs, reglas y manejo de errores. Consulta
las [sesiones de Battleship](../../02-ejemplos/battleship/docs/README.md) y
traslada cada concepto al catálogo antes de avanzar a la entrega siguiente.
Battleship es la referencia de aula, no una entrega.
