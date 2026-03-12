package com.studyenglish.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GrammarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAll_returns200WithTopics() throws Exception {
        mockMvc.perform(get("/api/grammar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(14)))
                .andExpect(jsonPath("$[0].sortOrder").value(1))
                .andExpect(jsonPath("$[0].title").isString());
    }

    @Test
    void updateCompleted_validValue_returns200() throws Exception {
        mockMvc.perform(put("/api/grammar/1/completed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"completed\": 1}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateCompleted_invalidValue_returns400() throws Exception {
        mockMvc.perform(put("/api/grammar/1/completed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"completed\": 5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getContent_existingTopic_returns200WithHtml() throws Exception {
        mockMvc.perform(get("/api/grammar/1/content"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("<")));
    }

    @Test
    void getContent_nonExistingTopic_returns404() throws Exception {
        mockMvc.perform(get("/api/grammar/99999/content"))
                .andExpect(status().isNotFound());
    }
}
