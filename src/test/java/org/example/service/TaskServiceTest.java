package org.example.service;

import org.example.domain.Task;
import org.example.dto.CreateTaskDTO;
import org.example.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository repository;

    @InjectMocks
    TaskService service;

    @Test
    void createStripsTitleEdges() {
        when(repository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.createTask(new CreateTaskDTO("  Repasar Spring Boot  "));

        assertThat(result.title()).isEqualTo("Repasar Spring Boot");
    }

    @Test
    void createKeepsTitleAndStartsNotDone() {
        when(repository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.createTask(new CreateTaskDTO("Repasar Spring Boot"));

        assertThat(result.title()).isEqualTo("Repasar Spring Boot");
        assertThat(result.done()).isFalse();
    }
}
