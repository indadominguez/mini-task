package org.example.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskControllerV2.class)
class TaskControllerV2Test {

    @Autowired
    MockMvc mvc;

    @Test
    void createsTaskWithLocation() throws Exception {
        mvc.perform(post("/v2/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Preparar la entrega"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/v2/tasks/")))
                .andExpect(jsonPath("$.title").value("Preparar la entrega"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void listsTasks() throws Exception {
        mvc.perform(get("/v2/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
