package org.example.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskControllerV1.class)
class TaskControllerV1Test {

    @Autowired
    MockMvc mvc;

    @Test
    void createsTaskAsNotDone() throws Exception {
        mvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Comprar leche"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Comprar leche"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void listsCreatedTasks() throws Exception {
        mvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Repasar Spring Boot"}
                                """))
                .andExpect(status().isCreated());

        mvc.perform(get("/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Repasar Spring Boot")));
    }

    @Test
    void getsExistingTaskById() throws Exception {
        String created = mvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Consultar una tarea"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int id = JsonPath.read(created, "$.id");

        mvc.perform(get("/v1/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Consultar una tarea"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void missingTaskReturns404() throws Exception {
        mvc.perform(get("/v1/tasks/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
