package org.example.service;

import org.example.domain.Task;
import org.example.dto.CreateTaskDTO;
import org.example.dto.TaskResponseDTO;
import org.example.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponseDTO createTask(CreateTaskDTO dto) {
        Task saved = repository.save(new Task(dto.title().strip()));
        return new TaskResponseDTO(saved.getId(), saved.getTitle(), saved.isCompleted());
    }
}
