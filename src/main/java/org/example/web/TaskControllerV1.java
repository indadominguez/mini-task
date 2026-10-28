package org.example.web;

import org.example.dto.CreateTaskDTO;
import org.example.model.SimpleTask;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/v1/tasks")
public class TaskControllerV1 {

    private final List<SimpleTask> tasks = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong();

    @GetMapping
    public List<SimpleTask> list() {
        return tasks;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleTask create(@RequestBody CreateTaskDTO dto) {
        SimpleTask task = new SimpleTask(sequence.incrementAndGet(), dto.title(), false);
        tasks.add(task);
        return task;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimpleTask> get(@PathVariable Long id) {
        return tasks.stream()
                .filter(candidate -> candidate.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
