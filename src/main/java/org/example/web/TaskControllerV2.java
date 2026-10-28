package org.example.web;

import org.example.dto.CreateTaskDTO;
import org.example.model.SimpleTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/v2/tasks")
public class TaskControllerV2 {

    private final List<SimpleTask> tasks = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong();

    @GetMapping
    public ResponseEntity<List<SimpleTask>> list() {
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<SimpleTask> create(@RequestBody CreateTaskDTO dto) {
        SimpleTask task = new SimpleTask(sequence.incrementAndGet(), dto.title(), false);
        tasks.add(task);
        return ResponseEntity.created(URI.create("/v2/tasks/" + task.getId())).body(task);
    }
}
