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
class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getWords_noFilter_returns200WithContent() throws Exception {
        mockMvc.perform(get("/api/words"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(100)))
                .andExpect(jsonPath("$.totalElements").value(1025))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(100));
    }

    @Test
    void getWords_withLevelFilter_returns200() throws Exception {
        mockMvc.perform(get("/api/words").param("level", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].level").value(1));
    }

    @Test
    void getWords_withPagination_returns200() throws Exception {
        mockMvc.perform(get("/api/words").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPages").isNumber());
    }

    @Test
    void getWord_existingId_returns200() throws Exception {
        mockMvc.perform(get("/api/words/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.word").isString());
    }

    @Test
    void getWord_nonExistingId_returns404() throws Exception {
        mockMvc.perform(get("/api/words/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateKnown_validValue_returns200() throws Exception {
        mockMvc.perform(put("/api/words/1/known")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"known\": 1}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateKnown_invalidValue_returns400() throws Exception {
        mockMvc.perform(put("/api/words/1/known")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"known\": 5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStats_returns200WithCounts() throws Exception {
        mockMvc.perform(get("/api/words/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1025))
                .andExpect(jsonPath("$.unknown").isNumber())
                .andExpect(jsonPath("$.learning").isNumber())
                .andExpect(jsonPath("$.mastered").isNumber());
    }
}
